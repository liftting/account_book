package cn.jiajixin.nuwa

import cn.jiajixin.nuwa.util.*
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

class NuwaPlugin implements Plugin<Project> {
    HashSet<String> includePackage
    HashSet<String> excludeClass
    def debugOn
    def patchList = []
    def beforeDexTasks = []
    private static final String NUWA_DIR = "NuwaDir"
    private static final String NUWA_PATCHES = "nuwaPatches"

    private static final String MAPPING_TXT = "mapping.txt"
    private static final String HASH_TXT = "hash.txt"

    private static final String DEBUG = "debug"


    @Override
    void apply(Project project) {

        project.extensions.create("nuwa", NuwaExtension, project)



        project.afterEvaluate {
            def extension = project.extensions.findByName("nuwa") as NuwaExtension
            includePackage = extension.includePackage
            excludeClass = extension.excludeClass
            debugOn = extension.debugOn

            project.android.applicationVariants.each { variant ->

                if (!variant.name.contains(DEBUG) || (variant.name.contains(DEBUG) && debugOn)) {

                    Map hashMap
                    File nuwaDir
                    File patchDir
                    //
                    // 查找到三个task  preDex dex proguard
                    def preDexTask = project.tasks.findByName("preDex${variant.name.capitalize()}")
                    def dexTask = project.tasks.findByName("dex${variant.name.capitalize()}")
                    def proguardTask = project.tasks.findByName("proguard${variant.name.capitalize()}")

                    // manifest 文件task  为了得到一些应用信息
                    def processManifestTask = project.tasks.findByName("process${variant.name.capitalize()}Manifest")
                    def manifestFile = processManifestTask.outputs.files.files[0]

                    /**
                     * 这个属性是从控制台输入的,代表之前release版本生成的混淆文件和hash文件目录,这两个文件发版时需要保持
                     * ./gradlew clean nuwaQihooDebugPatch -P NuwaDir=/Users/jason/Documents/nuwa
                     */

                    def oldNuwaDir = NuwaFileUtils.getFileFromProperty(project, NUWA_DIR)
                    if (oldNuwaDir) {
                        /**
                         * 如果文件夹存在的话混淆的时候应用mapping
                         * 从
                         */
                        def mappingFile = NuwaFileUtils.getVariantFile(oldNuwaDir, variant, MAPPING_TXT)
                        NuwaAndroidUtils.applymapping(proguardTask, mappingFile)
                    }
                    if (oldNuwaDir) {
                        /**
                         * 如果文件夹存在的话获得各个class的hash
                         */
                        def hashFile = NuwaFileUtils.getVariantFile(oldNuwaDir, variant, HASH_TXT)
                        hashMap = NuwaMapUtils.parseMap(hashFile)
                    }

                    def dirName = variant.dirName //  -- /debug
                    nuwaDir = new File("${project.buildDir}/outputs/nuwa") //定位到项目编译的输出路径
                    def outputDir = new File("${nuwaDir}/${dirName}") ///build/outputs/nuwa/debug/
                    def hashFile = new File(outputDir, "hash.txt") // /build/outputs/nuwa/debug/hash.txt

                    //创建闭包文件处理
                    Closure nuwaPrepareClosure = {
                        def applicationName = NuwaAndroidUtils.getApplication(manifestFile)
                        if (applicationName != null) { //获取 applicationName
                            excludeClass.add(applicationName) /// 自定义了 不执行class字节码修改
                        }

                        outputDir.mkdirs()
                        if (!hashFile.exists()) {
                            hashFile.createNewFile()
                        }

                        if (oldNuwaDir) {
                            /**
                             * 此目录存patch的classes
                             * /build/outputs/nuwa/debug/patch/
                             */
                            patchDir = new File("${nuwaDir}/${dirName}/patch")
                            patchDir.mkdirs()
                            patchList.add(patchDir)
                        }
                    }

                    //task是在这里进行动态注入的 这里是我们自己命令行 注入的 task
                    //nuwaDebugPatch  nuwaReleasePath
                    def nuwaPatch = "nuwa${variant.name.capitalize()}Patch"
                    project.task(nuwaPatch) << {
                        if (patchDir) {// task执行时  执行的 dex操作
                            NuwaAndroidUtils.dex(project, patchDir)
                        }
                    }
                    def nuwaPatchTask = project.tasks[nuwaPatch]

                    Closure copyMappingClosure = {
                        if (proguardTask) {// 混淆处理了
                            def mapFile = new File("${project.buildDir}/outputs/mapping/${variant.dirName}/mapping.txt") //拿到mapping文件
                            def newMapFile = new File("${nuwaDir}/${variant.dirName}/mapping.txt");
                            FileUtils.copyFile(mapFile, newMapFile) //将打包的 mapping文件copy到nuwa路径中
                        }
                    }

                    /**
                     * 对于没有开启Multidex的情况，则会存在一个preDex的Task
                     *
                     * preDex会在dex任务之前把所有的库工程和第三方jar包提前打成dex，下次运行只需重新dex被修改的库，以此节省时间。
                     * dex任务会把preDex生成的dex文件和主工程中的class文件一起生成class.dex，这样就需要针对有无preDex，做不同的修改字节码策略即可
                     *
                     */

                    if (preDexTask) {
                        /**
                         * 处理jar文件，这些jar是所有的库工程和第三方jar包，是preDexTask的输入文件
                         */
                        def nuwaJarBeforePreDex = "nuwaJarBeforePreDex${variant.name.capitalize()}"
                        project.task(nuwaJarBeforePreDex) << {
                            /**
                             * 得到所有的jar文件
                             */
                            Set<File> inputFiles = preDexTask.inputs.files.files
                            inputFiles.each { inputFile ->
                                def path = inputFile.absolutePath
                                /**
                                 * 如果是以classes.jar结尾的文件并且路径中不包含com.android.support且路径中中不包含/android/m2repository
                                 */
                                if (NuwaProcessor.shouldProcessPreDexJar(path)) {
                                    /**
                                     * 处理classes.jar,注入字节码 可能修改的是第三方库中的代码结构
                                     */
                                    NuwaProcessor.processJar(hashFile, inputFile, patchDir, hashMap, includePackage, excludeClass)
                                }
                            }
                        }
                        //配置task依赖
                        /**
                         * 处理task依赖
                         * nuwaJarBeforePreDexTask依赖preDexTask之前所有的task
                         * preDexTask依赖nuwaJarBeforePreDexTask
                         */
                        def nuwaJarBeforePreDexTask = project.tasks[nuwaJarBeforePreDex]
                        nuwaJarBeforePreDexTask.dependsOn preDexTask.taskDependencies.getDependencies(preDexTask)
                        preDexTask.dependsOn nuwaJarBeforePreDexTask

                        nuwaJarBeforePreDexTask.doFirst(nuwaPrepareClosure)


                        //创建另外一个task，处理主工程的
                        /**
                         * 处理classes文件，注意这里是主工程的class文件，是dexTask的输入文件
                         */
                        def nuwaClassBeforeDex = "nuwaClassBeforeDex${variant.name.capitalize()}"
                        project.task(nuwaClassBeforeDex) << {
                            Set<File> inputFiles = dexTask.inputs.files.files
                            inputFiles.each { inputFile ->
                                def path = inputFile.absolutePath
                                /**
                                 * 以class结尾,不包含R路径,不是R.class,不是BuildConfig.class文件
                                 */
                                if (path.endsWith(".class") && !path.contains("/R\$") && !path.endsWith("/R.class") && !path.endsWith("/BuildConfig.class")) {
                                    /**
                                     * 包含在includePackage内
                                     */
                                    if (NuwaSetUtils.isIncluded(path, includePackage)) {
                                        /**
                                         * 不包含在excludeClass内
                                         */
                                        if (!NuwaSetUtils.isExcluded(path, excludeClass)) {

                                            /**
                                             * 往class中注入字节码
                                             */

                                            def bytes = NuwaProcessor.processClass(inputFile)
                                            path = path.split("${dirName}/")[1]
                                            /**
                                             * hash校验
                                             */
                                            def hash = DigestUtils.shaHex(bytes)
                                            /**
                                             * 往hash.txt文件中写入hash值
                                             */
                                            hashFile.append(NuwaMapUtils.format(path, hash))
                                            /**
                                             * 与上一个release版本hash值不一样则复制出来,
                                             * 作为patch.jar的组成部分
                                             */
                                            if (NuwaMapUtils.notSame(hashMap, path, hash)) {
                                                /**
                                                 * copy 到Patch的目录中
                                                 */
                                                NuwaFileUtils.copyBytesToFile(inputFile.bytes, NuwaFileUtils.touchFile(patchDir, path))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        /**
                         * 重新处理task依赖关系
                         * nuwaClassBeforeDexTask依赖dexTask这个task之前依赖的所有Task
                         * dexTask这个Task依赖 nuwaClassBeforeDexTask这个Task
                         *
                         * dexTask.taskDependencies.getDependencies(dexTask) 这个是获取 dexTask所有的前依赖Task
                         *
                         */
                        def nuwaClassBeforeDexTask = project.tasks[nuwaClassBeforeDex]
                        nuwaClassBeforeDexTask.dependsOn dexTask.taskDependencies.getDependencies(dexTask)


                        dexTask.dependsOn nuwaClassBeforeDexTask  // 配置dexTask 依赖我们自己的task

                        /**
                         * 最后拷贝mapping文件备份
                         */
                        nuwaClassBeforeDexTask.doLast(copyMappingClosure) // 执行完后，执行 copyMappingClosure 闭包


                        /**
                         * patch的dex操作依赖字节码修改之后的task,即nuwaClassBeforeDexTask
                         */
                        nuwaPatchTask.dependsOn nuwaClassBeforeDexTask
                        beforeDexTasks.add(nuwaClassBeforeDexTask)
                    } else {
                        def nuwaJarBeforeDex = "nuwaJarBeforeDex${variant.name.capitalize()}"
                        project.task(nuwaJarBeforeDex) << {
                            Set<File> inputFiles = dexTask.inputs.files.files
                            /**
                             * 如果没开启preDex 那么就会执行dex操作时，打包所有的 主 子工程 lib库等
                             * 遍历处理
                             */
                            inputFiles.each { inputFile ->
                                def path = inputFile.absolutePath
                                if (path.endsWith(".jar")) {
                                    NuwaProcessor.processJar(hashFile, inputFile, patchDir, hashMap, includePackage, excludeClass)
                                }
                            }
                        }
                        /**
                         * 配置依赖
                         */
                        def nuwaJarBeforeDexTask = project.tasks[nuwaJarBeforeDex]
                        nuwaJarBeforeDexTask.dependsOn dexTask.taskDependencies.getDependencies(dexTask)
                        dexTask.dependsOn nuwaJarBeforeDexTask

                        /**
                         * 1，在修改字节码注入时，先执行创建 闭包文件
                         * 因为nuwaJarBeforeDex 会进行class的注入
                         * 2，doLast 会进行mapping处理，
                         */
                        nuwaJarBeforeDexTask.doFirst(nuwaPrepareClosure)
                        nuwaJarBeforeDexTask.doLast(copyMappingClosure)

                        nuwaPatchTask.dependsOn nuwaJarBeforeDexTask
                        beforeDexTasks.add(nuwaJarBeforeDexTask)
                    }

                }
            }

            project.task(NUWA_PATCHES) << {
                patchList.each { patchDir ->
                    NuwaAndroidUtils.dex(project, patchDir)
                }
            }
            beforeDexTasks.each {
                project.tasks[NUWA_PATCHES].dependsOn it
            }
        }
    }
}



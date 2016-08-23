package cn.jiajixin.nuwa.util

import org.apache.commons.io.FileUtils
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project

/**
 * Created by jixin.jia on 15/11/10.
 */
class NuwaFileUtils {

    public static File touchFile(File dir, String path) {
        def file = new File("${dir}/${path}")
        file.getParentFile().mkdirs()
        return file
    }

    public static copyBytesToFile(byte[] bytes, File file) {
        if (!file.exists()) {
            file.createNewFile()
        }
        FileUtils.writeByteArrayToFile(file, bytes)
    }

    public static File getFileFromProperty(Project project, String property) {
        def file
        if (project.hasProperty(property)) {
            /**
             * ./gradlew clean nuwaDebugPatch -P NuwaDir=/Users/jason/Documents/nuwa
             *
             * 获得NuwaDir对应的目录,即上次发包的mapping和hash文件所在目录
             */
            file = new File(project.getProperties()[property])
            if (!file.exists()) {
                throw new InvalidUserDataException("${project.getProperties()[property]} does not exist")
            }
            if (!file.isDirectory()) {
                throw new InvalidUserDataException("${project.getProperties()[property]} is not directory")
            }
        }
        return file
    }

    public static File getVariantFile(File dir, def variant, String fileName) {
        return new File("${dir}/${variant.dirName}/${fileName}")
    }

}

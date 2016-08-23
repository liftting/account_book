<?php
/**
 * Created by PhpStorm.
 * User: wm
 * Date: 16/8/18
 * Time: 上午10:32
 */

// Or if you just download the medoo.php into directory, require it with the correct path.
require_once 'medoo.php';

// Initialize
//文件创建的对象，执行完后就释放掉了，
$database = new medoo([
    'database_type' => 'mysql',
    'database_name' => 'name',
    'server' => 'localhost',
    'username' => 'root',
    'password' => 'root',
    'charset' => 'utf8'
]);


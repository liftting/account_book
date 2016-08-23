drop database if exists account_book;

create database account_book;

use account_book;

create table acc_user(
id int(4) not null primary key auto_increment,
name char(20) not null,
passowrd char(100) not null,
nickname char(30),
url text,
is_enable int
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

create table acc_type_main(
id int(4) not null primary key auto_increment,
type_name char(100) not null,
level int
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

create table acc_type_sub(
id int(4) not null primary key auto_increment,
type_name char(100) not null,
parent_id int not null,
level int
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

create table acc_bill_info(
id int(4) not null primary key auto_increment,
bill_use_type int not null,
bill_type int not null,
bill_time timestamp,
bill_note text,
bill_url text
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;



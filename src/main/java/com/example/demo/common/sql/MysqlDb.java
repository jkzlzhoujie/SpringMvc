package com.example.demo.common.sql;

public class MysqlDb implements Db {
    @Override
    public String getDate(String value) {
        return "str_to_date('" + value + "','%Y-%M-%D')";
    }

    @Override
    public String getLongDate(String value) {
        return "str_to_date('" + value + "','%Y-%m-%d %H:%i:%S')";
    }

    @Override
    public String getNumber(String value) {
        return "cast(" + value + " as signed integer)";
    }
}

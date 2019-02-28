package com.example.demo.common.sql;

public interface Db {
    String getDate(String value);

    String getLongDate(String value);

    String getNumber(String value);

    /**
     * 支持的数据类型
     */
    public enum Type {
        /**
         * oracle
         */
        oracle,
        /**
         * mysql
         */
        mysql
    }
}

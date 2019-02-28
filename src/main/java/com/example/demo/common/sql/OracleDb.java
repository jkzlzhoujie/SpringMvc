package com.example.demo.common.sql;

public class OracleDb implements Db {
    @Override
    public String getDate(String value) {
        return "to_date('" + value + "','YYYY-MM-DD')";
    }

    @Override
    public String getLongDate(String value) {
        return "to_date('" + value + "','YYYY-MM-DD HH24:MI:SS')";
    }

    @Override
    public String getNumber(String value) {
        return "to_number(" + value + ")";
    }
}

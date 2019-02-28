package com.example.demo.common.sql;

import java.util.HashMap;

public class DbKit {
    private Db defaultDb;
    private HashMap<Db.Type, Db> mapDB = new HashMap<>();

    private DbKit(Db.Type type) {
        Db db = mapDB.get(type);
        if (db != null) {
            defaultDb = db;
            return;
        }

        switch (type) {
            case mysql:
                db = new MysqlDb();
                break;
            case oracle:
                db = new OracleDb();
                break;
            default:
                break;
        }

        defaultDb = db;
        mapDB.put(type, db);
    }

    public static DbKit use() {
        return use(Db.Type.mysql);
    }

    public static DbKit use(Db.Type type) {
        return new DbKit(type);
    }

    public String getDate(String value) {
        return defaultDb.getDate(value);
    }

    public String getLongDate(String value) {
        return defaultDb.getLongDate(value);
    }

    public String getNumber(String value) {
        return defaultDb.getNumber(value);
    }
}

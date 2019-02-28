package com.example.demo;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;


public class UtilsTest {

    @Test
    public void getGeocoderLatitude() {
        try {
            System.out.println("经度 : " );
            System.out.println("纬度 : " );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Test
    public void searchEsBySql() {
        String start = "";
        String end = "";
        boolean isAll = false;
        String operationType = "1";
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT COUNT(*) as result FROM ").append("log").append(" where  operationType in(")
                .append(operationType).append(")");
        if(StringUtils.isNotEmpty(start) && StringUtils.isNotEmpty(end)){
            sb.append(" and createTime >= '").append(start).append("T00:00:00+0800 ' and createTime<='").append(end).append("T23:59:59+0800' ");
        }
        sb.append(" group by ");
        if (isAll) {
            sb.append("type");
        } else {
            sb.append("informationId,type");
        }
        System.out.println(sb);
    }


}
package com.example.demo.job;

import com.example.demo.common.kafka.Producer;
import com.example.demo.common.sql.DbKit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于一个表就是一个多维数据集的情况，如组织机构表的数据采集
 * 数据采集表要求，不符合要求的表需要先改造后进行数据采集：
 * <p>
 * 表必须是单字段唯一键（或主键），不支持复合唯一键（或主键）
 * 过滤字段只支持单字段，不支持多字段过滤
 * 过滤字段只支持时间和数字字段，不支持其他类型字段
 *
 * @author l4qiang
 * @date 2018-09-18
 */
@Component
@Scope("prototype")
@DisallowConcurrentExecution
public class SingleTableJob implements Job {
    static private Logger logger = LoggerFactory.getLogger(SingleTableJob.class);

    /**
     * 数据来源库
     */
    protected String database;

    /**
     * 数据来源表
     */
    protected String table;

    /**
     * 表主键
     */
    protected String primeKey;

    /**
     * 过滤字段
     */
    protected String filterField;
    /**
     * 过滤字段类型
     */
    protected String filterFieldType;

    /**
     * 过滤数据步长
     */
    protected String size;

    /**
     * 开始时间
     */
    protected String start;

    /**
     * 结束时间
     */
    protected String end;

    /**
     * 查询列
     */
    protected String searchColumn;

    /**
     * 数据集id
     */
    protected String cubeId;

    protected String initializeType;//初始化方式 全表数据 table  列数据 cloumn

    @Autowired
    private Producer producer;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        prepare(jobExecutionContext);

        if(initializeType.equals("table")){
            boolean cleanFlag =  cleanData();
            if( !cleanFlag ){
                return;
            }
        }
        String sql = sqlGenerate();
        String[] countSql = sql.split("from");
        sql = "select count(*) from " + countSql[1];
        try {
            int rows = jdbcTemplate.queryForObject(sql, Integer.class);
            int perCount = 10000;
            if (rows > perCount) {
                int count = rows / perCount;
                int remainder = rows % perCount;
                if (remainder != 0) {
                    count++;
                } else {
                    remainder = perCount;
                }
                for (int i = 0; i < count; i++) {
                    int row,start = 0;
                    if (i != 0) {
                        start = i * perCount;
                    }
                    // 确定抽取多少条数据
                    if (i + 1 == count) {
                        row = remainder;
                    } else {
                        row = perCount;
                    }
                    List<Map<String, Object>> list = fetch(start, row);
                    saveData(list);
                }
            } else {
                List<Map<String, Object>> list = fetch(0, perCount);
                saveData(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepare(JobExecutionContext jobExecutionContext) {
        //spring注入
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();


        initializeType = jobDataMap.getString("initializeType");
        database = jobDataMap.getString("database");
        table = jobDataMap.getString("table");
        primeKey = jobDataMap.getString("primeKey");
        filterField = jobDataMap.getString("filterField");
        filterFieldType = jobDataMap.getString("filterFieldType");
        size = jobDataMap.getString("size");
        start = jobDataMap.getString("start");
        end = jobDataMap.getString("end");
        searchColumn = jobDataMap.getString("searchColumn");
        cubeId = jobDataMap.getString("cubeId");
    }

    /**
     * 清空数据 发送消息
     */
    private boolean cleanData() {
        Map<String, Object> dataMap = new HashMap<>(2);
        dataMap.put("dataSource", "mysql");
        dataMap.put("database", database);
        dataMap.put("table", table);
        dataMap.put("action", "DelAll");
        dataMap.put("cubeId", cubeId);
        try {
            String jsonData = objectMapper.writeValueAsString(dataMap);
            logger.info("清除消息：{}",jsonData);
           return producer.sendMessage(Producer.sepTopic, jsonData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return  false;
        }
    }

    private void saveData(List<Map<String, Object>> list) {
        if (list == null) {
            logger.warn("未获取到数据");
            return;
        }
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("database", database);
        dataMap.put("dataSource", "mysql");
        dataMap.put("action", "PutAll");
        dataMap.put("table", table);
        dataMap.put("cubeId", cubeId);
        List<Map<String,Object>> dataList = new ArrayList<>();
        int p = 1;
        int perCount = 200;
        int d = list.size()/perCount;
        int y = list.size()%perCount;
        for(int i = 0; i < list.size() ; i++){
            Map<String,Object> map = new HashMap<>();
            Map<String,Object> item = list.get(i);
            item.forEach((key, value) -> {
                if (key.equals(primeKey)) {
                    map.put("rowkey", value);
                }
                map.put(key, value);
            });
            dataList.add(map);
            if(list.size() < perCount){
                dataMap.put("dataList", dataList);
                sendDataMessage(dataMap);
                dataList.clear();
            }else {
                if((i+1) == perCount*p){
                    p++;
                    dataMap.put("dataList", dataList);
                    sendDataMessage(dataMap);
                    dataList.clear();
                }else{
                    //有余数时，最后一组数据
                    if(d > 0 && y > 0 && i==list.size()-1){
                        dataMap.put("dataList", dataList);
                        sendDataMessage(dataMap);
                        dataList.clear();
                    }
                }
            }
        }
    }

    private boolean sendDataMessage(Map<String,Object> dataMap){
        try {
            String jsonData = objectMapper.writeValueAsString(dataMap);
            Thread.sleep(50);
//            logger.info("消息：{}",jsonData);
            return producer.sendMessage(Producer.sepTopic, jsonData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<Map<String, Object>> fetch(Integer start, Integer row) {
        String sql = sqlGenerate();
        sql += " limit " + start + "," + row;
        logger.info("sql={}",sql);
        return jdbcTemplate.queryForList(sql);
    }

    public String sqlGenerate() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(searchColumn)) {
            sb.append("select ").append(primeKey).append(",").append(searchColumn).append(" from ").append(database).append(".").append(table);
        } else {
            sb.append("select * from ").append(database).append(".").append(table);
        }

        if (StringUtils.isNotEmpty(filterField) && (StringUtils.isNotEmpty(start) || StringUtils.isNotEmpty(end))) {
            sb.append(" where ");
            if ("number".equals(filterFieldType)) {
                if (StringUtils.isNotEmpty(start) && StringUtils.isNotEmpty(end)) {
                    sb.append(filterField).append(">=").append(start).append(" and ").append(filterField).append("<=").append(end);
                } else if (StringUtils.isNotEmpty(start) && StringUtils.isEmpty(end)) {
                    sb.append(filterField).append(">=").append(start);
                } else if (StringUtils.isEmpty(start) && StringUtils.isNotEmpty(end)) {
                    sb.append(filterField).append("<=").append(end);
                }
            } else if ("date".equals(filterFieldType)) {
                if (StringUtils.isNotEmpty(start) && StringUtils.isNotEmpty(end)) {
                    sb.append(filterField).append(">=").append(DbKit.use().getLongDate(start)).append(" and ")
                            .append(filterField).append("<=").append(DbKit.use().getLongDate(end));
                } else if (StringUtils.isNotEmpty(start) && StringUtils.isEmpty(end)) {
                    sb.append(filterField).append(">=").append(DbKit.use().getLongDate(start));
                } else if (StringUtils.isEmpty(start) && StringUtils.isNotEmpty(end)) {
                    sb.append(filterField).append("<=").append(DbKit.use().getLongDate(end));
                }
            } else {
                logger.warn("不支持的过滤字段类型");
                return null;
            }
        }
        return sb.toString();
    }

    /**
     * TODO:没有设置数据库来源和数据库类型，当前使用默认数据库
     *
     * @return
     */
    /*private List<Map<String, Object>> fetch() {
        String sql = "";
        if ("number".equals(filterFieldType)) {
            Long lngTemp = Long.parseLong(start) + Long.parseLong(size);
            String temp = lngTemp.toString();

            sql = "select * from " + table +
                    " where " + filterField + ">=" + start + " and " + filterField + "<" + temp + " and " +
                    filterField + "<=" + end;

            start = temp;

        } else if ("date".equals(filterFieldType)) {
            Date date = DateUtil.toDateFromTime(start);
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(java.util.Calendar.HOUR_OF_DAY, Integer.parseInt(size));
            String temp = DateUtil.toString(calendar.getTime(), DateUtil.DEFAULT_YMDHMSDATE_FORMAT);

            sql = "select * from " + table +
                    " where " + filterField + ">=" + DbKit.use().getLongDate(start) + " and " + filterField + "<" + DbKit.use().getLongDate(temp) + " and " +
                    filterField + "<=" + DbKit.use().getLongDate(end);

            start = temp;
        } else {
            logger.warn("不支持的过滤字段类型");
            return null;
        }

        return jdbcTemplate.queryForList(sql);
    }*/
}

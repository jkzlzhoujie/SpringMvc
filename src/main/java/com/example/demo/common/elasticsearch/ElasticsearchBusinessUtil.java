package com.example.demo.common.elasticsearch;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.example.demo.common.util.date.DateUtil;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.nlpcn.es4sql.domain.Select;
import org.nlpcn.es4sql.jdbc.ObjectResult;
import org.nlpcn.es4sql.jdbc.ObjectResultsExtractor;
import org.nlpcn.es4sql.parse.ElasticSqlExprParser;
import org.nlpcn.es4sql.parse.SqlParser;
import org.nlpcn.es4sql.query.AggregationQueryAction;
import org.nlpcn.es4sql.query.DefaultQueryAction;
import org.nlpcn.es4sql.query.SqlElasticSearchRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author janseny
 * @version 1.1.0
 *
 */
@Component
public class ElasticsearchBusinessUtil {

    private Logger logger = LoggerFactory.getLogger(ElasticsearchBusinessUtil.class);

    @Autowired
    private ElasticFactory elasticFactory;

    public List excute(String sql, Class clazz, String esType, String esIndex) {
        List saveModels = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            //解决 group by之后默认是200的问题
            if (sql.toLowerCase().contains("group by")) {
                sql = sql + " limit 0,2000";
            }

            SQLExprParser parser = new ElasticSqlExprParser(sql);
            SQLExpr expr = parser.expr();
            SQLQueryExpr queryExpr = (SQLQueryExpr) expr;

            Select select = null;
            select = new SqlParser().parseSelect(queryExpr);

            //通过抽象语法树，封装成自定义的Select，包含了select、from、where group、limit等
            AggregationQueryAction action = null;
            DefaultQueryAction queryAction = null;
            SqlElasticSearchRequestBuilder requestBuilder = null;
            if (select.isAgg) {
                //包含计算的的排序分组的
                action = new AggregationQueryAction(elasticFactory.getTransportClient(), select);
                requestBuilder = action.explain();
            } else {
                //封装成自己的Select对象
                queryAction = new DefaultQueryAction(elasticFactory.getTransportClient(), select);
                requestBuilder = queryAction.explain();
            }
            SearchResponse response = (SearchResponse) requestBuilder.get();
            Object queryResult = null;
            if (sql.toUpperCase().indexOf("GROUP") != -1 || sql.toUpperCase().indexOf("SUM") != -1 || select.isAgg) {
                queryResult = response.getAggregations();
            } else {
                queryResult = response.getHits();
            }
            ObjectResult temp = new ObjectResultsExtractor(true, true, true).extractResults(queryResult, true);
            List<String> heads = temp.getHeaders();
            temp.getLines().forEach(one -> {
                Object saveModel = null;
                try {

                    saveModel = clazz.newInstance();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
                for (int i = 0; i < one.size(); i++) {
                    try {
                        String key = null;
                        Object value = one.get(i);
                        if (heads.get(i).startsWith("_")) {
                            if(heads.get(i).contains("_id")){
                                clazz.getMethod("setId", String.class).invoke(saveModel, value);
                            }
                            continue;
                        }
                        key = "set" + UpFirstStr(heads.get(i));
                        if (heads.get(i).contains("quotaDate") || heads.get(i).contains("createTime") || heads.get(i).contains("date_histogram")) {
                            if (heads.get(i).contains("date_histogram")) {
                                key = "setQuotaDate";
                            }
                            
                            try {
                                //yyyy-MM-dd'T'HH:mm:ssXX
                                value = dateFormat.parse(String.valueOf(one.get(i)));
                            } catch (Exception e) {
                                //yyyy-MM-dd HH:mm:ss
                                try {
                                    value = dateFormat1.parse(String.valueOf(one.get(i)));
                                }catch (Exception e1){
                                    Timestamp ts = new Timestamp(Long.parseLong(String.valueOf(one.get(i))));
                                    try {
                                        Date date = new Date();
                                        date = ts;
                                        value =date;
                                    
                                    } catch (Exception e2) {
                                        value = String.valueOf(one.get(i));
                                    }
                                }
                                
                            }
//                            value = DateUtil.strToDate(String.valueOf(value).replace("T00:00:00+0800", " 00:00:00"), "yyyy-MM-dd HH:mm:ss");
                        }

                        if (value instanceof String) {
                            clazz.getMethod(key, String.class).invoke(saveModel, value);
                        } else if (value instanceof Integer) {
                            clazz.getMethod(key, Integer.class).invoke(saveModel, value);
                        } else if (value instanceof Double) {
                            clazz.getMethod(key, Double.class).invoke(saveModel, value);
                        } else if (value instanceof Date) {
                            clazz.getMethod(key, Date.class).invoke(saveModel, value);
                        } else if (value instanceof List) {
                            clazz.getMethod(key, List.class).invoke(saveModel, value);
                        }
                    } catch (Exception e) {
                        logger.warn(e.getMessage());
                    }
                }
                saveModels.add(saveModel);
            });
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return saveModels;
    }

    public Long excuteForLong(String sql, String esType, String esIndex) {
        try {
            SQLExprParser parser = new ElasticSqlExprParser(sql);
            SQLExpr expr = parser.expr();
            SQLQueryExpr queryExpr = (SQLQueryExpr) expr;

            Select select = null;
            select = new SqlParser().parseSelect(queryExpr);

            //通过抽象语法树，封装成自定义的Select，包含了select、from、where group、limit等
            AggregationQueryAction action = null;
            DefaultQueryAction queryAction = null;
            SqlElasticSearchRequestBuilder requestBuilder = null;
            if (select.isAgg) {
                //包含计算的的排序分组的
                action = new AggregationQueryAction(elasticFactory.getTransportClient(), select);
                requestBuilder = action.explain();
            } else {
                //封装成自己的Select对象
                queryAction = new DefaultQueryAction(elasticFactory.getTransportClient(), select);
                requestBuilder = queryAction.explain();
            }
            SearchResponse response = (SearchResponse) requestBuilder.get();
            ObjectResult temp = new ObjectResultsExtractor(true, true, true).extractResults(response.getAggregations(), true);
            Long Longvalue = ((Double) temp.getLines().get(0).get(0)).longValue();
            return Longvalue;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return 0L;
    }

    /**
     * 首字母大写
     *
     * @param str
     * @return
     */
    private String UpFirstStr(String str) {
        return str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toUpperCase());
    }

    public Object excuteOneObject(String sql, Class clazz, String esType, String esIndex) {
        try {
            SQLExprParser parser = new ElasticSqlExprParser(sql);
            SQLExpr expr = parser.expr();
            SQLQueryExpr queryExpr = (SQLQueryExpr) expr;

            Select select = null;
            select = new SqlParser().parseSelect(queryExpr);

            //通过抽象语法树，封装成自定义的Select，包含了select、from、where group、limit等
            AggregationQueryAction action = null;
            DefaultQueryAction queryAction = null;
            SqlElasticSearchRequestBuilder requestBuilder = null;
            if (select.isAgg) {
                //包含计算的的排序分组的
                action = new AggregationQueryAction(elasticFactory.getTransportClient(), select);
                requestBuilder = action.explain();
            } else {
                //封装成自己的Select对象
                queryAction = new DefaultQueryAction(elasticFactory.getTransportClient(), select);
                requestBuilder = queryAction.explain();
            }
            SearchResponse response = (SearchResponse) requestBuilder.get();
            ObjectResult temp = new ObjectResultsExtractor(true, true, true).extractResults(response.getHits(), true);
            List<String> heads = temp.getHeaders();
            Object saveModel = clazz.newInstance();
            try {
                for (int i = 0; i < temp.getLines().get(0).size(); i++) {
                    String key = null;
                    Object value = temp.getLines().get(0).get(i);
                    if (heads.get(i).contains("createTime")) {
                        key = "setCreateTime";
                        value = DateUtil.strToDate(String.valueOf(value).replace("+0800", "").replace("T", " "), "yyyy-MM-dd HH:mm:ss");
                    } else {
                        key = "set" + UpFirstStr(heads.get(i));
                    }

                    if (value instanceof String) {
                        clazz.getMethod(key, String.class).invoke(saveModel, value);
                    } else if (value instanceof Integer) {
                        clazz.getMethod(key, Integer.class).invoke(saveModel, value);
                    } else if (value instanceof Double) {
                        clazz.getMethod(key, Double.class).invoke(saveModel, value);
                    } else if (value instanceof Date) {
                        clazz.getMethod(key, Date.class).invoke(saveModel, value);
                    }
                }
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
            return saveModel;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 时间格式转换  yyyy-MM-dd转成 2017-07-17T00:00:00+0800
     *
     * @param quotaDate
     */
    private String changeDate(String quotaDate) {
        try {
            quotaDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX").format(new SimpleDateFormat("yyyy-MM-dd").parse(quotaDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return quotaDate;
    }

    public String changeTime(String time) {
        try {
            if (time.length() == 10) {
                time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX").format(new SimpleDateFormat("yyyy-MM-dd").parse(time));
            } else if (time.length() == 19) {
                time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }


    public List<Map<String, Object>> excuteDataModel(String sql) {
        List<Map<String, Object>> returnModels = new ArrayList<>();
        try {
            SQLExprParser parser = new ElasticSqlExprParser(sql);
            SQLExpr expr = parser.expr();
            SQLQueryExpr queryExpr = (SQLQueryExpr) expr;
            Select select = null;
            select = new SqlParser().parseSelect(queryExpr);

            //通过抽象语法树，封装成自定义的Select，包含了select、from、where group、limit等
            AggregationQueryAction action = null;
            DefaultQueryAction queryAction = null;
            SqlElasticSearchRequestBuilder requestBuilder = null;
            if (select.isAgg) {
                //包含计算的的排序分组的
                action = new AggregationQueryAction(elasticFactory.getTransportClient(), select);
                requestBuilder = action.explain();
            } else {
                //封装成自己的Select对象
                Client client = elasticFactory.getTransportClient();
                queryAction = new DefaultQueryAction(client, select);
                requestBuilder = queryAction.explain();
            }
            SearchResponse response = (SearchResponse) requestBuilder.get();
            Object queryResult = null;
            if (sql.toUpperCase().indexOf("GROUP") != -1 || sql.toUpperCase().indexOf("SUM") != -1) {
                queryResult = response.getAggregations();
            } else {
                queryResult = response.getHits();
            }
            ObjectResult temp = new ObjectResultsExtractor(true, true, true).extractResults(queryResult, true);
            List<String> heads = temp.getHeaders();
            temp.getLines().stream().forEach(one -> {
                try {
                    Map<String, Object> oneMap = new HashMap<String, Object>();
                    for (int i = 0; i < one.size(); i++) {
                        String key = null;
                        Object value = one.get(i);
                        key = heads.get(i);
                        oneMap.put(key, value);
                    }
                    returnModels.add(oneMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnModels;
    }
}

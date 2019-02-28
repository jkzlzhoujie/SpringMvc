package com.example.demo.common.elasticsearch;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.ValuesSourceAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.*;

/**
 * Created by janseny on 2018/11/30.
 */
@Component
public class ElasticSearchUtil {
    @Autowired
    private ElasticSearchPool elasticSearchPool;

    public ElasticSearchUtil() {
    }

    public void mapping(String index, String type, Map<String, Map<String, String>> source, Map<String, Object> setting) throws IOException {
        TransportClient transportClient = this.elasticSearchPool.getClient();
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder().startObject().startObject("properties");
        Iterator createIndexRequestBuilder = source.keySet().iterator();

        while(createIndexRequestBuilder.hasNext()) {
            String field = (String)createIndexRequestBuilder.next();
            xContentBuilder.startObject(field);
            Map propsMap = (Map)source.get(field);
            Iterator var10 = propsMap.keySet().iterator();

            while(var10.hasNext()) {
                String prop = (String)var10.next();
                xContentBuilder.field(prop, (String)propsMap.get(prop));
            }

            xContentBuilder.endObject();
        }

        xContentBuilder.endObject().endObject();
        CreateIndexRequestBuilder createIndexRequestBuilder1 = transportClient.admin().indices().prepareCreate(index);
        createIndexRequestBuilder1.addMapping(type, xContentBuilder);
        if(setting != null && !setting.isEmpty()) {
            createIndexRequestBuilder1.setSettings(setting);
        }

        createIndexRequestBuilder1.get();
    }

    public void remove(String index) {
        TransportClient transportClient = this.elasticSearchPool.getClient();
        DeleteIndexRequestBuilder deleteIndexRequestBuilder = transportClient.admin().indices().prepareDelete(new String[]{index});
        deleteIndexRequestBuilder.get();
    }

    public Map<String, Object> index(String index, String type, Map<String, Object> source) throws ParseException {
        TransportClient transportClient = this.elasticSearchPool.getClient();
        String _id = (String)source.remove("_id");
        IndexResponse response;
        if(StringUtils.isEmpty(_id)) {
            response = (IndexResponse)transportClient.prepareIndex(index, type).setSource(source).get();
            source.put("_id", response.getId());
        } else {
            response = (IndexResponse)transportClient.prepareIndex(index, type, _id).setSource(source).get();
            source.put("_id", response.getId());
        }

        return source;
    }

    public void bulkIndex(String index, String type, List<Map<String, Object>> source) throws ParseException {
        if(source.size() > 0) {
            TransportClient transportClient = this.elasticSearchPool.getClient();
            BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
            source.forEach((item) -> {
                String _id = (String)item.remove("_id");
                if(StringUtils.isEmpty(_id)) {
                    bulkRequestBuilder.add(transportClient.prepareIndex(index, type).setSource(item));
                } else {
                    bulkRequestBuilder.add(transportClient.prepareIndex(index, type, _id).setSource(item));
                }

            });
            bulkRequestBuilder.get();
        }

    }

    public void delete(String index, String type, String id) {
        TransportClient transportClient = this.elasticSearchPool.getClient();
        transportClient.prepareDelete(index, type, id).get();
    }

    public void bulkDelete(String index, String type, String[] idArr) {
        if(idArr.length > 0) {
            TransportClient transportClient = this.elasticSearchPool.getClient();
            BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
            String[] var6 = idArr;
            int var7 = idArr.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                String id = var6[var8];
                bulkRequestBuilder.add(transportClient.prepareDelete(index, type, id));
            }

            bulkRequestBuilder.get();
        }

    }

    public void deleteByField(String index, String type, String field, Object value) {
        this.deleteByFilter(index, type, field + "=" + value);
    }

    public void deleteByFilter(String index, String type, String filters) {
        QueryBuilder queryBuilder = this.getQueryBuilder(filters);
        this.deleteByFilter(index, type, queryBuilder);
    }

    public void deleteByFilter(String index, String type, QueryBuilder queryBuilder) {
        long count = this.count(index, type, queryBuilder);
        long page = count / 10000L == 0L?1L:count / 10000L + 1L;

        for(long i = 0L; i < page; ++i) {
            List idList = this.getIds(index, type, queryBuilder);
            if(idList.size() > 0) {
                TransportClient transportClient = this.elasticSearchPool.getClient();
                String[] idArr = new String[idList.size()];
                idArr = (String[])idList.toArray(idArr);
                BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
                String[] var14 = idArr;
                int var15 = idArr.length;

                for(int var16 = 0; var16 < var15; ++var16) {
                    String id = var14[var16];
                    bulkRequestBuilder.add(transportClient.prepareDelete(index, type, id));
                }

                bulkRequestBuilder.get();
            }
        }

    }

    public Map<String, Object> update(String index, String type, String id, Map<String, Object> source) throws DocumentMissingException {
        TransportClient transportClient = this.elasticSearchPool.getClient();
        source.remove("_id");
        transportClient.prepareUpdate(index, type, id).setDoc(source).setRetryOnConflict(5).get();
        return this.findById(index, type, id);
    }

    public void voidUpdate(String index, String type, String id, Map<String, Object> source) throws DocumentMissingException {
        TransportClient transportClient = this.elasticSearchPool.getClient();
        source.remove("_id");
        transportClient.prepareUpdate(index, type, id).setDoc(source).setRetryOnConflict(5).get();
    }

    public void bulkUpdate(String index, String type, List<Map<String, Object>> source) throws DocumentMissingException {
        if(source.size() > 0) {
            TransportClient transportClient = this.elasticSearchPool.getClient();
            BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
            source.forEach((item) -> {
                String _id = (String)item.remove("_id");
                if(!StringUtils.isEmpty(_id)) {
                    bulkRequestBuilder.add(transportClient.prepareUpdate(index, type, _id).setDoc(item).setRetryOnConflict(5));
                }

            });
            bulkRequestBuilder.get();
        }

    }

    public Map<String, Object> findById(String index, String type, String id) {
        TransportClient transportClient = this.elasticSearchPool.getClient();
        GetRequest getRequest = new GetRequest(index, type, id);
        GetResponse response = (GetResponse)transportClient.get(getRequest).actionGet();
        Map source = response.getSource();
        if(source != null) {
            source.put("_id", response.getId());
        }

        return source;
    }

    public List<Map<String, Object>> findByField(String index, String type, String field, Object value) {
        return this.list(index, type, field + "=" + value);
    }

    public List<Map<String, Object>> list(String index, String type, String filters) {
        QueryBuilder queryBuilder = this.getQueryBuilder(filters);
        return this.list(index, type, queryBuilder);
    }

    public List<Map<String, Object>> list(String index, String type, QueryBuilder queryBuilder) {
        int size = (int)this.count(index, type, queryBuilder);
        SearchRequestBuilder builder = this.searchRequestBuilder(index, type, queryBuilder, (List)null, Integer.valueOf(0), Integer.valueOf(size));
        SearchResponse response = (SearchResponse)builder.get();
        SearchHits hits = response.getHits();
        ArrayList resultList = new ArrayList();
        SearchHit[] var9 = hits.getHits();
        int var10 = var9.length;

        for(int var11 = 0; var11 < var10; ++var11) {
            SearchHit hit = var9[var11];
            Map source = hit.getSource();
            source.put("_id", hit.getId());
            resultList.add(source);
        }

        return resultList;
    }

    public Page<Map<String, Object>> page(String index, String type, String filters, int page, int size) {
        return this.page(index, type, (String)filters, (String)null, page, size);
    }

    public Page<Map<String, Object>> pageBySort(String index, String type, String filters, String sorts, int page, int size) {
        return this.page(index, type, filters, sorts, page, size);
    }

    public Page<Map<String, Object>> page(String index, String type, String filters, String sorts, int page, int size) {
        QueryBuilder queryBuilder = this.getQueryBuilder(filters);
        List sortBuilders = this.getSortBuilder(sorts);
        return this.page(index, type, queryBuilder, sortBuilders, page, size);
    }

    public Page<Map<String, Object>> page(String index, String type, QueryBuilder queryBuilder, List<SortBuilder> sortBuilders, int page, int size) {
        SearchRequestBuilder builder = this.searchRequestBuilder(index, type, queryBuilder, sortBuilders, Integer.valueOf((page - 1) * size), Integer.valueOf(size));
        SearchResponse response = (SearchResponse)builder.get();
        SearchHits hits = response.getHits();
        ArrayList resultList = new ArrayList();
        SearchHit[] var11 = hits.getHits();
        int var12 = var11.length;

        for(int var13 = 0; var13 < var12; ++var13) {
            SearchHit hit = var11[var13];
            Map source = hit.getSource();
            source.put("_id", hit.getId());
            resultList.add(source);
        }

        return new PageImpl(resultList, new PageRequest(page - 1, size), hits.totalHits());
    }

    public List<String> getIds(String index, String type, String filters) {
        QueryBuilder queryBuilder = this.getQueryBuilder(filters);
        return this.getIds(index, type, queryBuilder);
    }

    public List<String> getIds(String index, String type, QueryBuilder queryBuilder) {
        int size = (int)this.count(index, type, queryBuilder);
        size = size > 10000?10000:size;
        SearchRequestBuilder builder = this.searchRequestBuilder(index, type, queryBuilder, (List)null, Integer.valueOf(0), Integer.valueOf(size));
        SearchResponse response = (SearchResponse)builder.get();
        SearchHits hits = response.getHits();
        ArrayList resultList = new ArrayList();
        SearchHit[] var9 = hits.getHits();
        int var10 = var9.length;

        for(int var11 = 0; var11 < var10; ++var11) {
            SearchHit hit = var9[var11];
            resultList.add(hit.getId());
        }

        return resultList;
    }

    public long count(String index, String type, String filters) {
        QueryBuilder queryBuilder = this.getQueryBuilder(filters);
        return this.count(index, type, queryBuilder);
    }

    public long count(String index, String type, QueryBuilder queryBuilder) {
        SearchRequestBuilder builder = this.searchRequestBuilder(index, type, queryBuilder, (List)null, (Integer)null, (Integer)null);
        return ((SearchResponse)builder.get()).getHits().totalHits();
    }

    public List<Map<String, Object>> findBySql(List<String> field, String sql) throws Exception {
        ArrayList list = new ArrayList();
        DruidDataSource druidDataSource = null;
        DruidPooledConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        ArrayList var9;
        try {
            druidDataSource = this.elasticSearchPool.getDruidDataSource();
            connection = druidDataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                HashMap e = new HashMap();
                Iterator var17 = field.iterator();

                while(var17.hasNext()) {
                    String _field = (String)var17.next();
                    e.put(_field, resultSet.getObject(_field));
                }

                list.add(e);
            }

            ArrayList e1 = list;
            return e1;
        } catch (Exception var14) {
            if(!"Error".equals(var14.getMessage())) {
                var14.printStackTrace();
            }

            var9 = new ArrayList();
        } finally {
            if(resultSet != null) {
                resultSet.close();
            }

            if(preparedStatement != null) {
                preparedStatement.close();
            }

            if(connection != null) {
                connection.close();
            }

            if(druidDataSource != null) {
                druidDataSource.close();
            }

        }

        return var9;
    }

    public ResultSet findBySql(String sql) throws Exception {
        DruidDataSource druidDataSource = null;
        DruidPooledConnection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        ResultSet var6;
        try {
            druidDataSource = this.elasticSearchPool.getDruidDataSource();
            connection = druidDataSource.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            var6 = resultSet;
        } finally {
            if(resultSet != null) {
                resultSet.close();
            }

            if(preparedStatement != null) {
                preparedStatement.close();
            }

            if(connection != null) {
                connection.close();
            }

            if(druidDataSource != null) {
                druidDataSource.close();
            }

        }

        return var6;
    }

    public Map<String, Long> dateHistogram(String index, String type, String filters, Date start, Date end, String field, DateHistogramInterval interval, String format) {
        QueryBuilder queryBuilder = this.getQueryBuilder(filters);
        SearchRequestBuilder builder = this.searchRequestBuilder(index, type, queryBuilder, (List)null, Integer.valueOf(0), Integer.valueOf(0));
        DateHistogramBuilder dateHistogramBuilder = new DateHistogramBuilder(index + "-" + field);
        dateHistogramBuilder.field(field);
        dateHistogramBuilder.interval(interval);
        if(!StringUtils.isEmpty(format)) {
            dateHistogramBuilder.format(format);
        }

        dateHistogramBuilder.minDocCount(0L);
        dateHistogramBuilder.extendedBounds(Long.valueOf(start.getTime()), Long.valueOf(end.getTime()));
        builder.addAggregation(dateHistogramBuilder);
        SearchResponse response = (SearchResponse)builder.get();
        Histogram histogram = (Histogram)response.getAggregations().get(index + "-" + field);
        HashMap temp = new HashMap();
        histogram.getBuckets().forEach((item) -> {
            Long var10000 = (Long)temp.put(item.getKeyAsString(), Long.valueOf(item.getDocCount()));
        });
        return temp;
    }

    public int cardinality(String index, String type, String filters, String filed) {
        QueryBuilder queryBuilder = this.getQueryBuilder(filters);
        SearchRequestBuilder builder = this.searchRequestBuilder(index, type, queryBuilder, (List)null, Integer.valueOf(0), Integer.valueOf(0));
        CardinalityBuilder cardinality = (CardinalityBuilder) AggregationBuilders.cardinality("cardinality").field(filed);
        builder.addAggregation(cardinality);
        SearchResponse response = (SearchResponse)builder.get();
        InternalCardinality internalCard = (InternalCardinality)response.getAggregations().get("cardinality");
        return (new Double(internalCard.getProperty("value").toString())).intValue();
    }

    public Map<String, Long> countByGroup(String index, String type, String filters, String groupField) {
        QueryBuilder queryBuilder = this.getQueryBuilder(filters);
        SearchRequestBuilder builder = this.searchRequestBuilder(index, type, queryBuilder, (List)null, (Integer)null, (Integer)null);
        ValuesSourceAggregationBuilder aggregation = AggregationBuilders.terms("count").field(groupField);
        builder.addAggregation(aggregation);
        SearchResponse response = (SearchResponse)builder.get();
        Terms terms = (Terms)response.getAggregations().get("count");
        List buckets = terms.getBuckets();
        HashMap groupMap = new HashMap();
        Iterator var12 = buckets.iterator();

        while(var12.hasNext()) {
            Terms.Bucket bucket = (Terms.Bucket)var12.next();
            groupMap.put(bucket.getKey().toString(), Long.valueOf(bucket.getDocCount()));
        }

        return groupMap;
    }

    public Map<String, Double> sumByGroup(String index, String type, String filters, String sumField, String groupField) {
        QueryBuilder queryBuilder = this.getQueryBuilder(filters);
        SearchRequestBuilder builder = this.searchRequestBuilder(index, type, queryBuilder, (List)null, (Integer)null, (Integer)null);
        TermsBuilder aggregation = (TermsBuilder)AggregationBuilders.terms("sum_query").field(groupField);
        SumBuilder sumBuilder = (SumBuilder)AggregationBuilders.sum("sum_row").field(sumField);
        aggregation.subAggregation(sumBuilder);
        builder.addAggregation(aggregation);
        SearchResponse response = (SearchResponse)builder.get();
        Terms terms = (Terms)response.getAggregations().get("sum_query");
        List buckets = terms.getBuckets();
        HashMap groupMap = new HashMap();
        Iterator var14 = buckets.iterator();

        while(var14.hasNext()) {
            Terms.Bucket bucket = (Terms.Bucket)var14.next();
            Sum sum2 = (Sum)bucket.getAggregations().get("sum_row");
            groupMap.put(bucket.getKey().toString(), Double.valueOf(sum2.getValue()));
        }

        return groupMap;
    }

    public SearchRequestBuilder searchRequestBuilder(String index, String type, QueryBuilder queryBuilder, List<SortBuilder> sortBuilders, Integer from, Integer size) {
        TransportClient transportClient = this.elasticSearchPool.getClient();
        SearchRequestBuilder builder = transportClient.prepareSearch(new String[]{index});
        builder.setTypes(new String[]{type});
        builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        builder.setQuery(queryBuilder);
        builder.setExplain(true);
        if(sortBuilders != null) {
            sortBuilders.forEach((item) -> {
                builder.addSort(item);
            });
        }

        if(from != null) {
            builder.setFrom(from.intValue());
        }

        if(size != null) {
            builder.setSize(size.intValue());
        }

        return builder;
    }

    public List<SortBuilder> getSortBuilder(String sorts) {
        ArrayList sortBuilderList = new ArrayList();
        if(StringUtils.isEmpty(sorts)) {
            return sortBuilderList;
        } else {
            String[] sortArr = sorts.split(";");
            String[] var4 = sortArr;
            int var5 = sortArr.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String sort = var4[var6];
                String operator = sort.substring(0, 1);
                FieldSortBuilder sortBuilder = new FieldSortBuilder(sort.substring(1));
                if("-".equalsIgnoreCase(operator.trim())) {
                    sortBuilder.order(SortOrder.DESC);
                } else if("+".equalsIgnoreCase(operator.trim())) {
                    sortBuilder.order(SortOrder.ASC);
                } else {
                    sortBuilder.order(SortOrder.DESC);
                }

                sortBuilderList.add(sortBuilder);
            }

            return sortBuilderList;
        }
    }

    public QueryBuilder getQueryBuilder(String filters) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(StringUtils.isEmpty(filters)) {
            return boolQueryBuilder;
        } else {
            String[] filterArr = filters.split(";");
            String[] var4 = filterArr;
            int var5 = filterArr.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String filter = var4[var6];
                String[] condition;
                if(!filter.contains("||")) {
                    if(filter.contains("?")) {
                        condition = filter.split("\\?");
                        if("null".equals(condition[1])) {
                            condition[1] = "";
                        }

                        MatchQueryBuilder var15 = QueryBuilders.matchPhraseQuery(condition[0], condition[1]);
                        boolQueryBuilder.must(var15);
                    } else {
                        String[] var16;
                        TermQueryBuilder var17;
                        TermsQueryBuilder var19;
                        if(filter.contains("<>")) {
                            condition = filter.split("<>");
                            if(condition[1].contains(",")) {
                                var16 = condition[1].split(",");
                                var19 = QueryBuilders.termsQuery(condition[0], var16);
                                boolQueryBuilder.mustNot(var19);
                            } else {
                                if("null".equals(condition[1])) {
                                    condition[1] = "";
                                }

                                var17 = QueryBuilders.termQuery(condition[0], condition[1]);
                                boolQueryBuilder.mustNot(var17);
                            }
                        } else {
                            RangeQueryBuilder var18;
                            if(filter.contains(">=")) {
                                condition = filter.split(">=");
                                var18 = QueryBuilders.rangeQuery(condition[0]);
                                var18.gte(condition[1]);
                                boolQueryBuilder.must(var18);
                            } else if(filter.contains(">")) {
                                condition = filter.split(">");
                                var18 = QueryBuilders.rangeQuery(condition[0]);
                                var18.gt(condition[1]);
                                boolQueryBuilder.must(var18);
                            } else if(filter.contains("<=")) {
                                condition = filter.split("<=");
                                var18 = QueryBuilders.rangeQuery(condition[0]);
                                var18.lte(condition[1]);
                                boolQueryBuilder.must(var18);
                            } else if(filter.contains("<")) {
                                condition = filter.split("<");
                                var18 = QueryBuilders.rangeQuery(condition[0]);
                                var18.lt(condition[1]);
                                boolQueryBuilder.must(var18);
                            } else if(filter.contains("=")) {
                                condition = filter.split("=");
                                if(condition[1].contains(",")) {
                                    var16 = condition[1].split(",");
                                    var19 = QueryBuilders.termsQuery(condition[0], var16);
                                    boolQueryBuilder.must(var19);
                                } else {
                                    if("null".equals(condition[1])) {
                                        condition[1] = "";
                                    }

                                    var17 = QueryBuilders.termQuery(condition[0], condition[1]);
                                    boolQueryBuilder.must(var17);
                                }
                            }
                        }
                    }
                } else {
                    condition = filter.split("\\|\\|");
                    BoolQueryBuilder termQueryBuilder = QueryBuilders.boolQuery();
                    String[] termsQueryBuilder = condition;
                    int var11 = condition.length;

                    for(int var12 = 0; var12 < var11; ++var12) {
                        String filed = termsQueryBuilder[var12];
                        String[] condition1 = filed.split("=");
                        if("null".equals(condition1[1])) {
                            condition1[1] = "";
                        }

                        termQueryBuilder.should(QueryBuilders.termQuery(condition1[0], condition1[1]));
                    }

                    boolQueryBuilder.must(termQueryBuilder);
                }
            }

            return boolQueryBuilder;
        }
    }
}

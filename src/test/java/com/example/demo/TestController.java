package com.example.demo;

import com.example.demo.common.elasticsearch.ElasticSearchUtil;
import com.example.demo.common.kafka.Producer;
import com.example.demo.common.solr.SolrUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by janseny on 2018/11/30.
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
public class TestController {

    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private Producer producer;
    @Autowired
    private SolrUtil solrUtil;

    @Test
    public void sendKafkaMessage(){
        String topic = "sep-hbase-data";
        System.out.println("test");
        producer.sendMessage(topic,"ss");
    }




    @Test
    public void getEsInfo(){
        try {
            System.out.println("test2");
            elasticSearchUtil.findBySql("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSolr() throws Exception {
        long c = solrUtil.count("new_core","");
        System.out.println("ss= " + c);

    }

}

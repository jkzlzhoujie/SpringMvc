package com.example.demo.common.kafka.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * Created by janseny on 2018/9/13.
 * kafka消费监听
 */
public class ConsumerListener {

    @KafkaListener(topics = "sep-hbase-data")
    public void loadData(ConsumerRecord<?, ?> record) {
        if(record.value() != null){
            System.out.println("消息：" + record.value().toString());
        }
    }

}

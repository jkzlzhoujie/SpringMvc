package com.example.demo.common.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author janseny
 * @date 2018/9/14
 */
@Component
@Configuration
public class Producer {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    public static String sepTopic = "sep-hbase-data";
    @Value("${kafka.broker.address}")
    private String kafkaBrokerAddress;

    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokerAddress);
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    public boolean sendMessage(String topic, String message) {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("send Message success.");
            }

            KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory());
            kafkaTemplate.send(topic, message);

            return true;
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("send Message fail." + "topic:" + topic + ",message:" + message + "error:" + e.getMessage(), e);
            }

            return false;
        }
    }

}

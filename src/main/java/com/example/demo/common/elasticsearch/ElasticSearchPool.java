package com.example.demo.common.elasticsearch;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ElasticSearchDruidDataSourceFactory;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.Properties;

/**
 * Created by janseny on 2018/11/30.
 */
@Component
@Scope("singleton")
public class ElasticSearchPool {
        private static volatile TransportClient transportClient;
        @Value("${elasticsearch.cluster-nodes}")
        private String clusterNodes;
        @Value("${elasticsearch.cluster-name}")
        private String clusterName;


        public ElasticSearchPool() {
        }

        private TransportClient getTransportClient() {
            Settings settings = Settings.builder().put("cluster.name", clusterName).put("client.transport.sniff", false).build();
            String[] nodeArr = clusterNodes.split(",");
            InetSocketTransportAddress[] socketArr = new InetSocketTransportAddress[nodeArr.length];

            for(int i = 0; i < socketArr.length; ++i) {
                if(!StringUtils.isEmpty(nodeArr[i])) {
                    String[] nodeInfo = nodeArr[i].split(":");
                    socketArr[i] = new InetSocketTransportAddress(new InetSocketAddress(nodeInfo[0], (new Integer(nodeInfo[1])).intValue()));
                }
            }

            return TransportClient.builder().settings(settings).build().addTransportAddresses(socketArr);
        }

        public TransportClient getClient() {
            Class var1;
            if(transportClient != null) {
                if(transportClient.connectedNodes().isEmpty()) {
                    var1 = TransportClient.class;
                    synchronized(TransportClient.class) {
                        if(transportClient.connectedNodes().isEmpty()) {
                            transportClient = this.getTransportClient();
                        }
                    }
                }

                return transportClient;
            } else {
                var1 = TransportClient.class;
                synchronized(TransportClient.class) {
                    if(null == transportClient) {
                        transportClient = this.getTransportClient();
                    }
                }

                return transportClient;
            }
        }

        public DruidDataSource getDruidDataSource() throws Exception {
            Properties properties = new Properties();
            properties.put("url", "jdbc:elasticsearch://" + clusterNodes + "/");
            DruidDataSource druidDataSource = (DruidDataSource) ElasticSearchDruidDataSourceFactory.createDataSource(properties);
            druidDataSource.setInitialSize(1);
            return druidDataSource;
        }
}

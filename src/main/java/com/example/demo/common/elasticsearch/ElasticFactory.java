package com.example.demo.common.elasticsearch;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by janseny on 2018/11/29.
 */

@Component
public class ElasticFactory {

    private static JestClientFactory factory = null;
    private TransportClient transportClient;

    @Value("${elasticsearch.cluster-nodes}")
    private String esHost;
    @Value("${elasticsearch.cluster-name}")
    private String clusterName;

    //9300是tcp通讯端口，集群间和TCPClient都走的它；9200是http协议的RESTful接口
    //9300端口： ES节点之间通讯使用 9200端口： ES节点 和 外部 通讯使用

    /**
     * @param "http://localhost:9200"
     * @return
     * jest是一个基于 HTTP Rest 的连接es服务的api工具集
     */
    public JestClient getJestClient() {
        if (factory == null) {
            //初始化链接
            init();
        }
        return factory.getObject();
    }

    /**
     * 初始化链接
     * 9200
     */
    public synchronized void init() {
        String[] hostArray = esHost.split(",");
        // Construct a new Jest client according to configuration via factory
        factory = new JestClientFactory();
        HttpClientConfig httpClientConfig = new HttpClientConfig
                .Builder(Arrays.asList(hostArray))
                .multiThreaded(true)
                .maxTotalConnection(50)// 最大链接
                .maxConnectionIdleTime(10, TimeUnit.MINUTES)//链接等待时间
                .connTimeout(60 * 1000*10)
                // .discoveryEnabled(true)
                .readTimeout(60 * 1000*10)//60秒
                .build();
        factory.setHttpClientConfig(httpClientConfig);//得到链接
    }

    /**
     * 通过HTTP去请求ES的集群，对于集群而言，它是一个外部因素
     * @return
     */
    public Client getTransportClient() {
        try {
            initTranClient();
            return transportClient;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 9300
     *
     * @throws UnknownHostException
     */
    private synchronized void initTranClient() throws UnknownHostException {
        if (transportClient == null) {
            String[] hosts = esHost.split(",");
            Settings settings = Settings.settingsBuilder()
                    // .put("client.transport.sniff", true)//开启嗅探功能
                    .put("cluster.name", StringUtils.isEmpty(clusterName) ? "jkzl" : clusterName)//默认集群名字是jkzl
                    .build();
            transportClient = TransportClient.builder().settings(settings).build();
            for (String oneHost : hosts) {
                String[] hostAndport = oneHost.split(":");
                transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostAndport[0]), Integer.valueOf(hostAndport[1])));
            }
        }
    }
}


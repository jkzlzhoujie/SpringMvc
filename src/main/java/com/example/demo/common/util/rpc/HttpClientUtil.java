package com.example.demo.common.util.rpc;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * HTTP 请求工具类
 */
public class HttpClientUtil {
    public static Logger logger  = Logger.getLogger(HttpClientUtil.class);

    /**
     * 发送 GET 请求（HTTP），不带输入数据 不加密
     *
     * @param url
     * @return
     */
    public static String doGet(String url) throws Exception {
        return doGet(url, new HashMap<String, Object>(), "", "");
    }

    /**
     * 发送 GET 请求（HTTP），不带输入数据 不加密
     *
     * @param url
     * @return
     */
    public static String doGet(String url, Map<String, Object> params) throws Exception {
        return doGet(url, params, "", "");
    }

    /**
     * 发送 GET 请求（HTTP），不带输入数据 加密
     *
     * @param url
     * @return
     */
    public static String doGet(String url, String username, String password) throws Exception {
        return doGet(url, new HashMap<String, Object>(), username, password);
    }

    /**
     * httpClient的get请求方式
     *
     * @return
     * @throws Exception
     */
    public static String doGet(String url, Map<String, Object> params, String username, String password)
            throws Exception {
        String getKey = UUID.randomUUID().toString();
        logger.info( "用户：" + username+"；发起get请求连接：" + url+"，请求标示："+getKey);
        HttpClient httpClient = new HttpClient();
        String response = "";
        List<BasicNameValuePair> jsonParams = new ArrayList<>();
        GetMethod getMethod = null;
        StringBuilder param = new StringBuilder();
        int i = 0;
        try {
            if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                //需要验证
                UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
                httpClient.getState().setCredentials(AuthScope.ANY, creds);
            }
            //配置参数
            for (String key : params.keySet()) {
                jsonParams.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
            }
            getMethod = new GetMethod(url + "?" + URLEncodedUtils.format(jsonParams, HTTP.UTF_8));
            getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
            int statusCode = httpClient.executeMethod(getMethod);
            BufferedReader reader = new BufferedReader(new InputStreamReader(getMethod.getResponseBodyAsStream(), "UTF-8"));
            StringBuffer stringBuffer = new StringBuffer();
            String str = "";
            while((str = reader.readLine()) != null){
                stringBuffer.append(str);
            }
            response = stringBuffer.toString();
            if (statusCode != HttpStatus.SC_OK) {
                String jsonObject =  JSONObject.valueToString(params);
//                String jsonObject = net.minidev.json.JSONObject.toJSONString(params);
                logger.debug("用户：" + username + "\r\n发起get请求出错:\r\n请求连接" + url + "\r\n请求参数:" + jsonObject + ",状态：" + statusCode + "\r\nresponseBody" + response + "，\r\n请求标示：" + getKey +"\r\n");
                throw new Exception("请求出错: " + getMethod.getStatusLine());
            }
        } catch (HttpException e) {
            logger.debug("用户："+username+";发起get请求出错，请求连接：" + url + "，HttpException异常信息：" + e.getLocalizedMessage()+"，请求标示："+getKey);
            e.printStackTrace();
        } catch (IOException e) {
            logger.debug("用户："+username+";发起get请求出错，请求连接：" + url + "，IOException异常信息：" + e.getLocalizedMessage()+"，请求标示："+getKey);
            e.printStackTrace();
        } finally {
            // getMethod.releaseConnection();
        }
        return response;
    }

    /**
     * 发送 POST 请求（HTTP），不带输入数据 不加密
     */
    public static String doPost(String url, Map<String, Object> params) throws Exception {
        return doPost(url, params, "", "");
    }

    /**
     *  httpClient的post请求方式
     * @param url
     * @param params
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static String doPost(String url, Map<String, Object> params, String username, String password) throws Exception {
         String postKey = UUID.randomUUID().toString();
        logger.info( "用户：" + username+"；发起post请求连接：" + url+"，请求标示："+postKey);
        HttpClient httpClient = new HttpClient();
        String response = "";
        List<NameValuePair> jsonParams=new ArrayList<NameValuePair>();
        PostMethod postMethod = null;
        StringBuilder param = new StringBuilder();
        int i = 0;
        try {
            if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                //需要验证
                UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
                httpClient.getState().setCredentials(AuthScope.ANY, creds);
            }
            NameValuePair nameValuePair  =null;
            //配置参数
            postMethod = new PostMethod(url);
            for (String key : params.keySet()) {
                nameValuePair = new NameValuePair(key, String.valueOf(params.get(key)));
                postMethod.addParameter(nameValuePair);
            }
            postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
            postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"utf-8");
            int statusCode = httpClient.executeMethod(postMethod);
            BufferedReader reader = new BufferedReader(new InputStreamReader(postMethod.getResponseBodyAsStream(), "UTF-8"));
            StringBuffer stringBuffer = new StringBuffer();
            String str = "";
            while((str = reader.readLine()) != null){
                stringBuffer.append(str);
            }
            response = stringBuffer.toString();
            if (statusCode != HttpStatus.SC_OK) {
                String jsonObject =  JSONObject.valueToString(params);
//                String jsonObject = net.minidev.json.JSONObject.toJSONString(params);
                logger.debug("用户：" + username + "\r\n发起post请求出错:\r\n请求连接" + url + "\r\n请求参数:" + jsonObject + ",状态：" + statusCode + "\r\nresponseBody"+ response + "，\r\n请求标示：" + postKey + "\r\n");
                throw new Exception("请求出错: " + postMethod.getStatusLine());
            }
        } catch (HttpException e) {
            logger.debug("用户：" + username + ";发起post请求出错，请求连接：" + url + "，HttpException异常信息：" + e.getLocalizedMessage() + "，请求标示：" + postKey);
            e.printStackTrace();
        } catch (IOException e) {
            logger.debug("用户：" + username + ";发起post请求出错，请求连接：" + url + "，IOException异常信息：" + e.getLocalizedMessage() + "，请求标示：" + postKey);
            e.printStackTrace();
        } finally {
            // getMethod.releaseConnection();
        }
        return response;
    }

    /**
     *  httpClient的put请求方式
     * @param url
     * @param params
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static String doPut(String url, Map<String, Object> params, String username, String password) throws Exception {
        String putKey = UUID.randomUUID().toString();
        logger.info( "用户：" + username+"；发起put请求连接：" + url+"，请求标示："+putKey);
        HttpClient httpClient = new HttpClient();
        String response = "";
        List<BasicNameValuePair> jsonParams = new ArrayList<>();
        PutMethod putMethod = null;
        StringBuilder param = new StringBuilder();
        int i = 0;
        try {
            if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                //需要验证
                UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
                httpClient.getState().setCredentials(AuthScope.ANY, creds);
            }
            //配置参数
            for (String key : params.keySet()) {
                jsonParams.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
            }
            putMethod = new PutMethod(url + "?" + URLEncodedUtils.format(jsonParams, HTTP.UTF_8));
            putMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
            int statusCode = httpClient.executeMethod(putMethod);
            BufferedReader reader = new BufferedReader(new InputStreamReader(putMethod.getResponseBodyAsStream(), "UTF-8"));
            StringBuffer stringBuffer = new StringBuffer();
            String str = "";
            while((str = reader.readLine()) != null){
                stringBuffer.append(str);
            }
            response = stringBuffer.toString();
            if (statusCode != HttpStatus.SC_OK) {
                String jsonObject =  JSONObject.valueToString(params);
//                String jsonObject = net.minidev.json.JSONObject.toJSONString(params);
                logger.debug("用户：" + username + "\r\n发起put请求出错:\r\n请求连接" + url + "\r\n请求参数:" + jsonObject + ",状态：" + statusCode + "\r\nresponseBody"+ response + "，\r\n请求标示：" + putKey + "\r\n");
                throw new Exception("请求出错: " + putMethod.getStatusLine());
            }
        } catch (HttpException e) {
            logger.debug("用户：" + username + ";发起put请求出错，请求连接：" + url + "，HttpException异常信息：" + e.getLocalizedMessage() + "，请求标示：" + putKey);
            e.printStackTrace();
        } catch (IOException e) {
            logger.debug("用户：" + username + ";发起put请求出错，请求连接：" + url + "，IOException异常信息：" + e.getLocalizedMessage() + "，请求标示：" + putKey);
            e.printStackTrace();
        } finally {
            // getMethod.releaseConnection();
        }
        return response;
    }

    /**
     *  httpClient的delete请求方式
     * @param url
     * @param params
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static String doDelete(String url, Map<String, Object> params, String username, String password) throws Exception {
        String deleteKey = UUID.randomUUID().toString();
        logger.info( "用户：" + username+"；发起delete请求连接：" + url+"，请求标示："+deleteKey);
        HttpClient httpClient = new HttpClient();
        String response = "";
        List<BasicNameValuePair> jsonParams = new ArrayList<>();
        DeleteMethod deleteMethod = null;
        StringBuilder param = new StringBuilder();
        int i = 0;
        try {
            if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
                //需要验证
                UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
                httpClient.getState().setCredentials(AuthScope.ANY, creds);
            }
            //配置参数
            for (String key : params.keySet()) {
                jsonParams.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
            }
            deleteMethod = new DeleteMethod(url + "?" + URLEncodedUtils.format(jsonParams, HTTP.UTF_8));
            deleteMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
            int statusCode = httpClient.executeMethod(deleteMethod);
            BufferedReader reader = new BufferedReader(new InputStreamReader(deleteMethod.getResponseBodyAsStream(), "UTF-8"));
            StringBuffer stringBuffer = new StringBuffer();
            String str = "";
            while((str = reader.readLine()) != null){
                stringBuffer.append(str);
            }
            response = stringBuffer.toString();
            if (statusCode != HttpStatus.SC_OK) {
                String jsonObject =  JSONObject.valueToString(params);
//                String jsonObject = net.minidev.json.JSONObject.toJSONString(params);
                logger.debug("用户：" + username + "\r\n发起delete请求出错:\r\n请求连接" + url + "\r\n请求参数:" + jsonObject + ",状态：" + statusCode + "\r\nresponseBody" + response +"，\r\n请求标示：" + deleteKey + "\r\n");
                throw new Exception("请求出错: " + deleteMethod.getStatusLine());
            }
        } catch (HttpException e) {
            logger.debug("用户：" + username + ";发起delete请求出错，请求连接：" + url + "，HttpException异常信息：" + e.getLocalizedMessage() + "，请求标示：" + deleteKey);
            e.printStackTrace();
        } catch (IOException e) {
            logger.debug("用户：" + username + ";发起delete请求出错，请求连接：" + url + "，IOException异常信息：" + e.getLocalizedMessage() + "，请求标示：" + deleteKey);
            e.printStackTrace();
        } finally {
            // getMethod.releaseConnection();
        }
        return response;
    }
}
package com.example.demo.controller.rpcSvr;

import com.example.demo.common.util.rpc.HttpClientUtil;
import com.example.demo.common.util.rpc.RestTemplateClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by janseny on 2018/12/6.
 */
@RequestMapping("rpc")
@Controller
public class TestRpcCpntroller {

    @Value("${service-gateway.url}")
    private String remoteUrl;
    @Autowired
    private RestTemplateClient restTemplateClient;

    @RequestMapping(value = "getInfoHttpClient",method = RequestMethod.GET)
    @ResponseBody
    public String getInfoHttpClient(
            @RequestParam(name = "id",required = false) String id
    ){
        String resultStr = null;
        try {
            resultStr = HttpClientUtil.doGet(remoteUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultStr;
    }

    @RequestMapping(value = "getInfoRestTemplate",method = RequestMethod.GET)
    @ResponseBody
    public String getInfoRestTemplate(
            @RequestParam(name = "id",required = false) String id
    ){
        String result = restTemplateClient.doPost(remoteUrl + "/ambulance/idOrPhoneExistence", null);
        return result;
    }

}

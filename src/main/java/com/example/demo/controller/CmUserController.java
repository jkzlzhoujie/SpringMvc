package com.example.demo.controller;

import com.example.demo.common.export.WorkbookUtil;
import com.example.demo.common.quartz.SchedulerHelper;
import com.example.demo.common.solr.SolrUtil;
import com.example.demo.controller.base.BaseController;
import com.example.demo.entity.CmUserInfo;
import com.example.demo.service.CmUserService;
import io.swagger.annotations.ApiParam;
import jxl.Workbook;
import org.quartz.ObjectAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by janseny on 2018/11/29.
 */
@Controller
@RequestMapping("admin/cmUser")
public class CmUserController extends BaseController {
    @Autowired
    private CmUserService cmUserService;
    @Autowired
    private SolrUtil solrUtil;
    @Autowired
    private SchedulerHelper schedulerHelper;

    @RequestMapping(value = "solrTest",method = RequestMethod.GET)
    public void testSolr() throws Exception {
        long c = solrUtil.count("new_core","");
        System.out.println("ss= " + c);
    }

    @RequestMapping(value = "testScheduler",method = RequestMethod.GET)
    public void testScheduler() throws Exception {
        try {
            Map<String,Object> params = new HashMap<>();
            params.put("startTime","2018-12-04");
            params.put("endTime","2018-12-04");
            Class jobName = Class.forName("com.example.demo.job.SchedulerJob");
//            schedulerHelper.startNow(jobName, "sdddd", params);
            schedulerHelper.addJob(jobName,"10 27 11 * * ?","sdwewre", params);
        } catch (Exception e) {
            throw new ObjectAlreadyExistsException("job 已存在！");
        }
        System.out.println("testScheduler 执行完 ");
    }


    @RequestMapping(value = "findByName",method = RequestMethod.GET)
    public String findByName(
            @ApiParam(name = "name", value = "姓名", required = false)
            @RequestParam(value = "name",required = false)String name ){
          cmUserService.findByName(name);
        return "user/user";
    }

    @RequestMapping(value = "findById",method = RequestMethod.GET)
    @ResponseBody
    public Object findById(
            @ApiParam(name = "id", value = "自增ID", required = false)
            @RequestParam(value = "id",required = false)long id ){
        return  cmUserService.findById(id);
    }

    @RequestMapping(value = "export",method = RequestMethod.GET)
    public void export(
            @ApiParam(name = "id", value = "自增ID", required = false)
            @RequestParam(value = "id",required = false)long id ,
            HttpServletResponse response){
        try {
            CmUserInfo cmUserInfo = cmUserService.findById(id);
            response.setContentType("octets/stream");
            response.setHeader("Content-Disposition", "attachment; filename="+ System.currentTimeMillis() + new String(".xls"));
            OutputStream os = response.getOutputStream();
            List<Map<String,Object>> listMap = new ArrayList<>();
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("name",cmUserInfo.getName());
            dataMap.put("wallet",cmUserInfo.getWallet());
            listMap.add(dataMap);

            Map<Integer,String> columnMap = new HashMap<>();
            columnMap.put(0,"name");
            columnMap.put(1,"wallet");
            String[] header =  {"姓名","钱包（元）"};
            WorkbookUtil workbookUtil = new WorkbookUtil();
            workbookUtil.write(Workbook.createWorkbook(os), listMap, columnMap, header);
        }catch (Exception e){
            error(e);
        }

    }

}

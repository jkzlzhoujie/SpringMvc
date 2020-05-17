package com.example.demo.controller.user;

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
import org.springframework.security.core.userdetails.UserDetails;
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
@RequestMapping("/cmUser")
public class CmUserController extends BaseController {
    @Autowired
    private CmUserService cmUserService;
    @Autowired
    private SolrUtil solrUtil;
    @Autowired
    private SchedulerHelper schedulerHelper;

    @ResponseBody
    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public boolean addUser(
            @ApiParam(name = "name", value = "姓名", required = true)
            @RequestParam(value = "name", required = false) String name,
            @ApiParam(name = "loginName", value = "登录名", required = true)
            @RequestParam(value = "loginName", required = false) String loginName,
            @ApiParam(name = "password", value = "密码", required = true)
            @RequestParam(value = "password", required = false) String password) {
        System.out.println("添加用户");
        CmUserInfo user = new CmUserInfo();
        user.setPassword(password);
        user.setName(name);
        user.setLoginName(loginName);
        return cmUserService.createCmUserInfo(user);
    }


    @RequestMapping(value = "/getUserList", method = RequestMethod.GET)
    public String getUserList() throws Exception {
        String result = "user/userList";
        System.out.println(result);
        return result;
    }

    @RequestMapping(value = "solrTest", method = RequestMethod.GET)
    public void testSolr() throws Exception {
        long c = solrUtil.count("new_core", "");
        System.out.println("ss= " + c);
    }

    @RequestMapping(value = "testScheduler", method = RequestMethod.GET)
    public void testScheduler() throws Exception {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("startTime", "2018-12-04");
            params.put("endTime", "2018-12-04");
            Class jobName = Class.forName("com.example.demo.job.SchedulerJob");
//            schedulerHelper.startNow(jobName, "sdddd", params);
            schedulerHelper.addJob(jobName, "10 27 11 * * ?", "sdwewre", params);
        } catch (Exception e) {
            throw new ObjectAlreadyExistsException("job 已存在！");
        }
        System.out.println("testScheduler 执行完 ");
    }


    @ResponseBody
    @RequestMapping(value = "findByName", method = RequestMethod.GET)
    public CmUserInfo findByName(
            @ApiParam(name = "name", value = "姓名", required = false)
            @RequestParam(value = "name", required = false) String name) {
        CmUserInfo cmUserInfo = cmUserService.findByName(name);
        if (cmUserInfo != null) {
            System.out.println("用户：" + cmUserInfo.getName());
        } else {
            System.out.println("么有找到用户");
        }
        return cmUserInfo;
    }

    @ResponseBody
    @RequestMapping(value = "findAllUser", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public Object findByName() {
        List<CmUserInfo> userInfos = cmUserService.findAll();
        System.out.println("总共有用户：" + userInfos.size());
        return userInfos;
    }

    @ResponseBody
    @RequestMapping(value = "findById", method = RequestMethod.GET)
    public Object findById(
            @ApiParam(name = "id", value = "自增ID", required = false)
            @RequestParam(value = "id", required = false) long id) {
        CmUserInfo cmUserInfo = cmUserService.findById(id);
        return cmUserInfo;
    }

    @RequestMapping(value = "export", method = RequestMethod.GET)
    public void export(
            @ApiParam(name = "id", value = "自增ID", required = false)
            @RequestParam(value = "id", required = false) long id,
            HttpServletResponse response) {
        try {
            CmUserInfo cmUserInfo = cmUserService.findById(id);
            response.setContentType("octets/stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + System.currentTimeMillis() + new String(".xls"));
            OutputStream os = response.getOutputStream();
            List<Map<String, Object>> listMap = new ArrayList<>();
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("name", cmUserInfo.getName());
            dataMap.put("wallet", cmUserInfo.getWallet());
            listMap.add(dataMap);

            Map<Integer, String> columnMap = new HashMap<>();
            columnMap.put(0, "name");
            columnMap.put(1, "wallet");
            String[] header = {"姓名", "钱包（元）"};
            WorkbookUtil workbookUtil = new WorkbookUtil();
            workbookUtil.write(Workbook.createWorkbook(os), listMap, columnMap, header);
        } catch (Exception e) {
            error(e);
        }

    }

}

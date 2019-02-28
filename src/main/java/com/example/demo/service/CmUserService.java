package com.example.demo.service;

import com.example.demo.dao.CmUser2Dao;
import com.example.demo.dao.CmUserDao;
import com.example.demo.entity.CmUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by janseny on 2018/11/29.
 */
@Service
public class CmUserService {
    @Autowired
    private CmUserDao cmUserDao;

    public CmUserInfo findByName(String name){
        return  cmUserDao.findByName(name);
    }

    public CmUserInfo findById(long id ){
       return cmUserDao.findOne(id);
    }
}

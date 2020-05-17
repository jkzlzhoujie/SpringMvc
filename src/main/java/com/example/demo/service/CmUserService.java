package com.example.demo.service;

import com.example.demo.dao.CmUserDao;
import com.example.demo.entity.CmUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by janseny on 2018/11/29.
 * UserDetailsService springSercurity 用户登录验证类
 * 实现其方法 loadUserByUsername 用于验证用户登录校验
 */
@Service
public class CmUserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsService.class);

    @Autowired
    private CmUserDao cmUserDao;

    /**
     * springSercurity 用户登录验证 并返回 security User用户对象
     *
     * @param loginName
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {
        logger.info("用户的用户名: {}", loginName);
        // 根据用户名，查找到对应的密码，与权限
        CmUserInfo cmUserInfo = cmUserDao.findByName(loginName);
        if(cmUserInfo == null){
            return  null;
        }
        String password = cmUserInfo.getPassword();
        if(StringUtils.isEmpty(password)){
            BCryptPasswordEncoder passEncoder = new BCryptPasswordEncoder();
            cmUserInfo.setName(passEncoder.encode("123456"));
        }
        // 封装用户信息，并返回。参数分别是：用户名，密码，用户权限
        UserDetails userDetails = User.withUsername(loginName).password(cmUserInfo.getPassword()).authorities("admin").build();
        return userDetails;
    }

    public boolean createCmUserInfo(CmUserInfo user) {
        //进行加密
        BCryptPasswordEncoder passEncoder = new BCryptPasswordEncoder();
        user.setPassword(passEncoder.encode(user.getPassword().trim()));
        CmUserInfo cmUserInfo = cmUserDao.save(user);
        if (cmUserInfo != null) {
            return true;
        }
        return false;
    }

    public void deleteCmUserInfo(long id) {
        cmUserDao.delete(id);
    }

    public CmUserInfo findByName(String loginName) {
        return cmUserDao.findByName(loginName);
    }

    public CmUserInfo findById(long id) {
        return cmUserDao.findOne(id);
    }

    public List<CmUserInfo> findAll() {
        List<CmUserInfo> userInfos = (List<CmUserInfo>) cmUserDao.findAll();
        return userInfos;

    }


}

package com.example.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 客户信息
 */
@Entity
@Table(name = "cm_user_info")
public class CmUserInfo extends IdEntity{

	private String loginName;
	private String password;
	private String name;			//姓名
	private Integer sex;			//性别  0 女 1 男
	private String age ;			//年龄
	private String phone;			//联系电话
	private Long city ;				//城市
	private String work;			//职业
	private String openId;			//微信openId
	private String headImageUrl;    //头像url
	private Double wallet;			//钱包余额
	private Integer status ;		//状态 1 正常 2 冻结
	private Date createTime;		//创建时间
	private Date updateTime;		//修改时间

	public CmUserInfo() {}

	@Column(name = "loginName")
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@Column(name = "password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "sex")
	public Integer getSex() {
		return sex;
	}
	public void setSex(Integer sex) {
		this.sex = sex;
	}

	@Column(name = "age")
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}

	@Column(name = "phone")
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "city")
	public Long getCity() {
		return city;
	}
	public void setCity(Long city) {
		this.city = city;
	}

	@Column(name = "work")
	public String getWork() {
		return work;
	}
	public void setWork(String work) {
		this.work = work;
	}

	@Column(name = "status")
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "create_time")
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "update_time")
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "wallet")
	public Double getWallet() {
		return wallet;
	}
	public void setWallet(Double wallet) {
		this.wallet = wallet;
	}

	@Column(name = "head_image_url")
	public String getHeadImageUrl() {
		return headImageUrl;
	}
	public void setHeadImageUrl(String headImageUrl) {
		this.headImageUrl = headImageUrl;
	}

	@Column(name = "openId")
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}






}

/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.example.demo.dao;

import com.example.demo.entity.CmUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CmUser2Dao extends JpaRepository<CmUserInfo,Long> {

	@Query("select c from CmUserInfo c where c.name = ?1")
	CmUserInfo findByName(String name);
}

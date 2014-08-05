package com.onetouch.model.dao;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.mchange.v2.c3p0.ComboPooledDataSource;


public class BaseDAO extends JdbcDaoSupport{

	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	//private ComboPooledDataSource dataSource;//BasicDataSource dataSource;
	
	@PostConstruct
	private void init() {
	setDataSource(dataSource);
	
	}
	
	
}

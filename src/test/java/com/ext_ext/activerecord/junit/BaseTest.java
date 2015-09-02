package com.ext_ext.activerecord.junit;

import org.junit.BeforeClass;

import com.ext_ext.mybatisext.activerecord.DB;
import com.ext_ext.mybatisext.activerecord.MybatisExt;
import com.ext_ext.mybatisext.activerecord.Record;
import com.ext_ext.mybatisext.activerecord.Table;

public class BaseTest {

	protected static DB db;

	protected static Table<Record, Long> table;


	@BeforeClass
	public static void beforeTest() {
		db = MybatisExt.open("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:db_name", "sa", "");
		String creatTable = "create table person(id bigint primary key,name varchar(30),age int)";
		db.update(creatTable);
		table = db.active("person");
	}
}

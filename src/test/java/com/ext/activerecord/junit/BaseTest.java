package com.ext.activerecord.junit;

import org.junit.BeforeClass;

import com.ext.mybatisext.activerecord.DB;
import com.ext.mybatisext.activerecord.MybatisExt;
import com.ext.mybatisext.activerecord.Record;
import com.ext.mybatisext.activerecord.Table;

public class BaseTest {

	protected static DB db;

	protected static Table<Record, Long> table;


	@BeforeClass
	public static void beforeTest() {
		db = MybatisExt.open("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:db_name", "sa", "");
		String creatTable = "create table person(id bigint identity primary key,name varchar(30),age int)";
		db.update(creatTable);
		table = db.active("person");
	}
}

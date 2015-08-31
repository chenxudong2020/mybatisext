package com.test.activerecord.junit;

import org.junit.BeforeClass;

import com.github.mybatisext.activerecord.DB;
import com.github.mybatisext.activerecord.MybatisExt;
import com.github.mybatisext.activerecord.Record;
import com.github.mybatisext.activerecord.Table;

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

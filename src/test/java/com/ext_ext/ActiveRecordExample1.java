/**
 * ActiveRecordExample.java com.ext_ext Copyright (c) 2015, 北京微课创景教育科技有限公司版权所有.
 */

package com.ext_ext;

import java.util.List;

import com.alibaba.druid.support.json.JSONUtils;
import com.ext_ext.mybatisext.activerecord.DB;
import com.ext_ext.mybatisext.activerecord.MybatisExt;
import com.ext_ext.mybatisext.activerecord.Record;


/**
 * <p>

 * @author   宋汝波
 * @date	 2015年11月10日 
 * @version  1.0.0	 
 */
public class ActiveRecordExample1 {

	static DB db;


	public static void main( String[] args ) {
		//创建连接
		db = MybatisExt.open("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:db_name", "sa", "");
		//创建person表
		db.update("create table person(id bigint identity primary key,name varchar(30),age int)");
		//创建book表
		db.update("create table book(id bigint identity primary key,title varchar(30))");
		//创建person_book表
		db.update("create table person_book(id bigint identity primary key,personId bigint,bookId bigint)");
		//插入person数据
		db.update("insert into person values(1,'myName',12)");
		db.update("insert into person values(?,?,?)", 2, "大仙", 68);
		//插入book数据
		db.update("insert into book values(1,'天外飞仙')");
		db.update("insert into book values(?,?)", 2, "神雕侠侣");
		//插入person_book数据
		db.update("insert into person_book(personId,bookId) values(?,?)", 1, 1);
		db.update("insert into person_book(personId,bookId) values(?,?)", 1, 2);
		//连接查询
		List<Record> data = db
				.list(
					"select person.name,book.title from person join person_book on person.id=person_book.personId join book on person_book.bookId=book.id where person.id=?",
					1);
		System.out.println(JSONUtils.toJSONString(data));


	}

}

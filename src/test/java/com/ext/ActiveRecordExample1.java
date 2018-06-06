package com.ext;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.ext.activerecord.junit.Person;
import com.ext.mybatisext.activerecord.DB;
import com.ext.mybatisext.activerecord.MybatisExt;
import com.ext.mybatisext.activerecord.Record;
import com.ext.mybatisext.helper.Page;
import com.ext.mybatisext.helper.PageImpl;

/**
 * <p>

 * @author   宋汝波
 * @date	 2015年11月10日 
 * @version  1.0.0	 
 */
public class ActiveRecordExample1 {

	static DB db;

	public static void main(String[] args) {
		//MybatisExt.setColumnMappingAdaptor(new ColumnMappingAdaptorImpl());
		//创建连接
		db = MybatisExt.open("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:db_name", "sa", "");
		//创建person表
		db.update("create table person(id bigint identity primary key,name varchar(30),age int)");
		//创建book表
		db.update("create table book(id bigint identity primary key,title varchar(30))");
		//创建person_book表
		db.update("create table person_book(id bigint identity primary key,person_id bigint,book_id bigint)");
		//插入person数据
		db.update("insert into person values(1,'myName',12)");
		db.update("insert into person values(?,?,?)", 2, "大仙", 68);
		//插入book数据
		db.update("insert into book values(1,'天外飞仙')");
		db.update("insert into book values(?,?)", 2, "神雕侠侣");
		//插入person_book数据
		db.update("insert into person_book(person_id,book_id) values(?,?)", 1, 1);
		db.update("insert into person_book(person_id,book_id) values(?,?)", 1, 2);
		//连接多条查询
		List<Record> data = db
				.list("select person.name,book.title,person_book.person_id,person_book.book_id from person join person_book on person.id=person_book.person_id join book on person_book.book_id=book.id where person.id=?",
						1);
		System.out.println(JSON.toJSONString(data));
		//单条查询
		Record rec = db.one("select * from person limit 1");
		System.out.println(JSON.toJSONString(rec));

		//统计查询
		int count = db.count("select count(1) from person");
		System.out.println(count);

		//分页查询
		Page<Record> page = new PageImpl<Record>();
		page = db.paging(page, "select *  from person");
		System.out.println(JSON.toJSONString(page.getRecords()));
		System.out.println(page.getCount());

		//脚本查询
		Record parameter = new Record();
		parameter.put("id", 1);
		List<Person> dataList = db.listScript("select * from person where id=#{id}", Person.class, parameter);

		System.out.println(JSON.toJSONString(dataList));
		Person person = db.oneScript("select * from person where id=#{id}", Person.class, parameter);

		System.out.println(JSON.toJSONString(person));

		data = db
				.listScript(
						"select person.name,book.title,person_book.person_id,person_book.book_id from person join person_book on person.id=person_book.person_id join book on person_book.book_id=book.id where person.id=#{id}",
						1);
		System.out.println(JSON.toJSONString(data));

	}

}

package com.ext_ext;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.ext_ext.mybatisext.activerecord.DB;
import com.ext_ext.mybatisext.activerecord.MybatisExt;
import com.ext_ext.mybatisext.activerecord.Record;
import com.ext_ext.mybatisext.activerecord.Table;
import com.ext_ext.mybatisext.test.ColumnMappingAdaptorImpl;


/**
 * <p>

 * @author   宋汝波
 * @date	 2015年11月10日 
 * @version  1.0.0	 
 */
public class ActiveRecordExample2 {

	static DB db;


	public static void main( String[] args ) {
		MybatisExt.setColumnMappingAdaptor(new ColumnMappingAdaptorImpl());
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
		List<PersonBook> data = db
				.list(
					"select person.name,book.title,person_book.person_id ,person_book.book_id from person join person_book on person.id=person_book.person_id join book on person_book.book_id=book.id where person.id=?",
					PersonBook.class, 1);
		System.out.println(JSON.toJSONString(data));


		Table<PersonBook, Long> table1 = db.active("person_book", PersonBook.class);

		List<PersonBook> t1 = table1.list("select * from person_book");

		System.out.println(JSON.toJSONString(t1));

		t1 = table1.listScript("select * from person_book", new Record());

		System.out.println(JSON.toJSONString(t1));

	}
}

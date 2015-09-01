package com.test.activerecord.junit.db;

import java.util.List;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.mybatisext.activerecord.Record;
import com.github.mybatisext.helper.IdWorker;
import com.github.mybatisext.helper.Page;
import com.github.mybatisext.helper.PageImpl;
import com.test.activerecord.junit.BaseTest;
import com.test.activerecord.junit.Person;


/**
 * <p>

 * @author   宋汝波
 * @date	 2015年8月25日 
 * @version  1.0.0	 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestDb extends BaseTest {


	static String SQL_SELECT = "select id,name,age from person where name=?";


	@Test
	public void _1testInsert() {
		int count = db.update("insert into person(id,name,age)values(?,?,?)", IdWorker.getId(), "bobo", 28);
		Assert.assertEquals(1, count);

		Record rec = new Record();
		rec.put("id", IdWorker.getId());
		rec.put("name", "hh");
		rec.put("age", 30);
		count = db.updateScript("insert into person(id,name,age)values(#{id},#{name},#{age})", rec);

		Assert.assertEquals(1, count);

	}


	@Test
	public void _2testList() {

		List<Record> list = db.list(SQL_SELECT, "bobo");

		Assert.assertEquals(1, list.size());

		Assert.assertEquals("bobo", list.get(0).getString("name"));

		List<Person> listPerson = db.list(SQL_SELECT, Person.class, "bobo");

		Assert.assertEquals(1, listPerson.size());

		listPerson = db.queryScript("select * from person where name=#{name}", Person.class, "bobo");

		Assert.assertEquals(1, listPerson.size());

		Assert.assertEquals("bobo", listPerson.get(0).getName());

	}


	@Test
	public void _3testOne() {
		Record rec = db.one(SQL_SELECT, "bobo");
		Assert.assertEquals("bobo", rec.getString("name"));
		Person person = db.one(SQL_SELECT, Person.class, "bobo");
		Assert.assertEquals("bobo", person.getName());
	}


	@Test
	public void _4testCount() {
		Person person = new Person();
		person.setName("bobo");
		int count = db.countScript("select count(1) from person where name=#{name} ", person);
		Assert.assertEquals(1, count);

		count = db.count("select count(1) from person where name=? ", "bobo");

		Assert.assertEquals(1, count);
	}


	@Test
	public void _5testPaging() {
		Page<Record> page = new PageImpl<Record>();

		page = db.paging(page, "select * from person");

		Assert.assertEquals(2, page.getCount());

		Page<Person> pagePerson = new PageImpl<Person>();
		pagePerson = db.paging(pagePerson, "select * from person", Person.class);
		Assert.assertEquals(2, pagePerson.getCount());


		db.pagingScript(pagePerson, "select * from person where name=#{name}", Person.class, "bobo");

		Assert.assertEquals(1, pagePerson.getCount());
	}


	@Test
	public void _6testUpdate() {


		int i = db.update("update person set age=? where name=?", 30, "bobo");

		Assert.assertEquals(1, i);

		Person person = new Person();
		person.setName("bobo");
		person.setAge(25);
		i = db.updateScript("update person set age=#{age} where name=#{name}", person);

		Assert.assertEquals(1, i);
	}


	@Test
	public void _7testDelete() {
		int i = db.update("delete from person where name=?", "bobo");

		Assert.assertEquals(1, i);

		i = db.updateScript("delete from person where name=#{name}", "hh");

		Assert.assertEquals(1, i);
	}


}

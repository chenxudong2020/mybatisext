package com.ext_ext.activerecord.junit.table;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.ext_ext.activerecord.junit.BaseTest;
import com.ext_ext.activerecord.junit.Person;
import com.ext_ext.mybatisext.activerecord.Record;
import com.ext_ext.mybatisext.activerecord.Table;
import com.ext_ext.mybatisext.activerecord.sql.DeleteSQL;
import com.ext_ext.mybatisext.activerecord.sql.InsertSQL;
import com.ext_ext.mybatisext.activerecord.sql.SelectSQL;
import com.ext_ext.mybatisext.helper.Page;
import com.ext_ext.mybatisext.helper.PageImpl;


/**
 * <p>

 * @author   宋汝波
 * @date	 2015年8月25日 
 * @version  1.0.0	 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestTable extends BaseTest {

	Table<Person, Long> table1;

	Table<Record, Long> table2;


	@Before
	public void before() {
		table1 = db.active(Person.class);
		table2 = db.active("person");
	}


	@Test
	public void _1testInsert() {


		Person person = new Person();
		//person.setId(3L);
		person.setAge(26);
		person.setName("wahaha");
		int count = table1.updateScript("insert into person(id,name,age) values(#{id},#{name},#{age})", person);
		Assert.assertEquals(1, count);
		//自增Id
		Assert.assertEquals(0, person.getId().longValue());


		InsertSQL insertSql = new InsertSQL("id", "name", "age").values(1, "bobo", 28);
		count = table1.excute(insertSql);
		Assert.assertEquals(1, count);

		count = table1.update("insert into person(id,name,age)values(?,?,?)", 2, "hh", 25);
		Assert.assertEquals(1, count);

	}


	@Test
	public void _2testSelect() {

		List<Person> personList = table1.list("select * from person");

		Assert.assertEquals(3, personList.size());


		personList = table1.queryScript("select * from person where name=#{name}", "bobo");

		Assert.assertEquals(1, personList.size());


		Person persion = table1.one("select * from person limit 1");

		Assert.assertNotNull(persion);


		personList = table1.queryScript("select * from person where name=#{name}", "bobo");

		Assert.assertEquals(1, personList.size());

		SelectSQL selectSql = new SelectSQL("id", "name").where("name", "bobo").and("id", 1L).orderBy("id", "asc");
		personList = table1.excute(selectSql);
	}


	@Test
	public void _3testPaging() {
		Page<Person> page = new PageImpl<Person>();

		page = table1.paging(page, "select * from person");

		Assert.assertEquals(3, page.getCount());

		Page<Person> pagePerson = new PageImpl<Person>();
		pagePerson = table1.pagingScript(pagePerson, "select * from person", new Record());
		Assert.assertEquals(3, pagePerson.getCount());


		table1.pagingScript(pagePerson, "select * from person where name=#{name}", "bobo");

		Assert.assertEquals(1, pagePerson.getCount());


		List<Person> personList = table1.paging(1, 10, "select * from person");

		Assert.assertEquals(3, personList.size());
	}


	@Test
	public void _4testUpdate() {
		int count = table1.update("update person set name=? where name=?", "haha", "bobo");
		Assert.assertEquals(1, count);
		Record rec = new Record();
		rec.put("value", "123");
		rec.put("name", "haha");
		count = table1.updateScript("update person set name=#{value} where name=#{name}", rec);
		Assert.assertEquals(1, count);
	}


	@Test
	public void _5testDelete() {

		int count = table1.update("delete from person where id=?", 1L);
		Assert.assertEquals(1, count);
		count = table1.updateScript("delete from person where id=#{id}", 2L);
		Assert.assertEquals(1, count);
		DeleteSQL deleteSql = new DeleteSQL("id", "=", 0L);
		count = table1.excute(deleteSql);
		Assert.assertEquals(1, count);
	}

}

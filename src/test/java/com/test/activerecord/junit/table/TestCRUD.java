package com.test.activerecord.junit.table;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.github.mybatisext.activerecord.Record;
import com.github.mybatisext.activerecord.Table;
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
public class TestCRUD extends BaseTest {

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
		person.setAge(28);
		person.setName("bobo");
		person.setId(1L);

		int count = table1.getInsert().insert(person);
		Assert.assertEquals(1, count);


		List<Person> list = new ArrayList<Person>();
		person = new Person();
		person.setAge(28);
		person.setName("bobo");
		person.setId(2L);

		list.add(person);
		person = new Person();
		person.setAge(28);
		person.setName("bobo");
		person.setId(3L);
		list.add(person);
		count = table1.getInsert().insert(list);
		Assert.assertEquals(2, count);


		Record rec = new Record();
		rec.put("id", 4L);
		rec.put("name", "bobo");
		rec.put("age", 28);

		count = table2.getInsert().insert(rec);

		Assert.assertEquals(1, count);


		List<Record> list2 = new ArrayList<Record>();
		rec = new Record();
		rec.put("id", 5L);
		rec.put("name", "bobo");
		rec.put("age", 28);

		list2.add(rec);
		rec = new Record();
		rec.put("id", 6L);
		rec.put("name", "bobo");
		rec.put("age", 28);
		list2.add(rec);

		count = table2.getInsert().insert(list2);

		Assert.assertEquals(2, count);
	}


	@Test
	public void _2testSelect() {
		Person person = table1.getSelect().selectById(1L, "id", "name");
		Assert.assertNotNull(person);

		table1.getSelect().list("name", "bobo", "id");
		table1.getSelect().list(person, "name");
		table1.getSelect().one(person, "name", "id");
		table1.getSelect().one("name", "bobo", "id", "name");
	}


	@Test
	public void _3testPaging() {
		Person condition = new Person();
		condition.setName("bobo");

		List<Person> list = table1.getSelect().paging(1, 1, condition, "name");

		Assert.assertEquals(1, list.size());

		Page<Person> page = new PageImpl<Person>();
		page = table1.getSelect().paging(page, null, "name");


		Assert.assertEquals(6, page.getCount());
	}


	@Test
	public void _3testCount() {
		Person condition = new Person();
		condition.setName("bobo");
		int count = table1.getSelect().count();
		Assert.assertEquals(6, count);
		count = table1.getSelect().count(condition);
		Assert.assertEquals(6, count);
		count = table1.getSelect().count("name", "bobo");
		Assert.assertEquals(6, count);
	}


	@Test
	public void _4testUpdate() {
		Person condition = new Person();
		condition.setName("bobo");

		Person value = new Person();
		value.setAge(88);

		int count = table1.getUpdate().update(condition, value);

		Assert.assertEquals(6, count);

		count = table1.getUpdate().update(condition, "age", 66);

		Assert.assertEquals(6, count);
		count = table1.getUpdate().update("name", "bobo", "age", 11);
		Assert.assertEquals(6, count);

		value = new Person();
		value.setId(1L);
		value.setAge(10);
		count = table1.getUpdate().updateById(value);
		Assert.assertEquals(1, count);
		count = table1.getUpdate().updateById(value, true);
		Assert.assertEquals(1, count);


		//Map


		Record conditionMap = new Record();
		conditionMap.put("name", "bobo");

		Record valueMap = new Record();
		valueMap.put("age", 99);

		count = table2.getUpdate().update(conditionMap, valueMap);

		Assert.assertEquals(5, count);

		count = table2.getUpdate().update(conditionMap, "age", 66);

		Assert.assertEquals(5, count);
		count = table2.getUpdate().update("name", "bobo", "age", 11);
		Assert.assertEquals(5, count);

		valueMap = new Record();
		valueMap.put("id", 2L);
		valueMap.put("age", 10);
		count = table2.getUpdate().updateById(valueMap);
		Assert.assertEquals(1, count);
		count = table2.getUpdate().updateById(valueMap, true);
		Assert.assertEquals(1, count);
	}


	@Test
	public void _5testDelete() {

		int count = table1.getDelete().deleteById(1L);
		Assert.assertEquals(1, count);
		count = table1.getDelete().delete("id", "=", 2L);
		Assert.assertEquals(1, count);
		Person condition = new Person();
		condition.setId(3L);
		count = table1.getDelete().delete(condition);
		Assert.assertEquals(1, count);

		count = table2.getDelete().deleteById(4L);
		Assert.assertEquals(1, count);
		Record valueMap = new Record();
		valueMap.put("id", 5L);
		count = table2.getDelete().delete(valueMap);
		Assert.assertEquals(1, count);

	}


}

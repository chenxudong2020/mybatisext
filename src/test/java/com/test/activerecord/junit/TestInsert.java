package com.test.activerecord.junit;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.github.mybatisext.activerecord.Record;
import com.github.mybatisext.activerecord.sql.InsertSQL;

public class TestInsert extends BaseTest {

	@Test
	public void testInsert() {
		InsertSQL insertSql = new InsertSQL("id", "name", "age").values(1L, "bobo", 30);
		int count = table.excute(insertSql);
		Assert.assertEquals(count, 1);
		List<Record> list = new ArrayList<Record>();

		Record rec = new Record();
		rec.put("id", 2L);
		rec.put("name", "bobo");
		rec.put("age", 30);
		count = table.getInsert().insert(rec);
		Assert.assertEquals(count, 1);
		rec = new Record();
		rec.put("id", 3L);
		rec.put("name", "bobo");
		rec.put("age", 30);
		list.add(rec);
		rec = new Record();
		rec.put("id", 4L);
		rec.put("name", "bobo");
		rec.put("age", 30);
		list.add(rec);
		count = table.getInsert().insert(list);

		Assert.assertEquals(count, 2);
	}
}

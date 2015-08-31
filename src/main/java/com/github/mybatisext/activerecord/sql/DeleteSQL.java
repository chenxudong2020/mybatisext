package com.github.mybatisext.activerecord.sql;

import java.util.ArrayList;
import java.util.List;

import com.github.mybatisext.activerecord.sql.Where.Clause;

public class DeleteSQL {

	List<Where> whereList = new ArrayList<Where>();


	public DeleteSQL( String field, String operator, Object value ) {

		whereList.add(new Where(Clause.WHERE, field, operator, value));
	}


	public DeleteSQL and( String field, String operator, Object value ) {
		whereList.add(new Where(Clause.AND, field, operator, value));
		return this;
	}


	public DeleteSQL and( String field, Object value ) {
		return and(field, "=", value);
	}


	public List<Where> getWhereClauses() {
		return whereList;
	}


}

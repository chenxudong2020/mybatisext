package com.github.mybatisext.activerecord.sql;

import java.util.ArrayList;
import java.util.List;

import com.github.mybatisext.activerecord.sql.Where.Clause;

public class SelectSQL {

	String[] fields;

	String orderBy;

	String groupBy;

	List<Where> whereList = new ArrayList<Where>();

	boolean invokeWhere = false;


	public SelectSQL( String... fields ) {

		this.fields = fields;
	}


	public SelectSQL where( String field, String operator, Object value ) {
		if ( invokeWhere ) {
			throw new RuntimeException("where只需要调用一次,请试试and或者or");
		}
		whereList.add(new Where(Clause.WHERE, field, operator, value));
		invokeWhere = true;
		return this;
	}


	public SelectSQL where( String field, Object value ) {
		return where(field, "=", value);
	}


	public SelectSQL and( String field, String operator, Object value ) {
		if ( !invokeWhere ) {
			throw new RuntimeException("调用下where吧");
		}
		whereList.add(new Where(Clause.AND, field, operator, value));
		return this;
	}


	public SelectSQL and( String field, Object value ) {
		return and(field, "=", value);
	}


	public SelectSQL orderBy( String field, String desc ) {
		orderBy = field + " " + desc;
		return this;
	}


	public SelectSQL groupBy( String field ) {
		groupBy = field;
		return this;
	}


	public String[] getFields() {
		return fields;
	}


	public String getOrderBy() {
		return orderBy;
	}


	public String getGroupBy() {
		return groupBy;
	}


	public List<Where> getWhereClauses() {
		return whereList;
	}


}

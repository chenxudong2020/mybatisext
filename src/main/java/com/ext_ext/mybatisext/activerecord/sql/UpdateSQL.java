package com.ext.mybatisext.activerecord.sql;

import java.util.ArrayList;
import java.util.List;

import com.ext.mybatisext.activerecord.Record;
import com.ext.mybatisext.activerecord.sql.Where.Clause;

public class UpdateSQL {

	Record sets = new Record();

	List<Where> whereList = new ArrayList<Where>();

	boolean invokeWhere = false;


	public UpdateSQL( String field, Object value ) {

		sets.put(field, value);
	}


	public UpdateSQL set( String field, Object value ) {
		sets.put(field, value);
		return this;
	}


	public UpdateSQL where( String field, String operator, Object value ) {
		if ( invokeWhere ) {
			throw new RuntimeException("where只需要调用一次,请试试and或者or");
		}
		whereList.add(new Where(Clause.WHERE, field, operator, value));
		invokeWhere = true;
		return this;
	}


	public UpdateSQL where( String field, Object value ) {
		return where(field, "=", value);
	}


	public UpdateSQL and( String field, String operator, Object value ) {
		if ( !invokeWhere ) {
			throw new RuntimeException("调用下where吧");
		}
		whereList.add(new Where(Clause.AND, field, operator, value));
		return this;
	}


	public UpdateSQL and( String field, Object value ) {
		return and(field, "=", value);
	}


	public Record getSets() {
		return sets;
	}


	public List<Where> getWhereClauses() {
		return whereList;
	}

}

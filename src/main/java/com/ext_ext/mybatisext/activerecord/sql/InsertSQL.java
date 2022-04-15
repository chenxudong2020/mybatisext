package com.ext.mybatisext.activerecord.sql;


public class InsertSQL {


	String[] fields;

	Object[] values;


	public InsertSQL( String... fields ) {

		this.fields = fields;
	}


	public InsertSQL values( Object... val ) {
		this.values = val;
		if ( fields.length != val.length ) {
			throw new RuntimeException("字段field数量和值values数量不一样");
		}

		return this;
	}


	public String[] getFields() {
		return fields;
	}


	public Object[] getValues() {
		return values;
	}


}

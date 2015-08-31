package com.github.mybatisext.activerecord.sql;


/**
 * <p>

 * @author   宋汝波
 * @date	 2015年8月20日 
 * @version  1.0.0	 
 */
public class Where {

	public enum Clause {
		AND, WHERE;
	}

	Clause type;

	String operator;

	String field;

	Object value;


	public Where( Clause type, String field, String operator, Object value ) {
		this.type = type;
		this.field = field;
		this.operator = operator;
		this.value = value;
	}


	public Clause getType() {
		return type;
	}


	public String getOperator() {
		return operator;
	}


	public String getField() {
		return field;
	}


	public Object getValue() {
		return value;
	}


}

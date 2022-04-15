package com.ext.mybatisext.helper;

import java.sql.Date;
import java.text.SimpleDateFormat;


/**
 * <p>

 * @author   宋汝波
 * @date	 2015年8月31日 
 * @version  1.0.0	 
 */
public class SmartDate extends Date {

	private static final long serialVersionUID = 1L;

	private static final String DATE_FORMAT = "yyyy-MM-dd";

	private static final String TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";


	public SmartDate( long date ) {

		super(date);
	}


	public String format( String format ) {
		return new SimpleDateFormat(format).format(this);
	}


	public String formatDate() {
		return new SimpleDateFormat(DATE_FORMAT).format(this);
	}


	public String formatTime() {
		return new SimpleDateFormat(TIME_FORMAT).format(this);
	}


	@Override
	public String toString() {
		SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
		return format.format(this);

	}

}

/**
 * PersonBook.java com.ext_ext Copyright (c) 2015, 北京微课创景教育科技有限公司版权所有.
 */

package com.ext_ext;

import com.ext_ext.mybatisext.helper.Identity;


/**
 * <p>

 * @author   宋汝波
 * @date	 2015年11月13日 
 * @version  1.0.0	 
 */
public class PersonBook extends Identity {

	Long personId;

	Long bookId;


	public Long getPersonId() {
		return personId;
	}


	public void setPersonId( Long personId ) {
		this.personId = personId;
	}


	public Long getBookId() {
		return bookId;
	}


	public void setBookId( Long bookId ) {
		this.bookId = bookId;
	}


}

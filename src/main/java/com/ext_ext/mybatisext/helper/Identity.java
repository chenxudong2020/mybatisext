package com.ext.mybatisext.helper;


/**
 * 实体实现此接口表示有主键id,且如果插入时为null,
 * 则自动通过idworker获取id并赋值
 * <p>

 * @author   宋汝波
 * @date	 2014年11月13日 
 * @version  1.0.0	 
 */
public abstract class Identity {


	protected Long id;


	public Long getId() {
		return id;
	}


	public void setId( Long id ) {
		this.id = id;
	}

}

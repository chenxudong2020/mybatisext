package com.github.mybatisext.mapper;


/**
 * 基础增删改查操作,子类继承此接口,并加TableName注解
 * <p>
 * 
 * @author 宋汝波
 * @date 2015年2月4日
 * @version 1.0.0
 */
public interface CommonMapper<T> extends AutoMapper<T, Long> {


}

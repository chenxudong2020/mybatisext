package com.ext_ext.mybatisext.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ext_ext.mybatisext.annotation.Batch;

/**
 * 基础增删改查操作,子类继承此接口,并加TableName注解
 * <p>
 * 
 * @author 宋汝波
 * @date 2015年2月4日
 * @version 1.0.0
 */
public interface AutoMapper<T, K> {

	/**
	 *
	 * 查询（根据主键ID查询）
	 *
	 **/

	T selectById( K id );


	/**
	 *
	 * 删除（根据主键ID删除）
	 *
	 **/
	int deleteById( K id );


	/**
	 *
	 * 添加 （匹配有值的字段）
	 *
	 **/
	int insertSelective( T record );


	/**
	 *
	 * 修改 （匹配有值的字段）
	 *
	 **/
	int updateByIdSelective( T record );


	/**
	 * 以下批量操作
	 * */

	@Batch
	List<T> selectByIdBatch( List<K> idList );


	@Batch
	int deleteByIdBatch( List<K> idList );


	@Batch
	int insertSelectiveBatch( List<T> recordList );


	@Batch
	int updateByIdSelectiveBatch( List<T> recordList );


	/**
	 * 统计查询
	 * <p>
	 *
	 * @param record
	 * @return
	*/
	int count( T record );


	/**
	 * 查询list
	 * <p>
	 *
	 * @param record
	 * @param ordered 排序字段
	 * @return
	*/
	List<T> selectList( @Param("record") T record, @Param("ordered") String ordered );


	/**
	 * 查询单条信息,多条会报错
	 * <p>
	 *
	 * @param record
	 * @return
	*/
	T selectOne( @Param("record") T record );


	/**
	 * 选择性删除
	 * <p>
	 *
	 * @param record
	 * @return
	*/
	int deleteSelective( T record );

}

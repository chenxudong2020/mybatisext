package com.github.mybatisext.mapper;

import java.util.List;

import com.github.mybatisext.annotation.Batch;

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
	 * 添加
	 *
	 **/
	int insert( T record );


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
	 *
	 * 修改（根据主键ID修改）
	 *
	 **/
	int updateById( T record );


	/**
	 * 以下批量操作
	 * */

	@Batch
	List<T> selectByIdBatch( List<K> idList );


	/**
	 * 批量删除,根据主键
	 * <p>
	 *
	 * @param idList
	 * @return
	*/
	@Batch
	int deleteByIdBatch( List<K> idList );


	/**
	 * 批量插入
	 * <p>
	 *
	 * @param recordList
	 * @return
	*/
	@Batch
	int insertBatch( List<T> recordList );


	/**
	 * 批量插入
	 * <p>
	 *
	 * @param recordList
	 * @return
	*/
	@Batch
	int insertSelectiveBatch( List<T> recordList );


	/**
	 * 批量更新
	 * <p>
	 *
	 * @param recordList
	 * @return
	*/
	@Batch
	int updateByIdSelectiveBatch( List<T> recordList );


	/**
	 * 批量更新,需有主键
	 * <p>
	 *
	 * @param recordList
	 * @return
	*/
	@Batch
	int updateByIdBatch( List<T> recordList );


	/**
	 * 根据对象中的条件查询列表
	 * <p>
	 *
	 * @param record
	 * @return
	*/
	List<T> selectList( T record );


	/**
	 * 根据条件查询一条数据
	 * <p>
	 *
	 * @param record
	 * @return
	*/
	T selectOne( T record );


}

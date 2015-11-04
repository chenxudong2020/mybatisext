package com.ext_ext.mybatisext.activerecord;

import java.util.List;

import com.ext_ext.mybatisext.activerecord.meta.TableMeta;
import com.ext_ext.mybatisext.activerecord.sql.DeleteSQL;
import com.ext_ext.mybatisext.activerecord.sql.InsertSQL;
import com.ext_ext.mybatisext.activerecord.sql.SelectSQL;
import com.ext_ext.mybatisext.activerecord.sql.UpdateSQL;
import com.ext_ext.mybatisext.activerecord.statement.Delete;
import com.ext_ext.mybatisext.activerecord.statement.Insert;
import com.ext_ext.mybatisext.activerecord.statement.Select;
import com.ext_ext.mybatisext.activerecord.statement.Update;
import com.ext_ext.mybatisext.annotation.Trans;
import com.ext_ext.mybatisext.helper.Page;

/**
 * 表操作对象
 * <p>
 *
 * @author   宋汝波
 * @date	 2015年11月4日
 * @version  1.0.0
 */
public interface Table<TABLE, ID> {

	/**
	 * 表信息
	 * <p>
	 *
	 * @return
	*/
	public TableMeta<TABLE, ID> getTableMeta();


	/**
	 * 数据库操作对象
	 * <p>
	 *
	 * @return
	*/
	public DB getDB();


	/**
	 * 插入操作对象
	 * <p>
	 *
	 * @return
	*/
	public Insert<TABLE, ID> getInsert();


	/**
	 * 查询操作对象
	 * <p>
	 *
	 * @return
	*/
	public Select<TABLE, ID> getSelect();


	/**
	 * 删除操作对象
	 * <p>
	 *
	 * @return
	*/
	public Delete<TABLE, ID> getDelete();


	/**
	 * 更新操作对象
	 * <p>
	 *
	 * @return
	*/
	public Update<TABLE, ID> getUpdate();


	/**
	 * 根据插入对象执行
	 * <p>
	 *
	 * @param insertSql
	 * @return
	*/
	@Trans
	public int excute( InsertSQL insertSql );


	/**
	 * 根据查询对象查询
	 * <p>
	 *
	 * @param selectSql
	 * @return
	*/
	@Trans
	public List<TABLE> excute( SelectSQL selectSql );


	/**
	 * 根据更新对象更新
	 * <p>
	 *
	 * @param updateSql
	 * @return
	*/
	@Trans
	public int excute( UpdateSQL updateSql );


	/**
	 * 根据删除对象删除
	 * <p>
	 *
	 * @param deleteSql
	 * @return
	*/
	@Trans
	public int excute( DeleteSQL deleteSql );


	/**
	 * 脚本查询
	 * <p>
	 *
	 * @param script
	 * @param parameter
	 * @return
	*/
	@Trans
	public List<TABLE> listScript( String script, Object parameter );


	/**
	 * 脚本一条结果
	 * <p>
	 *
	 * @param script
	 * @param parameter
	 * @return
	*/
	@Trans
	public TABLE oneScript( String script, Object parameter );


	/**
	 * 脚本更新，支持自增主键返回
	 * <p>
	 *
	 * @param script
	 * @param parameter
	 * @return
	*/
	@Trans
	public int updateScript( String script, Object parameter );


	/**
	 * 脚本分页查询
	 * <p>
	 *
	 * @param page
	 * @param script
	 * @param parameter
	 * @return
	*/
	@Trans
	Page<TABLE> pagingScript( Page<TABLE> page, String script, Object parameter );


	/**
	 * 分页查询
	 * <p>
	 *
	 * @param pageNo
	 * @param size
	 * @param script
	 * @param parameter
	 * @return
	*/
	@Trans
	List<TABLE> pagingScript( int pageNo, int size, String script, Object parameter );


	/**
	 * 查询列表数据
	 * <p>
	 *
	 * @param sql 普通sql 
	 * @param parameter 占位符参数
	 * @return
	*/
	@Trans
	public List<TABLE> list( String sql, Object... parameter );


	/**
	 * 分页查询
	 * <p>
	 *
	 * @param page 分页对象
	 * @param sql 普通sql
	 * @param parameter 占位符的参数
	 * @return
	*/
	@Trans
	public Page<TABLE> paging( Page<TABLE> page, String sql, Object... parameter );


	/**
	 * 查询一条数据
	 * <p>
	 *
	 * @param sql 普通sql
	 * @param parameter 参数列表
	 * @return
	*/
	@Trans
	public TABLE one( String sql, Object... parameter );


	/**
	 * 执行更新操作
	 * <p>
	 *
	 * @param sql 普通sql
	 * @param parameter 传入参数
	 * @return
	*/
	@Trans
	public int update( String sql, Object... parameter );


	/**
	 * 分页操作
	 * <p>
	 *
	 * @param pageNo 第几页的数据
	 * @param size 一页多少数据
	 * @param sql 普通sql语句
	 * @param parameter 参数列表
	 * @return
	*/
	@Trans
	List<TABLE> paging( int pageNo, int size, String sql, Object... parameter );
}

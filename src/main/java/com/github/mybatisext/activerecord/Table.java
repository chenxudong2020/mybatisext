package com.github.mybatisext.activerecord;

import java.util.List;

import com.github.mybatisext.activerecord.meta.TableMeta;
import com.github.mybatisext.activerecord.sql.DeleteSQL;
import com.github.mybatisext.activerecord.sql.InsertSQL;
import com.github.mybatisext.activerecord.sql.SelectSQL;
import com.github.mybatisext.activerecord.sql.UpdateSQL;
import com.github.mybatisext.activerecord.statement.Delete;
import com.github.mybatisext.activerecord.statement.Insert;
import com.github.mybatisext.activerecord.statement.Select;
import com.github.mybatisext.activerecord.statement.Update;
import com.github.mybatisext.annotation.Trans;
import com.github.mybatisext.helper.Page;

public interface Table<TABLE, ID> {

	/**
	 * 表信息
	 * <p>
	 *
	 * @return
	*/
	public TableMeta<TABLE, ID> getTableMeta();


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


	@Trans
	public int excute( InsertSQL insertSql );


	@Trans
	public List<TABLE> excute( SelectSQL selectSql );


	@Trans
	public int excute( UpdateSQL updateSql );


	@Trans
	public int excute( DeleteSQL deleteSql );


	//脚本执行
	@Trans
	public List<TABLE> queryScript( String script, Object parameter );


	@Trans
	public int updateScript( String script, Object parameter );


	@Trans
	Page<TABLE> pagingScript( Page<TABLE> page, String script, Object parameter );


	@Trans
	List<TABLE> pagingScript( int pageNo, int size, String script, Object parameter );


	//以下sql操作方法
	@Trans
	public List<TABLE> list( String sql, Object... parameter );


	@Trans
	public Page<TABLE> paging( Page<TABLE> page, String sql, Object... parameter );


	@Trans
	public TABLE one( String sql, Object... parameter );


	@Trans
	public int update( String sql, Object... parameter );


	@Trans
	List<TABLE> paging( int pageNo, int size, String sql, Object... parameter );
}

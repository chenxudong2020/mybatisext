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


	@org.apache.ibatis.annotations.Insert("")
	public int excute( InsertSQL insertSql );


	@org.apache.ibatis.annotations.Select("")
	public List<TABLE> excute( SelectSQL selectSql );


	@org.apache.ibatis.annotations.Update("")
	public int excute( UpdateSQL updateSql );


	@org.apache.ibatis.annotations.Delete("")
	public int excute( DeleteSQL deleteSql );


	//脚本执行
	@org.apache.ibatis.annotations.Select("")
	public List<TABLE> queryScript( String script, Object parameter );


	@org.apache.ibatis.annotations.Insert("")
	@org.apache.ibatis.annotations.Update("")
	@org.apache.ibatis.annotations.Delete("")
	public int updateScript( String script, Object parameter );


	@org.apache.ibatis.annotations.Select("")
	Page<TABLE> pagingScript( Page<TABLE> page, String script, Object parameter );


	@org.apache.ibatis.annotations.Select("")
	List<TABLE> pagingScript( int pageNo, int size, String script, Object parameter );


	//以下sql操作方法
	@org.apache.ibatis.annotations.Select("")
	public List<TABLE> list( String sql, Object... parameter );


	@org.apache.ibatis.annotations.Select("")
	public Page<TABLE> paging( Page<TABLE> page, String sql, Object... parameter );


	@org.apache.ibatis.annotations.Select("")
	public TABLE one( String sql, Object... parameter );


	@org.apache.ibatis.annotations.Insert("")
	@org.apache.ibatis.annotations.Update("")
	@org.apache.ibatis.annotations.Delete("")
	public int update( String sql, Object... parameter );


	@org.apache.ibatis.annotations.Select("")
	List<TABLE> paging( int pageNo, int size, String sql, Object... parameter );
}

package com.ext_ext.mybatisext.activerecord;

import java.util.List;

import org.apache.ibatis.mapping.MappedStatement;

import com.ext_ext.mybatisext.activerecord.meta.DBMeta;
import com.ext_ext.mybatisext.annotation.Trans;
import com.ext_ext.mybatisext.helper.Page;

/**
 * 数据库操作对象
 * <p>
 *
 * @author   宋汝波
 * @date	 2015年11月4日
 * @version  1.0.0
 */
public interface DB {

	/**
	 * 激活某个表进行相关操作
	 * <p>
	 *
	 * @param name 表名称，数据库表名
	 * @param tableType 表的实体映射java类
	 * @param idField 表的主键字段名称
	 * @param idType 表的主键字段java类型
	 * @return
	*/
	public <TABLE, ID> Table<TABLE, ID> active( String name, Class<TABLE> tableType, String idField, Class<ID> idType );


	/**
	 * 带TableName注解的class;
	 * 实体java类需带有注解，里面规定表名称、主键等信息
	 * <p>
	 *
	 * @param tableType 带有注解等java实体类
	 * @return
	*/
	public <TABLE, ID> Table<TABLE, ID> active( Class<TABLE> tableType );


	/**
	 * 指定id字段名称；
	 * 只指定主键名称和类型，没有实体类，默认用map替代
	 * <p>
	 *
	 * @param name 表名称
	 * @param idField 主键名称
	 * @param idType 主键类型
	 * @return
	*/
	public <ID> Table<Record, ID> active( String name, String idField, Class<ID> idType );


	/**
	 *  默认id主键,Long类型
	 * <p>
	 *
	 * @param name 表名称
	 * @return
	*/
	public Table<Record, Long> active( String name );


	/**
	 * 获取DB元信息
	 * <p>
	 *
	 * @return
	*/
	public DBMeta getDBMeta();


	/**
	 * 自定义sql语句进行查询操作,返回结果集
	 * <p>
	 *
	 * @param sql jdbc形式等sql语句，用?做占位符
	 * @param parameter 占位符的相应的值和占位符的数量一一对应
	 * @return
	*/
	@Trans
	public List<Record> list( String sql, Object... parameter );


	/**
	 * 分页查询根据sql语句
	 * <p>
	 *
	 * @param page 分页对象
	 * @param sql 语句
	 * @param parameter 参数
	 * @return
	*/
	@Trans
	public Page<Record> paging( Page<Record> page, String sql, Object... parameter );


	/**
	 * 根据提供的实体类，转换结果值
	 * <p>
	 *
	 * @param sql 语句
	 * @param type 返回的结果类型
	 * @param parameter 参数值
	 * @return
	*/
	@Trans
	public <T> List<T> list( String sql, Class<T> type, Object... parameter );


	/**
	 * 分页返回指定的结果类型
	 * <p>
	 *
	 * @param page 分页对象
	 * @param sql 语句
	 * @param type 返回的java实体类型
	 * @param parameter 传入的参数
	 * @return
	*/
	@Trans
	public <T> Page<T> paging( Page<T> page, String sql, Class<T> type, Object... parameter );


	/**
	 * 只取第一条，多于一条会抛出异常，小于1条返回null
	 * <p>
	 *
	 * @param sql 语句
	 * @param parameter 参数
	 * @return
	*/
	@Trans
	public Record one( String sql, Object... parameter );


	/**
	 * 指定返回类型，多于一条抛出异常，小于一条返回null
	 * <p>
	 *
	 * @param sql 语句
	 * @param type 返回结果类型
	 * @param parameter 参数
	 * @return
	*/
	@Trans
	public <T> T one( String sql, Class<T> type, Object... parameter );


	/**
	 * 执行增删改操作，返回影响行数
	 * <p>
	 *
	 * @param sql sql语句
	 * @param parameter 参数，不支持返回自增主键
	 * @return
	*/
	@Trans
	public int update( String sql, Object... parameter );


	/**
	 * 数量统计，传入统计语句，
	 * <p>
	 *
	 * @param sql 带count的语句
	 * @param parameter 参数
	 * @return
	*/
	@Trans
	public int count( String sql, Object... parameter );


	/**
	 * 执行脚本，ONGL脚本
	 * <p>
	 *
	 * @param script 脚本
	 * @param type 返回结果类型
	 * @param parameter 参数
	 * @return
	*/
	@Trans
	public <T> List<T> listScript( String script, Class<T> type, Object parameter );


	/**
	 * 同上，返回结果类型不一样
	 * <p>
	 *
	 * @param script 脚本
	 * @param parameter 参数
	 * @return
	*/
	@Trans
	public List<Record> listScript( String script, Object parameter );


	/**
	 * 根据脚本查询一条结果集，多于一条抛出异常，小于一条返回null
	 * <p>
	 *
	 * @param script 脚本
	 * @param type 返回结果类型
	 * @param parameter 传入参数
	 * @return
	*/
	@Trans
	public <T> T oneScript( String script, Class<T> type, Object parameter );


	/**
	 * 同上，返回结果不一样
	 * <p>
	 *
	 * @param script 脚本
	 * @param parameter 传入参数
	 * @return
	*/
	@Trans
	public Record oneScript( String script, Object parameter );


	/**
	 * 根据count语句返回条数
	 * <p>
	 *
	 * @param script 带count的脚本语句
	 * @param parameter 传入参数
	 * @return
	*/
	@Trans
	public int countScript( String script, Object parameter );


	/**
	 * 分页脚本，传入正常的语句，返回分页数据
	 * <p>
	 *
	 * @param page 分页对象
	 * @param script 脚本
	 * @param type 返回结果类型
	 * @param parameter 传入参数
	 * @return
	*/
	@Trans
	public <T> Page<T> pagingScript( Page<T> page, String script, Class<T> type, Object parameter );


	/**
	 * 
	 * <p>
	 *
	 * @param page
	 * @param script
	 * @param parameter
	 * @return
	*/
	@Trans
	public Page<Record> pagingScript( Page<Record> page, String script, Object parameter );


	/**
	 * 根据脚本执行更新操作，不支持返回自增主键，此时主键字段不确定
	 * <p>
	 *
	 * @param script 脚本
	 * @param parameter 传入参数
	 * @return
	*/
	@Trans
	public int updateScript( String script, Object parameter );


	/**
	 * 执行查询操作
	 * <p>
	 *
	 * @param statement 语句对象
	 * @param parameter 传入参数
	 * @return
	*/
	@Trans
	public <TABLE> List<TABLE> query( MappedStatement statement, Object parameter );


	/**
	 * 执行更新操作
	 * <p>
	 *
	 * @param statement 语句对象
	 * @param parameter 传入参数
	 * @return
	*/
	@Trans
	public int update( MappedStatement statement, Object parameter );
}

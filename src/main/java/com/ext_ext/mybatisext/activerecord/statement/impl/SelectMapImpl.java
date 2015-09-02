// package com.ext_ext.mybatisext.activerecord.statement.impl;
//
// import java.util.Collections;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;
//
// import org.apache.ibatis.mapping.MappedStatement;
// import org.apache.ibatis.mapping.SqlSource;
//
// import com.ext_ext.mybatisext.activerecord.Record;
// import com.ext_ext.mybatisext.activerecord.Table;
// import com.ext_ext.mybatisext.activerecord.meta.TableMeta;
// import com.ext_ext.mybatisext.activerecord.sql.SelectSQLBuilder;
// import com.ext_ext.mybatisext.activerecord.statement.Select;
// import com.ext_ext.mybatisext.helper.Page;
//
// /**
// * Map查询实现
// * <p>
// *
// * @author 宋汝波
// * @date 2015年8月18日
// * @version 1.0.0
// */
// public class SelectMapImpl<TABLE, ID> extends BaseStatement<TABLE, ID>
// implements Select<TABLE, ID> {
//
//
// protected MappedStatement selectById;
//
//
// public SelectMapImpl( TableMeta<TABLE, ID> tm ) {
//
// super(tm);
//
// SqlSource sqlSourceSelectById = driver.createSqlSource(configuration,
// SelectSQLBuilder.buildSelectById(tm),
// tableMeta.getType());
// selectById = getQueryStatement(tableMeta.getName() + ".selectById",
// sqlSourceSelectById);
// }
//
//
// @Override
// public TABLE selectById( ID id, String... columns ) {
// Record param = new Record();
// param.put("id", id);
// putDynamicColumn(param, columns);
// List<TABLE> list = query(selectById, param);
// if ( list.size() == 1 ) {
// return list.get(0);
// }
// if ( list.size() > 1 ) {
// throw new RuntimeException("根据主键查询的结果不能够大于1条");
// }
// return null;
//
// }
//
//
// @Override
// public List<TABLE> list( TABLE condition, String... columns ) {
//
// Map<String, Object> param = toMap(condition);
// String sql = SelectSQLBuilder.buildSelectListWithScript(tableMeta);
// // 后加入列
// putDynamicColumn(param, columns);
// SqlSource sqlSource = driver.createSqlSource(configuration, sql,
// tableMeta.getType());
//
// MappedStatement list = getQueryStatement(tableMeta.getName()
// + ".list(" + tableMeta.getType().getSimpleName() + ",String...)", sqlSource);
//
// return query(list, param);
//
// }
//
//
// protected void putDynamicColumn( Map<String, Object> param, String[] columns
// ) {
// if ( columns.length == 0 ) {
// param.put(SelectSQLBuilder.DYNAMIC_COLUMN,
// SelectSQLBuilder.buildDynamicColumn(tableMeta));
// } else {
// param.put(SelectSQLBuilder.DYNAMIC_COLUMN,
// SelectSQLBuilder.buildDynamicColumn(tableMeta, columns));
// }
// }
//
//
// @Override
// public List<TABLE> list( String field, Object value, String... columns ) {
// Record param = new Record();
// param.put(field, value);
// putDynamicColumn(param, columns);
// SqlSource sqlSource = driver.createSqlSource(configuration,
// SelectSQLBuilder.buildSelectByOne(tableMeta, field), tableMeta.getType());
// MappedStatement list = getQueryStatement(tableMeta.getName() +
// ".list(String,Object,String...)", sqlSource);
//
// return query(list, param);
//
// }
//
//
// @Override
// public TABLE one( TABLE condition, String... columns ) {
//
// List<TABLE> result = list(condition, columns);
// if ( result.size() > 0 ) {
// return result.get(0);
// }
//
// return null;
//
// }
//
//
// @Override
// public TABLE one( String field, Object value, String... columns ) {
//
// List<TABLE> result = list(field, value, columns);
// if ( result.size() > 0 ) {
// return result.get(0);
// }
//
// return null;
//
// }
//
//
// // @Override
// // public SelectSQL<TABLE, ID> select( String... field ) {
// //
// // // TODO Auto-generated method stub
// // return null;
// //
// // }
//
//
// @Override
// public int count() {
// Set<String> emptySet = Collections.emptySet();
// Record param = new Record();
// param.put(SelectSQLBuilder.DYNAMIC_COLUMN, " COUNT(*) as table_count ");
// SqlSource sqlSource = driver.createSqlSource(configuration,
// SelectSQLBuilder.buildSelectListWithScript(tableMeta, emptySet), Map.class);
// MappedStatement statement = getQueryStatement(tableMeta.getName() +
// ".count()", sqlSource);
//
// return getCount(param, statement);
//
// }
//
//
// private int getCount( Map<String, Object> param, MappedStatement statement )
// {
// List<TABLE> list = query(statement, param);
// if ( list.size() >= 0 ) {
// return Integer.parseInt(list.get(0).toString());
// }
// return 0;
// }
//
//
// @Override
// public int count( TABLE condition ) {
//
// Map<String, Object> param = toMap(condition);
// String sql = SelectSQLBuilder.buildSelectListWithScript(tableMeta,
// param.keySet());
// // 后加入列
// param.put(SelectSQLBuilder.DYNAMIC_COLUMN, " COUNT(*) as table_count ");
// SqlSource sqlSource = driver.createSqlSource(configuration, sql, Map.class);
//
// MappedStatement statement = getQueryStatement(tableMeta.getName()
// + ".count(" + tableMeta.getType().getSimpleName() + ")", sqlSource);
//
// return getCount(param, statement);
//
// }
//
//
// @Override
// public int count( String field, Object value ) {
//
// Record param = new Record();
// param.put(field, value);
// String sql = SelectSQLBuilder.buildSelectByOne(tableMeta, field);
//
// param.put(SelectSQLBuilder.DYNAMIC_COLUMN, " COUNT(*) as table_count ");
// SqlSource sqlSource = driver.createSqlSource(configuration, sql, Map.class);
// MappedStatement statement = getQueryStatement(tableMeta.getName() +
// ".count(String,Object)", sqlSource);
//
// return getCount(param, statement);
//
// }
//
//
// @Override
// public List<TABLE> paging( int pageNo, int size, String... columns ) {
// Set<String> emptySet = Collections.emptySet();
// StringBuilder str = new StringBuilder("<script>");
// String sql = SelectSQLBuilder.buildSelectList(tableMeta, emptySet);
// sql = db.getDBMeta().getDialectSQL().getPagingSQL(((pageNo < 1 ? 1 : pageNo)
// - 1) * size, size, sql);
// str.append(sql);
// str.append("</script>");
//
// Record param = new Record();
// putDynamicColumn(param, columns);
// SqlSource sqlSource = driver.createSqlSource(configuration, str.toString(),
// Map.class);
// MappedStatement statement = getQueryStatement(tableMeta.getName() +
// ".paging(int,int)", sqlSource);
//
// return query(statement, param);
//
// }
//
//
// @Override
// public Page<TABLE> paging( Page<TABLE> page, TABLE condition, String...
// columns ) {
// int pageNo = page.getPageNo();
// int size = page.getPageSize();
// Map<String, Object> param = toMap(condition);
// putDynamicColumn(param, columns);
// StringBuilder str = new StringBuilder("<script>");
// String sql = SelectSQLBuilder.buildSelectList(tableMeta, param.keySet());
// sql = db.getDBMeta().getDialectSQL().getPagingSQL(((pageNo < 1 ? 1 : pageNo)
// - 1) * size, size, sql);
// str.append(sql);
// str.append("</script>");
// SqlSource sqlSource = driver.createSqlSource(configuration, str.toString(),
// Map.class);
//
// MappedStatement statement = getQueryStatement(tableMeta.getName() +
// ".paging(Page,TABLE,String...)", sqlSource);
// List<TABLE> data = query(statement, param);
// // 拼接count语句
// page.setCount(count(condition));
// page.setRecords(data);
// return null;
// }
//
//
// @Override
// public Table<TABLE, ID> getTable() {
//
// return tableMeta.getTable();
//
// }
//
//
//}

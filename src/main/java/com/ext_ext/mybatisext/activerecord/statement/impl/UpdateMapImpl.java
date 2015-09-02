// package com.ext_ext.mybatisext.activerecord.statement.impl;
//
// import java.util.Map;
//
// import org.apache.ibatis.mapping.MappedStatement;
// import org.apache.ibatis.mapping.SqlCommandType;
// import org.apache.ibatis.mapping.SqlSource;
//
// import com.ext_ext.mybatisext.activerecord.Record;
// import com.ext_ext.mybatisext.activerecord.Table;
// import com.ext_ext.mybatisext.activerecord.meta.TableMeta;
// import com.ext_ext.mybatisext.activerecord.sql.UpdateSQLBuilder;
// import com.ext_ext.mybatisext.activerecord.statement.Update;
//
// /**
// * <p>
// *
// * @author 宋汝波
// * @date 2015年8月18日
// * @version 1.0.0
// */
// public class UpdateMapImpl<TABLE, ID> extends BaseStatement<TABLE, ID>
// implements Update<TABLE, ID> {
//
// public UpdateMapImpl( TableMeta<TABLE, ID> tm ) {
//
// super(tm);
//
// }
//
//
// @Override
// public int updateById( TABLE data ) {
//
// Map<String, Object> map = toMap(data);
//
// return doUpdateById(map, false);
//
// }
//
//
// @SuppressWarnings("unchecked")
// @Override
// public int updateById( TABLE data, boolean isAll ) {
// Map<String, Object> map = (Map<String, Object>) data;
//
// return doUpdateById(map, isAll);
// }
//
//
// private int doUpdateById( Map<String, Object> map, boolean isAll ) {
// if ( map.get(tableMeta.getIdName()) == null ) {
// throw new RuntimeException("您传入的数据没有主键");
// }
// // 去掉主键
// String sql = UpdateSQLBuilder.buildUpdateById(tableMeta, isAll);
//
// sql = UpdateSQLBuilder.appendScript(sql);
//
// SqlSource sqlSource = driver.createSqlSource(configuration, sql, Map.class);
// MappedStatement update = getUpdateStatement(tableMeta.getName() +
// ".updateById", sqlSource,
// SqlCommandType.UPDATE);
//
// return update(update, map);
// }
//
//
// @Override
// public int update( TABLE condition, TABLE value ) {
// Map<String, Object> param = toMap(condition);
// Map<String, Object> sets = toMap(value);
//
// String sql = UpdateSQLBuilder.buildUpdate(tableMeta, param.keySet(),
// sets.keySet());
// sql = UpdateSQLBuilder.appendScript(sql);
//
// SqlSource sqlSource = driver.createSqlSource(configuration, sql, Map.class);
// MappedStatement update = getUpdateStatement(tableMeta.getName() +
// ".updateById", sqlSource,
// SqlCommandType.UPDATE);
// // 合并
// sets.putAll(param);
// return update(update, sets);
//
// }
//
//
// @Override
// public int update( TABLE condition, String field, Object value ) {
//
// Map<String, Object> param = toMap(condition);
// Record sets = new Record();
// sets.put(field, value);
//
// String sql = UpdateSQLBuilder.buildUpdate(tableMeta, param.keySet(),
// sets.keySet());
// sql = UpdateSQLBuilder.appendScript(sql);
//
// SqlSource sqlSource = driver.createSqlSource(configuration, sql, Map.class);
// MappedStatement update = getUpdateStatement(tableMeta.getName() +
// ".update(TABLE, String, Object)", sqlSource,
// SqlCommandType.UPDATE);
// // 合并
// sets.putAll(param);
// return update(update, sets);
//
// }
//
//
// @Override
// public int update( String conditionField, Object conditionValue, String
// field, Object value ) {
// Record param = new Record();
// param.put(conditionField, conditionValue);
// Record sets = new Record();
// sets.put(field, value);
//
// String sql = UpdateSQLBuilder.buildUpdate(tableMeta, param.keySet(),
// sets.keySet());
// sql = UpdateSQLBuilder.appendScript(sql);
//
// SqlSource sqlSource = driver.createSqlSource(configuration, sql, Map.class);
// MappedStatement update = getUpdateStatement(tableMeta.getName() +
// ".update(String,Object, String, Object)",
// sqlSource, SqlCommandType.UPDATE);
// // 合并
// sets.putAll(param);
// return update(update, sets);
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

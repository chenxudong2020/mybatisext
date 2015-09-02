package com.ext_ext.mybatisext.activerecord.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;

import com.ext_ext.mybatisext.activerecord.DB;
import com.ext_ext.mybatisext.activerecord.Table;
import com.ext_ext.mybatisext.helper.PropertyHelper;

public class TableMeta<TABLE, ID> {


	/**
	 * 表操作对象
	 */
	protected Table<TABLE, ID> table;

	/**
	 * 数据库操作对象
	 */
	protected DB db;

	/**
	 * 表名称
	 */
	protected String name;

	/**
	 * 表对应的实体类型
	 */
	protected Class<TABLE> type;

	/**
	 * 主键字段值
	 */
	protected String idName;

	/**
	 * 主键类型
	 */
	protected Class<ID> idType;


	/**
	 * 结果映射,查询用
	 */
	protected List<ResultMap> resultMapList;

	/**
	 * 结果参数映射
	 */
	protected List<ResultMapping> resultMappings;

	/**
	 * 属性和字段映射
	 */
	protected Map<String, String> propertyColumnMapping;

	/**
	 * 字段和属性的映射
	 */
	protected Map<String, String> columnPropertyMapping;

	/**
	 * 参数映射
	 */
	protected List<ParameterMapping> parameterMappings;


	public List<ResultMapping> getResultMappings() {
		return resultMappings;
	}


	public void setResultMappings( List<ResultMapping> resultMappings ) {
		this.resultMappings = resultMappings;
		//构建映射关系
		buildResultMap();
	}


	public List<ParameterMapping> getParameterMappings() {
		return parameterMappings;
	}


	public void setParameterMappings( List<ParameterMapping> parameterMappings ) {
		this.parameterMappings = parameterMappings;
	}


	public Table<TABLE, ID> getTable() {
		return table;
	}


	public void setTable( Table<TABLE, ID> table ) {
		this.table = table;
	}


	public DB getDb() {
		return db;
	}


	public void setDb( DB db ) {
		this.db = db;
	}


	public String getName() {
		return name;
	}


	public void setName( String name ) {
		this.name = name;
	}


	public Class<TABLE> getType() {
		return type;
	}


	public void setType( Class<TABLE> type ) {
		this.type = type;
	}


	public String getIdName() {
		return idName;
	}


	public void setIdName( String idName ) {
		this.idName = idName;
	}


	public Class<ID> getIdType() {
		return idType;
	}


	public void setIdType( Class<ID> idType ) {
		this.idType = idType;
	}


	public boolean isMapType() {
		return Map.class.isAssignableFrom(type);
	}


	public Map<String, String> getPropertyColumnMapping() {
		return propertyColumnMapping;
	}


	public void setPropertyColumnMapping( Map<String, String> propertyColumnMapping ) {
		this.propertyColumnMapping = propertyColumnMapping;
	}


	public List<ResultMap> getResultMapList() {
		return resultMapList;
	}


	public void setResultMapList( List<ResultMap> resultMapList ) {
		this.resultMapList = resultMapList;
	}


	protected void buildResultMap() {
		resultMapList = new ArrayList<ResultMap>(1);
		//如果没有建立映射关系,则根据实体类建立
		if ( resultMappings.isEmpty() && !isMapType() ) {
			Map<String, Class<?>> propertyType = PropertyHelper.getPropertiesType(getType());
			for ( Map.Entry<String, Class<?>> property : propertyType.entrySet() ) {
				ResultMapping.Builder mapping = new ResultMapping.Builder(db.getDBMeta().getConfiguration(),
						property.getKey(), property.getKey(), property.getValue());
				resultMappings.add(mapping.build());
			}
		}
		ResultMap.Builder builder = new ResultMap.Builder(db.getDBMeta().getConfiguration(), getName() + "_ResultMap",
				getType(), resultMappings);
		resultMapList.add(builder.build());

	}


	public Map<String, String> getColumnPropertyMapping() {
		return columnPropertyMapping;
	}


	public void setColumnPropertyMapping( Map<String, String> columnPropertyMapping ) {
		this.columnPropertyMapping = columnPropertyMapping;
	}


	/**
	 * 根据属性,获取列名
	 * <p>
	 *
	 * @param property
	 * @return
	*/
	public String getColumnName( String property ) {
		Map<String, String> mapping = getPropertyColumnMapping();
		String column = mapping.get(property);
		if ( column == null ) {
			return property;
		}
		return column;
	}


	public String getPropertyName( String column ) {
		Map<String, String> mapping = getColumnPropertyMapping();
		String property = mapping.get(column);
		if ( property == null ) {
			return column;
		}
		return property;
	}

}

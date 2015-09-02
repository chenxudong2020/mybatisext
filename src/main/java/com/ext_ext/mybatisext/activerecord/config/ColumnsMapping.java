package com.ext_ext.mybatisext.activerecord.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandlerRegistry;

import com.ext_ext.mybatisext.activerecord.MybatisExt;
import com.ext_ext.mybatisext.activerecord.meta.DBMeta;
import com.ext_ext.mybatisext.activerecord.meta.TableMeta;
import com.ext_ext.mybatisext.helper.CloseHelper;
import com.ext_ext.mybatisext.helper.PropertyHelper;

// 获取数据库表字段，简历映射关系
public class ColumnsMapping {

	protected static Log logger = LogFactory.getLog(ColumnsMapping.class);

	protected ConcurrentHashMap<String, List<ResultMapping>> columnMappingCache = new ConcurrentHashMap<String, List<ResultMapping>>();

	protected ConcurrentHashMap<String, Map<String, String>> propertyMappingCache = new ConcurrentHashMap<String, Map<String, String>>();

	protected ConcurrentHashMap<String, List<ParameterMapping>> parameterMappingCache = new ConcurrentHashMap<String, List<ParameterMapping>>();

	protected DBMeta dbMeta;


	public ColumnsMapping( DBMeta dbMeta ) {
		this.dbMeta = dbMeta;
	}


	public <TABLE, ID> Map<String, String> propertyColumnMapping( TableMeta<TABLE, ID> meta ) {
		// 根据属性名称得到数据库字段名称

		Map<String, String> result = propertyMappingCache.get(meta.getName());
		if ( result != null ) {
			return result;
		}
		List<ResultMapping> mapping = getResultMapping(meta);
		result = new LinkedHashMap<String, String>();

		if ( mapping.isEmpty() && !meta.isMapType() ) {
			String[] property = PropertyHelper.getProperties(meta.getType());
			for ( int i = 0 ; i < property.length ; i++ ) {
				result.put(property[i], property[i]);
			}
		} else {
			for ( ResultMapping resultMapping : mapping ) {
				result.put(resultMapping.getProperty(), resultMapping.getColumn());
			}
		}
		// 如果是map类型是空的
		propertyMappingCache.put(meta.getName(), result);
		return result;
	}


	public <TABLE, ID> List<ParameterMapping> getParameterMapping( TableMeta<TABLE, ID> meta ) {
		List<ParameterMapping> parameterMappings = parameterMappingCache.get(meta.getName());
		if ( parameterMappings != null ) {
			return parameterMappings;
		}

		parameterMappings = new ArrayList<ParameterMapping>();
		List<ResultMapping> resultMapping = getResultMapping(meta);
		for ( ResultMapping result : resultMapping ) {
			ParameterMapping.Builder mapping = new ParameterMapping.Builder(dbMeta.getConfiguration(),
					result.getProperty(), result.getTypeHandler());
			mapping.jdbcType(result.getJdbcType());
			mapping.javaType(result.getJavaType());
			parameterMappings.add(mapping.build());

		}
		parameterMappingCache.put(meta.getName(), parameterMappings);
		return parameterMappings;
	}


	public <TABLE, ID> List<ResultMapping> getResultMapping( TableMeta<TABLE, ID> meta ) {
		List<ResultMapping> columnMap = columnMappingCache.get(meta.getName());
		if ( columnMap != null ) {
			return columnMap;
		}

		ColumnMappingAdaptor adaptor = MybatisExt.adaptor;
		columnMap = new ArrayList<ResultMapping>();

		Map<String, Class<?>> entityType = null;
		if ( !meta.isMapType() ) {
			//有实体类对应根据实体类建立关系
			entityType = PropertyHelper.getPropertiesType(meta.getType());
		}
		Transaction trans = dbMeta.getTransaction();
		PreparedStatement st = null;
		ResultSet rs = null;
		Configuration config = dbMeta.getConfiguration();
		TypeHandlerRegistry registry = config.getTypeHandlerRegistry();
		try {
			Connection conn = trans.getConnection();
			String sql = "SELECT * FROM " + meta.getName() + " where 1=2 ";
			st = conn.prepareStatement(sql);
			rs = st.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			for ( int i = 1 ; i <= rsmd.getColumnCount() ; i++ ) {
				// 全部小写
				String column = rsmd.getColumnLabel(i).toLowerCase();

				String property = column;
				//如果自定义映射关系不为null
				if ( adaptor != null ) {
					property = adaptor.adaptor(meta.getName(), column);
				}
				ResultMapping.Builder mapping = null;
				if ( entityType != null ) {
					mapping = new ResultMapping.Builder(config, property, column, entityType.get(property));
				} else {
					JdbcType type = JdbcType.forCode(rsmd.getColumnType(i));
					mapping = new ResultMapping.Builder(config, property, column, registry.getTypeHandler(type));
				}
				columnMap.add(mapping.build());
			}
		} catch ( Exception e ) {
			logger.error("查询映射关系", e);
		} finally {
			CloseHelper.close(trans, st, rs);
		}
		columnMappingCache.put(meta.getName(), columnMap);
		return columnMap;
	}
}

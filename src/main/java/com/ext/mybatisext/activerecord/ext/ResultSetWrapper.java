/*
 * Copyright 2009-2013 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ext.mybatisext.activerecord.ext;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.ObjectTypeHandler;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.ibatis.type.UnknownTypeHandler;

/**
 * @author Iwao AVE!
 */
public class ResultSetWrapper {

	protected final ResultSet resultSet;

	protected final TypeHandlerRegistry typeHandlerRegistry;

	protected final List<String> columnNames = new ArrayList<String>();

	protected final List<String> classNames = new ArrayList<String>();

	protected final List<JdbcType> jdbcTypes = new ArrayList<JdbcType>();

	protected final Map<String, Map<Class<?>, TypeHandler<?>>> typeHandlerMap = new HashMap<String, Map<Class<?>, TypeHandler<?>>>();

	protected final Map<String, List<String>> mappedColumnNamesMap = new HashMap<String, List<String>>();

	protected final Map<String, List<String>> unMappedColumnNamesMap = new HashMap<String, List<String>>();

	public ResultSetWrapper(ResultSet rs, Configuration configuration) throws SQLException {
		super();
		this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
		this.resultSet = rs;
		final ResultSetMetaData metaData = rs.getMetaData();
		final int columnCount = metaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			columnNames.add(configuration.isUseColumnLabel() ? metaData.getColumnLabel(i) : metaData.getColumnName(i));
			jdbcTypes.add(JdbcType.forCode(metaData.getColumnType(i)));
			classNames.add(metaData.getColumnClassName(i));
		}
	}

	public List<JdbcType> getJdbcTypes() {
		return jdbcTypes;
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public List<String> getColumnNames() {
		return this.columnNames;
	}

	/**
	 * Gets the type handler to use when reading the result set.
	 * Tries to get from the TypeHandlerRegistry by searching for the property type.
	 * If not found it gets the column JDBC type and tries to get a handler for it.
	 * 
	 * @param propertyType
	 * @param columnName
	 * @return
	 */
	public TypeHandler<?> getTypeHandler(Class<?> propertyType, String columnName) {
		TypeHandler<?> handler = null;
		Map<Class<?>, TypeHandler<?>> columnHandlers = typeHandlerMap.get(columnName);
		if (columnHandlers == null) {
			columnHandlers = new HashMap<Class<?>, TypeHandler<?>>();
			typeHandlerMap.put(columnName, columnHandlers);
		} else {
			handler = columnHandlers.get(propertyType);
		}
		if (handler == null) {
			handler = typeHandlerRegistry.getTypeHandler(propertyType);
			// Replicate logic of UnknownTypeHandler#resolveTypeHandler
			// See issue #59 comment 10
			if (handler == null || handler instanceof UnknownTypeHandler) {
				final int index = columnNames.indexOf(columnName);
				final JdbcType jdbcType = jdbcTypes.get(index);
				final Class<?> javaType = resolveClass(classNames.get(index));
				if (javaType != null && jdbcType != null) {
					handler = typeHandlerRegistry.getTypeHandler(javaType, jdbcType);
				} else if (javaType != null) {
					handler = typeHandlerRegistry.getTypeHandler(javaType);
				} else if (jdbcType != null) {
					handler = typeHandlerRegistry.getTypeHandler(jdbcType);
				}
			}
			if (handler == null || handler instanceof UnknownTypeHandler) {
				handler = new ObjectTypeHandler();
			}
			columnHandlers.put(propertyType, handler);
		}
		return handler;
	}

	protected Class<?> resolveClass(String className) {
		try {
			final Class<?> clazz = Resources.classForName(className);
			return clazz;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	protected void loadMappedAndUnmappedColumnNames(ResultMap resultMap, String columnPrefix) {

		List<String> mappedColumnNames = new ArrayList<String>();
		List<String> unmappedColumnNames = new ArrayList<String>();
		final String upperColumnPrefix = columnPrefix == null ? null : columnPrefix.toUpperCase(Locale.ENGLISH);
		final Set<String> mappedColumns = prependPrefixes(resultMap.getMappedColumns(), upperColumnPrefix);
		for (String columnName : columnNames) {
			final String upperColumnName = columnName.toUpperCase(Locale.ENGLISH);
			if (mappedColumns.contains(upperColumnName)) {
				mappedColumnNames.add(upperColumnName);
			} else {
				unmappedColumnNames.add(columnName);
			}
		}
		mappedColumnNamesMap.put(getMapKey(resultMap, columnPrefix), mappedColumnNames);
		unMappedColumnNamesMap.put(getMapKey(resultMap, columnPrefix), unmappedColumnNames);
	}

	public List<String> getMappedColumnNames(ResultMap resultMap, String columnPrefix) {
		List<String> mappedColumnNames = mappedColumnNamesMap.get(getMapKey(resultMap, columnPrefix));
		if (mappedColumnNames == null) {
			loadMappedAndUnmappedColumnNames(resultMap, columnPrefix);
			mappedColumnNames = mappedColumnNamesMap.get(getMapKey(resultMap, columnPrefix));
		}
		return mappedColumnNames;
	}

	public List<String> getUnmappedColumnNames(ResultMap resultMap, String columnPrefix) {
		List<String> unMappedColumnNames = unMappedColumnNamesMap.get(getMapKey(resultMap, columnPrefix));
		if (unMappedColumnNames == null) {
			loadMappedAndUnmappedColumnNames(resultMap, columnPrefix);
			unMappedColumnNames = unMappedColumnNamesMap.get(getMapKey(resultMap, columnPrefix));
		}
		return unMappedColumnNames;
	}

	protected String getMapKey(ResultMap resultMap, String columnPrefix) {
		return resultMap.getId() + ":" + columnPrefix;
	}

	/**
	 * <p>
	 *
	 * @param names
	 * @param prefix
	 * @return
	*/
	protected Set<String> prependPrefixes(Set<String> names, String prefix) {
		if (names == null || names.isEmpty() || prefix == null || prefix.length() == 0) {
			return names;
		}
		final Set<String> prefixed = new HashSet<String>();
		for (String columnName : names) {
			prefixed.add(prefix + columnName);
		}
		return prefixed;
	}

}

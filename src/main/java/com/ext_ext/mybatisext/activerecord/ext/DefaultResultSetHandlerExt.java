package com.ext_ext.mybatisext.activerecord.ext;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.loader.ResultLoader;
import org.apache.ibatis.executor.loader.ResultLoaderMap;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.result.DefaultResultContext;
import org.apache.ibatis.executor.result.DefaultResultHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.Discriminator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import com.ext_ext.mybatisext.activerecord.MybatisExt;
import com.ext_ext.mybatisext.helper.PropertyHelper;

/**
 * <p>
 * @author   bo
 * @Date	 2015-11-14 	 
 */
public class DefaultResultSetHandlerExt implements ResultSetHandler {

	protected static final Object NO_VALUE = new Object();

	protected final Executor executor;
	protected final Configuration configuration;
	protected final MappedStatement mappedStatement;
	protected final RowBounds rowBounds;
	protected final ParameterHandler parameterHandler;
	protected final ResultHandler resultHandler;
	protected final BoundSql boundSql;
	protected final TypeHandlerRegistry typeHandlerRegistry;
	protected final ObjectFactory objectFactory;

	// nested resultmaps
	protected final Map<CacheKey, Object> nestedResultObjects = new HashMap<CacheKey, Object>();
	protected final Map<CacheKey, Object> ancestorObjects = new HashMap<CacheKey, Object>();
	protected final Map<String, String> ancestorColumnPrefix = new HashMap<String, String>();

	// multiple resultsets
	protected final Map<String, ResultMapping> nextResultMaps = new HashMap<String, ResultMapping>();
	protected final Map<CacheKey, List<PendingRelation>> pendingRelations = new HashMap<CacheKey, List<PendingRelation>>();

	protected static class PendingRelation {
		public PendingRelation() {

		}

		public MetaObject metaObject;
		public ResultMapping propertyMapping;
	}

	public DefaultResultSetHandlerExt(Executor executor, MappedStatement mappedStatement,
			ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql, RowBounds rowBounds) {
		this.executor = executor;
		this.configuration = mappedStatement.getConfiguration();
		this.mappedStatement = mappedStatement;
		this.rowBounds = rowBounds;
		this.parameterHandler = parameterHandler;
		this.boundSql = boundSql;
		this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
		this.objectFactory = configuration.getObjectFactory();
		this.resultHandler = resultHandler;
	}

	//
	// HANDLE OUTPUT PARAMETER
	//

	@Override
	public void handleOutputParameters(CallableStatement cs) throws SQLException {
		final Object parameterObject = parameterHandler.getParameterObject();
		final MetaObject metaParam = configuration.newMetaObject(parameterObject);
		final List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		for (int i = 0; i < parameterMappings.size(); i++) {
			final ParameterMapping parameterMapping = parameterMappings.get(i);
			if (parameterMapping.getMode() == ParameterMode.OUT || parameterMapping.getMode() == ParameterMode.INOUT) {
				if (ResultSet.class.equals(parameterMapping.getJavaType())) {
					handleRefCursorOutputParameter((ResultSet) cs.getObject(i + 1), parameterMapping, metaParam);
				} else {
					final TypeHandler<?> typeHandler = parameterMapping.getTypeHandler();
					metaParam.setValue(parameterMapping.getProperty(), typeHandler.getResult(cs, i + 1));
				}
			}
		}
	}

	protected void handleRefCursorOutputParameter(ResultSet rs, ParameterMapping parameterMapping, MetaObject metaParam)
			throws SQLException {
		try {
			final String resultMapId = parameterMapping.getResultMapId();
			final ResultMap resultMap = configuration.getResultMap(resultMapId);
			final DefaultResultHandler resultHandler = new DefaultResultHandler(objectFactory);
			final ResultSetWrapper rsw = new ResultSetWrapper(rs, configuration);
			handleRowValues(rsw, resultMap, resultHandler, new RowBounds(), null);
			metaParam.setValue(parameterMapping.getProperty(), resultHandler.getResultList());
		} finally {
			closeResultSet(rs); // issue #228 (close resultsets)
		}
	}

	//
	// HANDLE RESULT SETS
	//

	@Override
	public List<Object> handleResultSets(Statement stmt) throws SQLException {
		ErrorContext.instance().activity("handling results").object(mappedStatement.getId());

		final List<Object> multipleResults = new ArrayList<Object>();

		int resultSetCount = 0;
		ResultSetWrapper rsw = getFirstResultSet(stmt);

		List<ResultMap> resultMaps = mappedStatement.getResultMaps();
		int resultMapCount = resultMaps.size();
		validateResultMapsCount(rsw, resultMapCount);
		while (rsw != null && resultMapCount > resultSetCount) {
			ResultMap resultMap = resultMaps.get(resultSetCount);
			if (MybatisExt.adaptor != null && Boolean.TRUE.equals(resultMap.getAutoMapping())
					&& resultMap.getResultMappings().size() == 0) {

				if (!Map.class.isAssignableFrom(resultMap.getType())) {
					Map<String, Class<?>> entityType = PropertyHelper.getPropertiesType(resultMap.getType());
					ArrayList<ResultMapping> mappings = new ArrayList<ResultMapping>();
					for (String column : rsw.getColumnNames()) {
						String property = column;
						property = MybatisExt.adaptor.adaptor(column);
						Class<?> propertyType = entityType.get(property.toUpperCase(Locale.ENGLISH));
						if (propertyType != null) {
							mappings.add(new ResultMapping.Builder(configuration, property, column, propertyType)
									.build());
						}
					}
					ResultMap.Builder builder = new ResultMap.Builder(configuration, "DB_ResultMap",
							resultMap.getType(), mappings, false);
					resultMap = builder.build();
				} else {
					ArrayList<ResultMapping> mappings = new ArrayList<ResultMapping>();
					List<JdbcType> types = rsw.getJdbcTypes();
					List<String> names = rsw.getColumnNames();
					for (int i = 0; i < names.size(); i++) {
						String property = names.get(i);
						property = MybatisExt.adaptor.adaptor(names.get(i));
						TypeHandler<?> handler = typeHandlerRegistry.getTypeHandler(types.get(i));
						if (handler != null) {
							mappings.add(new ResultMapping.Builder(configuration, property, names.get(i), handler)
									.build());
						}
					}
					ResultMap.Builder builder = new ResultMap.Builder(configuration, "DB_ResultMap",
							resultMap.getType(), mappings, false);
					resultMap = builder.build();
				}
			}
			handleResultSet(rsw, resultMap, multipleResults, null);
			rsw = getNextResultSet(stmt);
			cleanUpAfterHandlingResultSet();
			resultSetCount++;
		}

		String[] resultSets = mappedStatement.getResulSets();
		if (resultSets != null) {
			while (rsw != null && resultSetCount < resultSets.length) {
				ResultMapping parentMapping = nextResultMaps.get(resultSets[resultSetCount]);
				if (parentMapping != null) {
					String nestedResultMapId = parentMapping.getNestedResultMapId();
					ResultMap resultMap = configuration.getResultMap(nestedResultMapId);
					handleResultSet(rsw, resultMap, null, parentMapping);
				}
				rsw = getNextResultSet(stmt);
				cleanUpAfterHandlingResultSet();
				resultSetCount++;
			}
		}

		return collapseSingleResultList(multipleResults);
	}

	protected ResultSetWrapper getFirstResultSet(Statement stmt) throws SQLException {
		ResultSet rs = stmt.getResultSet();
		while (rs == null) {
			// move forward to get the first resultset in case the driver
			// doesn't return the resultset as the first result (HSQLDB 2.1)
			if (stmt.getMoreResults()) {
				rs = stmt.getResultSet();
			} else {
				if (stmt.getUpdateCount() == -1) {
					// no more results. Must be no resultset
					break;
				}
			}
		}
		return rs != null ? new ResultSetWrapper(rs, configuration) : null;
	}

	protected ResultSetWrapper getNextResultSet(Statement stmt) throws SQLException {
		// Making this method tolerant of bad JDBC drivers
		try {
			if (stmt.getConnection().getMetaData().supportsMultipleResultSets()) {
				// Crazy Standard JDBC way of determining if there are more results
				if (!((!stmt.getMoreResults()) && (stmt.getUpdateCount() == -1))) {
					ResultSet rs = stmt.getResultSet();
					return rs != null ? new ResultSetWrapper(rs, configuration) : null;
				}
			}
		} catch (Exception e) {
			// Intentionally ignored.
		}
		return null;
	}

	protected void closeResultSet(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			// ignore
		}
	}

	protected void cleanUpAfterHandlingResultSet() {
		nestedResultObjects.clear();
		ancestorColumnPrefix.clear();
	}

	protected void validateResultMapsCount(ResultSetWrapper rsw, int resultMapCount) {
		if (rsw != null && resultMapCount < 1) {
			throw new ExecutorException("A query was run and no Result Maps were found for the Mapped Statement '"
					+ mappedStatement.getId()
					+ "'.  It's likely that neither a Result Type nor a Result Map was specified.");
		}
	}

	protected void handleResultSet(ResultSetWrapper rsw, ResultMap resultMap, List<Object> multipleResults,
			ResultMapping parentMapping) throws SQLException {
		try {
			if (parentMapping != null) {
				handleRowValues(rsw, resultMap, null, RowBounds.DEFAULT, parentMapping);
			} else {
				if (resultHandler == null) {
					DefaultResultHandler defaultResultHandler = new DefaultResultHandler(objectFactory);
					handleRowValues(rsw, resultMap, defaultResultHandler, rowBounds, null);
					multipleResults.add(defaultResultHandler.getResultList());
				} else {
					handleRowValues(rsw, resultMap, resultHandler, rowBounds, null);
				}
			}
		} finally {
			closeResultSet(rsw.getResultSet()); // issue #228 (close resultsets)
		}
	}

	protected List<Object> collapseSingleResultList(List<Object> multipleResults) {
		if (multipleResults.size() == 1) {
			@SuppressWarnings("unchecked")
			List<Object> returned = (List<Object>) multipleResults.get(0);
			return returned;
		}
		return multipleResults;
	}

	//
	// HANDLE ROWS FOR SIMPLE RESULTMAP
	//

	protected void handleRowValues(ResultSetWrapper rsw, ResultMap resultMap, ResultHandler resultHandler,
			RowBounds rowBounds, ResultMapping parentMapping) throws SQLException {
		if (resultMap.hasNestedResultMaps()) {
			ensureNoRowBounds();
			checkResultHandler();
			handleRowValuesForNestedResultMap(rsw, resultMap, resultHandler, rowBounds, parentMapping);
		} else {
			handleRowValuesForSimpleResultMap(rsw, resultMap, resultHandler, rowBounds, parentMapping);
		}
	}

	protected void ensureNoRowBounds() {
		if (configuration.isSafeRowBoundsEnabled() && rowBounds != null
				&& (rowBounds.getLimit() < RowBounds.NO_ROW_LIMIT || rowBounds.getOffset() > RowBounds.NO_ROW_OFFSET)) {
			throw new ExecutorException(
					"Mapped Statements with nested result mappings cannot be safely constrained by RowBounds. "
							+ "Use safeRowBoundsEnabled=false setting to bypass this check.");
		}
	}

	protected void checkResultHandler() {
		if (resultHandler != null && configuration.isSafeResultHandlerEnabled() && !mappedStatement.isResultOrdered()) {
			throw new ExecutorException(
					"Mapped Statements with nested result mappings cannot be safely used with a custom ResultHandler. "
							+ "Use safeResultHandlerEnabled=false setting to bypass this check "
							+ "or ensure your statement returns ordered data and set resultOrdered=true on it.");
		}
	}

	protected void handleRowValuesForSimpleResultMap(ResultSetWrapper rsw, ResultMap resultMap,
			ResultHandler resultHandler, RowBounds rowBounds, ResultMapping parentMapping) throws SQLException {
		DefaultResultContext resultContext = new DefaultResultContext();
		skipRows(rsw.getResultSet(), rowBounds);
		while (shouldProcessMoreRows(resultContext, rowBounds) && rsw.getResultSet().next()) {
			ResultMap discriminatedResultMap = resolveDiscriminatedResultMap(rsw.getResultSet(), resultMap, null);
			Object rowValue = getRowValue(rsw, discriminatedResultMap);
			storeObject(resultHandler, resultContext, rowValue, parentMapping, rsw.getResultSet());
		}
	}

	protected void storeObject(ResultHandler resultHandler, DefaultResultContext resultContext, Object rowValue,
			ResultMapping parentMapping, ResultSet rs) throws SQLException {
		if (parentMapping != null) {
			linkToParents(rs, parentMapping, rowValue);
		} else {
			callResultHandler(resultHandler, resultContext, rowValue);
		}
	}

	protected void callResultHandler(ResultHandler resultHandler, DefaultResultContext resultContext, Object rowValue) {
		resultContext.nextResultObject(rowValue);
		resultHandler.handleResult(resultContext);
	}

	protected boolean shouldProcessMoreRows(ResultContext context, RowBounds rowBounds) throws SQLException {
		return !context.isStopped() && context.getResultCount() < rowBounds.getLimit();
	}

	protected void skipRows(ResultSet rs, RowBounds rowBounds) throws SQLException {
		if (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
			if (rowBounds.getOffset() != RowBounds.NO_ROW_OFFSET) {
				rs.absolute(rowBounds.getOffset());
			}
		} else {
			for (int i = 0; i < rowBounds.getOffset(); i++) {
				rs.next();
			}
		}
	}

	//
	// GET VALUE FROM ROW FOR SIMPLE RESULT MAP
	//

	protected Object getRowValue(ResultSetWrapper rsw, ResultMap resultMap) throws SQLException {
		final ResultLoaderMap lazyLoader = new ResultLoaderMap();
		Object resultObject = createResultObject(rsw, resultMap, lazyLoader, null);
		if (resultObject != null && !typeHandlerRegistry.hasTypeHandler(resultMap.getType())) {
			final MetaObject metaObject = configuration.newMetaObject(resultObject);
			boolean foundValues = resultMap.getConstructorResultMappings().size() > 0;
			if (shouldApplyAutomaticMappings(resultMap,
					!AutoMappingBehavior.NONE.equals(configuration.getAutoMappingBehavior()))) {
				foundValues = applyAutomaticMappings(rsw, resultMap, metaObject, null) || foundValues;
			}
			foundValues = applyPropertyMappings(rsw, resultMap, metaObject, lazyLoader, null) || foundValues;
			foundValues = lazyLoader.size() > 0 || foundValues;
			resultObject = foundValues ? resultObject : null;
			return resultObject;
		}
		return resultObject;
	}

	protected boolean shouldApplyAutomaticMappings(ResultMap resultMap, boolean def) {
		return resultMap.getAutoMapping() != null ? resultMap.getAutoMapping() : def;
	}

	//
	// PROPERTY MAPPINGS
	//

	protected boolean applyPropertyMappings(ResultSetWrapper rsw, ResultMap resultMap, MetaObject metaObject,
			ResultLoaderMap lazyLoader, String columnPrefix) throws SQLException {
		final List<String> mappedColumnNames = rsw.getMappedColumnNames(resultMap, columnPrefix);
		boolean foundValues = false;
		final List<ResultMapping> propertyMappings = resultMap.getPropertyResultMappings();
		for (ResultMapping propertyMapping : propertyMappings) {
			final String column = prependPrefix(propertyMapping.getColumn(), columnPrefix);
			if (propertyMapping.isCompositeResult()
					|| (column != null && mappedColumnNames.contains(column.toUpperCase(Locale.ENGLISH)))
					|| propertyMapping.getResultSet() != null) {
				Object value = getPropertyMappingValue(rsw.getResultSet(), metaObject, propertyMapping, lazyLoader,
						columnPrefix);
				String property = propertyMapping.getProperty(); // issue #541 make property optional
				if (!metaObject.hasSetter(property)) {
					property = metaObject.findProperty(property, configuration.isMapUnderscoreToCamelCase());
				}
				if (value != NO_VALUE && property != null && (value != null || configuration.isCallSettersOnNulls())) { // issue #377, call setter on nulls
					if (value != null || !metaObject.getSetterType(property).isPrimitive()) {
						metaObject.setValue(property, value);
					}
					foundValues = true;
				}
			}
		}
		return foundValues;
	}

	protected Object getPropertyMappingValue(ResultSet rs, MetaObject metaResultObject, ResultMapping propertyMapping,
			ResultLoaderMap lazyLoader, String columnPrefix) throws SQLException {
		if (propertyMapping.getNestedQueryId() != null) {
			return getNestedQueryMappingValue(rs, metaResultObject, propertyMapping, lazyLoader, columnPrefix);
		} else if (propertyMapping.getResultSet() != null) {
			addPendingChildRelation(rs, metaResultObject, propertyMapping);
			return NO_VALUE;
		} else if (propertyMapping.getNestedResultMapId() != null) {
			// the user added a column attribute to a nested result map, ignore it
			return NO_VALUE;
		} else {
			final TypeHandler<?> typeHandler = propertyMapping.getTypeHandler();
			final String column = prependPrefix(propertyMapping.getColumn(), columnPrefix);
			return typeHandler.getResult(rs, column);
		}
	}

	protected boolean applyAutomaticMappings(ResultSetWrapper rsw, ResultMap resultMap, MetaObject metaObject,
			String columnPrefix) throws SQLException {
		final List<String> unmappedColumnNames = rsw.getUnmappedColumnNames(resultMap, columnPrefix);
		boolean foundValues = false;
		for (String columnName : unmappedColumnNames) {
			String propertyName = columnName;
			if (columnPrefix != null && columnPrefix.length() > 0) {
				// When columnPrefix is specified,
				// ignore columns without the prefix.
				if (columnName.toUpperCase(Locale.ENGLISH).startsWith(columnPrefix)) {
					propertyName = columnName.substring(columnPrefix.length());
				} else {
					continue;
				}
			}
			final String property = metaObject.findProperty(propertyName, configuration.isMapUnderscoreToCamelCase());
			if (property != null && metaObject.hasSetter(property)) {
				final Class<?> propertyType = metaObject.getSetterType(property);
				if (typeHandlerRegistry.hasTypeHandler(propertyType)) {
					final TypeHandler<?> typeHandler = rsw.getTypeHandler(propertyType, columnName);
					final Object value = typeHandler.getResult(rsw.getResultSet(), columnName);
					if (value != null || configuration.isCallSettersOnNulls()) { // issue #377, call setter on nulls
						if (value != null || !propertyType.isPrimitive()) {
							metaObject.setValue(property, value);
						}
						foundValues = true;
					}
				}
			}
		}
		return foundValues;
	}

	// MULTIPLE RESULT SETS

	protected void linkToParents(ResultSet rs, ResultMapping parentMapping, Object rowValue) throws SQLException {
		CacheKey parentKey = createKeyForMultipleResults(rs, parentMapping, parentMapping.getColumn(),
				parentMapping.getForeignColumn());
		List<PendingRelation> parents = pendingRelations.get(parentKey);
		for (PendingRelation parent : parents) {
			if (parent != null) {
				final Object collectionProperty = instantiateCollectionPropertyIfAppropriate(parent.propertyMapping,
						parent.metaObject);
				if (rowValue != null) {
					if (collectionProperty != null) {
						final MetaObject targetMetaObject = configuration.newMetaObject(collectionProperty);
						targetMetaObject.add(rowValue);
					} else {
						parent.metaObject.setValue(parent.propertyMapping.getProperty(), rowValue);
					}
				}
			}
		}
	}

	protected Object instantiateCollectionPropertyIfAppropriate(ResultMapping resultMapping, MetaObject metaObject) {
		final String propertyName = resultMapping.getProperty();
		Object propertyValue = metaObject.getValue(propertyName);
		if (propertyValue == null) {
			Class<?> type = resultMapping.getJavaType();
			if (type == null) {
				type = metaObject.getSetterType(propertyName);
			}
			try {
				if (objectFactory.isCollection(type)) {
					propertyValue = objectFactory.create(type);
					metaObject.setValue(propertyName, propertyValue);
					return propertyValue;
				}
			} catch (Exception e) {
				throw new ExecutorException("Error instantiating collection property for result '"
						+ resultMapping.getProperty() + "'.  Cause: " + e, e);
			}
		} else if (objectFactory.isCollection(propertyValue.getClass())) {
			return propertyValue;
		}
		return null;
	}

	protected void addPendingChildRelation(ResultSet rs, MetaObject metaResultObject, ResultMapping parentMapping)
			throws SQLException {
		CacheKey cacheKey = createKeyForMultipleResults(rs, parentMapping, parentMapping.getColumn(),
				parentMapping.getColumn());
		PendingRelation deferLoad = new PendingRelation();
		deferLoad.metaObject = metaResultObject;
		deferLoad.propertyMapping = parentMapping;
		List<PendingRelation> relations = pendingRelations.get(cacheKey);
		// issue #255
		if (relations == null) {
			relations = new ArrayList<DefaultResultSetHandlerExt.PendingRelation>();
			pendingRelations.put(cacheKey, relations);
		}
		relations.add(deferLoad);
		ResultMapping previous = nextResultMaps.get(parentMapping.getResultSet());
		if (previous == null) {
			nextResultMaps.put(parentMapping.getResultSet(), parentMapping);
		} else {
			if (!previous.equals(parentMapping)) {
				throw new ExecutorException("Two different properties are mapped to the same resultSet");
			}
		}
	}

	protected CacheKey createKeyForMultipleResults(ResultSet rs, ResultMapping resultMapping, String names,
			String columns) throws SQLException {
		CacheKey cacheKey = new CacheKey();
		cacheKey.update(resultMapping);
		if (columns != null && names != null) {
			String[] columnsArray = columns.split(",");
			String[] namesArray = names.split(",");
			for (int i = 0; i < columnsArray.length; i++) {
				Object value = rs.getString(columnsArray[i]);
				if (value != null) {
					cacheKey.update(namesArray[i]);
					cacheKey.update(value);
				}
			}
		}
		return cacheKey;
	}

	//
	// INSTANTIATION & CONSTRUCTOR MAPPING
	//

	protected Object createResultObject(ResultSetWrapper rsw, ResultMap resultMap, ResultLoaderMap lazyLoader,
			String columnPrefix) throws SQLException {
		final List<Class<?>> constructorArgTypes = new ArrayList<Class<?>>();
		final List<Object> constructorArgs = new ArrayList<Object>();
		final Object resultObject = createResultObject(rsw, resultMap, constructorArgTypes, constructorArgs,
				columnPrefix);
		if (resultObject != null && !typeHandlerRegistry.hasTypeHandler(resultMap.getType())) {
			final List<ResultMapping> propertyMappings = resultMap.getPropertyResultMappings();
			for (ResultMapping propertyMapping : propertyMappings) {
				if (propertyMapping.getNestedQueryId() != null && propertyMapping.isLazy()) { // issue gcode #109 && issue #149
					return configuration.getProxyFactory().createProxy(resultObject, lazyLoader, configuration,
							objectFactory, constructorArgTypes, constructorArgs);
				}
			}
		}
		return resultObject;
	}

	protected Object createResultObject(ResultSetWrapper rsw, ResultMap resultMap, List<Class<?>> constructorArgTypes,
			List<Object> constructorArgs, String columnPrefix) throws SQLException {
		final Class<?> resultType = resultMap.getType();
		final List<ResultMapping> constructorMappings = resultMap.getConstructorResultMappings();
		if (typeHandlerRegistry.hasTypeHandler(resultType)) {
			return createPrimitiveResultObject(rsw, resultMap, columnPrefix);
		} else if (constructorMappings.size() > 0) {
			return createParameterizedResultObject(rsw, resultType, constructorMappings, constructorArgTypes,
					constructorArgs, columnPrefix);
		} else {
			return objectFactory.create(resultType);
		}
	}

	protected Object createParameterizedResultObject(ResultSetWrapper rsw, Class<?> resultType,
			List<ResultMapping> constructorMappings, List<Class<?>> constructorArgTypes, List<Object> constructorArgs,
			String columnPrefix) throws SQLException {
		boolean foundValues = false;
		for (ResultMapping constructorMapping : constructorMappings) {
			final Class<?> parameterType = constructorMapping.getJavaType();
			final String column = constructorMapping.getColumn();
			final Object value;
			if (constructorMapping.getNestedQueryId() != null) {
				value = getNestedQueryConstructorValue(rsw.getResultSet(), constructorMapping, columnPrefix);
			} else if (constructorMapping.getNestedResultMapId() != null) {
				final ResultMap resultMap = configuration.getResultMap(constructorMapping.getNestedResultMapId());
				value = getRowValue(rsw, resultMap);
			} else {
				final TypeHandler<?> typeHandler = constructorMapping.getTypeHandler();
				value = typeHandler.getResult(rsw.getResultSet(), prependPrefix(column, columnPrefix));
			}
			constructorArgTypes.add(parameterType);
			constructorArgs.add(value);
			foundValues = value != null || foundValues;
		}
		return foundValues ? objectFactory.create(resultType, constructorArgTypes, constructorArgs) : null;
	}

	protected Object createPrimitiveResultObject(ResultSetWrapper rsw, ResultMap resultMap, String columnPrefix)
			throws SQLException {
		final Class<?> resultType = resultMap.getType();
		final String columnName;
		if (resultMap.getResultMappings().size() > 0) {
			final List<ResultMapping> resultMappingList = resultMap.getResultMappings();
			final ResultMapping mapping = resultMappingList.get(0);
			columnName = prependPrefix(mapping.getColumn(), columnPrefix);
		} else {
			columnName = rsw.getColumnNames().get(0);
		}
		final TypeHandler<?> typeHandler = rsw.getTypeHandler(resultType, columnName);
		return typeHandler.getResult(rsw.getResultSet(), columnName);
	}

	//
	// NESTED QUERY
	//

	protected Object getNestedQueryConstructorValue(ResultSet rs, ResultMapping constructorMapping, String columnPrefix)
			throws SQLException {
		final String nestedQueryId = constructorMapping.getNestedQueryId();
		final MappedStatement nestedQuery = configuration.getMappedStatement(nestedQueryId);
		final Class<?> nestedQueryParameterType = nestedQuery.getParameterMap().getType();
		final Object nestedQueryParameterObject = prepareParameterForNestedQuery(rs, constructorMapping,
				nestedQueryParameterType, columnPrefix);
		Object value = null;
		if (nestedQueryParameterObject != null) {
			final BoundSql nestedBoundSql = nestedQuery.getBoundSql(nestedQueryParameterObject);
			final CacheKey key = executor.createCacheKey(nestedQuery, nestedQueryParameterObject, RowBounds.DEFAULT,
					nestedBoundSql);
			final Class<?> targetType = constructorMapping.getJavaType();
			final ResultLoader resultLoader = new ResultLoader(configuration, executor, nestedQuery,
					nestedQueryParameterObject, targetType, key, nestedBoundSql);
			value = resultLoader.loadResult();
		}
		return value;
	}

	protected Object getNestedQueryMappingValue(ResultSet rs, MetaObject metaResultObject,
			ResultMapping propertyMapping, ResultLoaderMap lazyLoader, String columnPrefix) throws SQLException {
		final String nestedQueryId = propertyMapping.getNestedQueryId();
		final String property = propertyMapping.getProperty();
		final MappedStatement nestedQuery = configuration.getMappedStatement(nestedQueryId);
		final Class<?> nestedQueryParameterType = nestedQuery.getParameterMap().getType();
		final Object nestedQueryParameterObject = prepareParameterForNestedQuery(rs, propertyMapping,
				nestedQueryParameterType, columnPrefix);
		Object value = NO_VALUE;
		if (nestedQueryParameterObject != null) {
			final BoundSql nestedBoundSql = nestedQuery.getBoundSql(nestedQueryParameterObject);
			final CacheKey key = executor.createCacheKey(nestedQuery, nestedQueryParameterObject, RowBounds.DEFAULT,
					nestedBoundSql);
			final Class<?> targetType = propertyMapping.getJavaType();
			if (executor.isCached(nestedQuery, key)) {
				executor.deferLoad(nestedQuery, metaResultObject, property, key, targetType);
			} else {
				final ResultLoader resultLoader = new ResultLoader(configuration, executor, nestedQuery,
						nestedQueryParameterObject, targetType, key, nestedBoundSql);
				if (propertyMapping.isLazy()) {
					lazyLoader.addLoader(property, metaResultObject, resultLoader);
				} else {
					value = resultLoader.loadResult();
				}
			}
		}
		return value;
	}

	protected Object prepareParameterForNestedQuery(ResultSet rs, ResultMapping resultMapping, Class<?> parameterType,
			String columnPrefix) throws SQLException {
		if (resultMapping.isCompositeResult()) {
			return prepareCompositeKeyParameter(rs, resultMapping, parameterType, columnPrefix);
		} else {
			return prepareSimpleKeyParameter(rs, resultMapping, parameterType, columnPrefix);
		}
	}

	protected Object prepareSimpleKeyParameter(ResultSet rs, ResultMapping resultMapping, Class<?> parameterType,
			String columnPrefix) throws SQLException {
		final TypeHandler<?> typeHandler;
		if (typeHandlerRegistry.hasTypeHandler(parameterType)) {
			typeHandler = typeHandlerRegistry.getTypeHandler(parameterType);
		} else {
			typeHandler = typeHandlerRegistry.getUnknownTypeHandler();
		}
		return typeHandler.getResult(rs, prependPrefix(resultMapping.getColumn(), columnPrefix));
	}

	protected Object prepareCompositeKeyParameter(ResultSet rs, ResultMapping resultMapping, Class<?> parameterType,
			String columnPrefix) throws SQLException {
		final Object parameterObject = instantiateParameterObject(parameterType);
		final MetaObject metaObject = configuration.newMetaObject(parameterObject);
		boolean foundValues = false;
		for (ResultMapping innerResultMapping : resultMapping.getComposites()) {
			final Class<?> propType = metaObject.getSetterType(innerResultMapping.getProperty());
			final TypeHandler<?> typeHandler = typeHandlerRegistry.getTypeHandler(propType);
			final Object propValue = typeHandler.getResult(rs,
					prependPrefix(innerResultMapping.getColumn(), columnPrefix));
			if (propValue != null) { // issue #353 & #560 do not execute nested query if key is null
				metaObject.setValue(innerResultMapping.getProperty(), propValue);
				foundValues = true;
			}
		}
		return foundValues ? parameterObject : null;
	}

	protected Object instantiateParameterObject(Class<?> parameterType) {
		if (parameterType == null) {
			return new HashMap<Object, Object>();
		} else {
			return objectFactory.create(parameterType);
		}
	}

	//
	// DISCRIMINATOR
	//

	public ResultMap resolveDiscriminatedResultMap(ResultSet rs, ResultMap resultMap, String columnPrefix)
			throws SQLException {
		Set<String> pastDiscriminators = new HashSet<String>();
		Discriminator discriminator = resultMap.getDiscriminator();
		while (discriminator != null) {
			final Object value = getDiscriminatorValue(rs, discriminator, columnPrefix);
			final String discriminatedMapId = discriminator.getMapIdFor(String.valueOf(value));
			if (configuration.hasResultMap(discriminatedMapId)) {
				resultMap = configuration.getResultMap(discriminatedMapId);
				Discriminator lastDiscriminator = discriminator;
				discriminator = resultMap.getDiscriminator();
				if (discriminator == lastDiscriminator || !pastDiscriminators.add(discriminatedMapId)) {
					break;
				}
			} else {
				break;
			}
		}
		return resultMap;
	}

	protected Object getDiscriminatorValue(ResultSet rs, Discriminator discriminator, String columnPrefix)
			throws SQLException {
		final ResultMapping resultMapping = discriminator.getResultMapping();
		final TypeHandler<?> typeHandler = resultMapping.getTypeHandler();
		return typeHandler.getResult(rs, prependPrefix(resultMapping.getColumn(), columnPrefix));
	}

	protected String prependPrefix(String columnName, String prefix) {
		if (columnName == null || columnName.length() == 0 || prefix == null || prefix.length() == 0) {
			return columnName;
		}
		return prefix + columnName;
	}

	//
	// HANDLE NESTED RESULT MAPS
	//

	protected void handleRowValuesForNestedResultMap(ResultSetWrapper rsw, ResultMap resultMap,
			ResultHandler resultHandler, RowBounds rowBounds, ResultMapping parentMapping) throws SQLException {
		final DefaultResultContext resultContext = new DefaultResultContext();
		skipRows(rsw.getResultSet(), rowBounds);
		Object rowValue = null;
		while (shouldProcessMoreRows(resultContext, rowBounds) && rsw.getResultSet().next()) {
			final ResultMap discriminatedResultMap = resolveDiscriminatedResultMap(rsw.getResultSet(), resultMap, null);
			final CacheKey rowKey = createRowKey(discriminatedResultMap, rsw, null);
			Object partialObject = nestedResultObjects.get(rowKey);
			if (mappedStatement.isResultOrdered()) { // issue #577 && #542
				if (partialObject == null && rowValue != null) {
					nestedResultObjects.clear();
					storeObject(resultHandler, resultContext, rowValue, parentMapping, rsw.getResultSet());
				}
				rowValue = getRowValue(rsw, discriminatedResultMap, rowKey, rowKey, null, partialObject);
			} else {
				rowValue = getRowValue(rsw, discriminatedResultMap, rowKey, rowKey, null, partialObject);
				if (partialObject == null) {
					storeObject(resultHandler, resultContext, rowValue, parentMapping, rsw.getResultSet());
				}
			}
		}
		if (rowValue != null && mappedStatement.isResultOrdered() && shouldProcessMoreRows(resultContext, rowBounds)) {
			storeObject(resultHandler, resultContext, rowValue, parentMapping, rsw.getResultSet());
		}
	}

	//
	// GET VALUE FROM ROW FOR NESTED RESULT MAP
	//

	protected Object getRowValue(ResultSetWrapper rsw, ResultMap resultMap, CacheKey combinedKey, CacheKey absoluteKey,
			String columnPrefix, Object partialObject) throws SQLException {
		final String resultMapId = resultMap.getId();
		Object resultObject = partialObject;
		if (resultObject != null) {
			final MetaObject metaObject = configuration.newMetaObject(resultObject);
			putAncestor(absoluteKey, resultObject, resultMapId, columnPrefix);
			applyNestedResultMappings(rsw, resultMap, metaObject, columnPrefix, combinedKey, false);
			ancestorObjects.remove(absoluteKey);
		} else {
			final ResultLoaderMap lazyLoader = new ResultLoaderMap();
			resultObject = createResultObject(rsw, resultMap, lazyLoader, columnPrefix);
			if (resultObject != null && !typeHandlerRegistry.hasTypeHandler(resultMap.getType())) {
				final MetaObject metaObject = configuration.newMetaObject(resultObject);
				boolean foundValues = resultMap.getConstructorResultMappings().size() > 0;
				if (shouldApplyAutomaticMappings(resultMap,
						AutoMappingBehavior.FULL.equals(configuration.getAutoMappingBehavior()))) {
					foundValues = applyAutomaticMappings(rsw, resultMap, metaObject, columnPrefix) || foundValues;
				}
				foundValues = applyPropertyMappings(rsw, resultMap, metaObject, lazyLoader, columnPrefix)
						|| foundValues;
				putAncestor(absoluteKey, resultObject, resultMapId, columnPrefix);
				foundValues = applyNestedResultMappings(rsw, resultMap, metaObject, columnPrefix, combinedKey, true)
						|| foundValues;
				ancestorObjects.remove(absoluteKey);
				foundValues = lazyLoader.size() > 0 || foundValues;
				resultObject = foundValues ? resultObject : null;
			}
			if (combinedKey != CacheKey.NULL_CACHE_KEY) {
				nestedResultObjects.put(combinedKey, resultObject);
			}
		}
		return resultObject;
	}

	protected void putAncestor(CacheKey rowKey, Object resultObject, String resultMapId, String columnPrefix) {
		if (!ancestorColumnPrefix.containsKey(resultMapId)) {
			ancestorColumnPrefix.put(resultMapId, columnPrefix);
		}
		ancestorObjects.put(rowKey, resultObject);
	}

	//
	// NESTED RESULT MAP (JOIN MAPPING)
	//

	protected boolean applyNestedResultMappings(ResultSetWrapper rsw, ResultMap resultMap, MetaObject metaObject,
			String parentPrefix, CacheKey parentRowKey, boolean newObject) {
		boolean foundValues = false;
		for (ResultMapping resultMapping : resultMap.getPropertyResultMappings()) {
			final String nestedResultMapId = resultMapping.getNestedResultMapId();
			if (nestedResultMapId != null && resultMapping.getResultSet() == null) {
				try {
					final String columnPrefix = getColumnPrefix(parentPrefix, resultMapping);
					final ResultMap nestedResultMap = getNestedResultMap(rsw.getResultSet(), nestedResultMapId,
							columnPrefix);
					CacheKey rowKey = null;
					Object ancestorObject = null;
					if (ancestorColumnPrefix.containsKey(nestedResultMapId)) {
						rowKey = createRowKey(nestedResultMap, rsw, ancestorColumnPrefix.get(nestedResultMapId));
						ancestorObject = ancestorObjects.get(rowKey);
					}
					if (ancestorObject != null) {
						if (newObject) {
							metaObject.setValue(resultMapping.getProperty(), ancestorObject);
						}
					} else {
						rowKey = createRowKey(nestedResultMap, rsw, columnPrefix);
						final CacheKey combinedKey = combineKeys(rowKey, parentRowKey);
						Object rowValue = nestedResultObjects.get(combinedKey);
						boolean knownValue = (rowValue != null);
						final Object collectionProperty = instantiateCollectionPropertyIfAppropriate(resultMapping,
								metaObject);
						if (anyNotNullColumnHasValue(resultMapping, columnPrefix, rsw.getResultSet())) {
							rowValue = getRowValue(rsw, nestedResultMap, combinedKey, rowKey, columnPrefix, rowValue);
							if (rowValue != null && !knownValue) {
								if (collectionProperty != null) {
									final MetaObject targetMetaObject = configuration.newMetaObject(collectionProperty);
									targetMetaObject.add(rowValue);
								} else {
									metaObject.setValue(resultMapping.getProperty(), rowValue);
								}
								foundValues = true;
							}
						}
					}
				} catch (SQLException e) {
					throw new ExecutorException("Error getting nested result map values for '"
							+ resultMapping.getProperty() + "'.  Cause: " + e, e);
				}
			}
		}
		return foundValues;
	}

	protected String getColumnPrefix(String parentPrefix, ResultMapping resultMapping) {
		final StringBuilder columnPrefixBuilder = new StringBuilder();
		if (parentPrefix != null) {
			columnPrefixBuilder.append(parentPrefix);
		}
		if (resultMapping.getColumnPrefix() != null) {
			columnPrefixBuilder.append(resultMapping.getColumnPrefix());
		}
		final String columnPrefix = columnPrefixBuilder.length() == 0 ? null : columnPrefixBuilder.toString()
				.toUpperCase(Locale.ENGLISH);
		return columnPrefix;
	}

	protected boolean anyNotNullColumnHasValue(ResultMapping resultMapping, String columnPrefix, ResultSet rs)
			throws SQLException {
		Set<String> notNullColumns = resultMapping.getNotNullColumns();
		boolean anyNotNullColumnHasValue = true;
		if (notNullColumns != null && !notNullColumns.isEmpty()) {
			anyNotNullColumnHasValue = false;
			for (String column : notNullColumns) {
				rs.getObject(prependPrefix(column, columnPrefix));
				if (!rs.wasNull()) {
					anyNotNullColumnHasValue = true;
					break;
				}
			}
		}
		return anyNotNullColumnHasValue;
	}

	protected ResultMap getNestedResultMap(ResultSet rs, String nestedResultMapId, String columnPrefix)
			throws SQLException {
		ResultMap nestedResultMap = configuration.getResultMap(nestedResultMapId);
		nestedResultMap = resolveDiscriminatedResultMap(rs, nestedResultMap, columnPrefix);
		return nestedResultMap;
	}

	//
	// UNIQUE RESULT KEY
	//

	protected CacheKey createRowKey(ResultMap resultMap, ResultSetWrapper rsw, String columnPrefix) throws SQLException {
		final CacheKey cacheKey = new CacheKey();
		cacheKey.update(resultMap.getId());
		List<ResultMapping> resultMappings = getResultMappingsForRowKey(resultMap);
		if (resultMappings.size() == 0) {
			if (Map.class.isAssignableFrom(resultMap.getType())) {
				createRowKeyForMap(rsw, cacheKey);
			} else {
				createRowKeyForUnmappedProperties(resultMap, rsw, cacheKey, columnPrefix);
			}
		} else {
			createRowKeyForMappedProperties(resultMap, rsw, cacheKey, resultMappings, columnPrefix);
		}
		return cacheKey;
	}

	protected CacheKey combineKeys(CacheKey rowKey, CacheKey parentRowKey) {
		if (rowKey.getUpdateCount() > 1 && parentRowKey.getUpdateCount() > 1) {
			CacheKey combinedKey;
			try {
				combinedKey = rowKey.clone();
			} catch (CloneNotSupportedException e) {
				throw new ExecutorException("Error cloning cache key.  Cause: " + e, e);
			}
			combinedKey.update(parentRowKey);
			return combinedKey;
		}
		return CacheKey.NULL_CACHE_KEY;
	}

	protected List<ResultMapping> getResultMappingsForRowKey(ResultMap resultMap) {
		List<ResultMapping> resultMappings = resultMap.getIdResultMappings();
		if (resultMappings.size() == 0) {
			resultMappings = resultMap.getPropertyResultMappings();
		}
		return resultMappings;
	}

	protected void createRowKeyForMappedProperties(ResultMap resultMap, ResultSetWrapper rsw, CacheKey cacheKey,
			List<ResultMapping> resultMappings, String columnPrefix) throws SQLException {
		for (ResultMapping resultMapping : resultMappings) {
			if (resultMapping.getNestedResultMapId() != null && resultMapping.getResultSet() == null) { // Issue #392
				final ResultMap nestedResultMap = configuration.getResultMap(resultMapping.getNestedResultMapId());
				createRowKeyForMappedProperties(nestedResultMap, rsw, cacheKey,
						nestedResultMap.getConstructorResultMappings(),
						prependPrefix(resultMapping.getColumnPrefix(), columnPrefix));
			} else if (resultMapping.getNestedQueryId() == null) {
				final String column = prependPrefix(resultMapping.getColumn(), columnPrefix);
				final TypeHandler<?> th = resultMapping.getTypeHandler();
				List<String> mappedColumnNames = rsw.getMappedColumnNames(resultMap, columnPrefix);
				if (column != null && mappedColumnNames.contains(column.toUpperCase(Locale.ENGLISH))) { // Issue #114
					final Object value = th.getResult(rsw.getResultSet(), column);
					if (value != null) {
						cacheKey.update(column);
						cacheKey.update(value);
					}
				}
			}
		}
	}

	protected void createRowKeyForUnmappedProperties(ResultMap resultMap, ResultSetWrapper rsw, CacheKey cacheKey,
			String columnPrefix) throws SQLException {
		final MetaClass metaType = MetaClass.forClass(resultMap.getType());
		List<String> unmappedColumnNames = rsw.getUnmappedColumnNames(resultMap, columnPrefix);
		for (String column : unmappedColumnNames) {
			String property = column;
			if (columnPrefix != null && columnPrefix.length() > 0) {
				// When columnPrefix is specified,
				// ignore columns without the prefix.
				if (column.toUpperCase(Locale.ENGLISH).startsWith(columnPrefix)) {
					property = column.substring(columnPrefix.length());
				} else {
					continue;
				}
			}
			if (metaType.findProperty(property, configuration.isMapUnderscoreToCamelCase()) != null) {
				String value = rsw.getResultSet().getString(column);
				if (value != null) {
					cacheKey.update(column);
					cacheKey.update(value);
				}
			}
		}
	}

	protected void createRowKeyForMap(ResultSetWrapper rsw, CacheKey cacheKey) throws SQLException {
		List<String> columnNames = rsw.getColumnNames();
		for (String columnName : columnNames) {
			final String value = rsw.getResultSet().getString(columnName);
			if (value != null) {
				cacheKey.update(columnName);
				cacheKey.update(value);
			}
		}
	}

}

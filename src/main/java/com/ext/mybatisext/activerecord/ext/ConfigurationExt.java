package com.ext.mybatisext.activerecord.ext;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.CacheRefResolver;
import org.apache.ibatis.builder.ResultMapResolver;
import org.apache.ibatis.builder.annotation.MethodResolver;
import org.apache.ibatis.builder.xml.XMLStatementBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.loader.ProxyFactory;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.LanguageDriverRegistry;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * 扩展配置类
 * <p>
 * @author   bo
 * @Date	 2015-11-14 	 
 */
public class ConfigurationExt extends Configuration {

	protected Configuration config;

	public ConfigurationExt(Environment environment) {
		super(environment);
	}

	public ConfigurationExt(Configuration config) {
		this.config = config;
	}

	@Override
	public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement,
			RowBounds rowBounds, ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql) {
		ResultSetHandler resultSetHandler = new DefaultResultSetHandlerExt(executor, mappedStatement, parameterHandler,
				resultHandler, boundSql, rowBounds);
		resultSetHandler = (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
		return resultSetHandler;
	}

	@Override
	public String getLogPrefix() {
		if (config != null) {
			return config.getLogPrefix();
		}
		return super.getLogPrefix();

	}

	@Override
	public void setLogPrefix(String logPrefix) {
		if (config != null) {
			config.setLogPrefix(logPrefix);
			return;
		}
		super.setLogPrefix(logPrefix);

	}

	@Override
	public Class<? extends Log> getLogImpl() {
		if (config != null) {
			return config.getLogImpl();
		}
		return super.getLogImpl();

	}

	@Override
	public void setLogImpl(Class<?> logImpl) {
		if (config != null) {
			config.setLogImpl(logImpl);
			return;
		}
		super.setLogImpl(logImpl);

	}

	@Override
	public boolean isCallSettersOnNulls() {
		if (config != null) {
			return config.isCallSettersOnNulls();
		}
		return super.isCallSettersOnNulls();

	}

	@Override
	public void setCallSettersOnNulls(boolean callSettersOnNulls) {
		if (config != null) {
			config.setCallSettersOnNulls(callSettersOnNulls);
			return;
		}
		super.setCallSettersOnNulls(callSettersOnNulls);

	}

	@Override
	public String getDatabaseId() {
		if (config != null) {
			return config.getDatabaseId();
		}
		return super.getDatabaseId();

	}

	@Override
	public void setDatabaseId(String databaseId) {
		if (config != null) {
			config.setDatabaseId(databaseId);
			return;
		}
		super.setDatabaseId(databaseId);

	}

	@Override
	public Class<?> getConfigurationFactory() {
		if (config != null) {
			return config.getLogImpl();
		}
		return super.getConfigurationFactory();

	}

	@Override
	public void setConfigurationFactory(Class<?> configurationFactory) {
		if (config != null) {
			config.setConfigurationFactory(configurationFactory);
			return;
		}
		super.setConfigurationFactory(configurationFactory);

	}

	@Override
	public boolean isSafeResultHandlerEnabled() {
		if (config != null) {
			return config.isSafeResultHandlerEnabled();
		}
		return super.isSafeResultHandlerEnabled();

	}

	@Override
	public void setSafeResultHandlerEnabled(boolean safeResultHandlerEnabled) {
		if (config != null) {
			config.setSafeResultHandlerEnabled(safeResultHandlerEnabled);
			return;
		}
		super.setSafeResultHandlerEnabled(safeResultHandlerEnabled);

	}

	@Override
	public boolean isSafeRowBoundsEnabled() {
		if (config != null) {
			return config.isSafeRowBoundsEnabled();
		}
		return super.isSafeRowBoundsEnabled();

	}

	@Override
	public void setSafeRowBoundsEnabled(boolean safeRowBoundsEnabled) {
		if (config != null) {
			config.setSafeRowBoundsEnabled(safeRowBoundsEnabled);
			return;
		}
		super.setSafeRowBoundsEnabled(safeRowBoundsEnabled);

	}

	@Override
	public boolean isMapUnderscoreToCamelCase() {
		if (config != null) {
			return config.isMapUnderscoreToCamelCase();
		}
		return super.isMapUnderscoreToCamelCase();

	}

	@Override
	public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
		if (config != null) {
			config.setMapUnderscoreToCamelCase(mapUnderscoreToCamelCase);
			return;
		}
		super.setMapUnderscoreToCamelCase(mapUnderscoreToCamelCase);

	}

	@Override
	public void addLoadedResource(String resource) {
		if (config != null) {
			config.addLoadedResource(resource);
			return;
		}
		super.addLoadedResource(resource);

	}

	@Override
	public boolean isResourceLoaded(String resource) {
		if (config != null) {
			return config.isResourceLoaded(resource);
		}
		return super.isResourceLoaded(resource);

	}

	@Override
	public Environment getEnvironment() {
		if (config != null) {
			return config.getEnvironment();
		}
		return super.getEnvironment();

	}

	@Override
	public void setEnvironment(Environment environment) {
		if (config != null) {
			config.setEnvironment(environment);
			return;
		}
		super.setEnvironment(environment);

	}

	@Override
	public AutoMappingBehavior getAutoMappingBehavior() {
		if (config != null) {
			return config.getAutoMappingBehavior();
		}
		return super.getAutoMappingBehavior();

	}

	@Override
	public void setAutoMappingBehavior(AutoMappingBehavior autoMappingBehavior) {
		if (config != null) {
			config.setAutoMappingBehavior(autoMappingBehavior);
			return;
		}
		super.setAutoMappingBehavior(autoMappingBehavior);

	}

	@Override
	public boolean isLazyLoadingEnabled() {
		if (config != null) {
			return config.isLazyLoadingEnabled();
		}
		return super.isLazyLoadingEnabled();

	}

	@Override
	public void setLazyLoadingEnabled(boolean lazyLoadingEnabled) {
		if (config != null) {
			config.setLazyLoadingEnabled(lazyLoadingEnabled);
			return;
		}
		super.setLazyLoadingEnabled(lazyLoadingEnabled);

	}

	@Override
	public ProxyFactory getProxyFactory() {
		if (config != null) {
			return config.getProxyFactory();
		}
		return super.getProxyFactory();

	}

	@Override
	public void setProxyFactory(ProxyFactory proxyFactory) {
		if (config != null) {
			config.setProxyFactory(proxyFactory);
			return;
		}
		super.setProxyFactory(proxyFactory);

	}

	@Override
	public boolean isAggressiveLazyLoading() {
		if (config != null) {
			return config.isAggressiveLazyLoading();
		}
		return super.isAggressiveLazyLoading();

	}

	@Override
	public void setAggressiveLazyLoading(boolean aggressiveLazyLoading) {
		if (config != null) {
			config.setAggressiveLazyLoading(aggressiveLazyLoading);
			return;
		}
		super.setAggressiveLazyLoading(aggressiveLazyLoading);

	}

	@Override
	public boolean isMultipleResultSetsEnabled() {
		if (config != null) {
			return config.isMultipleResultSetsEnabled();
		}
		return super.isMultipleResultSetsEnabled();

	}

	@Override
	public void setMultipleResultSetsEnabled(boolean multipleResultSetsEnabled) {
		if (config != null) {
			config.setMultipleResultSetsEnabled(multipleResultSetsEnabled);
			return;
		}
		super.setMultipleResultSetsEnabled(multipleResultSetsEnabled);

	}

	@Override
	public Set<String> getLazyLoadTriggerMethods() {
		if (config != null) {
			return config.getLazyLoadTriggerMethods();
		}
		return super.getLazyLoadTriggerMethods();

	}

	@Override
	public void setLazyLoadTriggerMethods(Set<String> lazyLoadTriggerMethods) {
		if (config != null) {
			config.setLazyLoadTriggerMethods(lazyLoadTriggerMethods);
			return;
		}
		super.setLazyLoadTriggerMethods(lazyLoadTriggerMethods);

	}

	@Override
	public boolean isUseGeneratedKeys() {
		if (config != null) {
			return config.isUseGeneratedKeys();
		}
		return super.isUseGeneratedKeys();

	}

	@Override
	public void setUseGeneratedKeys(boolean useGeneratedKeys) {
		if (config != null) {
			config.setUseGeneratedKeys(useGeneratedKeys);
			return;
		}
		super.setUseGeneratedKeys(useGeneratedKeys);

	}

	@Override
	public ExecutorType getDefaultExecutorType() {
		if (config != null) {
			return config.getDefaultExecutorType();
		}
		return super.getDefaultExecutorType();

	}

	@Override
	public void setDefaultExecutorType(ExecutorType defaultExecutorType) {
		if (config != null) {
			config.setDefaultExecutorType(defaultExecutorType);
			return;
		}
		super.setDefaultExecutorType(defaultExecutorType);

	}

	@Override
	public boolean isCacheEnabled() {
		if (config != null) {
			return config.isCacheEnabled();
		}
		return super.isCacheEnabled();

	}

	@Override
	public void setCacheEnabled(boolean cacheEnabled) {
		if (config != null) {
			config.setCacheEnabled(cacheEnabled);
			return;
		}
		super.setCacheEnabled(cacheEnabled);

	}

	@Override
	public Integer getDefaultStatementTimeout() {
		if (config != null) {
			return config.getDefaultStatementTimeout();
		}
		return super.getDefaultStatementTimeout();

	}

	@Override
	public void setDefaultStatementTimeout(Integer defaultStatementTimeout) {
		if (config != null) {
			config.setDefaultStatementTimeout(defaultStatementTimeout);
			return;
		}
		super.setDefaultStatementTimeout(defaultStatementTimeout);

	}

	@Override
	public boolean isUseColumnLabel() {
		if (config != null) {
			return config.isUseColumnLabel();
		}
		return super.isUseColumnLabel();

	}

	@Override
	public void setUseColumnLabel(boolean useColumnLabel) {
		if (config != null) {
			config.setUseColumnLabel(useColumnLabel);
			return;
		}
		super.setUseColumnLabel(useColumnLabel);

	}

	@Override
	public LocalCacheScope getLocalCacheScope() {
		if (config != null) {
			return config.getLocalCacheScope();
		}
		return super.getLocalCacheScope();

	}

	@Override
	public void setLocalCacheScope(LocalCacheScope localCacheScope) {
		if (config != null) {
			config.setLocalCacheScope(localCacheScope);
			return;
		}
		super.setLocalCacheScope(localCacheScope);

	}

	@Override
	public JdbcType getJdbcTypeForNull() {
		if (config != null) {
			return config.getJdbcTypeForNull();
		}
		return super.getJdbcTypeForNull();

	}

	@Override
	public void setJdbcTypeForNull(JdbcType jdbcTypeForNull) {
		if (config != null) {
			config.setJdbcTypeForNull(jdbcTypeForNull);
			return;
		}
		super.setJdbcTypeForNull(jdbcTypeForNull);

	}

	@Override
	public Properties getVariables() {
		if (config != null) {
			return config.getVariables();
		}
		return super.getVariables();

	}

	@Override
	public void setVariables(Properties variables) {
		if (config != null) {
			config.setVariables(variables);
			return;
		}
		super.setVariables(variables);

	}

	@Override
	public TypeHandlerRegistry getTypeHandlerRegistry() {
		if (config != null) {
			return config.getTypeHandlerRegistry();
		}
		return super.getTypeHandlerRegistry();

	}

	@Override
	public TypeAliasRegistry getTypeAliasRegistry() {
		if (config != null) {
			return config.getTypeAliasRegistry();
		}
		return super.getTypeAliasRegistry();

	}

	@Override
	public MapperRegistry getMapperRegistry() {
		if (config != null) {
			return config.getMapperRegistry();
		}
		return super.getMapperRegistry();

	}

	@Override
	public ObjectFactory getObjectFactory() {
		if (config != null) {
			return config.getObjectFactory();
		}
		return super.getObjectFactory();

	}

	@Override
	public void setObjectFactory(ObjectFactory objectFactory) {
		if (config != null) {
			config.setObjectFactory(objectFactory);
			return;
		}
		super.setObjectFactory(objectFactory);

	}

	@Override
	public ObjectWrapperFactory getObjectWrapperFactory() {
		if (config != null) {
			return config.getObjectWrapperFactory();
		}
		return super.getObjectWrapperFactory();

	}

	@Override
	public void setObjectWrapperFactory(ObjectWrapperFactory objectWrapperFactory) {
		if (config != null) {
			config.setObjectWrapperFactory(objectWrapperFactory);
			return;
		}
		super.setObjectWrapperFactory(objectWrapperFactory);

	}

	@Override
	public List<Interceptor> getInterceptors() {
		if (config != null) {
			return config.getInterceptors();
		}
		return super.getInterceptors();

	}

	@Override
	public LanguageDriverRegistry getLanguageRegistry() {
		if (config != null) {
			return config.getLanguageRegistry();
		}
		return super.getLanguageRegistry();

	}

	@Override
	public void setDefaultScriptingLanguage(Class<?> driver) {
		if (config != null) {
			config.setDefaultScriptingLanguage(driver);
			return;
		}
		super.setDefaultScriptingLanguage(driver);

	}

	@Override
	public LanguageDriver getDefaultScriptingLanuageInstance() {
		if (config != null) {
			return config.getDefaultScriptingLanuageInstance();
		}
		return super.getDefaultScriptingLanuageInstance();

	}

	@Override
	public MetaObject newMetaObject(Object object) {
		if (config != null) {
			return config.newMetaObject(object);
		}
		return super.newMetaObject(object);

	}

	@Override
	public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject,
			BoundSql boundSql) {
		if (config != null) {
			return config.newParameterHandler(mappedStatement, parameterObject, boundSql);
		}
		return super.newParameterHandler(mappedStatement, parameterObject, boundSql);

	}

	@Override
	public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement,
			Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
		if (config != null) {
			return config.newStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler,
					boundSql);
		}
		return super
				.newStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);

	}

	@Override
	public Executor newExecutor(Transaction transaction) {
		if (config != null) {
			return config.newExecutor(transaction);
		}
		return super.newExecutor(transaction);

	}

	@Override
	public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
		if (config != null) {
			return config.newExecutor(transaction, executorType);
		}
		return super.newExecutor(transaction, executorType);

	}

	@Override
	public void addKeyGenerator(String id, KeyGenerator keyGenerator) {
		if (config != null) {
			config.addKeyGenerator(id, keyGenerator);
			return;
		}
		super.addKeyGenerator(id, keyGenerator);

	}

	@Override
	public Collection<String> getKeyGeneratorNames() {
		if (config != null) {
			return config.getKeyGeneratorNames();
		}
		return super.getKeyGeneratorNames();

	}

	@Override
	public Collection<KeyGenerator> getKeyGenerators() {
		if (config != null) {
			return config.getKeyGenerators();
		}
		return super.getKeyGenerators();

	}

	@Override
	public KeyGenerator getKeyGenerator(String id) {
		if (config != null) {
			return config.getKeyGenerator(id);
		}
		return super.getKeyGenerator(id);

	}

	@Override
	public boolean hasKeyGenerator(String id) {
		if (config != null) {
			return config.hasKeyGenerator(id);
		}
		return super.hasKeyGenerator(id);

	}

	@Override
	public void addCache(Cache cache) {
		if (config != null) {
			config.addCache(cache);
			return;
		}
		super.addCache(cache);

	}

	@Override
	public Collection<String> getCacheNames() {
		if (config != null) {
			return config.getCacheNames();
		}
		return super.getCacheNames();

	}

	@Override
	public Collection<Cache> getCaches() {
		if (config != null) {
			return config.getCaches();
		}
		return super.getCaches();

	}

	@Override
	public Cache getCache(String id) {
		if (config != null) {
			return config.getCache(id);
		}
		return super.getCache(id);

	}

	@Override
	public boolean hasCache(String id) {
		if (config != null) {
			return config.hasCache(id);
		}
		return super.hasCache(id);
	}

	@Override
	public void addResultMap(ResultMap rm) {
		if (config != null) {
			config.addResultMap(rm);
			return;
		}
		super.addResultMap(rm);

	}

	@Override
	public Collection<String> getResultMapNames() {
		if (config != null) {
			return config.getResultMapNames();
		}
		return super.getResultMapNames();

	}

	@Override
	public Collection<ResultMap> getResultMaps() {
		if (config != null) {
			return config.getResultMaps();
		}
		return super.getResultMaps();

	}

	@Override
	public ResultMap getResultMap(String id) {
		if (config != null) {
			return config.getResultMap(id);
		}
		return super.getResultMap(id);

	}

	@Override
	public boolean hasResultMap(String id) {
		if (config != null) {
			return config.hasResultMap(id);
		}
		return super.hasResultMap(id);

	}

	@Override
	public void addParameterMap(ParameterMap pm) {
		if (config != null) {
			config.addParameterMap(pm);
			return;
		}
		super.addParameterMap(pm);

	}

	@Override
	public Collection<String> getParameterMapNames() {
		if (config != null) {
			return config.getParameterMapNames();
		}
		return super.getParameterMapNames();

	}

	@Override
	public Collection<ParameterMap> getParameterMaps() {
		if (config != null) {
			return config.getParameterMaps();
		}
		return super.getParameterMaps();

	}

	@Override
	public ParameterMap getParameterMap(String id) {
		if (config != null) {
			return config.getParameterMap(id);
		}
		return super.getParameterMap(id);

	}

	@Override
	public boolean hasParameterMap(String id) {
		if (config != null) {
			return config.hasParameterMap(id);
		}
		return super.hasParameterMap(id);

	}

	@Override
	public void addMappedStatement(MappedStatement ms) {
		if (config != null) {
			config.addMappedStatement(ms);
			return;
		}
		super.addMappedStatement(ms);

	}

	@Override
	public Collection<String> getMappedStatementNames() {
		if (config != null) {
			return config.getMappedStatementNames();
		}
		return super.getMappedStatementNames();

	}

	@Override
	public Collection<MappedStatement> getMappedStatements() {
		if (config != null) {
			return config.getMappedStatements();
		}
		return super.getMappedStatements();

	}

	@Override
	public Collection<XMLStatementBuilder> getIncompleteStatements() {
		if (config != null) {
			return config.getIncompleteStatements();
		}
		return super.getIncompleteStatements();

	}

	@Override
	public void addIncompleteStatement(XMLStatementBuilder incompleteStatement) {
		if (config != null) {
			config.addIncompleteStatement(incompleteStatement);
			return;
		}
		super.addIncompleteStatement(incompleteStatement);

	}

	@Override
	public Collection<CacheRefResolver> getIncompleteCacheRefs() {
		if (config != null) {
			return config.getIncompleteCacheRefs();
		}
		return super.getIncompleteCacheRefs();

	}

	@Override
	public void addIncompleteCacheRef(CacheRefResolver incompleteCacheRef) {
		if (config != null) {
			config.addIncompleteCacheRef(incompleteCacheRef);
			return;
		}
		super.addIncompleteCacheRef(incompleteCacheRef);

	}

	@Override
	public Collection<ResultMapResolver> getIncompleteResultMaps() {
		if (config != null) {
			return config.getIncompleteResultMaps();
		}
		return super.getIncompleteResultMaps();

	}

	@Override
	public void addIncompleteResultMap(ResultMapResolver resultMapResolver) {
		if (config != null) {
			config.addIncompleteResultMap(resultMapResolver);
			return;
		}
		super.addIncompleteResultMap(resultMapResolver);

	}

	@Override
	public void addIncompleteMethod(MethodResolver builder) {
		if (config != null) {
			config.addIncompleteMethod(builder);
			return;
		}
		super.addIncompleteMethod(builder);

	}

	@Override
	public Collection<MethodResolver> getIncompleteMethods() {
		if (config != null) {
			return config.getIncompleteMethods();
		}
		return super.getIncompleteMethods();

	}

	@Override
	public MappedStatement getMappedStatement(String id) {
		if (config != null) {
			return config.getMappedStatement(id);
		}
		return super.getMappedStatement(id);

	}

	@Override
	public MappedStatement getMappedStatement(String id, boolean validateIncompleteStatements) {
		if (config != null) {
			return config.getMappedStatement(id, validateIncompleteStatements);
		}
		return super.getMappedStatement(id, validateIncompleteStatements);

	}

	@Override
	public Map<String, XNode> getSqlFragments() {
		if (config != null) {
			return config.getSqlFragments();
		}
		return super.getSqlFragments();

	}

	@Override
	public void addInterceptor(Interceptor interceptor) {
		if (config != null) {
			config.addInterceptor(interceptor);
			return;
		}
		super.addInterceptor(interceptor);

	}

	@Override
	public void addMappers(String packageName, Class<?> superType) {
		if (config != null) {
			config.addMappers(packageName, superType);
			return;
		}
		super.addMappers(packageName, superType);

	}

	@Override
	public void addMappers(String packageName) {
		if (config != null) {
			config.addMappers(packageName);
			return;
		}
		super.addMappers(packageName);

	}

	@Override
	public <T> void addMapper(Class<T> type) {
		if (config != null) {
			config.addMapper(type);
			return;
		}
		super.addMapper(type);

	}

	@Override
	public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
		if (config != null) {
			return config.getMapper(type, sqlSession);
		}
		return super.getMapper(type, sqlSession);

	}

	@Override
	public boolean hasMapper(Class<?> type) {
		if (config != null) {
			return config.hasMapper(type);
		}
		return super.hasMapper(type);

	}

	@Override
	public boolean hasStatement(String statementName) {
		if (config != null) {
			return config.hasStatement(statementName);
		}
		return super.hasStatement(statementName);

	}

	@Override
	public boolean hasStatement(String statementName, boolean validateIncompleteStatements) {
		if (config != null) {
			return config.hasStatement(statementName, validateIncompleteStatements);
		}
		return super.hasStatement(statementName, validateIncompleteStatements);

	}

	@Override
	public void addCacheRef(String namespace, String referencedNamespace) {
		if (config != null) {
			config.addCacheRef(namespace, referencedNamespace);
			return;
		}
		super.addCacheRef(namespace, referencedNamespace);

	}

}

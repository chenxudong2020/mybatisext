package com.ext.mybatisext.environment;

import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.ext.mybatisext.helper.VelocityTemplateUtil;

/**
 * @author bo 探测运行环境
 */
public class EnvironmentDetect implements RunEnvironment, RunConfig, InitializingBean {

	private static final Log logger = LogFactory.getLog(EnvironmentDetect.class);


	public static String LOCAL = "LOCAL";

	public static String DEVELOP = "DEVELOP";

	public static String TEST = "TEST";

	public static String PRODUCT = "PRODUCT";

	protected static final String DOMAIN_NAME = "environment.config";

	protected static final byte[] LOCAL_HOSTS = new byte[ ] { 1, 0, 0, 0 };

	protected static final byte[] DEVELOP_HOSTS = new byte[ ] { 2, 0, 0, 0 };

	protected static final byte[] TEST_HOSTS = new byte[ ] { 3, 0, 0, 0 };

	protected static final byte[] PRODUCT_HOSTS = new byte[ ] { 4, 0, 0, 0 };

	private Resource[] resources;

	private Properties properties;

	// 当前环境
	private static final Environment environment;

	static {
		environment = detectEnvironment();
	}


	public static Environment detectEnvironment() {
		if ( environment != null ) {
			return environment;
		}
		Environment env = null;
		logger.warn("start environment ...");
		String env_string = System.getProperty("env");
		if ( env_string != null ) {
			if ( LOCAL.equals(env_string) ) {
				env = Environment.LOCAL;
			} else if ( DEVELOP.equals(env_string) ) {
				env = Environment.DEVELOP;
			} else if ( TEST.equals(env_string) ) {
				env = Environment.TEST;
			} else if ( PRODUCT.equals(env_string) ) {
				env = Environment.PRODUCT;
			}
		}
		if ( env == null ) {
			//判断hosts
			env = detectHostsEnvironment();
		}
		logger.warn("current environment is :" + env.getName());
		return env;
	}


	private static Environment detectHostsEnvironment() {
		Environment env = null;
		try {
			InetAddress address = InetAddress.getByName(DOMAIN_NAME);
			if ( address != null ) {
				byte[] addressBytes = address.getAddress();
				if ( Arrays.equals(LOCAL_HOSTS, addressBytes) ) {
					env = Environment.LOCAL;
				} else if ( Arrays.equals(DEVELOP_HOSTS, addressBytes) ) {
					env = Environment.DEVELOP;
				} else if ( Arrays.equals(TEST_HOSTS, addressBytes) ) {
					env = Environment.TEST;
				} else if ( Arrays.equals(PRODUCT_HOSTS, addressBytes) ) {
					env = Environment.PRODUCT;
				}
			}
		} catch ( UnknownHostException e ) {
			env = Environment.DEVELOP;
		}
		if ( env == null ) {
			env = Environment.DEVELOP;
		}
		return env;
	}


	@Override
	public Environment getEnvironment() {
		return environment;
	}


	@Override
	public RunConfig getRunConfig() {

		return this;
	}


	public void init() throws Exception {
		if ( properties != null ) {
			return;
		}
		properties = new Properties();
		Map<Object, Object> context = new HashMap<Object, Object>();
		context.put("environment", EnvironmentDetect.detectEnvironment());
		for ( Resource resource : resources ) {
			String location = resource.getURL().getFile();
			String config = VelocityTemplateUtil.getConfiguration(context, location);
			StringReader reader = new StringReader(config);
			properties.load(reader);
		}
		// 系统变量可以替换properties里面的变量
		properties.putAll(System.getProperties());
		// 特殊处理dubbo配置信息
		for ( Map.Entry<Object, Object> entry : properties.entrySet() ) {
			if ( entry.getKey().toString().contains("dubbo.") ) {
				System.setProperty(entry.getKey().toString(), entry.getValue().toString());
			}
		}
	}


	@Override
	public String getProperty( String key ) {
		return properties.getProperty(key);
	}


	@Override
	public String getProperty( String key, String defaultValue ) {
		return properties.getProperty(key, defaultValue);
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(resources, "属性文件配置不能为null");
		init();

	}


	@Override
	public Properties getProperties() {
		return properties;
	}


	public Resource[] getResources() {
		return resources;
	}


	public void setResources( Resource[] resources ) {
		this.resources = resources;
	}

}
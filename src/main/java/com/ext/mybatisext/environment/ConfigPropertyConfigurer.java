package com.ext.mybatisext.environment;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;
import org.springframework.util.StringValueResolver;

@SuppressWarnings("synthetic-access")
public class ConfigPropertyConfigurer extends PropertyPlaceholderConfigurer {


	RunEnvironment environment;



	public ConfigPropertyConfigurer() {
		super();
	}


	@Override
	protected Properties mergeProperties() throws IOException {
		//RunConfig config = this.context.getBean(RunConfig.class);
		this.setProperties(environment.getRunConfig().getProperties());
		this.setNullValue("");
		return super.mergeProperties();
	}


	@Override
	protected void processProperties( ConfigurableListableBeanFactory beanFactoryToProcess, Properties props )
		throws BeansException {

		StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(props);
		this.doProcessProperties(beanFactoryToProcess, valueResolver);

	}


	private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

		private final PropertyPlaceholderHelper helper;

		private final PlaceholderResolver resolver;


		public PlaceholderResolvingStringValueResolver( Properties props ) {
			this.helper = new PropertyPlaceholderHelperExt(placeholderPrefix, placeholderSuffix, valueSeparator,
					ignoreUnresolvablePlaceholders);
			this.resolver = new PropertyPlaceholderConfigurerResolver(props);
		}


		@Override
		public String resolveStringValue( String strVal ) throws BeansException {
			String value = this.helper.replacePlaceholders(strVal, this.resolver);
			return (value.equals(nullValue) ? null : value);
		}
	}

	private class PropertyPlaceholderConfigurerResolver implements PlaceholderResolver {

		private final Properties props;


		private PropertyPlaceholderConfigurerResolver( Properties props ) {
			this.props = props;
		}


		@Override
		public String resolvePlaceholder( String placeholderName ) {
			String value = ConfigPropertyConfigurer.this.resolvePlaceholder(placeholderName, props,
				PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_FALLBACK);
			return value;
		}
	}


	public RunEnvironment getEnvironment() {
		return environment;
	}


	public void setEnvironment( RunEnvironment environment ) {
		this.environment = environment;
	}

}

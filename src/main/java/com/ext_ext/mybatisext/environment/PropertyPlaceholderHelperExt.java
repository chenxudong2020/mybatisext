package com.ext.mybatisext.environment;

import java.util.Set;

import org.springframework.util.PropertyPlaceholderHelper;


/**
 * 处理不赋值的情况
 * <p>

 * @author   宋汝波
 * @date	 2014年11月14日 
 * @version  1.0.0	 
 */
public class PropertyPlaceholderHelperExt extends PropertyPlaceholderHelper {

	public PropertyPlaceholderHelperExt(
			String placeholderPrefix,
			String placeholderSuffix,
			String valueSeparator,
			boolean ignoreUnresolvablePlaceholders ) {

		super(placeholderPrefix, placeholderSuffix, valueSeparator, ignoreUnresolvablePlaceholders);

	}


	@Override
	protected String parseStringValue( String strVal, PlaceholderResolver placeholderResolver,
			Set<String> visitedPlaceholders ) {
		String value = super.parseStringValue(strVal, placeholderResolver, visitedPlaceholders);
		if ( value.startsWith("$") ) {
			return "";
		}
		return value;

	}

}

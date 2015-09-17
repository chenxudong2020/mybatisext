package com.ext_ext.mybatisext.helper;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.MetaClass;

/**
 * 保留list和bean的某些属性值
 * <p>
 * 
 * @author 宋汝波
 * @date 2015年3月13日
 * @version 1.0.0
 */
@SuppressWarnings("rawtypes")
public class PropertyHelper {

	private static Log logger = LogFactory.getLog(PropertyHelper.class);


	@SuppressWarnings("unchecked")
	public static <T> T getProperty( Object obj, String propertyName ) {

		if ( obj == null ) {
			return null;
		}
		MetaClass metaClass = MetaClass.forClass(obj.getClass());
		try {
			return (T) metaClass.getGetInvoker(propertyName).invoke(obj, new Object[ ] {});
		} catch ( Exception e ) {
			logger.error("", e);

		}
		return null;
	}


	public static Map<String, Object> getPropertiesValue( Object obj ) {
		MetaClass metaClass = MetaClass.forClass(obj.getClass());
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String[] getters = metaClass.getGetterNames();

			for ( String getter : getters ) {
				Object value = metaClass.getGetInvoker(getter).invoke(obj, new Object[ ] {});
				// 过滤掉null值
				if ( value != null ) {
					result.put(getter, value);
				}
			}
		} catch ( Exception e ) {
			logger.error("", e);

		}

		return result;
	}


	public static Map<String, Class<?>> getPropertiesType( Class<?> cls ) {
		MetaClass metaClass = MetaClass.forClass(cls);
		Map<String, Class<?>> result = new HashMap<String, Class<?>>();

		String[] getters = metaClass.getGetterNames();

		for ( String getter : getters ) {
			Class<?> value = metaClass.getGetterType(getter);
			result.put(getter, value);
		}

		return result;
	}


	public static String[] getProperties( Class<?> cls ) {
		MetaClass metaClass = MetaClass.forClass(cls);
		String[] getters = metaClass.getGetterNames();
		return getters;
	}


	public static void map2Object( Map map, Object newObj ) {
		MetaClass metaClass = MetaClass.forClass(newObj.getClass());
		try {
			String[] setter = metaClass.getSetterNames();
			for ( String set : setter ) {
				Object obj = map.get(set.toLowerCase());
				metaClass.getSetInvoker(set).invoke(newObj, new Object[ ] { obj });
			}
		} catch ( Exception e ) {
			logger.error("", e);

		}
	}


	/**
	 * 保留属性值
	 * <p>
	 *
	 * @param list
	 * @param property
	 */
	public static void reserveProperty( List<? extends Serializable> list, String... property ) {
		if ( list == null || list.size() == 0 ) {
			return;
		}
		try {
			Set<String> setProperty = new HashSet<String>(Arrays.asList(property));
			MetaClass metaClass = MetaClass.forClass(list.get(0).getClass());
			String[] setter = metaClass.getSetterNames();
			for ( String set : setter ) {
				// 不保留这些属性
				if ( !setProperty.contains(set) ) {
					for ( Object obj : list ) {
						metaClass.getSetInvoker(set).invoke(obj, new Object[ ] { null });
					}
				}
			}
		} catch ( Exception e ) {
			logger.error("", e);
		}

	}


	/**
	 * 保留属性值
	 * <p>
	 *
	 * @param object
	 * @param property
	 */
	public static void reserveProperty( Serializable object, String... property ) {
		if ( object == null ) {
			return;
		}
		try {
			Set<String> setProperty = new HashSet<String>(Arrays.asList(property));
			MetaClass metaClass = MetaClass.forClass(object.getClass());
			String[] setter = metaClass.getSetterNames();
			for ( String set : setter ) {
				// 不保留这些属性
				if ( !setProperty.contains(set) ) {
					metaClass.getSetInvoker(set).invoke(object, new Object[ ] { null });
				}
			}
		} catch ( Exception e ) {
			logger.error("", e);
		}
	}


	/**
	 * 对象数据拷贝,可以全部赋值,包括null
	 * <p>
	 *
	 * @param oldObj
	 * @param newObj
	 * @param isAll
	 */
	public static void copyProperty( Object oldObj, Object newObj, boolean isAll ) {
		if ( oldObj == null || newObj == null ) {
			throw new RuntimeException("对象不能为null");
		}
		try {
			MetaClass oldClass = MetaClass.forClass(oldObj.getClass());
			MetaClass newClass = MetaClass.forClass(newObj.getClass());
			String[] getter = oldClass.getGetterNames();
			String[] setter = newClass.getSetterNames();
			Set<String> getProperty = new HashSet<String>(Arrays.asList(getter));
			for ( String set : setter ) {
				if ( getProperty.contains(set) ) {
					Object result = oldClass.getGetInvoker(set).invoke(oldObj, null);
					if ( isAll ) {
						newClass.getSetInvoker(set).invoke(newObj, new Object[ ] { result });
					} else {
						if ( result != null ) {
							newClass.getSetInvoker(set).invoke(newObj, new Object[ ] { result });
						}
					}

				}
			}
		} catch ( Exception e ) {
			logger.error("", e);
		}
	}


	/**
	 * 数据拷贝,默认只拷贝不是null的值,只要有相同属性就可以进行赋值操作
	 * <p>
	 *
	 * @param oldObj
	 * @param newObj
	 */
	public static void copyProperty( Object oldObj, Object newObj ) {
		copyProperty(oldObj, newObj, false);
	}


	public static void main( String[] args ) {

	}
}

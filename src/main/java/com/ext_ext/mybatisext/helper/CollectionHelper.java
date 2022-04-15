package com.ext_ext.mybatisext.helper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.reflection.MetaClass;
import org.springframework.util.ReflectionUtils;


/**
 * <p>

 * @author   宋汝波
 * @date	 2015年8月20日 
 * @version  1.0.0	 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class CollectionHelper {


	public static <K, V> Map<K, V> list2Map( List<V> list, String property ) {
		if ( list == null ) {
			return Collections.emptyMap();
		}
		Map<K, V> map = new LinkedHashMap<K, V>(list.size());
		try {
			for ( V v : list ) {
				if ( Map.class.isAssignableFrom(v.getClass()) ) {
					Map m = (Map) v;
					map.put((K) m.get(property), v);
					continue;
				}
				MetaClass meta = MetaClass.forClass(v.getClass());
				Object fieldValue = meta.getGetInvoker(property).invoke(v, new Object[ ] {});
				map.put((K) fieldValue, v);
			}
		} catch ( Exception e ) {
			throw new RuntimeException(e);
		}
		return map;
	}


	public static <T> Set<T> list2Set( List<?> list, String property ) {
		if ( list == null ) {
			return Collections.emptySet();
		}
		Set<T> set = new HashSet<T>(list.size());
		for ( Object v : list ) {
			Field field = ReflectionUtils.findField(v.getClass(), property);
			ReflectionUtils.makeAccessible(field);
			Object fieldValue = ReflectionUtils.getField(field, v);
			set.add((T) fieldValue);
		}
		return set;
	}


	public static <K, V> Map<K, List<V>> list2MapList( List<V> list, String property ) {
		if ( list == null ) {
			return Collections.emptyMap();
		}
		Map<K, List<V>> map = new LinkedHashMap<K, List<V>>(list.size());
		for ( V v : list ) {
			Field field = ReflectionUtils.findField(v.getClass(), property);
			ReflectionUtils.makeAccessible(field);
			Object fieldValue = ReflectionUtils.getField(field, v);
			List<V> lst = map.get(fieldValue);
			if ( lst == null ) {
				lst = new ArrayList<V>();
				map.put((K) fieldValue, lst);
			}
			lst.add(v);
		}
		return map;
	}


	public static <V> List<V> listProperty( List<?> list, String property ) {
		if ( list == null ) {
			return Collections.emptyList();
		}
		List<V> result = new ArrayList<V>();
		for ( Object v : list ) {
			Field field = ReflectionUtils.findField(v.getClass(), property);
			ReflectionUtils.makeAccessible(field);
			Object fieldValue = ReflectionUtils.getField(field, v);
			result.add((V) fieldValue);
		}
		return result;
	}


	public static String join( Collection<String> collection, String dot ) {
		StringBuilder str = new StringBuilder();
		Iterator<String> iterator = collection.iterator();
		boolean addDot = false;
		while ( iterator.hasNext() ) {
			if ( addDot ) {
				str.append(dot);
			}
			String value = iterator.next();
			if ( value != null ) {
				str.append(value);
				addDot = true;
			}
		}
		return str.toString();

	}


}

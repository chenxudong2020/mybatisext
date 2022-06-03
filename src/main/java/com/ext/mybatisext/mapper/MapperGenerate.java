package com.ext.mybatisext.mapper;

import com.ext.mybatisext.activerecord.config.ColumnsMapping;
import com.ext.mybatisext.activerecord.config.MybatisVersionAdaptorWrapper;
import com.ext.mybatisext.annotation.Column;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.session.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.ReflectionUtils;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.*;


/**
 * <p>

 * @author   宋汝波
 * @date	 2015年2月5日 
 * @version  1.0.0	 
 */
public class MapperGenerate {

	protected static Log logger = LogFactory.getLog(ColumnsMapping.class);

	private final String tableName;

	private final String beanName;

	private final Configuration configuration;

	private final Class<?> beanClass;

	private final Class<?> mapperClass;

	private final String idName;


	public MapperGenerate(
			Configuration configuration,
			Class<?> beanClass,
			Class<?> mapperClass,
			String tableName,
			String idName ) {
		this.configuration = configuration;
		this.beanClass = beanClass;
		this.tableName = tableName;
		this.mapperClass = mapperClass;
		this.idName = idName;
		beanName = beanClass.getName();
	}


	private Map<String, String> getColumnAndType() {
		LinkedHashMap<String, String> propertyType = new LinkedHashMap<String, String>();
		MetaClass metaClass = MybatisVersionAdaptorWrapper.forClass(beanClass);
		Set<String> propertySet = new HashSet<String>();
		String[] getter = metaClass.getGetterNames();
		for ( String property : getter ) {
			String lower = property.toLowerCase();

			if ( !propertySet.contains(lower) ) {

					propertyType.put(property, metaClass.getGetterType(property).getName());



			} else {
				logger.warn(beanName + "存在相同属性:" + property + ",请查看getter方法命名");
			}
		}
		return propertyType;
	}


	protected List<String> matchIndex( Map<String, List<String>> indexMap, List<String> columns ) {
		if ( indexMap == null ) {
			return columns;
		}
		List<String> indexColumn = null;
		int maxMatch = 0;
		for ( Map.Entry<String, List<String>> indexEntry : indexMap.entrySet() ) {
			List<String> indexSet = indexEntry.getValue();//.contains(o);
			if ( indexSet.size() > maxMatch ) {
				maxMatch = indexSet.size();
				indexColumn = indexSet;
			}
		}
		if ( indexColumn != null ) {
			List<String> newColumn = new ArrayList<String>(indexColumn);
			//重新组织列,以适合where查询
			for ( String column : columns ) {
				if ( !indexColumn.contains(column) ) {
					newColumn.add(column);
				}
			}
			return newColumn;
		}
		return columns;
	}


	public void build() {
		try {
			String mapperStr = buildMapperXml();
			ByteArrayResource resourse = new ByteArrayResource(mapperStr.getBytes("UTF-8"));
			XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(resourse.getInputStream(), configuration, beanName
					+ ".commonMapper", configuration.getSqlFragments());
			xmlMapperBuilder.parse();
		} catch ( Exception e ) {
			throw new RuntimeException("Failed to parse mapping resource", e);
		}
	}


	private String buildMapperXml() throws Exception {
		String namespace = mapperClass.getName();
		StringWriter bw = new StringWriter();
		bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		bw.write("\r\n");
		bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" ");
		bw.write("\r\n");
		bw.write("    \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
		bw.write("\r\n");
		bw.write("<mapper namespace=\"" + namespace + "\">");
		bw.write("\r\n");
		bw.write("\r\n");
		buildSQL(bw);
		bw.write("</mapper>");
		bw.flush();
		bw.close();

		return bw.toString();
	}


	private String selectById( String id, List<String> columns ) {
		int size = columns.size();
		StringWriter writer = new StringWriter();
		// 查询（根据主键ID查询）
		writer.write("\t<!-- 查询（根据主键ID查询） -->");
		writer.write("\r\n");
		String resultMapName=String.format("%s%s",tableName,"Map");
		writer.write("\t<select id=\"" + id + "\" resultMap=\"" + resultMapName + "\">");
		writer.write("\r\n");
		writer.write("\t\t SELECT");
		writer.write("\r\n");

		for ( int i = 0 ; i < size ; i++ ) {
			if ( i != 0 ) {
				writer.write(",");
			}
			writer.write("\t" + this.getColumn(columns.get(i)));
		}


		writer.write("\r\n");
		writer.write("\t\t FROM " + tableName);
		writer.write("\r\n");
		writer.write("\t\t WHERE " + idName + " = #{" + idName + "}");
		writer.write("\r\n");
		writer.write("\t</select>");
		writer.write("\r\n");
		writer.write("\r\n");
		// 查询完
		return writer.toString();
	}


	private String deleteById( String id ) {
		StringWriter writer = new StringWriter();
		// 删除（根据主键ID删除）
		writer.write("\t<!--删除：根据主键ID删除-->");
		writer.write("\r\n");
		writer.write("\t<delete id=\"" + id + "\">");
		writer.write("\r\n");
		writer.write("\t\t DELETE FROM " + tableName);
		writer.write("\r\n");
		writer.write("\t\t WHERE " + idName + " = #{" + idName + "}");
		writer.write("\r\n");
		writer.write("\t</delete>");
		writer.write("\r\n");
		writer.write("\r\n");
		// 删除完
		return writer.toString();
	}

	private String getColumn(String property){

		Field field=ReflectionUtils.findField(beanClass,property);
		if(field!=null){
			field.setAccessible(true);
		}
		if(field.isAnnotationPresent(Column.class)){
			Column column=field.getAnnotation(Column.class);
			return column.value();
		}
		return property;
	}

	private void writeResultMap( List<String> columns,StringWriter bw){
		int size = columns.size();
		String resultMapName=String.format("%s%s",tableName,"Map");
		bw.write("\t<resultMap id=\"" + resultMapName + "\"");
		bw.write(" type=\"" + beanClass.getName() + "\">");
		bw.write("\r\n");
		String tempField = null;
		for ( int i = 0 ; i < size ; i++ ) {
			tempField = columns.get(i);
			bw.write("<result column=\"" + this.getColumn(tempField) + "\" property=\"" + tempField + "\" />");
			bw.write("\r\n");
		}
       bw.write("</resultMap>");

	}




	private String insert( String id2, List<String> columns ) {
		int size = columns.size();
		StringWriter bw = new StringWriter();

		//---------------  insert方法（匹配有值的字段）
		bw.write("\t<!-- 添加 （匹配有值的字段）-->");
		bw.write("\r\n");
		bw.write("\t<insert id=\"" + id2 + "\">");
		bw.write("\r\n");
		bw.write("\t\t INSERT INTO " + tableName);
		bw.write("\r\n");
		bw.write("\t\t <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\" >");
		bw.write("\r\n");

		String tempField = null;
		for ( int i = 0 ; i < size ; i++ ) {
			tempField = columns.get(i);
			bw.write("\t\t\t<if test=\"" + tempField + " != null\">");
			bw.write("\r\n");

			bw.write("\t\t\t\t " + this.getColumn(tempField) + ",");
			bw.write("\r\n");
			bw.write("\t\t\t</if>");
			bw.write("\r\n");
		}

		bw.write("\r\n");
		bw.write("\t\t </trim>");
		bw.write("\r\n");

		bw.write("\t\t <trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\" >");
		bw.write("\r\n");

		tempField = null;
		for ( int i = 0 ; i < size ; i++ ) {
			tempField = columns.get(i);
			bw.write("\t\t\t<if test=\"" + tempField + "!=null\">");
			bw.write("\r\n");
			bw.write("\t\t\t\t #{" + tempField + "},");
			bw.write("\r\n");
			bw.write("\t\t\t</if>");
			bw.write("\r\n");
		}

		bw.write("\t\t </trim>");
		bw.write("\r\n");
		bw.write("\t</insert>");
		bw.write("\r\n");
		bw.write("\r\n");
		//---------------  完毕

		return bw.toString();
	}


	private String update( String id2, List<String> columns ) {
		int size = columns.size();
		String tempField = null;
		StringWriter bw = new StringWriter();
		bw.write("\t<!-- 修 改-->");
		bw.write("\r\n");
		bw.write("\t<update id=\"" + id2 + "\">");
		bw.write("\r\n");
		bw.write("\t\t UPDATE " + tableName);
		bw.write("\r\n");
		bw.write(" \t\t <set> ");
		bw.write("\r\n");


		for ( int i = 0 ; i < size ; i++ ) {
			tempField = columns.get(i);
			bw.write("\t\t\t<if test=\"" + tempField + " != null\">");
			bw.write("\r\n");
			bw.write("\t\t\t\t " +this.getColumn(tempField)  + " = #{" + tempField + "},");
			bw.write("\r\n");
			bw.write("\t\t\t</if>");
			bw.write("\r\n");
		}

		bw.write("\r\n");
		bw.write(" \t\t </set>");
		bw.write("\r\n");
		bw.write("\t\t WHERE " + idName + " = #{" + idName + "}");
		bw.write("\r\n");
		bw.write("\t</update>");
		bw.write("\r\n");
		bw.write("\r\n");


		return bw.toString();
	}


	private void buildSQL( StringWriter bw ) throws Exception {
		Map<String, String> property = getColumnAndType();
		List<String> columns = new ArrayList<String>();
		List<String> types = new ArrayList<String>();
		for ( Map.Entry<String, String> entry : property.entrySet() ) {
			columns.add(entry.getKey());
			types.add(entry.getValue());
		}
		bw.write("\r\n");
		bw.write("\r\n");

		this.writeResultMap(columns,bw);

		//查询
		String selectOne = selectById("selectById", columns);
		String selectBatch = selectById("selectByIdBatch", columns);
		bw.write(selectOne);
		bw.write(selectBatch);
		//删除
		String deleteOne = deleteById("deleteById");
		String deleteBatch = deleteById("deleteByIdBatch");
		String deleteSelective = deleteList("deleteSelective", columns);
		bw.write(deleteOne);
		bw.write(deleteBatch);
		bw.write(deleteSelective);

		//插入
		String insertOne = insert("insertSelective", columns);
		String insertBatch = insert("insertSelectiveBatch", columns);
		bw.write(insertOne);
		bw.write(insertBatch);
		//更新
		String updateOne = update("updateByIdSelective", columns);
		String updateBatch = update("updateByIdSelectiveBatch", columns);
		bw.write(updateOne);
		bw.write(updateBatch);

		//拼装按条件查询,自动匹配索引列
		String selectList = selectListAndOne("selectList", false, columns);
		String select = selectListAndOne("selectOne", true, columns);

		bw.write(selectList);
		bw.write(select);

		String count = countSelect("count", columns);
		bw.write(count);
		bw.write("\r\n");
	}


	private String selectListAndOne( String id, boolean isOne, List<String> newColumns ) {

		int size = newColumns.size();
		StringWriter writer = new StringWriter();
		// 查询（根据主键ID查询）
		writer.write("\r\n");
		String resultMapName=String.format("%s%s",tableName,"Map");
		writer.write("\t<select id=\"" + id + "\" resultMap=\"" + resultMapName + "\">");
		writer.write("\r\n");
		writer.write("\t\t SELECT");
		writer.write("\r\n");

		for ( int i = 0 ; i < size ; i++ ) {
			if ( i != 0 ) {
				writer.write(",");
			}
			writer.write("\t" + this.getColumn(newColumns.get(i)));
		}


		writer.write("\r\n");
		writer.write("\t\t FROM " + tableName);
		writer.write("\r\n");
		writer.write("\t\t <where>\r\n");

		String tempField = null;
		for ( int i = 0 ; i < size ; i++ ) {
			tempField = newColumns.get(i);
			writer.write("\t\t\t<if test=\"record." + tempField + "!=null\">\r\n");
			writer.write("\t\t\t AND " + this.getColumn(tempField) + "=#{record." + tempField + "}\r\n");
			writer.write("\t\t\t</if>\r\n");
		}

		writer.write("\t\t </where>\r\n");


		//排序加入
		if ( !isOne ) {
			writer.write("<if test=\"ordered!=null and ordered!=''\">");
			writer.write(" ORDER BY ${ordered}");
			writer.write("</if>");
		}

		//if ( isOne ) {
		//	writer.write("\t\t limit 1\r\n");
		//}
		writer.write("\t</select>");
		writer.write("\r\n");
		writer.write("\r\n");
		// 查询完
		return writer.toString();
	}


	private String deleteList( String id, List<String> newColumns ) {

		int size = newColumns.size();
		StringWriter writer = new StringWriter();
		// 查询（根据主键ID查询）
		writer.write("\r\n");
		writer.write("\t<delete id=\"" + id + "\" >");
		writer.write("\r\n");
		writer.write("\t\t DELETE ");

		writer.write("\r\n");
		writer.write("\t\t FROM " + tableName);
		writer.write("\r\n");
		writer.write("\t\t <where>\r\n");

		String tempField = null;
		for ( int i = 0 ; i < size ; i++ ) {
			tempField = newColumns.get(i);
			writer.write("\t\t\t<if test=\"" + tempField + "!=null\">\r\n");
			writer.write("\t\t\t AND " + this.getColumn(tempField) + "=#{" + tempField + "}\r\n");
			writer.write("\t\t\t</if>\r\n");
		}

		writer.write("\t\t </where>\r\n");
		writer.write("\t</delete>");
		writer.write("\r\n");
		writer.write("\r\n");
		// 查询完
		return writer.toString();
	}


	private String countSelect( String id, List<String> newColumns ) {

		int size = newColumns.size();
		StringWriter writer = new StringWriter();
		// 查询（根据主键ID查询）
		writer.write("\r\n");
		writer.write("\t<select id=\"" + id + "\" resultType=\"int\">");
		writer.write("\r\n");
		writer.write("\t\t SELECT count(1) ");
		writer.write("\r\n");
		writer.write("\t\t FROM " + tableName);
		writer.write("\r\n");
		writer.write("\t\t <where>\r\n");

		String tempField = null;
		for ( int i = 0 ; i < size ; i++ ) {
			tempField = newColumns.get(i);
			writer.write("\t\t\t<if test=\"" + tempField + "!=null\">\r\n");
			writer.write("\t\t\t AND " + this.getColumn(tempField) + "=#{" + tempField + "}\r\n");
			writer.write("\t\t\t</if>\r\n");
		}

		writer.write("\t\t </where>\r\n");
		writer.write("\t</select>");
		writer.write("\r\n");
		writer.write("\r\n");
		// 查询完
		return writer.toString();
	}
}

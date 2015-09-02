package com.ext_ext.mybatisext.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 自动生成MyBatis的实体类、实体映射XML文件、Mapper
 * <p>
 *
 * @date	 2015-5-8
 * @version  V1.0.0
 */
@SuppressWarnings("hiding")
public class AutoEntityUtil {

	private static final Logger logger = LoggerFactory.getLogger(AutoEntityUtil.class);


	/**
	 ********************************** 使用前必读*********************
	 **															 **
	 ** 使用前请将moduleName更改为自己模块的名称即可（一般情况下与数据库名一致），其他无须改动     **
	 **															 **
	 **************************************************************
	 */
	private final String type_char = "char";

	private final String type_date = "date";

	private final String type_timestamp = "timestamp";

	private final String type_int = "int";

	private final String type_bigint = "bigint";

	private final String type_text = "text";

	private final String type_bit = "bit";

	private final String type_decimal = "decimal";

	private final String type_blob = "blob";

	private final String type_float = "float";

	private final String type_double = "double";

	/**
	 * MYSQL数据库实例名 对应模块名称（根据自己模块做相应调整!!!务必修改^_^）
	 */
	public static String moduleName = "vvvv";

	private final String bean_path = "d:/" + moduleName + "/entity_bean";

	private final String mapper_path = "d:/" + moduleName + "/entity_mapper";

	private final String xml_path = "d:/" + moduleName + "/entity_mapper/xml";

	/**
	 * 代码目录结构 例如 springmvc 生成结构为: springmvc.entity.User
	 */
	private final String package_path = "net.vvvvv." + moduleName;

	private final String bean_package = package_path + ".entity";

	private final String mapper_package = package_path + ".mapper";

	private final String driverName = "com.mysql.jdbc.Driver";

	private final String user = "vvvvvvv";

	private final String password = "vvvvvv";

	private final String url = "jdbc:mysql://192.168.1.251:3306/" + moduleName + "?characterEncoding=utf8";

	private String tableName = null;

	private String beanName = null;

	private String mapperName = null;

	private Connection conn = null;


	/**
	 * 使用注意： moduleName 数据库实例名 package_path 代码目录结构
	 */
	public static void main( String[] args ) {
		try {
			// 创建目录
			Runtime.getRuntime().exec("cmd /c mkdir D:\\" + AutoEntityUtil.moduleName);

			// 自动生成代码
			new AutoEntityUtil().generate();

			// 自动打开生成文件的目录
			Runtime.getRuntime().exec("cmd /c start explorer D:\\" + AutoEntityUtil.moduleName);
		} catch ( ClassNotFoundException e ) {
			logger.error(e.getMessage(), e);
		} catch ( SQLException e ) {
			logger.error(e.getMessage(), e);
		} catch ( IOException e ) {
			logger.error(e.getMessage(), e);
		}
	}


	private void init() throws ClassNotFoundException, SQLException {
		Class.forName(driverName);
		conn = DriverManager.getConnection(url, user, password);
	}


	/**
	 * 获取所有的表
	 *
	 * @return
	 * @throws SQLException
	 */
	private List<String> getTables() throws SQLException {
		List<String> tables = new ArrayList<String>();
		PreparedStatement pstate = conn.prepareStatement("show tables");
		ResultSet results = pstate.executeQuery();
		while ( results.next() ) {
			String tableName = results.getString(1);
			// if ( tableName.toLowerCase().startsWith("yy_") ) {
			tables.add(tableName);
			// }
		}
		return tables;
	}


	private void processTable( String table ) {
		StringBuffer sb = new StringBuffer(table.length());
		String tableName = table.toLowerCase();
		String[] tables = tableName.split("_");
		String temp = null;
		for ( int i = 1 ; i < tables.length ; i++ ) {
			temp = tables[i].trim();
			sb.append(temp.substring(0, 1).toUpperCase()).append(temp.substring(1));
		}
		beanName = sb.toString();
		mapperName = beanName + "Mapper";
	}


	private String processType( String type ) {
		if ( type.indexOf(type_char) > -1 ) {
			return "String";
		} else if ( type.indexOf(type_bigint) > -1 ) {
			return "Long";
		} else if ( type.indexOf(type_int) > -1 ) {
			return "Integer";
		} else if ( type.indexOf(type_date) > -1 ) {
			return "java.util.Date";
		} else if ( type.indexOf(type_text) > -1 ) {
			return "String";
		} else if ( type.indexOf(type_timestamp) > -1 ) {
			return "java.util.Date";
		} else if ( type.indexOf(type_bit) > -1 ) {
			return "Boolean";
		} else if ( type.indexOf(type_decimal) > -1 ) {
			return "java.math.BigDecimal";
		} else if ( type.indexOf(type_blob) > -1 ) {
			return "byte[]";
		} else if ( type.indexOf(type_float) > -1 ) {
			return "Float";
		} else if ( type.indexOf(type_double) > -1 ) {
			return "Double";
		}
		return null;
	}


	private String processField( String field ) {
		StringBuffer sb = new StringBuffer(field.length());
		// field = field.toLowerCase();
		String[] fields = field.split("_");
		String temp = null;
		sb.append(fields[0]);
		for ( int i = 1 ; i < fields.length ; i++ ) {
			temp = fields[i].trim();
			sb.append(temp.substring(0, 1).toUpperCase()).append(temp.substring(1));
		}
		return sb.toString();
	}


	/**
	 * 构建类上面的注释
	 *
	 * @param bw
	 * @param text
	 * @return
	 * @throws IOException
	 */
	private BufferedWriter buildClassComment( BufferedWriter bw, String text ) throws IOException {
		bw.newLine();
		bw.newLine();
		bw.write("/**");
		bw.newLine();
		bw.write(" *");
		bw.newLine();
		bw.write(" * " + text);
		bw.newLine();
		bw.write(" *");
		bw.newLine();
		bw.write(" */");
		return bw;
	}


	/**
	 * 生成实体类
	 *
	 * @param columns
	 * @param types
	 * @param comments
	 * @throws IOException
	 */
	private void buildEntityBean( List<String> columns, List<String> types, List<String> comments, String tableComment )
		throws IOException {
		File folder = new File(bean_path);
		if ( !folder.exists() ) {
			folder.mkdir();
		}

		File beanFile = new File(bean_path, beanName + ".java");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(beanFile)));
		bw.write("package " + bean_package + ";");
		bw.newLine();
		bw.newLine();
		bw.write("import java.io.Serializable;");
		bw = buildClassComment(bw, tableComment);
		bw.newLine();
		bw.write("public class " + beanName + " implements Serializable {");
		bw.newLine();
		bw.newLine();
		bw.write("\tprivate static final long serialVersionUID = 1L;");
		bw.newLine();
		int size = columns.size();
		for ( int i = 0 ; i < size ; i++ ) {
			bw.newLine();
			bw.write("\t/** " + comments.get(i) + " */");
			bw.newLine();
			bw.write("\tprivate " + processType(types.get(i)) + " " + processField(columns.get(i)) + ";");
			bw.newLine();
		}
		// 生成get 和 set方法
		String tempField = null;
		String _tempField = null;
		String tempType = null;
		for ( int i = 0 ; i < size ; i++ ) {
			tempType = processType(types.get(i));
			_tempField = processField(columns.get(i));
			tempField = _tempField.substring(0, 1).toUpperCase() + _tempField.substring(1);
			bw.newLine();
			bw.newLine();
			// bw.write("\tpublic void set" + tempField + "(" + tempType + " _"
			// + _tempField + "){");
			bw.write("\tpublic void set" + tempField + "(" + tempType + " " + _tempField + ") {");
			bw.newLine();
			// bw.write("\t\tthis." + _tempField + "=_" + _tempField + ";");
			bw.write("\t\tthis." + _tempField + " = " + _tempField + ";");
			bw.newLine();
			bw.write("\t}");
			bw.newLine();
			bw.newLine();
			bw.newLine();
			bw.write("\tpublic " + tempType + " get" + tempField + "() {");
			bw.newLine();
			bw.write("\t\treturn this." + _tempField + ";");
			bw.newLine();
			bw.write("\t}");
			bw.newLine();
		}
		bw.newLine();
		bw.write("}");
		bw.newLine();
		bw.flush();
		bw.close();
	}


	/**
	 * 构建Mapper文件
	 *
	 * @throws IOException
	 */
	private void buildMapper( String tableName ) throws IOException {
		File folder = new File(mapper_path);
		if ( !folder.exists() ) {
			folder.mkdirs();
		}

		File mapperFile = new File(mapper_path, mapperName + ".java");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperFile), "utf-8"));
		bw.write("package " + mapper_package + ";");
		bw.newLine();
		bw.newLine();
		bw.write("import " + bean_package + "." + beanName + ";");
		bw.newLine();
		bw.write("import cn.vko.mybatis.annotation.TableName;");
		bw.newLine();
		bw.write("import com.ext_ext.mybatisext.activerecord.Table;");
		bw.newLine();


		bw = buildClassComment(bw, mapperName + "数据库操作接口类");
		bw.newLine();
		bw.write("@TableName(table = \"" + tableName + "\", entity = " + beanName + ".class)");
		bw.newLine();
		bw.write("public interface " + mapperName + " extends Table<" + beanName + ",Long>{");
		bw.newLine();
		bw.newLine();

		// ----------定义Mapper中的方法End----------
		bw.newLine();
		bw.write("}");
		bw.flush();
		bw.close();
	}


	/**
	 * 构建实体类映射XML文件
	 *
	 * @param columns
	 * @param types
	 * @param comments
	 * @throws IOException
	 */
	private void buildMapperXml( List<String> columns, List<String> types, List<String> comments ) throws IOException {
		File folder = new File(xml_path);
		if ( !folder.exists() ) {
			folder.mkdirs();
		}

		File mapperXmlFile = new File(xml_path, mapperName + ".xml");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperXmlFile)));
		bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		bw.newLine();
		bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
		bw.newLine();
		bw.write("<mapper namespace=\"" + mapper_package + "." + mapperName + "\">");
		bw.newLine();
		bw.newLine();


		// 下面开始写SqlMapper中的方法
		buildSQL(bw, columns);

		bw.write("</mapper>");
		bw.flush();
		bw.close();
	}


	private void buildSQL( BufferedWriter bw, List<String> columns ) throws IOException {
		int size = columns.size();
		// 通用结果列
		bw.write("\t<!-- 通用查询结果列-->");
		bw.newLine();
		bw.write("\t<sql id=\"Base_Column_List\">");
		bw.newLine();

		bw.write("\t\t id,");
		for ( int i = 1 ; i < size ; i++ ) {
			bw.write(" " + columns.get(i));
			if ( i != size - 1 ) {
				bw.write(",");
			}
		}

		bw.newLine();
		bw.write("\t</sql>");
		bw.newLine();
		bw.newLine();

	}


	/**
	 * 获取所有的数据库表注释
	 *
	 * @return
	 * @throws SQLException
	 */
	private Map<String, String> getTableComment() throws SQLException {
		Map<String, String> maps = new HashMap<String, String>();
		PreparedStatement pstate = conn.prepareStatement("show table status");
		ResultSet results = pstate.executeQuery();
		while ( results.next() ) {
			String tableName = results.getString("NAME");
			String comment = results.getString("COMMENT");
			maps.put(tableName, comment);
		}
		return maps;
	}


	public void generate() throws ClassNotFoundException, SQLException, IOException {
		init();
		String prefix = "show full fields from ";
		List<String> columns = null;
		List<String> types = null;
		List<String> comments = null;
		PreparedStatement pstate = null;
		List<String> tables = getTables();
		Map<String, String> tableComments = getTableComment();
		for ( String table : tables ) {
			columns = new ArrayList<String>();
			types = new ArrayList<String>();
			comments = new ArrayList<String>();
			pstate = conn.prepareStatement(prefix + table);
			ResultSet results = pstate.executeQuery();
			while ( results.next() ) {
				columns.add(results.getString("FIELD"));
				types.add(results.getString("TYPE"));
				comments.add(results.getString("COMMENT"));
			}
			tableName = table;
			processTable(table);
			// this.outputBaseBean();
			String tableComment = tableComments.get(tableName);
			buildEntityBean(columns, types, comments, tableComment);
			buildMapper(tableName);
			buildMapperXml(columns, types, comments);
		}
		conn.close();
	}
}

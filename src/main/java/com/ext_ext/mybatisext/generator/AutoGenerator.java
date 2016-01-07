package com.ext_ext.mybatisext.generator;

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

/**
 * 
 * 映射文件自动生成类
 * 
 * @author hubin
 *
 */
public class AutoGenerator {

	private ConfigGenerator config;


	public ConfigGenerator getConfig() {
		return config;
	}


	public void setConfig( ConfigGenerator config ) {
		this.config = config;
	}


	public AutoGenerator() {

	}

	public AutoGenerator(ConfigGenerator config) {
		this.config = config;
	}
	
	/**
	 * run 执行
	 */
	public static void run(ConfigGenerator config) {
		if ( config == null ) {
			try {
				throw new Exception(" ConfigGenerator is null. ");
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		
		/**
		 * 检查文件夹是否存在
		 */
		File gf = new File(config.getSaveDir());
		if ( !gf.exists() ) {
			gf.mkdirs();
		}
		
		/**
		 * 开启生成映射关系
		 */
		new AutoGenerator(config).generate();

		
		/**
		 * 自动打开生成文件的目录
		 * <p>
		 * 根据 osName 执行相应命令
		 * </p>
		 */
		try {
			String osName = System.getProperty("os.name");
			if ( osName != null ) {
				if (osName.contains("Mac")) {
					Runtime.getRuntime().exec("open " + config.getSaveDir());
				} else if (osName.contains("Windows")) {
					Runtime.getRuntime().exec("cmd /c start " + config.getSaveDir());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(" generate success! ");
		
	}
	
	

	/**
	 * 生成映射文件
	 * 
	 * @param includePrefix
	 *            是否包含前缀，例如: tb_xxx 其中 tb_ 为前缀
	 */
	public void generate() {
		Connection conn = null;
		try {
			/**
			 * 创建连接
			 */
			Class.forName(config.getDbDriverName());
			conn = DriverManager.getConnection(config.getDbUrl(), config.getDbUser(), config.getDbPassword());

			/**
			 * 获取数据库表相关信息
			 */
			List<String> tables = getTables(conn);
			Map<String, String> tableComments = getTableComment(conn);
			for (String table : tables) {
				List<String> columns = new ArrayList<String>();
				List<String> types = new ArrayList<String>();
				List<String> comments = new ArrayList<String>();
				ResultSet results = conn.prepareStatement("show full fields from " + table).executeQuery();
				while (results.next()) {
					columns.add(results.getString("FIELD"));
					types.add(results.getString("TYPE"));
					comments.add(results.getString("COMMENT"));
				}

				String beanName = getBeanName(table, config.isDbPrefix());
				String mapperName = beanName + "Mapper";

				/**
				 * 生成映射文件
				 */
				buildEntityBean(columns, types, comments, tableComments.get(table), beanName);
				buildMapper(table, beanName, mapperName);
				buildMapperXml(columns, types, comments, mapperName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取所有的表
	 *
	 * @return
	 * @throws SQLException
	 */
	private List<String> getTables(Connection conn) throws SQLException {
		List<String> tables = new ArrayList<String>();
		PreparedStatement pstate = conn.prepareStatement("show tables");
		ResultSet results = pstate.executeQuery();
		while (results.next()) {
			tables.add(results.getString(1));
		}
		return tables;
	}

	/**
	 * 生成 beanName
	 * 
	 * @param table
	 *            表名
	 * @return beanName
	 */
	private String getBeanName(String table, boolean includePrefix) {
		StringBuffer sb = new StringBuffer();
		if (table.contains("_")) {
			String[] tables = table.split("_");
			int l = tables.length;
			int s = 0;
			if (includePrefix) {
				s = 1;
			}
			for (int i = s; i < l; i++) {
				String temp = tables[i].trim();
				sb.append(temp.substring(0, 1).toUpperCase()).append(temp.substring(1));
			}
		} else {
			sb.append(table.substring(0, 1).toUpperCase()).append(table.substring(1));
		}
		return sb.toString();
	}

	/**
	 * 字段类型转换
	 * 
	 * @param type
	 *            字段类型
	 * @return
	 */
	private String processType(String type) {
		if (type.indexOf("char") > -1) {
			return "String";
		} else if (type.indexOf("bigint") > -1) {
			return "Long";
		} else if (type.indexOf("int") > -1) {
			return "Integer";
		} else if (type.indexOf("date") > -1) {
			return "java.util.Date";
		} else if (type.indexOf("text") > -1) {
			return "String";
		} else if (type.indexOf("timestamp") > -1) {
			return "java.util.Date";
		} else if (type.indexOf("bit") > -1) {
			return "Boolean";
		} else if (type.indexOf("decimal") > -1) {
			return "java.math.BigDecimal";
		} else if (type.indexOf("blob") > -1) {
			return "byte[]";
		} else if (type.indexOf("float") > -1) {
			return "Float";
		} else if (type.indexOf("double") > -1) {
			return "Double";
		}
		return null;
	}

	private String processField(String field) {
		StringBuffer sb = new StringBuffer(field.length());
		String[] fields = field.split("_");
		String temp = null;
		sb.append(fields[0]);
		for (int i = 1; i < fields.length; i++) {
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
	private BufferedWriter buildClassComment(BufferedWriter bw, String text) throws IOException {
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
	 * 
	 * 生成文件地址
	 * 
	 * @param segment
	 *            文件地址片段
	 * @return
	 */
	private String getFileName(String segment) {
		File folder = new File(config.getSaveDir() + segment);
		if (!folder.exists()) {
			folder.mkdir();
		}
		return folder.getPath();
	}

	/**
	 * 
	 * 生成实体类
	 *
	 * @param columns
	 * @param types
	 * @param comments
	 * @throws IOException
	 */
	private void buildEntityBean(List<String> columns, List<String> types, List<String> comments, String tableComment,
			String beanName) throws IOException {
		File beanFile = new File(this.getFileName("entity"), beanName + ".java");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(beanFile)));
		bw.write("package " + config.getEntityPackage() + ";");
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
		for (int i = 0; i < size; i++) {
			bw.newLine();
			bw.write("\t/** " + comments.get(i) + " */");
			bw.newLine();
			bw.write("\tprivate " + processType(types.get(i)) + " " + processField(columns.get(i)) + ";");
			bw.newLine();
		}
		
		/*
		 * 生成get 和 set方法
		 */
		for (int i = 0; i < size; i++) {
			String _tempType = processType(types.get(i));
			String _tempField = processField(columns.get(i));
			String _field = _tempField.substring(0, 1).toUpperCase() + _tempField.substring(1);
			bw.newLine();
			bw.newLine();
			bw.write("\tpublic void set" + _field + "(" + _tempType + " " + _tempField + ") {");
			bw.newLine();
			bw.write("\t\tthis." + _tempField + " = " + _tempField + ";");
			bw.newLine();
			bw.write("\t}");
			bw.newLine();
			bw.newLine();
			bw.newLine();
			bw.write("\tpublic " + _tempType + " get" + _field + "() {");
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
	 * 
	 * 构建Mapper文件
	 *
	 * @throws IOException
	 */
	private void buildMapper(String tableName, String beanName, String mapperName) throws IOException {
		File mapperFile = new File(this.getFileName("mapper"), mapperName + ".java");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperFile), "utf-8"));
		bw.write("package " + config.getMapperPackage() + ";");
		bw.newLine();
		bw.newLine();
		bw.write("import " + config.getEntityPackage() + "." + beanName + ";");
		bw.newLine();
		bw.write("import com.ext_ext.mybatisext.annotation.TableName;");
		bw.newLine();
		bw.write("import com.ext_ext.mybatisext.mapper.CommonMapper;");
		bw.newLine();

		bw = buildClassComment(bw, mapperName + "数据库操作接口类");
		bw.newLine();
		bw.write("@TableName(name = \"" + tableName + "\", type = " + beanName + ".class)");
		bw.newLine();
		bw.write("public interface " + mapperName + " extends CommonMapper<" + beanName + ">{");
		bw.newLine();
		bw.newLine();

		// ----------定义Mapper中的方法End----------
		bw.newLine();
		bw.write("}");
		bw.flush();
		bw.close();
	}

	/**
	 * 
	 * 构建实体类映射XML文件
	 *
	 * @param columns
	 * @param types
	 * @param comments
	 * @throws IOException
	 */
	private void buildMapperXml(List<String> columns, List<String> types, List<String> comments, String mapperName)
			throws IOException {
		File mapperXmlFile = new File(this.getFileName("xml"), mapperName + ".xml");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperXmlFile)));
		bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		bw.newLine();
		bw.write(
				"<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
		bw.newLine();
		bw.write("<mapper namespace=\"" + config.getMapperPackage() + "." + mapperName + "\">");
		bw.newLine();
		bw.newLine();

		/*
		 * 下面开始写SqlMapper中的方法
		 */
		buildSQL(bw, columns);

		bw.write("</mapper>");
		bw.flush();
		bw.close();
	}

	/**
	 * 
	 * 通用返回参数
	 * 
	 * @param bw
	 * @param columns
	 * @throws IOException
	 */
	private void buildSQL(BufferedWriter bw, List<String> columns) throws IOException {
		int size = columns.size();
		bw.write("\t<!-- 通用查询结果列-->");
		bw.newLine();
		bw.write("\t<sql id=\"Base_Column_List\">");
		bw.newLine();
		bw.write("\t\t id,");
		for (int i = 1; i < size; i++) {
			bw.write(" " + columns.get(i));
			if (i != size - 1) {
				bw.write(",");
			}
		}
		bw.newLine();
		bw.write("\t</sql>");
		bw.newLine();
		bw.newLine();
	}

	/**
	 * 
	 * 获取所有的数据库表注释
	 *
	 * @return
	 * @throws SQLException
	 */
	private Map<String, String> getTableComment(Connection conn) throws SQLException {
		Map<String, String> maps = new HashMap<String, String>();
		PreparedStatement pstate = conn.prepareStatement("show table status");
		ResultSet results = pstate.executeQuery();
		while (results.next()) {
			maps.put(results.getString("NAME"), results.getString("COMMENT"));
		}
		return maps;
	}

}

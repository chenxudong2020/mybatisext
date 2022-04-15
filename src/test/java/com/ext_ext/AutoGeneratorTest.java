package com.ext_ext;

import com.ext_ext.mybatisext.generator.AutoGenerator;
import com.ext_ext.mybatisext.generator.ConfigGenerator;

/**
 * 
 * 自动生成映射工具类测试
 * 
 * @author hubin
 *
 */
public class AutoGeneratorTest {

	/**
	 * 
	 * 测试 run 执行
	 * 
	 * <p>
	 * 配置方法查看 {@link ConfigGenerator}
	 * </p>
	 * 
	 */
	public static void main( String[] args ) {
		ConfigGenerator cg = new ConfigGenerator();
		cg.setEntityPackage("com.ext_ext.entity");
		cg.setMapperPackage("com.ext_ext.mapper");
		cg.setSaveDir("D:/ext_ext/");
		cg.setDbDriverName("com.mysql.jdbc.Driver");
		cg.setDbUser("root");
		cg.setDbPassword("");
		cg.setDbUrl("jdbc:mysql://127.0.0.1:3306/ext_ext?characterEncoding=utf8");
		cg.setDbPrefix(false);
		AutoGenerator.run(cg);
	}
	
}

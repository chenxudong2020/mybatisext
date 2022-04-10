package com.ext.mybatisext.environment;


public interface RunEnvironment {

	/**
	 * @author bo 本地,开发,测试,产品
	 */
	public enum Environment {
		LOCAL("local", "LOCAL"), DEVELOP("develop", "DEVELOP"), TEST("test", "TEST"), PRODUCT("product", "PRODUCT");

		String name;

		String code;


		private Environment( String name, String code ) {
			this.name = name;
			this.code = code;
		}


		public String getName() {
			return this.name;
		}


		public String getCode() {
			return this.code;
		}


		public boolean isLocal() {
			return EnvironmentDetect.LOCAL.equals(this.code);
		}


		public boolean isTest() {
			return EnvironmentDetect.TEST.equals(this.code);
		}


		public boolean isRelease() {
			return isTest() || isProduct();
		}


		public boolean isDevelop() {
			return EnvironmentDetect.DEVELOP.equals(this.code);
		}


		public boolean isProduct() {
			return EnvironmentDetect.PRODUCT.equals(this.code);
		}
	}


	public Environment getEnvironment();


	public RunConfig getRunConfig();
}

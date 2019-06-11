package com.ssx.maxwell;




public class EntityCodesInit {

	public static void main(String[] args) {
		GeneratePlugin generatePlugin = new GeneratePlugin();
		generatePlugin
				.setUrl("jdbc:mysql://192.168.225.1:3306/test?useUnicode=true&characterEncoding=UTF8&useSSL=false&allowMultiQueries=true&autoReconnect=true&failOverReadOnly=false&maxReconnects=10");
		generatePlugin.setUsername("root");
		generatePlugin.setPassword("root");
		generatePlugin.setBasePackage("com.ssx.maxwell.kafka.enjoy");

		buildTEST(generatePlugin);
		generatePlugin.execute();
	}
	static void buildTEST(GeneratePlugin generatePlugin){
		generatePlugin.setEntityPath("common.model.entity.test");
		generatePlugin.setMapperPath("mapper");
		generatePlugin.setXmlPath("mapper.impl");
		generatePlugin.setServicePath("service");
		generatePlugin.setServiceImplPath("service.impl");
		generatePlugin.setControllerPath("controller");
		generatePlugin.setIncludeTable(new String[]{"sys_order"});
	}

}

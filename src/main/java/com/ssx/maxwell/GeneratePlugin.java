package com.ssx.maxwell;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class GeneratePlugin {

	String projectPath = System.getProperty("user.dir") + "\\src\\main\\java";
	String basePackage;
	String entityPath = "common.model.entity";
	String mapperPath = "mapper";
	String xmlPath = mapperPath + "." + "impl";
	String servicePath = "service";
	String serviceImplPath = servicePath + "." + "impl";
	String controllerPath = "controller";
	String author = "shuaishuai.xiao";
	String[] tablePrefix = new String[]{}, includeTable, excludeTable;
	String url, username, password, driverName = "com.mysql.jdbc.Driver";

	public void execute() {

		if (StringUtils.isAnyBlank(basePackage, url, username, password)) {
			throw new NullPointerException("必要属性不能为空");
		}

		AutoGenerator mpg = new AutoGenerator();
		mpg.setTemplateEngine(new FreemarkerTemplateEngine());

		GlobalConfig gc = new GlobalConfig();
		gc.setOutputDir(projectPath);
		gc.setFileOverride(true);
		gc.setActiveRecord(false);
		gc.setEnableCache(false);
		gc.setBaseResultMap(true);
		gc.setBaseColumnList(true);
		gc.setAuthor(author);
		gc.setServiceName("%sService");
		mpg.setGlobalConfig(gc);

		DataSourceConfig dsc = new DataSourceConfig();
		dsc.setDbType(DbType.MYSQL);
		dsc.setTypeConvert(new MySqlTypeConvert() {
			@Override
			public DbColumnType processTypeConvert(String fieldType) {
				return super.processTypeConvert(fieldType);
			}
		});
		dsc.setDriverName(driverName);
		dsc.setUsername(username);
		dsc.setPassword(password);
		dsc.setUrl(url);
		mpg.setDataSource(dsc);

		StrategyConfig strategy = new StrategyConfig();
		strategy.setEntityLombokModel(true);
		strategy.setRestControllerStyle(true);
		strategy.setDbColumnUnderline(true);
		strategy.setEntityBuilderModel(true);
		strategy.setTablePrefix(tablePrefix);
		strategy.setNaming(NamingStrategy.underline_to_camel);
		strategy.setSuperControllerClass("com.ssx.maxwell.kafka.enjoy.controller.BaseController");
		strategy.setInclude(includeTable);
		strategy.setExclude(excludeTable);
		mpg.setStrategy(strategy);

		PackageConfig pc = new PackageConfig();
		pc.setParent(basePackage);
		pc.setController(controllerPath);
		pc.setEntity(entityPath);
		pc.setMapper(mapperPath);
		pc.setXml(xmlPath);
		pc.setService(servicePath);
		pc.setServiceImpl(serviceImplPath);
		mpg.setPackageInfo(pc);

		mpg.execute();
	}

	public static GeneratePlugin build(String basePackage, String url, String username, String password) {
		GeneratePlugin plugin = new GeneratePlugin();
		plugin.setBasePackage(basePackage);
		plugin.setUrl(url);
		plugin.setUsername(username);
		plugin.setPassword(password);
		return plugin;
	}

}

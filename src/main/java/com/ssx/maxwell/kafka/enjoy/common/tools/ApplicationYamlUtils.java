package com.ssx.maxwell.kafka.enjoy.common.tools;

import com.google.common.collect.Lists;
import com.ssx.maxwell.kafka.enjoy.configuration.DynamicDsInfo;
import com.ssx.maxwell.kafka.enjoy.enumerate.SqlConstants;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author: shuaishuai.xiao
 * @date: 2019-6-13 21:59:32
 * @description:
 */
@Slf4j
public class ApplicationYamlUtils {


    /**
     * 查询动态数据源db_key
     *
     * @return
     */
    public static List<DynamicDsInfo> readDynamicConfig() {
        List<DynamicDsInfo> res = Lists.newArrayList();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            Yaml yaml = new Yaml();
            URL url = ApplicationYamlUtils.class.getClassLoader().getResource("application.yml");
            if (url != null) {
                Map map = yaml.load(new FileInputStream(url.getFile()));
                log.info("读取application.yml=={}", map);
                Map mapSpring = (Map) map.get("spring");
                Map mapDatasource = (Map) mapSpring.get("datasource");
                Map mapDynamic = (Map) mapDatasource.get("dynamic");
                Map mapDynamicDatasource = (Map) mapDynamic.get("datasource");
                Map master = (Map) mapDynamicDatasource.get("master");
                String driverClassName = (String) master.get("driver-class-name");
                String dUrl = (String) master.get("url");
                String username = (String) master.get("username");
                String password = (String) master.get("password");
                connection = DbUtils.connection(driverClassName, dUrl, username, password);
                preparedStatement = DbUtils.preparedStatement(connection, SqlConstants.LOAD_DS_NAME_SQL);
                resultSet = DbUtils.resultSet(p -> {
                    ResultSet rs = p.executeQuery();
                    while (rs.next()) {
                        String dbDatabase = rs.getString("db_database");
                        res.add(new DynamicDsInfo(dbDatabase));
                    }
                    return rs;
                }, preparedStatement);
            }
        } catch (SQLException sqlE) {
            log.error("sql执行异常,e={}", sqlE);
        } catch (Exception e) {
            log.error("读取动态数据源配置key异常, e={}", e);
        } finally {
            if (null != resultSet) {
                DbUtils.closeResultSet(resultSet);
            }
            if (null != preparedStatement) {
                DbUtils.closeStatement(preparedStatement);
            }
            if (null != connection) {
                DbUtils.closeConnection(connection);
            }
        }
        return res;
    }

}

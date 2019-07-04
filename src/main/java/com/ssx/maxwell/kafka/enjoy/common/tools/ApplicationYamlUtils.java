package com.ssx.maxwell.kafka.enjoy.common.tools;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ssx.maxwell.kafka.enjoy.configuration.DynamicDsInfo;
import com.ssx.maxwell.kafka.enjoy.enumerate.SqlConstants;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    static Yaml YAML;
    static URL URL;
    static Map MAP;

    static {
        YAML = new Yaml();
        URL = ApplicationYamlUtils.class.getClassLoader().getResource("application.yml");
        try {
            MAP = YAML.load(new FileInputStream(URL.getFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
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
            if (URL != null) {
                log.info("读取application.yml=={}", MAP);
                Map mapSpring = (Map) MAP.get("spring");
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

    public static void main(String[] args) throws FileNotFoundException {

        System.out.println(isSentinel());
        System.out.println(isCluster());
    }

    public static boolean isSentinel() {
//        private static final String REDIS_SENTINEL_MASTER_CONFIG_PROPERTY = "spring.redis.sentinel.master";
//        private static final String REDIS_SENTINEL_NODES_CONFIG_PROPERTY = "spring.redis.sentinel.nodes";
        Map spring = (Map) MAP.get("spring");
        Map redis = (Map) spring.get("redis");
        if(null == redis){
            return false;
        }
        Map sentinel = (Map) redis.get("sentinel");
        if(null == sentinel){
            return false;
        }
        return !Strings.isNullOrEmpty((String)sentinel.get("master")) && !Strings.isNullOrEmpty((String)sentinel.get("nodes"));
    }

    public static boolean isCluster() {
//        private static final String REDIS_CLUSTER_NODES_CONFIG_PROPERTY = "spring.redis.cluster.nodes";
//        private static final String REDIS_CLUSTER_MAX_REDIRECTS_CONFIG_PROPERTY = "spring.redis.cluster.max-redirects";
        Map spring = (Map) MAP.get("spring");
        Map redis = (Map) spring.get("redis");
        if(null == redis){
            return false;
        }
        Map cluster = (Map) redis.get("cluster");
        if(null == cluster){
            return false;
        }
        return !Strings.isNullOrEmpty((String)cluster.get("nodes")) && !Strings.isNullOrEmpty((String)cluster.get("max-redirects"));
    }


}

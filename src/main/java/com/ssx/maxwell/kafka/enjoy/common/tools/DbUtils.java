package com.ssx.maxwell.kafka.enjoy.common.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.sql.*;

/**
 * @author: shuaishuai.xiao
 * @date: 2019/6/28 14:50
 * @description: jdbc工具类
 */
@Slf4j
public class DbUtils {

    public static Connection connection(String className, String url, String user, String password) throws ClassNotFoundException, SQLException {
        Class.forName(className);
        return DriverManager.getConnection(url, user, password);
    }

    public static PreparedStatement preparedStatement(Connection connection, String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    public static ResultSet resultSet(ExecuteSql executeSql, PreparedStatement preparedStatement) throws SQLException {
        return executeSql.ps(preparedStatement);
    }

    public static void closeConnection(@Nullable Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException var2) {
                log.debug("Could not close JDBC Connection", var2);
            } catch (Throwable var3) {
                log.debug("Unexpected exception on closing JDBC Connection", var3);
            }
        }

    }

    public static void closeStatement(@Nullable Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException var2) {
                log.trace("Could not close JDBC Statement", var2);
            } catch (Throwable var3) {
                log.trace("Unexpected exception on closing JDBC Statement", var3);
            }
        }

    }

    public static void closeResultSet(@Nullable ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException var2) {
                log.trace("Could not close JDBC ResultSet", var2);
            } catch (Throwable var3) {
                log.trace("Unexpected exception on closing JDBC ResultSet", var3);
            }
        }

    }

    @FunctionalInterface
    public interface ExecuteSql {
        ResultSet ps(PreparedStatement preparedStatement) throws SQLException;
    }
}

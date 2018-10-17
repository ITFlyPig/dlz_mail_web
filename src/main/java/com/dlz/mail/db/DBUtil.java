package com.dlz.mail.db;

import com.dlz.mail.utils.DesUtil;
import com.dlz.mail.utils.TextUtil;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mysql.cj.jdbc.PreparedStatement;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.LogManager;

import java.beans.PropertyVetoException;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 获得操作数据的dataSource
 */
public class DBUtil {
    private static final org.apache.log4j.Logger logger = LogManager.getLogger(DBUtil.class);
    private static ComboPooledDataSource dataSource;

    static {
        dataSource = new ComboPooledDataSource();
        Properties props = new Properties();
        String root = System.getProperty("user.dir");
        logger.debug("获取到的root:" + root);

        try {
            InputStream in = new BufferedInputStream(new FileInputStream(root + "/conf/db.properties"));
            props.load(in);
            in.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String driver = props.getProperty("db.driver");
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String passsord = props.getProperty("db.pwd");
        String isEncrypt = props.getProperty("db.pwd.encrypted");

        try {
            if (!TextUtil.isEmpty(isEncrypt)) {
                if (isEncrypt.equalsIgnoreCase("1")) {//加密过的，需要解密
                    passsord = DesUtil.getInstance().decrypt(passsord);
                } else {//没加密，则加密更新
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(root + "/conf/db.properties"));
                    String encryptPwd = DesUtil.getInstance().encrypt(passsord);
                    props.setProperty("db.pwd.encrypted", "1");
                    props.setProperty("db.pwd", encryptPwd);
                    props.store(out, " update");
                    out.close();

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("密码加解密异常：" + e.getLocalizedMessage());
        }

        logger.debug("driver:" + driver + " url:" + url + " user:" + user);

        try {
            dataSource.setDriverClass(driver);
            dataSource.setJdbcUrl(url);
            dataSource.setUser(user);
            dataSource.setPassword(passsord);
            dataSource.setInitialPoolSize(10);
            dataSource.setMinPoolSize(10);
            dataSource.setMaxPoolSize(30);

//            dataSource.setInitialPoolSize(1);
//            dataSource.setMinPoolSize(1);
//            dataSource.setMaxPoolSize(3);

            dataSource.setMaxIdleTime(3600);
            dataSource.setTestConnectionOnCheckin(true);//设置为true，异步检测连接的有效性
            dataSource.setTestConnectionOnCheckout(false);//设置为true，所有的连接都将检测其有效性，会影响性能，所以将其设置为false
            dataSource.setIdleConnectionTestPeriod(18000);//每隔多少秒c3p0检测连接的有效性

        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

    }

    public static ComboPooledDataSource getDataSource() {
        return dataSource;

    }


    //取得链接
    public static Connection getConn() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 关闭链接
    public static void close(Connection conn) throws SQLException {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    public static void close(PreparedStatement pstate) throws SQLException {
        if (pstate != null) {
            pstate.close();
        }
    }

    public static void close(ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
    }

    /**
     * 更新
     *
     * @param sql
     * @param params
     */
    public static boolean update(String sql, Object... params) {
        ComboPooledDataSource dataSource = DBUtil.getDataSource();
        QueryRunner queryRunner = new QueryRunner(dataSource);
        try {
            queryRunner.update(sql, params);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}


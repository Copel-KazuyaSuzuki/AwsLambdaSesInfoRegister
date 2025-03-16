package copel.sesproductpackage.register.entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 【フレームワーク部品】
 * DBコネクションを取得するためのクラス.
 *
 * @author 鈴木一矢
 *
 */
public class DBConnection {
    // ================================
    // フィールド定義
    // ================================
    private final static String URL;
    private final static String USER_NAME;
    private final static String PASSWORD;

    // ================================
    // staticイニシャライザ
    // ================================
    static {
        URL = System.getenv("SES_DB_ENDPOINT_URL");
        USER_NAME = System.getenv("SES_DB_USER_NAME");
        PASSWORD =  System.getenv("SES_DB_USER_PASSWORD");
    }

    /**
     * DBコネクションを生成し返却します.
     *
     * @return DBコネクション
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // (1) コネクションを取得する
        Connection connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
        // (2) オートコミットをオフにする
        connection.setAutoCommit(false);
        // (4) DBコネクションを返却する
        return connection;
    }
}

package copel.sesproductpackage.register.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

public class DBConnectionTest {

    @Mock
    private Connection mockConnection;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // 環境変数のモック
        System.setProperty("SES_DB_ENDPOINT_URL", "jdbc:mysql://localhost:3306/testdb");
        System.setProperty("SES_DB_USER_NAME", "testuser");
        System.setProperty("SES_DB_USER_PASSWORD", "testpassword");
    }

    @Test
    public void testGetConnection_Success() throws SQLException, ClassNotFoundException {
        // DriverManagerをモック
        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/testdb", "testuser", "testpassword")).thenReturn(mockConnection);

            // 実際のDB接続メソッド呼び出し
            Connection connection = DBConnection.getConnection();

            // 結果の検証
            assertNotNull(connection);
            verify(mockConnection).setAutoCommit(false); // setAutoCommitが呼ばれたことを確認
        }
    }

    @Test
    public void testGetConnection_Failure() throws SQLException, ClassNotFoundException {
        // DriverManagerをモック
        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/testdb", "testuser", "testpassword")).thenThrow(new SQLException("Connection failed"));

            // 失敗シナリオのテスト
            DBConnection.getConnection();
        }
    }
}

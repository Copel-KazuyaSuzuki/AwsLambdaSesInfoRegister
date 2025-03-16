package copel.sesproductpackage.register.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import copel.sesproductpackage.register.unit.OriginalDateTime;
import copel.sesproductpackage.register.unit.Transformer;

class SESAITJobTest {

    private SES_AI_T_JOB job;
    private Transformer transformerMock;
    private Connection connectionMock;
    private PreparedStatement preparedStatementMock;
    private ResultSet resultSetMock;

    @BeforeEach
    void setUp() throws IOException, RuntimeException {
        // SES_AI_T_JOBのインスタンスを初期化
        job = new SES_AI_T_JOB();

        // モックオブジェクトの作成
        transformerMock = mock(Transformer.class);
        connectionMock = mock(Connection.class);
        preparedStatementMock = mock(PreparedStatement.class);
        resultSetMock = mock(ResultSet.class);

        // モックの設定（transformerの動作を定義）
        when(transformerMock.embedding(anyString())).thenReturn(new float[] { 1.0f, 2.0f, 3.0f });

        // SES_AI_T_JOBのデータ設定
        job.setFromGroup("Group1");
        job.setFromId("ID123");
        job.setFromName("John Doe");
        job.setRawContent("Test content");
        job.setRegisterUser("admin");
    }

    @Test
    void testInsert() throws SQLException {
        // preparedStatementとconnectionのモックを設定
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1);

        // insertメソッドの呼び出し
        int result = job.insert(connectionMock);

        // 結果が1であることを確認
        assertEquals(1, result);
        // prepareStatementの呼び出しを検証
        verify(connectionMock).prepareStatement(anyString());
        // setStringメソッドが適切に呼ばれたかを検証
        verify(preparedStatementMock).setString(1, "Group1");
    }

    @Test
    void testEmbedding() throws IOException {
        // embeddingメソッドの呼び出し
        job.embedding(transformerMock);

        // ベクトルデータが設定されていることを確認
        assertNotNull(job.getVectorData());
        assertEquals(3, job.getVectorData().getValue().length);
        // transformerMockのembeddingメソッドが呼ばれたことを確認
        verify(transformerMock).embedding(anyString());
    }

    @Test
    void testUniqueCheck() throws SQLException {
        // uniqueCheckメソッドの動作をテスト
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getInt(1)).thenReturn(0);

        // uniqueCheckメソッドを呼び出す
        boolean isUnique = job.uniqueCheck(connectionMock, 0.8);

        // 結果がtrueであることを確認
        assertTrue(isUnique);
        // preparedStatementの呼び出しを確認
        verify(connectionMock).prepareStatement(anyString());
    }

    @Test
    void testToString() {
        // toStringメソッドのテスト
        job.setDistance(0.5);
        job.setRegisterDate(new OriginalDateTime());
        job.setTtl(new OriginalDateTime());

        // toStringメソッドの呼び出し
        String result = job.toString();

        // 文字列に予想通りの情報が含まれているかを確認
        assertTrue(result.contains("fromGroup: Group1"));
        assertTrue(result.contains("distance: 0.5"));
    }

    @Test
    void testInsertWithNullConnection() throws SQLException {
        // connectionがnullの場合のinsertメソッドの動作をテスト
        int result = job.insert(null);

        // null接続の場合、0が返されることを確認
        assertEquals(0, result);
    }
}

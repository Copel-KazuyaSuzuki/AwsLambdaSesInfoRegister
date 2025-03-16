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

class SES_AI_T_PERSONTest {

    private SES_AI_T_PERSON person;
    private Transformer transformerMock;
    private Connection connectionMock;
    private PreparedStatement preparedStatementMock;
    private ResultSet resultSetMock;

    @BeforeEach
    void setUp() throws IOException, RuntimeException {
        // SES_AI_T_PERSONのインスタンスを初期化
        person = new SES_AI_T_PERSON();

        // モックオブジェクトの作成
        transformerMock = mock(Transformer.class);
        connectionMock = mock(Connection.class);
        preparedStatementMock = mock(PreparedStatement.class);
        resultSetMock = mock(ResultSet.class);

        // モックの設定（transformerの動作を定義）
        when(transformerMock.embedding(anyString())).thenReturn(new float[] { 1.0f, 2.0f, 3.0f });

        // SES_AI_T_PERSONのデータ設定
        person.setFromGroup("Group1");
        person.setFromId("ID123");
        person.setFromName("John Doe");
        person.setRawContent("Test content");
        person.setRegisterUser("admin");
        person.setFileId("fileId123");
    }

    @Test
    void testInsert() throws SQLException {
        // preparedStatementとconnectionのモックを設定
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1);

        // insertメソッドの呼び出し
        int result = person.insert(connectionMock);

        // 結果が1であることを確認
        assertEquals(1, result);
        // prepareStatementの呼び出しを検証
        verify(connectionMock).prepareStatement(anyString());
        // setStringメソッドが適切に呼ばれたかを検証
        verify(preparedStatementMock).setString(1, "Group1");
    }

    @Test
    void testUpdate() throws SQLException {
        // preparedStatementとconnectionのモックを設定
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1);

        // updateメソッドの呼び出し
        int result = person.update(connectionMock, "key");

        // 結果が1であることを確認
        assertEquals(1, result);
        // preparedStatementの呼び出しを確認
        verify(connectionMock).prepareStatement(anyString());
    }

    @Test
    void testEmbedding() throws IOException {
        // embeddingメソッドの呼び出し
        person.embedding(transformerMock);

        // ベクトルデータが設定されていることを確認
        assertNotNull(person.getVectorData());
        assertEquals(3, person.getVectorData().getValue().length);
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
        boolean isUnique = person.uniqueCheck(connectionMock, 0.8);

        // 結果がtrueであることを確認
        assertTrue(isUnique);
        // preparedStatementの呼び出しを確認
        verify(connectionMock).prepareStatement(anyString());
    }

    @Test
    void testToString() {
        // toStringメソッドのテスト
        person.setDistance(0.5);
        person.setRegisterDate(new OriginalDateTime());
        person.setTtl(new OriginalDateTime());

        // toStringメソッドの呼び出し
        String result = person.toString();

        // 文字列に予想通りの情報が含まれているかを確認
        assertTrue(result.contains("fromGroup: Group1"));
        assertTrue(result.contains("distance: 0.5"));
    }

    @Test
    void testInsertWithNullConnection() throws SQLException {
        // connectionがnullの場合のinsertメソッドの動作をテスト
        int result = person.insert(null);

        // null接続の場合、0が返されることを確認
        assertEquals(0, result);
    }

    @Test
    void testIsスキルシート登録済WithNullFileId() {
        // fileIdがnullの場合、falseが返されることを確認
        person.setFileId(null);
        assertFalse(person.isスキルシート登録済());
    }

    @Test
    void testIsスキルシート登録済WithEmptyFileId() {
        // fileIdが空の場合、falseが返されることを確認
        person.setFileId("");
        assertFalse(person.isスキルシート登録済());
    }

    @Test
    void testIsスキルシート登録済WithNonEmptyFileId() {
        // fileIdが非空の場合、trueが返されることを確認
        person.setFileId("fileId123");
        assertTrue(person.isスキルシート登録済());
    }
}

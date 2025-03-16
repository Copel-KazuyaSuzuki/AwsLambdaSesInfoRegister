package copel.sesproductpackage.register.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import copel.sesproductpackage.register.unit.SkillSheet;
import copel.sesproductpackage.register.unit.Transformer;

class SES_AI_T_SKILLSHEETTest {

    private SES_AI_T_SKILLSHEET sesAiTSkillSheet;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        sesAiTSkillSheet = new SES_AI_T_SKILLSHEET();
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
    }

    @Test
    void testInsert() throws SQLException {
        // モックの設定
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);  // 1行が挿入されると仮定

        // メソッド実行
        int result = sesAiTSkillSheet.insert(mockConnection);

        // 結果の検証
        assertEquals(1, result);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testEmbedding() throws Exception {
        // モックの設定
        Transformer mockTransformer = mock(Transformer.class);

        // エンベディング処理
        sesAiTSkillSheet.setSkillSheet(new SkillSheet());
        sesAiTSkillSheet.getSkillSheet().setFileContent("some content");

        // メソッド実行
        sesAiTSkillSheet.embedding(mockTransformer);

        // 結果の検証
        assertNotNull(sesAiTSkillSheet.getVectorData());
    }

    @Test
    void testUniqueCheck() throws SQLException {
        // モックの設定
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);  // 1回だけデータが返ると仮定
        when(mockResultSet.getInt(1)).thenReturn(0);  // 類似データなしと仮定

        // メソッド実行
        boolean isUnique = sesAiTSkillSheet.uniqueCheck(mockConnection, 0.8);

        // 結果の検証
        assertTrue(isUnique);
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    void testUniqueCheckWithDuplicate() throws SQLException {
        // モックの設定
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);  // 1回だけデータが返ると仮定
        when(mockResultSet.getInt(1)).thenReturn(1);  // 類似データありと仮定

        // メソッド実行
        boolean isUnique = sesAiTSkillSheet.uniqueCheck(mockConnection, 0.8);

        // 結果の検証
        assertFalse(isUnique);
        verify(mockPreparedStatement, times(1)).executeQuery();
    }
}

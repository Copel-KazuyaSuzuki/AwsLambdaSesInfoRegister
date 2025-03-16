package copel.sesproductpackage.register.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import copel.sesproductpackage.register.entity.DBConnection;
import copel.sesproductpackage.register.entity.SES_AI_T_SKILLSHEET;
import copel.sesproductpackage.register.unit.OpenAI;
import copel.sesproductpackage.register.unit.aws.S3;

@ExtendWith(MockitoExtension.class)
class SES_AI_API_003Test {

    @Mock
    private Connection mockConnection;
    
    @Mock
    private SES_AI_T_SKILLSHEET mockSkillSheetEntity;
    
    @Mock
    private OpenAI mockOpenAI;
    
    @Mock
    private S3 mockS3;
    
    @InjectMocks
    private SES_AI_API_003 api;
    
    @BeforeEach
    void setUp() throws Exception {
        mockConnection = mock(Connection.class);
        mockSkillSheetEntity = mock(SES_AI_T_SKILLSHEET.class);
        mockOpenAI = mock(OpenAI.class);
        mockS3 = mock(S3.class);
    }
    
    @Test
    void testSkillSheetRegister_Success() throws Exception {
        when(mockSkillSheetEntity.uniqueCheck(any(Connection.class), anyDouble())).thenReturn(true);
        
        api.skillSheetRegister("testUser");
        
        verify(mockSkillSheetEntity, times(1)).insert(any(Connection.class));
        verify(mockConnection, times(1)).commit();
        assertEquals(200, api.resultStatus);
        assertTrue(api.resultMessage.contains("DBへの登録に成功しました。"));
    }
    
    @Test
    void testSkillSheetRegister_DuplicateRecord() throws Exception {
        when(mockSkillSheetEntity.uniqueCheck(any(Connection.class), anyDouble())).thenReturn(false);
        
        api.skillSheetRegister("testUser");
        
        verify(mockSkillSheetEntity, never()).insert(any(Connection.class));
        verify(mockConnection, never()).commit();
        assertEquals(200, api.resultStatus);
        assertTrue(api.resultMessage.contains("類似するレコードが存在するため"));
    }
    
    @Test
    void testSkillSheetRegister_SQLException() throws Exception {
        when(DBConnection.getConnection()).thenThrow(new SQLException("DB接続エラー"));
        
        api.skillSheetRegister("testUser");
        
        assertEquals(500, api.resultStatus);
        assertTrue(api.resultMessage.contains("DBへの登録に失敗しました。"));
    }
    
    @Test
    void testSkillSheetRegister_RuntimeException() throws Exception {
        when(mockSkillSheetEntity.insert(any(Connection.class))).thenThrow(new RuntimeException("予期しないエラー"));
        
        api.skillSheetRegister("testUser");
        
        assertEquals(500, api.resultStatus);
        assertTrue(api.resultMessage.contains("DBへの登録に失敗しました。"));
        verify(mockConnection, times(1)).rollback();
    }
}
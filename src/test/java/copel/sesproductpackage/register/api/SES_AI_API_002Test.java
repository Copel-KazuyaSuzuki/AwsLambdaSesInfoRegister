package copel.sesproductpackage.register.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import copel.sesproductpackage.register.entity.DBConnection;
import copel.sesproductpackage.register.entity.SES_AI_T_JOB;
import copel.sesproductpackage.register.unit.Content;
import copel.sesproductpackage.register.unit.OpenAI;
import copel.sesproductpackage.register.unit.OriginalDateTime;

@ExtendWith(MockitoExtension.class)
class SES_AI_API_002Test {

    @Mock
    private Connection mockConnection;
    
    @Mock
    private SES_AI_T_JOB mockJob;
    
    @Mock
    private OpenAI mockOpenAI;
    
    @Mock
    private RequestObject mockRequestObject;
    
    @InjectMocks
    private SES_AI_API_002 api;
    
    @BeforeEach
    void setUp() {
        when(mockRequestObject.getFromGroup()).thenReturn("testGroup");
        when(mockRequestObject.getFromId()).thenReturn("testId");
        when(mockRequestObject.getFromName()).thenReturn("testName");
        when(mockRequestObject.getRawContent()).thenReturn(new Content("testContent"));
        
        api = new SES_AI_API_002(mockRequestObject);
    }
    
    @Test
    void testJobRegister_Success() throws SQLException, ClassNotFoundException, IOException {
        try (MockedStatic<DBConnection> mockedDBConnection = Mockito.mockStatic(DBConnection.class);
             MockedStatic<OriginalDateTime> mockedDateTime = Mockito.mockStatic(OriginalDateTime.class)) {
            
            mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockJob.uniqueCheck(any(), anyDouble())).thenReturn(true);
            
            api.jobRegister("testUser");
            
            assertEquals(200, api.resultStatus);
            assertEquals("DBへの登録に成功しました。", api.resultMessage);
            verify(mockConnection, times(1)).commit();
            verify(mockConnection, times(1)).close();
        }
    }
    
    @Test
    void testJobRegister_DuplicateRecord() throws SQLException, ClassNotFoundException, IOException {
        try (MockedStatic<DBConnection> mockedDBConnection = Mockito.mockStatic(DBConnection.class)) {
            
            mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
            when(mockJob.uniqueCheck(any(), anyDouble())).thenReturn(false);
            
            api.jobRegister("testUser");
            
            assertEquals(200, api.resultStatus);
            assertEquals("類似するレコードが存在するため、DBへの登録を行いませんでした。", api.resultMessage);
            verify(mockConnection, times(1)).close();
        }
    }
    
    @Test
    void testJobRegister_Failure() throws SQLException, ClassNotFoundException, IOException {
        try (MockedStatic<DBConnection> mockedDBConnection = Mockito.mockStatic(DBConnection.class)) {
            
            mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
            doThrow(new SQLException("DB error"))
                .when(mockConnection).commit();
            
            api.jobRegister("testUser");
            
            assertEquals(500, api.resultStatus);
            assertEquals("DBへの登録に失敗しました。", api.resultMessage);
            verify(mockConnection, times(1)).rollback();
            verify(mockConnection, times(1)).close();
        }
    }
}
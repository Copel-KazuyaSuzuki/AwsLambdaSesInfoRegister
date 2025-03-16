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
import org.mockito.junit.jupiter.MockitoExtension;

import copel.sesproductpackage.register.entity.DBConnection;
import copel.sesproductpackage.register.entity.SES_AI_T_PERSON;
import copel.sesproductpackage.register.unit.Content;
import copel.sesproductpackage.register.unit.OpenAI;

@ExtendWith(MockitoExtension.class)
class SES_AI_API_001Test {

    @Mock
    private RequestObject requestObject;
    
    @Mock
    private Connection mockConnection;

    @Mock
    private SES_AI_T_PERSON mockPerson;

    @Mock
    private OpenAI mockOpenAI;

    @InjectMocks
    private SES_AI_API_001 api;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        when(requestObject.getFromGroup()).thenReturn("TestGroup");
        when(requestObject.getFromId()).thenReturn("TestID");
        when(requestObject.getFromName()).thenReturn("TestName");
        when(requestObject.getRawContent()).thenReturn(new Content("TestContent"));
        when(requestObject.getFileId()).thenReturn("TestFileId");
        
        mockStatic(DBConnection.class);
        when(DBConnection.getConnection()).thenReturn(mockConnection);
    }

    @Test
    void testPersonRegister_Success() throws SQLException, IOException {
        api = new SES_AI_API_001(requestObject);
        
        when(mockPerson.uniqueCheck(any(Connection.class), anyDouble())).thenReturn(true);
        doNothing().when(mockPerson).embedding(any(OpenAI.class));
        doNothing().when(mockPerson).insert(any(Connection.class));
        doNothing().when(mockConnection).commit();
        
        api.personRegister("TestUser");
        
        assertEquals(200, api.getResultStatus());
        assertEquals("DBへの登録に成功しました。", api.getResultMessage());
    }

    @Test
    void testPersonRegister_DuplicateRecord() throws SQLException, IOException {
        api = new SES_AI_API_001(requestObject);
        
        when(mockPerson.uniqueCheck(any(Connection.class), anyDouble())).thenReturn(false);
        
        api.personRegister("TestUser");
        
        assertEquals(200, api.getResultStatus());
        assertEquals("類似するレコードが存在するため、DBへの登録を行いませんでした。", api.getResultMessage());
    }

    @Test
    void testPersonRegister_SQLException() throws SQLException, IOException, ClassNotFoundException {
        api = new SES_AI_API_001(requestObject);
        
        when(DBConnection.getConnection()).thenThrow(new SQLException("DB connection error"));
        
        api.personRegister("TestUser");
        
        assertEquals(500, api.getResultStatus());
        assertEquals("DBへの登録に失敗しました。", api.getResultMessage());
    }
}
package copel.sesproductpackage.register.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import copel.sesproductpackage.register.unit.OriginalDateTime;
import copel.sesproductpackage.register.unit.Vector;

class SES_AI_T_PERSONLotTest {

    private SES_AI_T_PERSONLot sesAiTPersonLot;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        sesAiTPersonLot = new SES_AI_T_PERSONLot();
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
    }

    @Test
    void testRetrieve() throws SQLException {
        Vector mockVector = mock(Vector.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);  // 1回だけデータが返ると仮定
        when(mockResultSet.getString("from_group")).thenReturn("group1");
        when(mockResultSet.getString("from_id")).thenReturn("id1");
        when(mockResultSet.getString("from_name")).thenReturn("name1");
        when(mockResultSet.getString("raw_content")).thenReturn("content");
        when(mockResultSet.getString("register_date")).thenReturn("2025-03-15");
        when(mockResultSet.getString("register_user")).thenReturn("user1");
        when(mockResultSet.getString("ttl")).thenReturn("2025-03-15");
        when(mockResultSet.getDouble("distance")).thenReturn(1.23);

        // メソッド実行
        sesAiTPersonLot.retrieve(mockConnection, mockVector, 10);

        // 結果の検証
        assertEquals(1, sesAiTPersonLot.getLot().size());
        SES_AI_T_PERSON person = sesAiTPersonLot.getLot().iterator().next();
        assertEquals("group1", person.getFromGroup());
        assertEquals("id1", person.getFromId());
        assertEquals("name1", person.getFromName());
    }

    @Test
    void testSearchByRawContent() throws SQLException {
        String query = "sample content";
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);  // 1回だけデータが返ると仮定
        when(mockResultSet.getString("from_group")).thenReturn("group1");
        when(mockResultSet.getString("from_id")).thenReturn("id1");
        when(mockResultSet.getString("from_name")).thenReturn("name1");
        when(mockResultSet.getString("raw_content")).thenReturn("content");
        when(mockResultSet.getString("register_date")).thenReturn("2025-03-15");
        when(mockResultSet.getString("register_user")).thenReturn("user1");
        when(mockResultSet.getString("ttl")).thenReturn("2025-03-15");

        // メソッド実行
        sesAiTPersonLot.searchByRawContent(mockConnection, query);

        // 結果の検証
        assertEquals(1, sesAiTPersonLot.getLot().size());
        SES_AI_T_PERSON person = sesAiTPersonLot.getLot().iterator().next();
        assertEquals("group1", person.getFromGroup());
        assertEquals("id1", person.getFromId());
        assertEquals("name1", person.getFromName());
    }

    @Test
    void testSelectByAndQuery() throws SQLException {
        Map<String, String> andQuery = Map.of("from_group", "group1", "from_id", "id1");
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);  // 1回だけデータが返ると仮定
        when(mockResultSet.getString("from_group")).thenReturn("group1");
        when(mockResultSet.getString("from_id")).thenReturn("id1");
        when(mockResultSet.getString("from_name")).thenReturn("name1");
        when(mockResultSet.getString("raw_content")).thenReturn("content");
        when(mockResultSet.getString("register_date")).thenReturn("2025-03-15");
        when(mockResultSet.getString("register_user")).thenReturn("user1");
        when(mockResultSet.getString("ttl")).thenReturn("2025-03-15");

        // メソッド実行
        sesAiTPersonLot.selectByAndQuery(mockConnection, andQuery);

        // 結果の検証
        assertEquals(1, sesAiTPersonLot.getLot().size());
        SES_AI_T_PERSON person = sesAiTPersonLot.getLot().iterator().next();
        assertEquals("group1", person.getFromGroup());
        assertEquals("id1", person.getFromId());
        assertEquals("name1", person.getFromName());
    }

    @Test
    void testSelectByOrQuery() throws SQLException {
        Map<String, String> orQuery = Map.of("from_group", "group1", "from_id", "id1");
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);  // 1回だけデータが返ると仮定
        when(mockResultSet.getString("from_group")).thenReturn("group1");
        when(mockResultSet.getString("from_id")).thenReturn("id1");
        when(mockResultSet.getString("from_name")).thenReturn("name1");
        when(mockResultSet.getString("raw_content")).thenReturn("content");
        when(mockResultSet.getString("register_date")).thenReturn("2025-03-15");
        when(mockResultSet.getString("register_user")).thenReturn("user1");
        when(mockResultSet.getString("ttl")).thenReturn("2025-03-15");

        // メソッド実行
        sesAiTPersonLot.selectByOrQuery(mockConnection, orQuery);

        // 結果の検証
        assertEquals(1, sesAiTPersonLot.getLot().size());
        SES_AI_T_PERSON person = sesAiTPersonLot.getLot().iterator().next();
        assertEquals("group1", person.getFromGroup());
        assertEquals("id1", person.getFromId());
        assertEquals("name1", person.getFromName());
    }

    @Test
    void testSelectByRegisterDateAfter() throws SQLException {
        OriginalDateTime fromDate = new OriginalDateTime("2025-03-01");
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);  // 1回だけデータが返ると仮定
        when(mockResultSet.getString("from_group")).thenReturn("group1");
        when(mockResultSet.getString("from_id")).thenReturn("id1");
        when(mockResultSet.getString("from_name")).thenReturn("name1");
        when(mockResultSet.getString("raw_content")).thenReturn("content");
        when(mockResultSet.getString("register_date")).thenReturn("2025-03-15");
        when(mockResultSet.getString("register_user")).thenReturn("user1");
        when(mockResultSet.getString("ttl")).thenReturn("2025-03-15");

        // メソッド実行
        sesAiTPersonLot.selectByRegisterDateAfter(mockConnection, fromDate);

        // 結果の検証
        assertEquals(1, sesAiTPersonLot.getLot().size());
        SES_AI_T_PERSON person = sesAiTPersonLot.getLot().iterator().next();
        assertEquals("group1", person.getFromGroup());
        assertEquals("id1", person.getFromId());
        assertEquals("name1", person.getFromName());
    }

    @Test
    void testIterator() throws SQLException {
        sesAiTPersonLot.retrieve(mockConnection, mock(Vector.class), 10);
        assertNotNull(sesAiTPersonLot.iterator());
    }
}

package copel.sesproductpackage.register.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import copel.sesproductpackage.register.unit.Vector;

class SES_AI_T_SKILLSHEETLotTest {

    private SES_AI_T_SKILLSHEETLot sesAiTSkillSheetLot;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        sesAiTSkillSheetLot = new SES_AI_T_SKILLSHEETLot();
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
    }

    @Test
    void testRetrieve() throws SQLException {
        Vector mockVector = mock(Vector.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString("from_group")).thenReturn("group1");
        when(mockResultSet.getString("from_id")).thenReturn("id1");
        when(mockResultSet.getString("from_name")).thenReturn("name1");
        when(mockResultSet.getString("file_id")).thenReturn("skillSheet1");
        when(mockResultSet.getString("file_name")).thenReturn("file1");
        when(mockResultSet.getString("file_content")).thenReturn("content1");
        when(mockResultSet.getString("file_content_summary")).thenReturn("summary1");
        when(mockResultSet.getString("register_date")).thenReturn("2025-03-15");
        when(mockResultSet.getString("register_user")).thenReturn("user1");
        when(mockResultSet.getString("ttl")).thenReturn("2025-03-16");
        when(mockResultSet.getDouble("distance")).thenReturn(0.1);

        sesAiTSkillSheetLot.retrieve(mockConnection, mockVector, 10);

        assertNotNull(sesAiTSkillSheetLot.iterator());
        assertEquals(1, sesAiTSkillSheetLot.getLot().size());
        assertEquals("group1", sesAiTSkillSheetLot.getLot().iterator().next().getFromGroup());
    }

    @Test
    void testSelectLike() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString("from_group")).thenReturn("group2");
        when(mockResultSet.getString("from_id")).thenReturn("id2");
        when(mockResultSet.getString("from_name")).thenReturn("name2");

        sesAiTSkillSheetLot.selectLike(mockConnection, "file_content", "test");

        assertNotNull(sesAiTSkillSheetLot.iterator());
        assertEquals(1, sesAiTSkillSheetLot.getLot().size());
        assertEquals("group2", sesAiTSkillSheetLot.getLot().iterator().next().getFromGroup());
    }

    @Test
    void testSearchByFileContent() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString("from_group")).thenReturn("group3");
        when(mockResultSet.getString("from_id")).thenReturn("id3");
        when(mockResultSet.getString("from_name")).thenReturn("name3");

        sesAiTSkillSheetLot.searchByFileContent(mockConnection, "content");

        assertNotNull(sesAiTSkillSheetLot.iterator());
        assertEquals(1, sesAiTSkillSheetLot.getLot().size());
        assertEquals("group3", sesAiTSkillSheetLot.getLot().iterator().next().getFromGroup());
    }

    @Test
    void testSelectByAndQuery() throws SQLException {
        Map<String, String> andQuery = new HashMap<>();
        andQuery.put("from_group", "group4");
        andQuery.put("from_name", "name4");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString("from_group")).thenReturn("group4");
        when(mockResultSet.getString("from_name")).thenReturn("name4");

        sesAiTSkillSheetLot.selectByAndQuery(mockConnection, andQuery);

        assertNotNull(sesAiTSkillSheetLot.iterator());
        assertEquals(1, sesAiTSkillSheetLot.getLot().size());
        assertEquals("group4", sesAiTSkillSheetLot.getLot().iterator().next().getFromGroup());
    }

    @Test
    void testSelectByOrQuery() throws SQLException {
        Map<String, String> orQuery = new HashMap<>();
        orQuery.put("from_group", "group5");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString("from_group")).thenReturn("group5");
        when(mockResultSet.getString("from_name")).thenReturn("name5");

        sesAiTSkillSheetLot.selectByOrQuery(mockConnection, orQuery);

        assertNotNull(sesAiTSkillSheetLot.iterator());
        assertEquals(1, sesAiTSkillSheetLot.getLot().size());
        assertEquals("group5", sesAiTSkillSheetLot.getLot().iterator().next().getFromGroup());
    }
}

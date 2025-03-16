package copel.sesproductpackage.register.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import copel.sesproductpackage.register.unit.LogicalOperators;
import copel.sesproductpackage.register.unit.LogicalOperators.論理演算子;
import copel.sesproductpackage.register.unit.Vector;

class SES_AI_T_JOBLotTest {

    private SES_AI_T_JOBLot sesAiTJobLot;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        sesAiTJobLot = new SES_AI_T_JOBLot();
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
        sesAiTJobLot.retrieve(mockConnection, mockVector, 10);

        // 結果の検証
        assertEquals(1, sesAiTJobLot.getLot().size());
        SES_AI_T_JOB job = sesAiTJobLot.getLot().iterator().next();
        assertEquals("group1", job.getFromGroup());
        assertEquals("id1", job.getFromId());
        assertEquals("name1", job.getFromName());
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
        sesAiTJobLot.searchByRawContent(mockConnection, query);

        // 結果の検証
        assertEquals(1, sesAiTJobLot.getLot().size());
        SES_AI_T_JOB job = sesAiTJobLot.getLot().iterator().next();
        assertEquals("group1", job.getFromGroup());
        assertEquals("id1", job.getFromId());
        assertEquals("name1", job.getFromName());
    }

    @Test
    void testSearchByRawContentWithMultipleConditions() throws SQLException {
        String firstQuery = "sample content";
        List<LogicalOperators> conditions = new ArrayList<>();
        conditions.add(new LogicalOperators(論理演算子.AND, "value1"));
        conditions.add(new LogicalOperators(論理演算子.AND, "value2"));

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
        sesAiTJobLot.searchByRawContent(mockConnection, firstQuery, conditions);

        // 結果の検証
        assertEquals(1, sesAiTJobLot.getLot().size());
        SES_AI_T_JOB job = sesAiTJobLot.getLot().iterator().next();
        assertEquals("group1", job.getFromGroup());
        assertEquals("id1", job.getFromId());
        assertEquals("name1", job.getFromName());
    }

    @Test
    void testSelectByAndQuery() throws SQLException {
        Map<String, String> query = new HashMap<>();
        query.put("column1", "value1");
        query.put("column2", "value2");

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
        sesAiTJobLot.selectByAndQuery(mockConnection, query);

        // 結果の検証
        assertEquals(1, sesAiTJobLot.getLot().size());
        SES_AI_T_JOB job = sesAiTJobLot.getLot().iterator().next();
        assertEquals("group1", job.getFromGroup());
        assertEquals("id1", job.getFromId());
        assertEquals("name1", job.getFromName());
    }
}

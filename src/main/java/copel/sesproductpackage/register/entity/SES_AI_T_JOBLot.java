package copel.sesproductpackage.register.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import copel.sesproductpackage.register.unit.LogicalOperators;
import copel.sesproductpackage.register.unit.OriginalDateTime;
import copel.sesproductpackage.register.unit.Vector;

/**
 * 【Entityクラス】
 * 案件情報(SES_AI_T_JOB)テーブルのLotクラス.
 *
 * @author 鈴木一矢
 *
 */
public class SES_AI_T_JOBLot implements Iterable<SES_AI_T_JOB> {
    /**
     * ベクトル検索SQL.
     */
    private final static String RETRIEVE_SQL = "SELECT job_id, from_group, from_id, from_name, raw_content, register_date, register_user, ttl, vector_data <=> ?::vector AS distance FROM SES_AI_T_JOB ORDER BY distance LIMIT ?";
    /**
     * 全文検索SQL.
     */
    private final static String SELECT_LIKE_SQL = "SELECT job_id, from_group, from_id, from_name, raw_content, vector_data, register_date, register_user, ttl FROM SES_AI_T_JOB WHERE raw_content LIKE ?";
    /**
     * 検索SQL.
     */
    private final static String SELECT_SQL = "SELECT job_id, from_group, from_id, from_name, raw_content, vector_data, register_date, register_user, ttl FROM SES_AI_T_JOB WHERE ";

    /**
     * SES_AI_T_JOBを複数持つLot.
     */
    private Collection<SES_AI_T_JOB> entityLot;

    public SES_AI_T_JOBLot() {
        this.entityLot = new ArrayList<SES_AI_T_JOB>();
    }

    /**
     * ベクトル検索を実行し結果をこのLotに保持します.
     *
     * @param connection DBコネクション
     * @param query 検索ベクトル
     * @param limit 取得上限件数
     * @throws SQLException
     */
    public void retrieve(Connection connection, Vector query, int limit) throws SQLException {
        if (connection == null) {
            return;
        }
        PreparedStatement preparedStatement = connection.prepareStatement(RETRIEVE_SQL);
        preparedStatement.setString(1, query == null ? null : query.toString());
        preparedStatement.setInt(2, limit);
        ResultSet resultSet = preparedStatement.executeQuery();
        this.entityLot = new ArrayList<SES_AI_T_JOB>();
        while (resultSet.next()) {
            SES_AI_T_JOB SES_AI_T_JOB = new SES_AI_T_JOB();
            SES_AI_T_JOB.setJobId(resultSet.getString("job_id"));
            SES_AI_T_JOB.setFromGroup(resultSet.getString("from_group"));
            SES_AI_T_JOB.setFromId(resultSet.getString("from_id"));
            SES_AI_T_JOB.setFromName(resultSet.getString("from_name"));
            SES_AI_T_JOB.setRawContent(resultSet.getString("raw_content"));
            SES_AI_T_JOB.setRegisterDate(new OriginalDateTime(resultSet.getString("register_date")));
            SES_AI_T_JOB.setRegisterUser(resultSet.getString("register_user"));
            SES_AI_T_JOB.setTtl(new OriginalDateTime(resultSet.getString("ttl")));
            SES_AI_T_JOB.setDistance(resultSet.getDouble("distance"));
            this.entityLot.add(SES_AI_T_JOB);
        }
    }

    /**
     * raw_contentカラムで全文検索を実行し、結果をこのLotに保持します.
     *
     * @param connection DBコネクション
     * @param query 検索条件Map
     * @throws SQLException 
     */
    public void searchByRawContent(final Connection connection, final String query) throws SQLException {
        this.entityLot = new ArrayList<SES_AI_T_JOB>();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_LIKE_SQL);
        preparedStatement.setString(1, "%" + query + "%"); // ワイルドカードをつける
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            SES_AI_T_JOB SES_AI_T_JOB = new SES_AI_T_JOB();
            SES_AI_T_JOB.setJobId(resultSet.getString("job_id"));
            SES_AI_T_JOB.setFromGroup(resultSet.getString("from_group"));
            SES_AI_T_JOB.setFromId(resultSet.getString("from_id"));
            SES_AI_T_JOB.setFromName(resultSet.getString("from_name"));
            SES_AI_T_JOB.setRawContent(resultSet.getString("raw_content"));
            SES_AI_T_JOB.setRegisterDate(new OriginalDateTime(resultSet.getString("register_date")));
            SES_AI_T_JOB.setRegisterUser(resultSet.getString("register_user"));
            SES_AI_T_JOB.setTtl(new OriginalDateTime(resultSet.getString("ttl")));
            this.entityLot.add(SES_AI_T_JOB);
        }
    }

    /**
     * raw_contentカラムに対して複数条件で全文検索を実行し、結果をこのLotに保持します.
     *
     * @param connection DBコネクション
     * @param firstLikeQuery 1つ目のLIKE句の検索条件
     * @param query 検索条件リスト
     * @throws SQLException 
     */
    public void searchByRawContent(final Connection connection, final String firstLikeQuery, final List<LogicalOperators> query) throws SQLException {
        // 検索条件からSQLを生成
        String sql = SELECT_LIKE_SQL;
        for (final LogicalOperators logicalOperator : query) {
            logicalOperator.setColumnName("raw_content");
            sql += logicalOperator != null ? logicalOperator.getLikeQuery() : "";
        }

        // 検索条件を追加する
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, "%" + firstLikeQuery + "%"); // ワイルドカードをつける
        if (query != null && query.size() > 0) {
            for (int i = 0; i < query.size(); i++) {
                if (query.get(0) != null) {
                    preparedStatement.setString(i + 2, "%" + query.get(0).getValue() + "%"); // ワイルドカードをつける
                }
            }
        }

        // 検索を実行する
        this.entityLot = new ArrayList<SES_AI_T_JOB>();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            SES_AI_T_JOB SES_AI_T_JOB = new SES_AI_T_JOB();
            SES_AI_T_JOB.setJobId(resultSet.getString("job_id"));
            SES_AI_T_JOB.setFromGroup(resultSet.getString("from_group"));
            SES_AI_T_JOB.setFromId(resultSet.getString("from_id"));
            SES_AI_T_JOB.setFromName(resultSet.getString("from_name"));
            SES_AI_T_JOB.setRawContent(resultSet.getString("raw_content"));
            SES_AI_T_JOB.setRegisterDate(new OriginalDateTime(resultSet.getString("register_date")));
            SES_AI_T_JOB.setRegisterUser(resultSet.getString("register_user"));
            SES_AI_T_JOB.setTtl(new OriginalDateTime(resultSet.getString("ttl")));
            this.entityLot.add(SES_AI_T_JOB);
        }
    }

    /**
     * SELECTをAND条件で実行する.
     *
     * @param connection DBコネクション
     * @param andQuery カラム名と検索値をkey-valueで持つMap
     * @throws SQLException 
     */
    public void selectByAndQuery(final Connection connection, final Map<String, String> andQuery) throws SQLException {
        // 検索条件からSQLを生成
        String sql = SELECT_SQL;
        boolean isFirst = true;
        for (final String columnName : andQuery.keySet()) {
            if (isFirst) {
                sql += columnName + " = ?";
            } else {
                sql += "AND " + columnName + " = ?";
            }
        }

        // 検索条件を追加する
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        int i = 1;
        for (final String columnName : andQuery.keySet()) {
            preparedStatement.setString(i, andQuery.get(columnName));
            i++;
        }

        // 検索を実行する
        this.entityLot = new ArrayList<SES_AI_T_JOB>();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            SES_AI_T_JOB SES_AI_T_JOB = new SES_AI_T_JOB();
            SES_AI_T_JOB.setJobId(resultSet.getString("job_id"));
            SES_AI_T_JOB.setFromGroup(resultSet.getString("from_group"));
            SES_AI_T_JOB.setFromId(resultSet.getString("from_id"));
            SES_AI_T_JOB.setFromName(resultSet.getString("from_name"));
            SES_AI_T_JOB.setRawContent(resultSet.getString("raw_content"));
            SES_AI_T_JOB.setRegisterDate(new OriginalDateTime(resultSet.getString("register_date")));
            SES_AI_T_JOB.setRegisterUser(resultSet.getString("register_user"));
            SES_AI_T_JOB.setTtl(new OriginalDateTime(resultSet.getString("ttl")));
            this.entityLot.add(SES_AI_T_JOB);
        }
    }

    /**
     * SELECTをOR条件で実行する.
     *
     * @param connection DBコネクション
     * @param orQuery カラム名と検索値をkey-valueで持つMap
     * @throws SQLException 
     */
    public void selectByOrQuery(final Connection connection, final Map<String, String> orQuery) throws SQLException {
        // 検索条件からSQLを生成
        String sql = SELECT_SQL;
        boolean isFirst = true;
        for (final String columnName : orQuery.keySet()) {
            if (isFirst) {
                sql += columnName + " = ?";
            } else {
                sql += "OR " + columnName + " = ?";
            }
        }

        // 検索条件を追加する
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        int i = 1;
        for (final String columnName : orQuery.keySet()) {
            preparedStatement.setString(i, orQuery.get(columnName));
            i++;
        }

        // 検索を実行する
        this.entityLot = new ArrayList<SES_AI_T_JOB>();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            SES_AI_T_JOB SES_AI_T_JOB = new SES_AI_T_JOB();
            SES_AI_T_JOB.setJobId(resultSet.getString("job_id"));
            SES_AI_T_JOB.setFromGroup(resultSet.getString("from_group"));
            SES_AI_T_JOB.setFromId(resultSet.getString("from_id"));
            SES_AI_T_JOB.setFromName(resultSet.getString("from_name"));
            SES_AI_T_JOB.setRawContent(resultSet.getString("raw_content"));
            SES_AI_T_JOB.setRegisterDate(new OriginalDateTime(resultSet.getString("register_date")));
            SES_AI_T_JOB.setRegisterUser(resultSet.getString("register_user"));
            SES_AI_T_JOB.setTtl(new OriginalDateTime(resultSet.getString("ttl")));
            this.entityLot.add(SES_AI_T_JOB);
        }
    }

    /**
     * このクラスの持つLotオブジェクトを返却します.
     *
     * @return Lot
     */
    public Collection<SES_AI_T_JOB> getLot() {
    	return this.entityLot;
    }

    @Override
    public Iterator<SES_AI_T_JOB> iterator() {
        return this.entityLot != null ? this.entityLot.iterator() : null;
    }
}

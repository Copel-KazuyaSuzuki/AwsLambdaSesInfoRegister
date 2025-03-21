package copel.sesproductpackage.register.entity;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import copel.sesproductpackage.register.unit.OriginalDateTime;
import copel.sesproductpackage.register.unit.SkillSheet;
import copel.sesproductpackage.register.unit.Transformer;
import copel.sesproductpackage.register.unit.Vector;

/**
 * 【Entityクラス】
 * スキルシート情報(SES_AI_T_SKILLSHEET)テーブル.
 *
 * @author 鈴木一矢
 *
 */
public class SES_AI_T_SKILLSHEET {
    /**
     * INSERTR文.
     */
    private final static String INSERT_SQL = "INSERT INTO SES_AI_T_SKILLSHEET (from_group, from_id, from_name, file_id, file_name, file_content, file_content_summary, vector_data, register_date, register_user, ttl) VALUES (?, ?, ?, ?, ?, ?, ?, ?::vector, ?, ?, ?)";
    /**
     * 重複チェック用SQL.
     */
    private final static String CHECK_SQL = "SELECT COUNT(*) FROM SES_AI_T_SKILLSHEET WHERE file_content % ? AND similarity(file_content, ?) > ?";

    /**
     * 送信元グループ.
     */
    private String fromGroup;
    /**
     * 送信者ID.
     */
    private String fromId;
    /**
     * 送信者名.
     */
    private String fromName;
    /**
     * スキルシート.
     */
    private SkillSheet skillSheet;
    /**
     * OpenAIベクトルデータ.
     */
    private Vector vectorData;
    /**
     * 登録日時.
     */
    private OriginalDateTime registerDate;
    /**
     * 登録ユーザー.
     */
    private String registerUser;
    /**
     * 有効期限.
     */
    private OriginalDateTime ttl;
    /**
     * ユークリッド距離.
     */
    private double distance;

    /**
     * コンストラクタ.
     */
    public SES_AI_T_SKILLSHEET() {
        this.skillSheet = new SkillSheet();
    }

    /**
     * INSERT処理を実行します.
     *
     * @param connection DBコネクション
     * @throws SQLException
     */
    public int insert(Connection connection) throws SQLException {
        if (connection == null) {
            return 0;
        }
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL);
        preparedStatement.setString(1, this.fromGroup);
        preparedStatement.setString(2, this.fromId);
        preparedStatement.setString(3, this.fromName);
        preparedStatement.setString(4, this.skillSheet == null ? null : this.skillSheet.getFileId());
        preparedStatement.setString(5, this.skillSheet == null ? null : this.skillSheet.getFileName());
        preparedStatement.setString(6, this.skillSheet == null ? null : this.skillSheet.getFileContent());
        preparedStatement.setString(7, this.skillSheet == null ? null : this.skillSheet.getFileContentSummary());
        preparedStatement.setString(8, this.vectorData == null ? null : this.vectorData.toString());
        preparedStatement.setTimestamp(9, this.registerDate == null ? null : this.registerDate.toTimestamp());
        preparedStatement.setString(10, this.registerUser);
        preparedStatement.setTimestamp(11, this.ttl == null ? null : this.ttl.toTimestamp());
        return preparedStatement.executeUpdate();
    }

    /**
     * このエンティティが持つrawContentをエンベディングする.
     *
     * @param embeddingProcessListener エンベディング処理リスナー
     * @throws IOException
     * @throws RuntimeException
     */
    public void embedding(final Transformer embeddingProcessListener) throws IOException, RuntimeException {
        this.vectorData = new Vector(embeddingProcessListener);
        String content = this.skillSheet.getFileContent();
        if (content != null) {
            // 特殊文字や記号を省いた文字列をカウントし6000文字より少なければファイル内容をそのままエンベディングする
            // 6000文字以上であればエンベディングできない可能性が高いため、要約をエンベディングする
            content = content.replaceAll("[\\p{C}\\p{P}\"]", "");
            this.vectorData.setRawString(content.length() < 7000 ? content : this.skillSheet.getFileContentSummary());
            this.vectorData.embedding();
        }
    }

    /**
     * SES_AI_T_SKILLSHEETテーブル内にこのエンティティの持つrawContentと類似したrawContentがあるかどうを判定する.
     *
     * @param connection DBコネクション
     * @param similarityThreshold 類似度基準値(0.0～1.0で指定する。文章の一致率を示す。例えば0.8であれば、80%以上一致する文章が存在しなければユニークであると判定)
     * @return 類似するレコードがなければtrue、あればfalse
     * @throws SQLException
     */
    public boolean uniqueCheck(final Connection connection, final double similarityThreshold) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(CHECK_SQL);
        preparedStatement.setString(1, this.skillSheet == null ? null : this.skillSheet.getFileContent());
        preparedStatement.setString(2, this.skillSheet == null ? null : this.skillSheet.getFileContent());
        preparedStatement.setDouble(3, similarityThreshold);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1) < 1;
    }

    @Override
    public String toString() {
        return "{\n fromGroup: " + this.fromGroup
                + "\n fromId: " + this.fromId
                + "\n fromName: " + this.fromName
                + "\n skillsheetId: " + this.skillSheet == null ? null : this.skillSheet.getFileId()
                + "\n fileName: " + this.skillSheet == null ? null : this.skillSheet.getFileName()
                + "\n fileContent: " + this.skillSheet == null ? null : this.skillSheet.getFileContent()
                + "\n fileContentSummary: " + this.skillSheet == null ? null : this.skillSheet.getFileContentSummary()
                + "\n vectorData: " + this.vectorData
                + "\n registerDate: " + this.registerDate
                + "\n registerUser: " + this.registerUser
                + "\n ttl: " + this.ttl
                + "\n distance: " + this.distance
                + "\n}";
    }

    // ================================
    // Getter / Setter
    // ================================
    public String getFromGroup() {
        return this.fromGroup;
    }
    public void setFromGroup(String fromGroup) {
        this.fromGroup = fromGroup;
    }
    public String getFromId() {
        return this.fromId;
    }
    public void setFromId(String fromId) {
        this.fromId = fromId;
    }
    public String getFromName() {
        return this.fromName;
    }
    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
    public SkillSheet getSkillSheet() {
        return this.skillSheet;
    }
    public void setSkillSheet(SkillSheet skillSheet) {
        this.skillSheet = skillSheet;
    }
    public String getFileId() {
        return this.skillSheet == null ? null : this.skillSheet.getFileId();
    }
    public void setFileId(String fileId) {
        this.skillSheet.setFileId(fileId);
    }
    public String getFileName() {
        return this.skillSheet == null ? null : this.skillSheet.getFileName();
    }
    public void setFileName(String fileName) {
        this.skillSheet.setFileContent(fileName);
    }
    public String getFileContent() {
        return this.skillSheet == null ? null : this.skillSheet.getFileContent();
    }
    public void setFileContent(String fileContent) {
        this.skillSheet.setFileContent(fileContent);
    }
    public String getFileContentSummary() {
        return this.skillSheet == null ? null : this.skillSheet.getFileContentSummary();
    }
    public void setFileContentSummary(String fileContentSummary) {
        this.skillSheet.setFileContent(fileContentSummary);
    }
    public Vector getVectorData() {
        return this.vectorData;
    }
    public void setVectorData(Vector vectorData) {
        this.vectorData = vectorData;
    }
    public OriginalDateTime getRegisterDate() {
        return this.registerDate;
    }
    public void setRegisterDate(OriginalDateTime registerDate) {
        this.registerDate = registerDate;
    }
    public String getRegisterUser() {
        return this.registerUser;
    }
    public void setRegisterUser(String registerUser) {
        this.registerUser = registerUser;
    }
    public OriginalDateTime getTtl() {
        return this.ttl;
    }
    public void setTtl(OriginalDateTime ttl) {
        this.ttl = ttl;
    }
    public double getDistance() {
        return this.distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
}

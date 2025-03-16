package copel.sesproductpackage.register.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import copel.sesproductpackage.register.entity.DBConnection;
import copel.sesproductpackage.register.entity.SES_AI_T_PERSON;
import copel.sesproductpackage.register.unit.OpenAI;
import copel.sesproductpackage.register.unit.OriginalDateTime;
/**
 * 【SES_AI_API_001】
 * 要員情報を保存するAPI
 *
 * @author 鈴木一矢
 *
 */
public class SES_AI_API_001 extends ApiBase {
    // ================================================
    // 定数
    // ================================================
    /**
     * 要員情報(SES_AI_T_PERSON)テーブルのレコードのTTLデフォルト値(7日).
     */
    private final static Integer SES_AI_T_PERSON_TTL_DEFAULT = 7;
    /**
     * 要員情報(SES_AI_T_PERSON)テーブルでの一致率判断基準値のデフォルト値(80%).
     */
    private final static Double SES_AI_T_PERSON_SIMILARITY_THRESHOLD_DEFAULT = 0.8;

    // ================================================
    // 環境変数
    // ================================================
    /**
     * 要員情報(SES_AI_T_PERSON)テーブルのレコードのTTL(日数).
     */
    private static Integer SES_AI_T_PERSON_TTL;
    /**
     * 要員情報(SES_AI_T_PERSON)テーブルでの一致率判断基準値.
     */
    private static Double SES_AI_T_PERSON_SIMILARITY_THRESHOLD;
    /**
     * OpenAIのAPIキー.
     */
    private static String OPEN_AI_API_KEY;

    static {
        try {
            String ttlEnv = System.getenv("SES_AI_T_PERSON_TTL");
            SES_AI_T_PERSON_TTL = (ttlEnv != null) ? Integer.parseInt(ttlEnv) : SES_AI_T_PERSON_TTL_DEFAULT;

            String similarityEnv = System.getenv("SES_AI_T_PERSON_SIMILARITY_THRESHOLD");
            SES_AI_T_PERSON_SIMILARITY_THRESHOLD = (similarityEnv != null) ? Double.parseDouble(similarityEnv) : SES_AI_T_PERSON_SIMILARITY_THRESHOLD_DEFAULT;

            OPEN_AI_API_KEY = System.getenv("OPEN_AI_API_KEY");
            if (OPEN_AI_API_KEY == null) {
                System.out.println("[WARN] OPEN_AI_API_KEY が設定されていません。APIの呼び出しができません。");
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] 環境変数の値が不正です。デフォルト値を使用します。");
            e.printStackTrace();
            SES_AI_T_PERSON_TTL = SES_AI_T_PERSON_TTL_DEFAULT;
            SES_AI_T_PERSON_SIMILARITY_THRESHOLD = SES_AI_T_PERSON_SIMILARITY_THRESHOLD_DEFAULT;
        } catch (Exception e) {
            System.out.println("[ERROR] SES_AI_API_001 の環境変数読み込み中にエラーが発生しました。");
            e.printStackTrace();
        }
    }

    // ================================================
    // メンバ変数
    // ================================================
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
     * 原文.
     */
    private String rawContent;
    /**
     * ファイルID.
     */
    private String fileId;

    /**
     * コンストラクタ.
     */
    public SES_AI_API_001(final RequestObject requestObject) {
        this.fromGroup = requestObject != null ? requestObject.getFromGroup() : null;
        this.fromId = requestObject != null ? requestObject.getFromId() : null;
        this.fromName = requestObject != null ? requestObject.getFromName() : null;
        this.rawContent = requestObject != null ? (requestObject.getRawContent() != null ? requestObject.getRawContent().toString() : null) : null;
        this.fileId = requestObject != null ? requestObject.getFileId() : null;
    }

    // ================================================
    // API
    // ================================================
    /**
     * 要員情報をベクトルDBに保存する.
     */
    public void personRegister(final String registerUser) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            SES_AI_T_PERSON SES_AI_T_PERSON = new SES_AI_T_PERSON();
            SES_AI_T_PERSON.setFromGroup(this.fromGroup);
            SES_AI_T_PERSON.setFromId(this.fromId);
            SES_AI_T_PERSON.setFromName(this.fromName);
            SES_AI_T_PERSON.setRawContent(this.rawContent);
            SES_AI_T_PERSON.setFileId(this.fileId);
            SES_AI_T_PERSON.setRegisterDate(new OriginalDateTime());
            SES_AI_T_PERSON.setRegisterUser(registerUser);
            OriginalDateTime ttl = new OriginalDateTime();
            ttl.plusDays(SES_AI_T_PERSON_TTL);
            SES_AI_T_PERSON.setTtl(ttl);

            // SES_AI_T_PERSONテーブル内にrawContentが類似するレコードが存在しなければINSERTする
            if (SES_AI_T_PERSON.uniqueCheck(connection, SES_AI_T_PERSON_SIMILARITY_THRESHOLD)) {
                OpenAI client = new OpenAI(OPEN_AI_API_KEY);
                SES_AI_T_PERSON.embedding(client);
                SES_AI_T_PERSON.insert(connection);
                connection.commit();
                this.resultMessage = "DBへの登録に成功しました。";
            } else {
                this.resultMessage = "類似するレコードが存在するため、DBへの登録を行いませんでした。";
            }

            // 処理を終了する
            connection.close();
            this.resultStatus = 200;
        } catch (ClassNotFoundException | SQLException | IOException | RuntimeException e) {
            e.printStackTrace();
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            this.resultStatus = 500;
            this.resultMessage = "DBへの登録に失敗しました。";
        }
    }
}

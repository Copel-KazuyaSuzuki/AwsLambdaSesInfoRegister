package copel.sesproductpackage.register.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import copel.sesproductpackage.register.entity.DBConnection;
import copel.sesproductpackage.register.entity.SES_AI_T_JOB;
import copel.sesproductpackage.register.unit.OpenAI;
import copel.sesproductpackage.register.unit.OriginalDateTime;
import lombok.extern.slf4j.Slf4j;

/**
 * 【SES_AI_API_002】
 * 案件情報を保存するAPI
 *
 * @author 鈴木一矢
 *
 */
@Slf4j
public class SES_AI_API_002 extends ApiBase {
    // ================================================
    // 定数
    // ================================================
    /**
     * 案件情報(SES_AI_T_JOB)テーブルのレコードのTTLデフォルト値(7日).
     */
    private final static Integer SES_AI_T_JOB_TTL_DEFAULT = 7;
    /**
     * 案件情報(SES_AI_T_JOB)テーブルでの一致率判断基準値のデフォルト値(80%).
     */
    private final static Double SES_AI_T_JOB_SIMILARITY_THRESHOLD_DEFAULT = 0.8;

    // ================================================
    // 環境変数
    // ================================================
    /**
     * 案件情報(SES_AI_T_JOB)テーブルのレコードのTTL(日数).
     */
    private static Integer SES_AI_T_JOB_TTL;
    /**
     * 案件情報(SES_AI_T_JOB)テーブルでの一致率判断基準値.
     */
    private static Double SES_AI_T_JOB_SIMILARITY_THRESHOLD;
    /**
     * OpenAIのAPIキー.
     */
    private static String OPEN_AI_API_KEY;

    static {
        try {
            String ttlEnv = System.getenv("SES_AI_T_JOB_TTL");
            SES_AI_T_JOB_TTL = (ttlEnv != null) ? Integer.parseInt(ttlEnv) : SES_AI_T_JOB_TTL_DEFAULT;

            String similarityEnv = System.getenv("SES_AI_T_JOB_SIMILARITY_THRESHOLD");
            SES_AI_T_JOB_SIMILARITY_THRESHOLD = (similarityEnv != null) ? Double.parseDouble(similarityEnv) : SES_AI_T_JOB_SIMILARITY_THRESHOLD_DEFAULT;

            OPEN_AI_API_KEY = System.getenv("OPEN_AI_API_KEY");
            if (OPEN_AI_API_KEY == null) {
            	log.warn("OPEN_AI_API_KEY が設定されていません。APIの呼び出しができません。");
            }
        } catch (NumberFormatException e) {
        	log.error("環境変数の値が不正です。デフォルト値を使用します。");
            e.printStackTrace();
            SES_AI_T_JOB_TTL = SES_AI_T_JOB_TTL_DEFAULT;
            SES_AI_T_JOB_SIMILARITY_THRESHOLD = SES_AI_T_JOB_SIMILARITY_THRESHOLD_DEFAULT;
        } catch (Exception e) {
        	log.error("SES_AI_API_001 の環境変数読み込み中にエラーが発生しました。");
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
     * invoke_id.
     */
    private String invokeId;

    /**
     * コンストラクタ.
     */
    public SES_AI_API_002(final RequestObject requestObject, final String invokeId) {
        this.fromGroup = requestObject != null ? requestObject.getFromGroup() : null;
        this.fromId = requestObject != null ? requestObject.getFromId() : null;
        this.fromName = requestObject != null ? requestObject.getFromName() : null;
        this.rawContent = requestObject != null ? (requestObject.getRawContent() != null ? requestObject.getRawContent().toString() : null) : null;
        this.invokeId = invokeId;
    }

    // ================================================
    // API
    // ================================================
    /**
     * 案件情報をベクトルDBに保存する.
     */
    public void jobRegister(final String registerUser) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            SES_AI_T_JOB SES_AI_T_JOB = new SES_AI_T_JOB();
            SES_AI_T_JOB.setFromGroup(this.fromGroup);
            SES_AI_T_JOB.setFromId(this.fromId);
            SES_AI_T_JOB.setFromName(this.fromName);
            SES_AI_T_JOB.setRawContent(this.rawContent);
            SES_AI_T_JOB.setRegisterDate(new OriginalDateTime());
            SES_AI_T_JOB.setRegisterUser(registerUser);
            OriginalDateTime ttl = new OriginalDateTime();
            ttl.plusDays(SES_AI_T_JOB_TTL);
            SES_AI_T_JOB.setTtl(ttl);

            // SES_AI_T_JOBテーブル内にrawContentが類似するレコードが存在しなければINSERTする
            if (SES_AI_T_JOB.uniqueCheck(connection, SES_AI_T_JOB_SIMILARITY_THRESHOLD)) {
                OpenAI client = new OpenAI(OPEN_AI_API_KEY);
                SES_AI_T_JOB.embedding(client);
                SES_AI_T_JOB.insert(connection);
                connection.commit();
            	log.info("[Invoke ID: {}] SES_AI_T_JOBに案件情報を登録しました。", this.invokeId);
            } else {
            	log.info("[Invoke ID: {}] 類似するレコードが存在するため、DBへの登録を行いませんでした。", this.invokeId);
            }

            // 処理を終了する
            connection.close();
            this.resultStatus = 200;
            this.resultMessage = "DBへの登録に成功しました。";
        } catch (ClassNotFoundException | SQLException | IOException | RuntimeException e) {
            e.printStackTrace();
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        	log.error("[Invoke ID: {}] DBへの登録中にエラーが発生したため、登録処理は行われませんでした。", this.invokeId);
            this.resultStatus = 500;
            this.resultMessage = "DBへの登録に失敗しました。";
        }
    }
}

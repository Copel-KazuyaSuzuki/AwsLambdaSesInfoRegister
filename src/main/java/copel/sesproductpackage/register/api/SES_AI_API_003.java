package copel.sesproductpackage.register.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.SdkClientException;

import copel.sesproductpackage.register.entity.DBConnection;
import copel.sesproductpackage.register.entity.SES_AI_T_PERSON;
import copel.sesproductpackage.register.entity.SES_AI_T_PERSONLot;
import copel.sesproductpackage.register.entity.SES_AI_T_SKILLSHEET;
import copel.sesproductpackage.register.unit.OpenAI;
import copel.sesproductpackage.register.unit.OriginalDateTime;
import copel.sesproductpackage.register.unit.SkillSheet;
import copel.sesproductpackage.register.unit.Transformer;
import copel.sesproductpackage.register.unit.aws.Region;
import copel.sesproductpackage.register.unit.aws.S3;
import lombok.extern.slf4j.Slf4j;

/**
 * 【SES_AI_API_003】
 * スキルシート情報を保存するAPI
 *
 * @author 鈴木一矢
 *
 */
@Slf4j
public class SES_AI_API_003 extends ApiBase {
    // ================================================
    // 定数
    // ================================================
    /**
     * スキルシート情報(SES_AI_T_SKILLSHEET)テーブルのレコードのTTLデフォルト値(7日).
     */
    private final static Integer SES_AI_T_SKILLSHEET_TTL_DEFAULT = 7;
    /**
     * スキルシート情報(SES_AI_T_SKILLSHEET)テーブルでの一致率判断基準値のデフォルト値(80%).
     */
    private final static Double SES_AI_T_SKILLSHEET_SIMILARITY_THRESHOLD_DEFAULT = 0.8;

    // ================================================
    // 環境変数
    // ================================================
    /**
     * スキルシート情報(SES_AI_T_SKILLSHEET)テーブルのレコードのTTL(日数).
     */
    private static Integer SES_AI_T_SKILLSHEET_TTL;
    /**
     * スキルシート情報(SES_AI_T_SKILLSHEET)テーブルでの一致率判断基準値.
     */
    private static Double SES_AI_T_SKILLSHEET_SIMILARITY_THRESHOLD;
    /**
     * OpenAIのAPIキー.
     */
    private static String OPEN_AI_API_KEY;
    /**
     * AWSアクセスキー.
     */
    private static String AWS_ACCESS_KEY_ID;
    /**
     * AWSシークレットアクセスキー.
     */
    private static String AWS_SECRET_ACCESS_KEY;
    /**
     * S3バケット名.
     */
    private static String S3_BUCKET_NAME;

    static {
        try {
            String ttlEnv = System.getenv("SES_AI_T_SKILLSHEET_TTL");
            SES_AI_T_SKILLSHEET_TTL = (ttlEnv != null) ? Integer.parseInt(ttlEnv) : SES_AI_T_SKILLSHEET_TTL_DEFAULT;

            String similarityEnv = System.getenv("SES_AI_T_SKILLSHEET_SIMILARITY_THRESHOLD");
            SES_AI_T_SKILLSHEET_SIMILARITY_THRESHOLD = (similarityEnv != null) ? Double.parseDouble(similarityEnv) : SES_AI_T_SKILLSHEET_SIMILARITY_THRESHOLD_DEFAULT;

            OPEN_AI_API_KEY = System.getenv("OPEN_AI_API_KEY");
            if (OPEN_AI_API_KEY == null) {
            	log.warn("OPEN_AI_API_KEY が設定されていません。APIの呼び出しができません。");
            }
            AWS_ACCESS_KEY_ID = System.getenv("AWS_ACCESS_KEY_ID");
            if (AWS_ACCESS_KEY_ID == null) {
            	log.warn("AWS_ACCESS_KEY_ID が設定されていません。APIの呼び出しができません。");
            }
            AWS_SECRET_ACCESS_KEY = System.getenv("AWS_SECRET_ACCESS_KEY");
            if (AWS_SECRET_ACCESS_KEY == null) {
            	log.warn("AWS_SECRET_ACCESS_KEY が設定されていません。APIの呼び出しができません。");
            }
            S3_BUCKET_NAME = System.getenv("S3_BUCKET_NAME");
            if (S3_BUCKET_NAME == null) {
            	log.warn("S3_BUCKET_NAME が設定されていません。APIの呼び出しができません。");
            }
        } catch (NumberFormatException e) {
        	log.error("環境変数の値が不正です。デフォルト値を使用します。");
            e.printStackTrace();
            SES_AI_T_SKILLSHEET_TTL = SES_AI_T_SKILLSHEET_TTL_DEFAULT;
            SES_AI_T_SKILLSHEET_SIMILARITY_THRESHOLD = SES_AI_T_SKILLSHEET_SIMILARITY_THRESHOLD_DEFAULT;
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
     * ファイルID.
     */
    private String fileId;
    /**
     * ファイル名.
     */
    private String fileName;
    /**
     * ファイル内容.
     */
    private byte[] fileData;
    /**
     * invoke_id.
     */
    private String invokeId;

    /**
     * コンストラクタ.
     */
    public SES_AI_API_003(final RequestObject requestObject, final String invokeId) {
        this.fromGroup = requestObject != null ? requestObject.getFromGroup() : null;
        this.fromId = requestObject != null ? requestObject.getFromId() : null;
        this.fromName = requestObject != null ? requestObject.getFromName() : null;
        this.fileId = requestObject != null ? requestObject.getFileId() : null;
        this.fileName = requestObject != null ? requestObject.getFileName() : null;
        this.fileData = requestObject != null ? requestObject.getFileData() : null;
        this.invokeId = invokeId;
        this.resultMessage = "";
    }

    // ================================================
    // API
    // ================================================
    /**
     * スキルシート情報をベクトルDBに保存する.
     */
    public void skillSheetRegister(final String registerUser) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            SES_AI_T_SKILLSHEET SES_AI_T_SKILLSHEET = new SES_AI_T_SKILLSHEET();
            SES_AI_T_SKILLSHEET.setFromGroup(this.fromGroup);
            SES_AI_T_SKILLSHEET.setFromId(this.fromId);
            SES_AI_T_SKILLSHEET.setFromName(this.fromName);

            SkillSheet skillSheet = new SkillSheet();
            skillSheet.setFileId(this.fileId);
            skillSheet.setFileName(this.fileName);
            skillSheet.setFileContentFromByte(this.fileData);

            SES_AI_T_SKILLSHEET.setRegisterDate(new OriginalDateTime());
            SES_AI_T_SKILLSHEET.setRegisterUser(registerUser);

            OriginalDateTime ttl = new OriginalDateTime();
            ttl.plusDays(SES_AI_T_SKILLSHEET_TTL);
            SES_AI_T_SKILLSHEET.setTtl(ttl);

            // SES_AI_T_SKILLSHEETテーブル内にrawContentが類似するレコードが存在しなければINSERTする
            if (SES_AI_T_SKILLSHEET.uniqueCheck(connection, SES_AI_T_SKILLSHEET_SIMILARITY_THRESHOLD)) {
                // スキルシートの内容を要約
                Transformer transformer = new OpenAI(OPEN_AI_API_KEY);
                try {
                    skillSheet.generateSummary(transformer);
                } catch (RuntimeException e) {
                	e.printStackTrace();
                	log.info("[Invoke ID: {}] 要約の生成に失敗したため、要約部分は空のままデータを登録します。", this.invokeId);
                }
                SES_AI_T_SKILLSHEET.setSkillSheet(skillSheet);

                // スキルシートの内容をエンベディング
                SES_AI_T_SKILLSHEET.embedding(transformer);
                SES_AI_T_SKILLSHEET.insert(connection);
                connection.commit();
            	log.info("[Invoke ID: {}] SES_AI_T_SKILLSHEETにスキルシート情報を登録しました。", this.invokeId);

                // S3にファイルを保存する
                try {
                    S3 s3 = new S3(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, Region.東京);
                    s3.setBucketName(S3_BUCKET_NAME);
                    s3.setBucketFilePath(this.fileId + "_" + this.fileName);
                    s3.setData(this.fileData);
                    s3.save();
                	log.info("[Invoke ID: {}] S3へのファイルの登録しに成功しました。", this.invokeId);
                } catch (SdkClientException e) {
                	log.warn("[Invoke ID: {}] S3へのファイルの登録しに失敗したため、DBへの登録処理のみ行いました。", this.invokeId);
                }

                // 要員情報と紐づける処理
                // 現在時刻の3分前以降に登録された要員情報テーブルのレコードを全て取得する.
            	log.info("[Invoke ID: {}] 要員情報テーブルの情報との紐づけ処理を開始します。", this.invokeId);
                SES_AI_T_PERSONLot SES_AI_T_PERSONLot = new SES_AI_T_PERSONLot();
                OriginalDateTime now = new OriginalDateTime();
                now.minusMinutes(3);
                SES_AI_T_PERSONLot.selectByRegisterDateAfter(connection, now);

                // skillsheet_idカラムが空、かつfrom_groupとfrom_idが一致するレコードに絞り込む
                List<SES_AI_T_PERSON> targetList = new ArrayList<SES_AI_T_PERSON>();
                for (SES_AI_T_PERSON entity : SES_AI_T_PERSONLot) {
                    if (!entity.isスキルシート登録済() && this.fromGroup.equals(entity.getFromGroup()) && this.fromId.equals(entity.getFromId())) {
                        targetList.add(entity);
                    }
                }
                // 1件に絞りこむことができた場合、そのレコードにskillsheet_idを追加する
                if (targetList.size() == 1) {
                	log.info("[Invoke ID: {}] 紐づけられるレコードが1件見つかったため、紐づけ処理します。", this.invokeId);
                    SES_AI_T_PERSON targetEntity = targetList.get(0);
                	log.info("[Invoke ID: {}] 要員ID：{}", this.invokeId, targetEntity.getPersonId());
                    targetEntity.setFileId(this.fileId);
                    // UPDATE処理
                	targetEntity.updateByPk(connection);
                	log.info("[Invoke ID: {}] SES_AI_T_PERSONテーブルを更新し、要員情報とスキルシートの紐づけに成功しました。", this.invokeId);
                }
                connection.commit();
            } else {
                connection.close();
            	log.info("[Invoke ID: {}] 類似するレコードが存在するため、DBへの登録を行いませんでした。", this.invokeId);
            }

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
        	log.error("[Invoke ID: {}] DBまたはS3への登録中にエラーが発生したため、処理を中断します。", this.invokeId);
            this.resultStatus = 500;
            this.resultMessage += "DBへの登録に失敗しました。";
        }
    }
}

package copel.sesproductpackage.register;

import java.io.IOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import copel.sesproductpackage.register.api.RequestObject;
import copel.sesproductpackage.register.api.SES_AI_API_001;
import copel.sesproductpackage.register.api.SES_AI_API_002;
import copel.sesproductpackage.register.api.SES_AI_API_003;

/**
 * 【SES AIアシスタント】
 * Lambdaがリクエストを受け付け、処理を開始するMainクラス.
 * SQSで受信したメッセージを処理しRDSにデータを登録する.
 *
 * @author 鈴木一矢
 *
 */
public class LambdaHandler implements RequestHandler<SQSEvent, String> {
    // =====================================
    // 環境変数
    // =====================================
    /**
     * LINE Messaging APIのChannel Access Token.
     */
    private static final String LINE_CHANNEL_ACCESS_TOKEN = System.getenv("LINE_CHANNEL_ACCESS_TOKEN");

    public String handleRequest(SQSEvent event, Context context) {
        ObjectMapper objectMapper = new ObjectMapper();

        // (1) 開始ログを出力する（JSONに異常があれば処理終了）
        try {
            context.getLogger().log("SQSイベントを受信: " + objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            context.getLogger().log("JSON変換エラーのため、処理を終了します。");
            context.getLogger().log(e.getMessage());
            return null;
        }

        // (2) SQS内の各メッセージごとに処理を実施する
        // SQSの入力チェック
        if (event.getRecords() == null || event.getRecords().size() < 1) {
            context.getLogger().log("レコードが存在しないSQSを受信しました。処理を終了します。");
            return "process failed.";
        }

        // 各レコードに対して処理を行う
        for (final SQSMessage message : event.getRecords()) {
            RequestObject requestObject = new RequestObject(message.getBody());
            context.getLogger().log("リクエストボディ：\n" + requestObject.toString());
            // リクエストボディが正常データであれば、処理を行う
            if (requestObject.isValid()) {
                context.getLogger().log("リクエスト種別：" + requestObject.getRequestType().name());
                // リクエスト内容に応じてAPIを呼び出す
                if (requestObject.isスキルシート()) {
                    try {
                        context.getLogger().log("スキルシートをベクトルDBに登録します");
                        requestObject.downloadFileData(LINE_CHANNEL_ACCESS_TOKEN);
                        SES_AI_API_003 SES_AI_API_003 = new SES_AI_API_003(requestObject);
                        SES_AI_API_003.skillSheetRegister("AWS Lambda SQS Event");
                        context.getLogger().log(SES_AI_API_003.toString());
                    } catch (IOException | InterruptedException e) {
                        context.getLogger().log("スキルシートのDLに失敗しました。処理をスキップします。");
                    }
                    break;
                }
                if (requestObject.is案件情報()) {
                    context.getLogger().log("案件情報をベクトルDBに登録します");
                    SES_AI_API_002 SES_AI_API_002 = new SES_AI_API_002(requestObject);
                    SES_AI_API_002.jobRegister("AWS Lambda SQS Event");
                    context.getLogger().log(SES_AI_API_002.toString());
                    break;
                } else if (requestObject.is要員情報()) {
                    context.getLogger().log("要員情報をベクトルDBに登録します");
                    SES_AI_API_001 SES_AI_API_001 = new SES_AI_API_001(requestObject);
                    SES_AI_API_001.personRegister("AWS Lambda SQS Event");
                    context.getLogger().log(SES_AI_API_001.toString());
                    break;
                } else {
                    context.getLogger().log("無関係な情報を取得しました。DBへの登録は行わず、処理をスキップします。");
                    break;
                }
            } else {
            	context.getLogger().log("リクエストボディが不正のため、処理をスキップします。");
            }
        }

        // 処理結果を返す
        context.getLogger().log("SQS内の全てのレコードの処理を完了しました。");
        return "process complete.";
    }
}

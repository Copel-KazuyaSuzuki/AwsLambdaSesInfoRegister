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
import lombok.extern.slf4j.Slf4j;

/**
 * 【SES AIアシスタント】
 * Lambdaがリクエストを受け付け、処理を開始するMainクラス.
 * SQSで受信したメッセージを処理しRDSにデータを登録する.
 *
 * @author 鈴木一矢
 *
 */
@Slf4j
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
        	log.info("[Invoke ID: {}] SQSイベントを受信: {}", context.getAwsRequestId(), objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
        	log.error("[Invoke ID: {}] JSON変換エラーのため、処理を終了します。", context.getAwsRequestId());
        	e.printStackTrace();
            return null;
        }

        // (2) SQS内の各メッセージごとに処理を実施する
        // SQSの入力チェック
        if (event.getRecords() == null || event.getRecords().size() < 1) {
        	log.info("[Invoke ID: {}] レコードが存在しないSQSを受信しました。処理を終了します。", context.getAwsRequestId());
            return "process failed.";
        }

        // 各レコードに対して処理を行う
        for (final SQSMessage message : event.getRecords()) {
            RequestObject requestObject = new RequestObject(message.getBody());
        	log.debug("[Invoke ID: {}] リクエストボディ：{}", context.getAwsRequestId(), requestObject.toString());
            // リクエストボディが正常データであれば、処理を行う
            if (requestObject.isValid()) {
            	log.info("[Invoke ID: {}] リクエスト種別：{}", context.getAwsRequestId(), requestObject.getRequestType().name());
                // リクエスト内容に応じてAPIを呼び出す
                if (requestObject.isスキルシート()) {
                    try {
                    	log.info("[Invoke ID: {}] スキルシートをベクトルDBに登録します。", context.getAwsRequestId());
                        requestObject.downloadFileData(LINE_CHANNEL_ACCESS_TOKEN);
                        SES_AI_API_003 SES_AI_API_003 = new SES_AI_API_003(requestObject, context.getAwsRequestId());
                        SES_AI_API_003.skillSheetRegister("AWS Lambda SQS Event");
                        log.info(SES_AI_API_003.toString());
                    } catch (IOException | InterruptedException e) {
                    	log.info("[Invoke ID: {}] スキルシートのDLに失敗しました。処理をスキップします。", context.getAwsRequestId());
                    }
                    break;
                }
                if (requestObject.is案件情報()) {
                	log.info("[Invoke ID: {}] 案件情報をベクトルDBに登録します。", context.getAwsRequestId());
                    SES_AI_API_002 SES_AI_API_002 = new SES_AI_API_002(requestObject, context.getAwsRequestId());
                    SES_AI_API_002.jobRegister("AWS Lambda SQS Event");
                    log.info(SES_AI_API_002.toString());
                    break;
                } else if (requestObject.is要員情報()) {
                	log.info("[Invoke ID: {}] 要員情報をベクトルDBに登録します。", context.getAwsRequestId());
                    SES_AI_API_001 SES_AI_API_001 = new SES_AI_API_001(requestObject, context.getAwsRequestId());
                    SES_AI_API_001.personRegister("AWS Lambda SQS Event");
                    log.info(SES_AI_API_001.toString());
                    break;
                } else {
                	log.info("[Invoke ID: {}] 無関係な情報を取得しました。DBへの登録は行わず、処理をスキップします。", context.getAwsRequestId());
                    break;
                }
            } else {
            	log.error("[Invoke ID: {}] リクエストボディが不正のため、処理をスキップします。", context.getAwsRequestId());
            }
        }

        // 処理結果を返す
    	log.info("[Invoke ID: {}] SQS内の全てのレコードの処理を完了しました。", context.getAwsRequestId());
        return "process complete.";
    }
}

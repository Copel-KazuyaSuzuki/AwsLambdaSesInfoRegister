package copel.sesproductpackage.register.unit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 【SES AIアシスタント】
 * OpenAIクラス.
 *
 * @author 鈴木一矢.
 *
 */
public class OpenAI implements Transformer {
    /**
     * OpenAI APIのエンベディング処理のエンドポイント.
     */
    private static final String EMBEDDING_API_URL = "https://api.openai.com/v1/embeddings";
    /**
     * OpenAIのエンベディング処理を実施するモデル名.
     */
    private static final String EMBEDDING_MODEL = "text-embedding-ada-002";
    /**
     * OpenAIの質問応答APIのエンドポイント.
     */
    private static final String COMPLETION_API_URL = "https://api.openai.com/v1/chat/completions";
    /**
     * OpenAIの質問応答を処理するモデル名.
     */
    private static final String COMPLETION_MODEL_DEFAULT = "gpt-3.5-turbo";
    /**
     * OpenAIの質問応答を処理する際のtemperatureパラメータのデフォルト値.
     */
    private static final Float COMPLETION_TEMPERATURE = (float) 0.7;
    /**
     * OpenAIのファイルアップロードAPIのエンドポイント.
     */
    private static final String FILE_UPLOAD_URL = "https://api.openai.com/v1/files";
    /**
     * OpenAIのファインチューニングAPIのエンドポイント.
     */
    private static final String FINE_TUNE_URL = "https://api.openai.com/v1/fine-tunes";

    /**
     * OpenAIのAPIキー.
     */
    private final String apiKey;
    /**
     * OpenAIのモデル.
     */
    private final String completionModel;
    /**
     * 使用トークン数.
     */
    private int totalTokenCount = 0;
    /**
     * 合計使用金額.
     */
    private int totalAmount = 0;

    /**
     * コンストラクタ.
     *
     * @param apiKey APIキー
     */
    public OpenAI(final String apiKey) {
        this.apiKey = apiKey;
        this.completionModel = COMPLETION_MODEL_DEFAULT;
    }
    /**
     * コンストラクタ.
     *
     * @param apiKey APIキー
     * @param completionModel GPTモデル
     */
    public OpenAI(final String apiKey, final String completionModel) {
        this.apiKey = apiKey;
        this.completionModel = completionModel;
    }

    @Override
    public float[] embedding(final String inputString) throws IOException {
        URL url = new URL(EMBEDDING_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + this.apiKey);
        conn.setDoOutput(true);

        String jsonBody = "{\"input\": \"" + inputString + "\", \"model\": \"" + EMBEDDING_MODEL + "\"}";
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        switch (responseCode) {
            case 200:
                break;
            case 400:
                conn.disconnect();
                throw new RuntimeException("400 Bad Request: 無効なパラメータ、不適切なリクエストフォーマット、支払い上限超過エラー");
            case 401:
                conn.disconnect();
                throw new RuntimeException("401 Unauthorized: APIキーが無効、または提供されていないエラー");
            case 403:
                conn.disconnect();
                throw new RuntimeException("403 Forbidden: アカウントの制限、または対象モデルが利用不可のエラー");
            case 404:
                conn.disconnect();
                throw new RuntimeException("404 Not Found: APIのエンドポイントが間違っている、またはモデル名が無効のエラー");
            case 408:
                conn.disconnect();
                throw new RuntimeException("408 Request Timeout: リクエストが時間内に処理されなかったエラー");
            case 429:
                conn.disconnect();
                throw new RuntimeException("429 Too Many Requests: クレジット不足、短時間に過剰なリクエストを送信したためエラーが発生しました");
            case 500:
                conn.disconnect();
                throw new RuntimeException("500 Internal Server Error: OpenAIのサーバーで問題が発生しました");
            case 503:
                conn.disconnect();
                throw new RuntimeException("503 Service Unavailable: OpenAIのサーバーがメンテナンス中、または負荷が高い状態です");
            default:
                break;
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }
        conn.disconnect();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response.toString());
        JsonNode embeddingArray = jsonResponse.get("data").get(0).get("embedding");

        float[] vectorValue = new float[embeddingArray.size()];
        for (int i = 0; i < embeddingArray.size(); i++) {
            vectorValue[i] = embeddingArray.get(i).floatValue();
        }
        return vectorValue;
    }

    @Override
    public String generate(final String prompt) throws IOException {
        return this.generate(prompt, COMPLETION_TEMPERATURE);
    }

    /**
     * OpenAIのLLMに回答の生成を実行させその回答を返却します.
     *
     * @param prompt プロンプト
     * @param temperature 温度（回答のばらつき度を示す）
     * @return 回答
     * @throws IOException
     */
    public String generate(final String prompt, final Float temperature) throws IOException {
        if (temperature == null || prompt == null) {
            return null;
        }
        URL url = new URL(COMPLETION_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + this.apiKey);
        conn.setDoOutput(true);

        String content = prompt.replaceAll("[\\p{C}\\p{P}\"]", "");
        String jsonBody = "{\"model\": \"" + this.completionModel + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + content + "\"}], \"temperature\": " + temperature.toString() + "}";
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        switch (responseCode) {
            case 200:
                break;
            case 400:
                conn.disconnect();
                throw new RuntimeException("400 Bad Request: 無効なパラメータ、不適切なリクエストフォーマット、支払い上限超過エラー");
            case 401:
                conn.disconnect();
                throw new RuntimeException("401 Unauthorized: APIキーが無効、または提供されていないエラー");
            case 403:
                conn.disconnect();
                throw new RuntimeException("403 Forbidden: アカウントの制限、または対象モデルが利用不可のエラー");
            case 404:
                conn.disconnect();
                throw new RuntimeException("404 Not Found: APIのエンドポイントが間違っている、またはモデル名が無効のエラー");
            case 408:
                conn.disconnect();
                throw new RuntimeException("408 Request Timeout: リクエストが時間内に処理されなかったエラー");
            case 429:
                conn.disconnect();
                throw new RuntimeException("429 Too Many Requests: クレジット不足、短時間に過剰なリクエストを送信したためエラーが発生しました");
            case 500:
                conn.disconnect();
                throw new RuntimeException("500 Internal Server Error: OpenAIのサーバーで問題が発生しました");
            case 503:
                conn.disconnect();
                throw new RuntimeException("503 Service Unavailable: OpenAIのサーバーがメンテナンス中、または負荷が高い状態です");
            default:
                break;
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }
        conn.disconnect();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response.toString());
        return jsonResponse.get("choices").get(0).get("message").get("content").asText();
    }

    /**
     * OpenAIにこのオブジェクトがもつcompletionModelに対するファインチューニングをリクエストする.
     *
     * @param trainingData ファインチューニング用データ（文字列形式）
     * @return ファインチューニングジョブのID
     * @throws IOException
     */
    public void fineTuning(final String trainingData) throws IOException {
        // 1. JSONLフォーマットに変換
        String jsonlData = "{\"messages\": [{\"role\": \"system\", \"content\": \"ファインチューニングデータ\"}]}\n"
                         + "{\"messages\": [{\"role\": \"user\", \"content\": \"" + trainingData + "\"}, {\"role\": \"assistant\", \"content\": \"OK\"}]}";

        // 2. OpenAI にデータをアップロード
        URL fileUrl = new URL(FILE_UPLOAD_URL);
        HttpURLConnection fileConn = (HttpURLConnection) fileUrl.openConnection();
        fileConn.setRequestMethod("POST");
        fileConn.setRequestProperty("Authorization", "Bearer " + this.apiKey);
        fileConn.setRequestProperty("Content-Type", "application/json");
        fileConn.setDoOutput(true);

        String jsonBody = "{\"purpose\": \"fine-tune\", \"file\": \"" + jsonlData + "\"}";
        try (OutputStream os = fileConn.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int fileResponseCode = fileConn.getResponseCode();
        switch (fileResponseCode) {
            case 200:
                break;
            case 400:
                fileConn.disconnect();
                throw new RuntimeException("400 Bad Request: 無効なパラメータ、不適切なリクエストフォーマット、支払い上限超過エラー");
            case 401:
                fileConn.disconnect();
                throw new RuntimeException("401 Unauthorized: APIキーが無効、または提供されていないエラー");
            case 403:
                fileConn.disconnect();
                throw new RuntimeException("403 Forbidden: アカウントの制限、または対象モデルが利用不可のエラー");
            case 404:
                fileConn.disconnect();
                throw new RuntimeException("404 Not Found: APIのエンドポイントが間違っている、またはモデル名が無効のエラー");
            case 408:
                fileConn.disconnect();
                throw new RuntimeException("408 Request Timeout: リクエストが時間内に処理されなかったエラー");
            case 429:
                fileConn.disconnect();
                throw new RuntimeException("429 Too Many Requests: クレジット不足、短時間に過剰なリクエストを送信したためエラーが発生しました");
            case 500:
                fileConn.disconnect();
                throw new RuntimeException("500 Internal Server Error: OpenAIのサーバーで問題が発生しました");
            case 503:
                fileConn.disconnect();
                throw new RuntimeException("503 Service Unavailable: OpenAIのサーバーがメンテナンス中、または負荷が高い状態です");
            default:
                break;
        }

        StringBuilder fileResponse = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fileConn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                fileResponse.append(line);
            }
        }
        fileConn.disconnect();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode fileJson = objectMapper.readTree(fileResponse.toString());
        String fileId = fileJson.get("id").asText();

        // 3. ファインチューニングジョブを開始
        URL fineTuneUrl = new URL(FINE_TUNE_URL);
        HttpURLConnection fineTuneConn = (HttpURLConnection) fineTuneUrl.openConnection();
        fineTuneConn.setRequestMethod("POST");
        fineTuneConn.setRequestProperty("Authorization", "Bearer " + this.apiKey);
        fineTuneConn.setRequestProperty("Content-Type", "application/json");
        fineTuneConn.setDoOutput(true);

        String fineTuneBody = "{\"training_file\": \"" + fileId + "\", \"model\": \"" + this.completionModel + "\"}";
        try (OutputStream os = fineTuneConn.getOutputStream()) {
            os.write(fineTuneBody.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = fineTuneConn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("Fine-tuning Error: " + responseCode);
        }
    }
}

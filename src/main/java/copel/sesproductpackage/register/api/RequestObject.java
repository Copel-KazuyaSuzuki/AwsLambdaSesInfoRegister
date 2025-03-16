package copel.sesproductpackage.register.api;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import copel.sesproductpackage.register.unit.Content;
import copel.sesproductpackage.register.unit.LineMessagingAPI;
import copel.sesproductpackage.register.unit.RequestType;

/**
 * リクエストのデータ構造を持つクラス.
 *
 * @author 鈴木一矢
 *
 */
public class RequestObject {
    /**
     * リクエスト種別
     */
    private RequestType requestType;
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
     * 原文(このリクエストがMessageであるなら使用される).
     */
    private Content rawContent;
    /**
     * スキルシートID(このリクエストがMessageであり、かつファイルが紐づているなら使用される).
     */
    private String fileId;
    /**
     * ファイル名(このリクエストがFileであるなら使用される).
     */
    private String fileName;
    /**
     * ファイル内容(このリクエストがFileであるなら使用される).
     */
    private byte[] fileData;

    /**
     * コンストラクタでJSON文字列を解析しこのフィールドに値をセットする.
     *
     * @param json JSON文字列
     */
    public RequestObject (final String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);

            this.requestType = jsonNode.has("request_type") && !jsonNode.get("request_type").isNull()
                    ? RequestType.getEnum(jsonNode.get("request_type").asText())
                    : null;

            // リクエスト種別が不明な場合はJSONパースエラー
            if (this.requestType == null) {
                throw new IllegalArgumentException("request_type is NULL");
            }

            this.fromGroup = jsonNode.has("from_group") && !jsonNode.get("from_group").isNull()
                    ? jsonNode.get("from_group").asText()
                    : null;
            this.fromId = jsonNode.has("from_id") && !jsonNode.get("from_id").isNull()
                    ? jsonNode.get("from_id").asText()
                    : null;
            this.fromName = jsonNode.has("from_name") && !jsonNode.get("from_name").isNull()
                    ? jsonNode.get("from_name").asText()
                    : null;
            this.rawContent = jsonNode.has("raw_content") && !jsonNode.get("raw_content").isNull()
                    ? new Content(jsonNode.get("raw_content").asText())
                    : new Content();
            this.fileId = jsonNode.has("file_id") && !jsonNode.get("file_id").isNull()
                    ? jsonNode.get("file_id").asText()
                    : null;
            this.fileName = jsonNode.has("file_name") && !jsonNode.get("file_name").isNull()
                    ? jsonNode.get("file_name").asText()
                    : null;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("リクエストボディの解析に失敗しました。", e);
        }
    }

    /**
     * 与えられたデータが正しいかどうかを判定します.
     *
     * @return 正常データならtrue、異常データならfalse
     */
    public boolean isValid() {
        switch (this.requestType) {
            case LineMessage:
                return this.fromGroup != null && this.fromId != null && this.fromName != null && this.rawContent != null;
            case LineFile:
                return this.fromGroup != null && this.fromId != null && this.fromName != null && this.fileName != null;
            case EmailMessage:
                return this.fromGroup != null && this.fromId != null && this.fromName != null && this.rawContent != null;
            case EmailFile:
                return this.fromGroup != null && this.fromId != null && this.fromName != null && this.fileName != null;
            case OtherMessage:
                return this.fromGroup != null && this.fromId != null && this.fromName != null && this.rawContent != null;
            case OtherFile:
                return this.fromGroup != null && this.fromId != null && this.fromName != null && this.fileName != null;
            default:
                return false;
        }
    }

    /**
     * このリクエストが案件情報であればtrue、そうでなければfalseを返却する.
     *
     * @return 判定結果
     */
    public boolean is案件情報() {
        return (this.requestType == RequestType.LineMessage 
                || this.requestType == RequestType.EmailMessage 
                || this.requestType == RequestType.OtherMessage) 
                && !this.rawContent.isEmpty() 
                && this.rawContent.is案件紹介文();
    }

    /**
     * このリクエストが案件情報であればtrue、そうでなければfalseを返却する.
     *
     * @return 判定結果
     */
    public boolean is要員情報() {
        return (this.requestType == RequestType.LineMessage 
                || this.requestType == RequestType.EmailMessage 
                || this.requestType == RequestType.OtherMessage) 
                && !this.rawContent.isEmpty() 
                && this.rawContent.is要員紹介文();
    }

    /**
     * このリクエストがスキルシートであればtrue、そうでなければfalseを返却する.
     *
     * @return 判定結果
     */
    public boolean isスキルシート() {
        if (RequestType.LineFile.equals(this.requestType)
        		|| RequestType.EmailFile.equals(this.requestType)
        		|| RequestType.OtherFile.equals(this.requestType)) {
            return this.fileName != null && !"".equals(this.fileName);
        } else {
            return false;
        }
    }

    /**
     * このリクエストで送られてきたファイルのデータをダウンロードしこのオブジェクトに持つ.
     *
     * @throws InterruptedException 
     * @throws IOException 
     */
    public void downloadFileData(final String lineChannelAccessToken) throws IOException, InterruptedException {
        switch (this.requestType) {
            case LineFile:
                LineMessagingAPI client = new LineMessagingAPI(lineChannelAccessToken);
                this.fileData = client.getFile(this.fileId);
                break;
            case EmailFile:
                break;
            case OtherFile:
                break;
            default:
                break;
        }
    }

    @Override
    public String toString() {
        return "{"
                + "\n  request_type: " + this.requestType.toString()
                + "\n  from_group: " + this.fromGroup
                + "\n  from_id: " + this.fromId
                + "\n  from_name: " + this.fromName
                + "\n  raw_content: " + this.rawContent
                + "\n  file_id: " + this.fileId
                + "\n  file_name: " + this.fileName
                + "\n}";
    }

    // ================================================
    // GETTER/SETTER
    // ================================================
    public RequestType getRequestType() {
        return requestType;
    }
    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
    public String getFromGroup() {
        return fromGroup;
    }
    public void setFromGroup(String fromGroup) {
        this.fromGroup = fromGroup;
    }
    public String getFromId() {
        return fromId;
    }
    public void setFromId(String fromId) {
        this.fromId = fromId;
    }
    public String getFromName() {
        return fromName;
    }
    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
    public Content getRawContent() {
        return rawContent;
    }
    public void setRawContent(Content rawContent) {
        this.rawContent = rawContent;
    }
    public String getFileId() {
        return fileId;
    }
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public byte[] getFileData() {
        return fileData;
    }
    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
}

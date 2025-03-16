package copel.sesproductpackage.register.api;

/**
 * APIクラスの基底クラス.
 *
 * @author 鈴木一矢
 *
 */
public abstract class ApiBase {
    /**
     * 処理結果ステータス.
     */
    protected int resultStatus;
    /**
     * 処理結果メッセージ.
     */
    protected String resultMessage;

    @Override
    public String toString() {
        return "{\n  status_code: " + this.resultStatus + "\n  message: " + this.resultMessage + "\n}";
    }

    public int getResultStatus() {
        return resultStatus;
    }
    public void setResultStatus(int resultStatus) {
        this.resultStatus = resultStatus;
    }
    public String getResultMessage() {
        return resultMessage;
    }
    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}

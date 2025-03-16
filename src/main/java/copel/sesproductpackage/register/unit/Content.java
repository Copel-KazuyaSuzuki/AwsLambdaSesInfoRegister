package copel.sesproductpackage.register.unit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Content {
    /**
     * 案件情報特徴ワードマスタ.
     */
    private static final String[] JOB_FEATURES_ARRAY = {
            "エンド", "エンド直", "支払サイト", "支払いサイト", "募集", "提案", "外国", "期間", "就業", 
            "歓迎", "支払い", "制限", "契約", "精算", "向け", "見合い", "優遇", "有識者", 
            "プロジェクト", "若手", "概要", "場所", "内容", "時期", "前後", "作業"
        };
    /**
     * 要員情報特徴ワードマスタ.
     */
    private static final String[] PERSONEL_FEATURES_ARRAY = {
            "氏名", "名前", "本人", "自己", "性格", "周囲", "勤怠", "職歴", "アピール", "人物", "対人",
            "稼働", "資格", "正社員", "応相談", "取得", "性別", "試験", "人柄", "男性", "女性",
            "キャッチアップ", "忍耐", "体力", "主体性", "作業", "最寄り駅", "希望", "性別", "直近", "活躍"
        };
    /**
     * 判定基準文字数(この文字数以上であれば要員 or 案件情報と判定する).
     */
    private static final int NUMBER_OF_CRITERIA = 150;

    /**
     * メッセージ原文.
     */
    private String rawContent;
    /**
     * 案件情報特徴ワードの登場回数.
     */
    private int jobFeatureCount = 0;
    /**
     * 要員情報特徴ワードの登場回数.
     */
    private int personelFeatureCount = 0;

    /**
     * コンストラクタ.
     */
    public Content() {
        this.rawContent = null;
    }
    public Content(final String rawContent) {
        this.rawContent = rawContent;

        for (final String fetureKeyWord : JOB_FEATURES_ARRAY) {
            Pattern pattern = Pattern.compile(Pattern.quote(fetureKeyWord)); // 特殊文字をエスケープ
            Matcher matcher = pattern.matcher(this.rawContent);
            while (matcher.find()) {
                this.jobFeatureCount++;
            }
        }

        for (final String fetureKeyWord : PERSONEL_FEATURES_ARRAY) {
            Pattern pattern = Pattern.compile(Pattern.quote(fetureKeyWord)); // 特殊文字をエスケープ
            Matcher matcher = pattern.matcher(this.rawContent);
            while (matcher.find()) {
                this.personelFeatureCount++;
            }
        }
    }

    /**
     * 原文が空であるかどうかを判定します.
     *
     * @return 空であればtrue、そうでなければfalse
     */
    public boolean isEmpty() {
        return this.rawContent == null || "".equals(this.rawContent);
    }

    /**
     * このメッセージが案件の紹介文であるかどうかを判定します.
     *
     * @return {NUMBER_OF_CRITERIA}文字以上かつ案件特徴ワードが要員特徴ワードよりも頻出であればtrue、それ以外はfalse
     */
    public boolean is案件紹介文() {
        return !this.isEmpty()
                ? (this.rawContent.length() >= NUMBER_OF_CRITERIA) && (this.jobFeatureCount > this.personelFeatureCount)
                : false;
    }

    /**
     * このメッセージが要員の紹介文であるかどうかを判定します.
     *
     * @return {NUMBER_OF_CRITERIA}文字以上かつ要員特徴ワードが案件特徴ワードよりも頻出であればtrue、それ以外はfalse
     */
    public boolean is要員紹介文() {
        return !this.isEmpty()
                ? (this.rawContent.length() >= NUMBER_OF_CRITERIA) && (this.personelFeatureCount > this.jobFeatureCount)
                : false;
    }

    @Override
    public String toString() {
        return this.rawContent;
    }
}

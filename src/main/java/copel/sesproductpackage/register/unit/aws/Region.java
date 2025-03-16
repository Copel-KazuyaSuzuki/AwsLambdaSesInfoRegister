package copel.sesproductpackage.register.unit.aws;

/**
 * AWSリージョンの列挙クラス.
 *
 * @author 鈴木一矢
 *
 */
public enum Region {
    バージニア北部("us-east-1"),
    オハイオ("us-east-2"),
    北カリフォルニア("us-west-1"),
    オレゴン("us-west-2"),
    ムンバイ("ap-south-1"),
    大阪("ap-northeast-3"),
    ソウル("ap-northeast-2"),
    シンガポール("ap-southeast-1"),
    シドニー("ap-southeast-2"),
    東京("ap-northeast-1"),
    中部("ca-central-1"),
    フランクフルト("eu-central-1"),
    アイルランド("eu-west-1"),
    ロンドン("eu-west-2"),
    パリ("eu-west-3"),
    ストックホルム("eu-north-1"),
    サンパウロ("sa-east-1");

    private final String regionCode;

    Region(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public static Region fromCode(String code) {
        for (Region region : values()) {
            if (region.getRegionCode().equals(code)) {
                return region;
            }
        }
        throw new IllegalArgumentException("Unknown region code: " + code);
    }
}

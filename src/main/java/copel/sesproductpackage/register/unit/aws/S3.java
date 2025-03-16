package copel.sesproductpackage.register.unit.aws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

/**
 * Amazon S3操作クラス.
 *
 * @author 鈴木一矢
 *
 */
public class S3 {
    /**
     * アクセスキー.
     */
    private String accessKey;
    /**
     * シークレットキー.
     */
    private String secretKey;
    /**
     * リージョン.
     */
    private Region region;
    /**
     * バケット名.
     */
    private String bucketName;
    /**
     * バケット内のファイルパス.
     */
    private String bucketFilePath;
    /**
     * データ.
     */
    private byte[] data;
    /**
     * 更新日時.
     */
    private Date updateDate;

    /**
     * コンストラクタ.
     *
     * @param accessKey アクセスキー.
     * @param secretKey シークレットキー.
     */
    public S3 (final String accessKey, final String secretKey, final Region region) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region != null ? region : Region.東京; // デフォルトは東京
    }

    /**
     * このオブジェクトに持つdataをS3に保存します.
     */
    public void save() {
        // バケット名かファイルパスが空であれば処理を終了する
        if (this.bucketName == null || this.bucketFilePath == null || this.data == null) {
            System.out.println("S3にファイルを保存しようとしましたが、バケット名、ファイルパス、データのいずれかがNULLであるため保存を中止しました。");
            return;
        }

        // AWS認証情報を設定
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(this.accessKey, this.secretKey);

        // AmazonS3クライアントの作成
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(this.region.getRegionCode())
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

        // PutObjectRequestでbyte[]をアップロード
        PutObjectRequest request = new PutObjectRequest(this.bucketName, this.bucketFilePath, 
            new java.io.ByteArrayInputStream(this.data), null);
    
        // アップロード
        s3Client.putObject(request);

        // 更新日時を現在時刻でセット
        this.updateDate = new Date();
    }

    /**
     * このオブジェクトが持つバケット名の中にあるファイルパス先のファイルを取得しdataに持つ.
     *
     * @throws IOException
     */
    public void getFile() throws IOException {
        // バケット名かファイルパスが空であれば処理を終了する
        if (this.bucketName == null || this.bucketFilePath == null) {
            return;
        }

        // AWS認証情報を設定
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(this.accessKey, this.secretKey);

        // AmazonS3クライアントの作成
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(this.region.getRegionCode())
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

        // S3オブジェクトの取得
        S3Object s3Object = s3Client.getObject(this.bucketName, this.bucketFilePath);

        // ファイルの保存日時を取得
        this.updateDate = s3Object.getObjectMetadata().getLastModified();

        // データの取得
        S3ObjectInputStream s3InputStream = s3Object.getObjectContent();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = s3InputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        this.data = byteArrayOutputStream.toByteArray();
    }

    /**
     * このオブジェクトが持つバケット内のファイルを削除する.
     */
    public void delete() {
        // バケット名かファイルパスが空であれば処理を終了する
        if (this.bucketName == null || this.bucketFilePath == null) {
            return;
        }

        // AWS認証情報を設定
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(this.accessKey, this.secretKey);

        // AmazonS3クライアントの作成
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(this.region.getRegionCode())
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

        // S3からファイルを削除
        s3Client.deleteObject(this.bucketName, this.bucketFilePath);
        this.updateDate = null;
    }

    // GETTER / SETTER
    public String getBucketName() {
        return bucketName;
    }
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
    public String getBucketFilePath() {
        return bucketFilePath;
    }
    public void setBucketFilePath(String bucketFilePath) {
        this.bucketFilePath = bucketFilePath;
    }
    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
    public Date getUpdateDate() {
        return updateDate;
    }
}

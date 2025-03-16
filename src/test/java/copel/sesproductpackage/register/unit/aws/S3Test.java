package copel.sesproductpackage.register.unit.aws;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class S3Test {
    @Mock
    private AmazonS3 s3Client;

    private S3 s3;
    private String accessKey = "dummyAccessKey";
    private String secretKey = "dummySecretKey";
    private Region region = Region.東京;
    private String bucketName = "test-bucket";
    private String filePath = "test/path/file.txt";
    private byte[] data = "dummy data".getBytes();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        s3 = new S3(accessKey, secretKey, region);
        s3.setBucketName(bucketName);
        s3.setBucketFilePath(filePath);
        s3.setData(data);
    }

    @Test
    public void testSaveShouldSaveToS3() {
        // Arrange
        PutObjectRequest putObjectRequest = mock(PutObjectRequest.class);
        doNothing().when(s3Client).putObject(any(PutObjectRequest.class));

        // Act
        s3.save();

        // Assert
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class));
    }

    @Test
    public void testSaveShouldNotSaveWhenDataIsNull() {
        s3.setData(null);

        // Act
        s3.save();

        // Assert
        // We expect that putObject is never called due to null data.
        verify(s3Client, times(0)).putObject(any(PutObjectRequest.class));
    }

    @Test
    public void testGetFileShouldRetrieveFileFromS3() throws IOException {
        // Arrange
        S3Object mockS3Object = mock(S3Object.class);
        S3ObjectInputStream mockS3ObjectInputStream = mock(S3ObjectInputStream.class);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        when(s3Client.getObject(bucketName, filePath)).thenReturn(mockS3Object);
        when(mockS3Object.getObjectContent()).thenReturn(mockS3ObjectInputStream);

        // Simulate reading data from the input stream
        when(mockS3ObjectInputStream.read(any(byte[].class))).thenReturn(7).thenReturn(-1);
        mockS3ObjectInputStream.read(new byte[8192]);

        // Act
        s3.getFile();

        // Assert
        assertNotNull(s3.getData());
        assertArrayEquals(data, s3.getData());
        assertNotNull(s3.getUpdateDate());
    }

    @Test
    public void testDeleteShouldDeleteFromS3() {
        // Arrange
        doNothing().when(s3Client).deleteObject(bucketName, filePath);

        // Act
        s3.delete();

        // Assert
        verify(s3Client, times(1)).deleteObject(bucketName, filePath);
        assertNull(s3.getUpdateDate());
    }

    @Test
    public void testSaveShouldNotSaveWhenBucketNameIsNull() {
        // Arrange
        s3.setBucketName(null);

        // Act
        s3.save();

        // Assert
        // We expect that putObject is never called due to null bucket name.
        verify(s3Client, times(0)).putObject(any(PutObjectRequest.class));
    }

    @Test
    public void testSaveShouldNotSaveWhenFilePathIsNull() {
        // Arrange
        s3.setBucketFilePath(null);

        // Act
        s3.save();

        // Assert
        // We expect that putObject is never called due to null file path.
        verify(s3Client, times(0)).putObject(any(PutObjectRequest.class));
    }

    @Test
    public void testGetFileShouldNotRetrieveWhenBucketNameIsNull() throws IOException {
        // Arrange
        s3.setBucketName(null);

        // Act
        s3.getFile();

        // Assert
        // We expect that data is not set because bucket name is null.
        assertNull(s3.getData());
    }

    @Test
    public void testGetFileShouldNotRetrieveWhenFilePathIsNull() throws IOException {
        // Arrange
        s3.setBucketFilePath(null);

        // Act
        s3.getFile();

        // Assert
        // We expect that data is not set because file path is null.
        assertNull(s3.getData());
    }

    @Test
    public void testDeleteShouldNotDeleteWhenBucketNameIsNull() {
        // Arrange
        s3.setBucketName(null);

        // Act
        s3.delete();

        // Assert
        // We expect that deleteObject is never called because bucket name is null.
        verify(s3Client, times(0)).deleteObject(anyString(), anyString());
    }

    @Test
    public void testDeleteShouldNotDeleteWhenFilePathIsNull() {
        // Arrange
        s3.setBucketFilePath(null);

        // Act
        s3.delete();

        // Assert
        // We expect that deleteObject is never called because file path is null.
        verify(s3Client, times(0)).deleteObject(anyString(), anyString());
    }
}

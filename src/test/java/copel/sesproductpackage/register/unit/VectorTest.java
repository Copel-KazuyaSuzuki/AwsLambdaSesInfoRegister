package copel.sesproductpackage.register.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class VectorTest {

    @Mock
    private Transformer mockTransformer;

    private Vector vector;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        vector = new Vector(mockTransformer);
    }

    // embedding() メソッドのテスト
    @Test
    public void testEmbedding_success() throws IOException {
        // モックされたTransformerが返すベクトル値
        float[] expectedValue = {1.0f, 2.0f, 3.0f};
        String inputString = "test string";

        // モックの振る舞いを定義
        when(mockTransformer.embedding(inputString)).thenReturn(expectedValue);

        // rawStringをセット
        vector.setRawString(inputString);

        // embedding()を実行
        vector.embedding();

        // ベクトル値が正しくセットされていることを確認
        assertArrayEquals(expectedValue, vector.getValue());
    }

    @Test
    public void testEmbedding_nullTransformer() throws IOException {
        // Transformerがnullの場合、エンベディング処理は行われないことを確認
        vector = new Vector(null);
        vector.setRawString("test string");
        
        vector.embedding(); // 例外が発生しないことを確認
        assertNull(vector.getValue()); // valueはnullであることを確認
    }

    @Test
    public void testEmbedding_emptyRawString() throws IOException {
        // 空のrawStringが設定されている場合、エンベディング処理が行われないことを確認
        vector.setRawString("");
        
        vector.embedding(); // 例外が発生しないことを確認
        assertNull(vector.getValue()); // valueはnullであることを確認
    }

    @Test
    public void testEmbedding_nullRawString() throws IOException {
        // nullのrawStringが設定されている場合、エンベディング処理が行われないことを確認
        vector.setRawString(null);
        
        vector.embedding(); // 例外が発生しないことを確認
        assertNull(vector.getValue()); // valueはnullであることを確認
    }

    // Getter / Setter のテスト
    @Test
    public void testGetRawString() {
        String testString = "test string";
        vector.setRawString(testString);
        
        assertEquals(testString, vector.getRawString());
    }

    @Test
    public void testGetValue() throws IOException, RuntimeException {
        float[] expectedValue = {1.0f, 2.0f, 3.0f};
        vector.setRawString("test string");
        when(mockTransformer.embedding("test string")).thenReturn(expectedValue);
        
        vector.embedding();

        assertArrayEquals(expectedValue, vector.getValue());
    }

    // toString() メソッドのテスト
    @Test
    public void testToString() throws IOException {
        float[] testValue = {1.1f, 2.2f, 3.3f};
        vector.setRawString("test string");
        when(mockTransformer.embedding("test string")).thenReturn(testValue);

        vector.embedding();

        String expectedToString = "[1.1,2.2,3.3]";
        assertEquals(expectedToString, vector.toString());
    }
}

package copel.sesproductpackage.register.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

public class OpenAITest {

    private static final String API_KEY = "test-api-key";
    private OpenAI openAI;

    @BeforeEach
    public void setUp() {
        openAI = new OpenAI(API_KEY);
    }

    @Test
    public void testEmbedding() throws IOException {
        String input = "test input";
        // Mock HTTP response
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getResponseCode()).thenReturn(200);
        when(mockConnection.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("{\"data\": [{\"embedding\": [0.1, 0.2, 0.3]}]}".getBytes()));

        try (MockedStatic<URL> mockedUrl = mockStatic(URL.class);
             MockedStatic<HttpURLConnection> mockedHttpURLConnection = mockStatic(HttpURLConnection.class)) {

            mockedUrl.when(() -> new URL(anyString())).thenReturn(mockConnection);
	         // URLをモックする
            URL mockUrl = mock(URL.class);

            // URL.openConnection()をモックして、HttpURLConnectionを返す
            when(mockUrl.openConnection()).thenReturn(mockConnection);

            // 実際にテスト対象のクラスでmockUrlを使うようにする
            mockedHttpURLConnection.when(() -> mockUrl.openConnection()).thenReturn(mockConnection);

            float[] result = openAI.embedding(input);
            assertNotNull(result);
            assertEquals(3, result.length);
            assertEquals(0.1f, result[0]);
            assertEquals(0.2f, result[1]);
            assertEquals(0.3f, result[2]);
        }
    }

    @Test
    public void testGenerate() throws IOException {
        String prompt = "test prompt";
        String expectedResponse = "generated response";
        
        // Mock HTTP response
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getResponseCode()).thenReturn(200);
        when(mockConnection.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("{\\\"choices\\\": [{\\\"message\\\": {\\\"content\\\": \\\"\" + expectedResponse + \"\\\"}}]}".getBytes()));

        try (MockedStatic<URL> mockedUrl = mockStatic(URL.class);
             MockedStatic<HttpURLConnection> mockedHttpURLConnection = mockStatic(HttpURLConnection.class)) {

            mockedUrl.when(() -> new URL(anyString())).thenReturn(mockConnection);
	         // URLをモックする
            URL mockUrl = mock(URL.class);

            // URL.openConnection()をモックして、HttpURLConnectionを返す
            when(mockUrl.openConnection()).thenReturn(mockConnection);

            // 実際にテスト対象のクラスでmockUrlを使うようにする
            mockedHttpURLConnection.when(() -> mockUrl.openConnection()).thenReturn(mockConnection);

            String result = openAI.generate(prompt);
            assertEquals(expectedResponse, result);
        }
    }

    @Test
    public void testGenerateWithTemperature() throws IOException {
        String prompt = "test prompt with temperature";
        float temperature = 0.5f;
        String expectedResponse = "generated response with temp";

        // Mock HTTP response
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getResponseCode()).thenReturn(200);
        String input = "{\"choices\": [{\"message\": {\"content\": \"" + expectedResponse + "\"}}]}";
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

        try (MockedStatic<URL> mockedUrl = mockStatic(URL.class);
             MockedStatic<HttpURLConnection> mockedHttpURLConnection = mockStatic(HttpURLConnection.class)) {

            mockedUrl.when(() -> new URL(anyString())).thenReturn(mockConnection);
	         // URLをモックする
            URL mockUrl = mock(URL.class);

            // URL.openConnection()をモックして、HttpURLConnectionを返す
            when(mockUrl.openConnection()).thenReturn(mockConnection);

            // 実際にテスト対象のクラスでmockUrlを使うようにする
            mockedHttpURLConnection.when(() -> mockUrl.openConnection()).thenReturn(mockConnection);

            String result = openAI.generate(prompt, temperature);
            assertEquals(expectedResponse, result);
        }
    }

    @Test
    public void testFineTuning() throws IOException {
        String trainingData = "training data for fine tuning";

        // Mock HTTP response
        HttpURLConnection mockFileConnection = mock(HttpURLConnection.class);
        when(mockFileConnection.getResponseCode()).thenReturn(200);
        when(mockFileConnection.getInputStream()).thenReturn(new java.io.ByteArrayInputStream("{\"id\": \"file-id\"}".getBytes()));

        HttpURLConnection mockFineTuneConnection = mock(HttpURLConnection.class);
        when(mockFineTuneConnection.getResponseCode()).thenReturn(200);

        try (MockedStatic<URL> mockedUrl = mockStatic(URL.class);
             MockedStatic<HttpURLConnection> mockedHttpURLConnection = mockStatic(HttpURLConnection.class)) {

            mockedUrl.when(() -> new URL(anyString()))
                    .thenReturn(mockFileConnection)
                    .thenReturn(mockFineTuneConnection);

            openAI.fineTuning(trainingData);
            verify(mockFileConnection, times(1)).getResponseCode();
            verify(mockFineTuneConnection, times(1)).getResponseCode();
        }
    }

    @Test
    public void testEmbeddingBadRequest() throws IOException {
        String input = "test input";

        // Mock HTTP response
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getResponseCode()).thenReturn(400);

        try (MockedStatic<URL> mockedUrl = mockStatic(URL.class);
             MockedStatic<HttpURLConnection> mockedHttpURLConnection = mockStatic(HttpURLConnection.class)) {

            mockedUrl.when(() -> new URL(anyString())).thenReturn(mockConnection);
	         // URLをモックする
            URL mockUrl = mock(URL.class);

            // URL.openConnection()をモックして、HttpURLConnectionを返す
            when(mockUrl.openConnection()).thenReturn(mockConnection);

            // 実際にテスト対象のクラスでmockUrlを使うようにする
            mockedHttpURLConnection.when(() -> mockUrl.openConnection()).thenReturn(mockConnection);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> openAI.embedding(input));
            assertEquals("400 Bad Request: 無効なパラメータ、不適切なリクエストフォーマット、支払い上限超過エラー", exception.getMessage());
        }
    }
}

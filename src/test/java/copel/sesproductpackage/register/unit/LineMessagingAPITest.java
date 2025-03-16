package copel.sesproductpackage.register.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LineMessagingAPITest {

    private LineMessagingAPI lineMessagingAPI;
    private HttpClient mockHttpClient;
    private HttpResponse<String> mockResponse;
    private HttpResponse<byte[]> mockFileResponse;

    @SuppressWarnings("unchecked")
	@BeforeEach
    void setUp() {
        // モックの作成
        mockHttpClient = mock(HttpClient.class);
        mockResponse = mock(HttpResponse.class);
        mockFileResponse = mock(HttpResponse.class);
        
        // モックをLineMessagingAPIにセット
        lineMessagingAPI = new LineMessagingAPI("mockChannelAccessToken");
    }

    @Test
    void testAddMessage() {
        lineMessagingAPI.addMessage("Test Message");
        List<String> messages = lineMessagingAPI.getMessageList();
        assertEquals(1, messages.size(), "Message should be added to the list");
        assertEquals("Test Message", messages.get(0), "The added message should match");
    }

    @Test
    void testSendSeparate() throws Exception {
        // モックレスポンス設定
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(200);

        lineMessagingAPI.addMessage("Test message");
        lineMessagingAPI.sendSeparate("testUserId");

        // HTTPリクエストが送信されたことを確認
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void testBroadCast() throws Exception {
        // モックレスポンス設定
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(200);

        lineMessagingAPI.addMessage("Broadcast message");
        lineMessagingAPI.broadCast();

        // HTTPリクエストが送信されたことを確認
        verify(mockHttpClient, times(1)).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void testGetFile() throws IOException, InterruptedException {
        byte[] mockFileContent = new byte[]{1, 2, 3};
        
        // モックレスポンス設定
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofByteArray())))
            .thenReturn(mockFileResponse);
        when(mockFileResponse.statusCode()).thenReturn(200);
        when(mockFileResponse.body()).thenReturn(mockFileContent);

        byte[] fileContent = lineMessagingAPI.getFile("mockMessageId");

        // ファイルの内容が取得できたことを確認
        assertArrayEquals(mockFileContent, fileContent, "The file content should be returned correctly");
    }

    @Test
    void testGetFileError() throws IOException, InterruptedException {
        // モックレスポンス設定
        when(mockHttpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofByteArray())))
            .thenReturn(mockFileResponse);
        when(mockFileResponse.statusCode()).thenReturn(500);  // エラーステータス

        byte[] fileContent = lineMessagingAPI.getFile("mockMessageId");

        // エラーが発生した場合、nullが返ることを確認
        assertNull(fileContent, "File content should be null on error");
    }
}

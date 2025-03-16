package copel.sesproductpackage.register.api;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import copel.sesproductpackage.register.unit.LineMessagingAPI;
import copel.sesproductpackage.register.unit.RequestType;

class RequestObjectTest {
    
    private RequestObject requestObject;

    @BeforeEach
    void setUp() {
        // 必要に応じて、モックやテストデータを準備
    }

    @Test
    void testRequestObjectConstructorValidJson() {
        String json = "{"
                + "\"request_type\":\"LineMessage\","
                + "\"from_group\":\"group1\","
                + "\"from_id\":\"id123\","
                + "\"from_name\":\"John Doe\","
                + "\"raw_content\":\"content\","
                + "\"file_id\":\"file1\","
                + "\"file_name\":\"file1.txt\""
                + "}";

        requestObject = new RequestObject(json);
        
        assertNotNull(requestObject);
        assertEquals(RequestType.LineMessage, requestObject.getRequestType());
        assertEquals("group1", requestObject.getFromGroup());
        assertEquals("id123", requestObject.getFromId());
        assertEquals("John Doe", requestObject.getFromName());
    }

    @Test
    void testRequestObjectConstructorInvalidJson() {
        String json = "{"
                + "\"request_type\":\"InvalidType\","
                + "\"from_group\":\"group1\""
                + "}";

        assertThrows(IllegalArgumentException.class, () -> new RequestObject(json));
    }

    @Test
    void testIsValidLineMessage() {
        String json = "{"
                + "\"request_type\":\"LineMessage\","
                + "\"from_group\":\"group1\","
                + "\"from_id\":\"id123\","
                + "\"from_name\":\"John Doe\","
                + "\"raw_content\":\"content\""
                + "}";

        requestObject = new RequestObject(json);
        assertTrue(requestObject.isValid());
    }

    @Test
    void testIsValidLineFile() {
        String json = "{"
                + "\"request_type\":\"LineFile\","
                + "\"from_group\":\"group1\","
                + "\"from_id\":\"id123\","
                + "\"from_name\":\"John Doe\","
                + "\"file_name\":\"file.txt\""
                + "}";

        requestObject = new RequestObject(json);
        assertTrue(requestObject.isValid());
    }

    @Test
    void testIs案件情報True() {
        String json = "{"
                + "\"request_type\":\"LineMessage\","
                + "\"from_group\":\"group1\","
                + "\"from_id\":\"id123\","
                + "\"from_name\":\"John Doe\","
                + "\"raw_content\":\"案件紹介文\""
                + "}";

        requestObject = new RequestObject(json);
        assertTrue(requestObject.is案件情報());
    }

    @Test
    void testIs案件情報False() {
        String json = "{"
                + "\"request_type\":\"LineMessage\","
                + "\"from_group\":\"group1\","
                + "\"from_id\":\"id123\","
                + "\"from_name\":\"John Doe\","
                + "\"raw_content\":\"\""
                + "}";

        requestObject = new RequestObject(json);
        assertFalse(requestObject.is案件情報());
    }

    @Test
    void testIs要員情報True() {
        String json = "{"
                + "\"request_type\":\"LineMessage\","
                + "\"from_group\":\"group1\","
                + "\"from_id\":\"id123\","
                + "\"from_name\":\"John Doe\","
                + "\"raw_content\":\"要員紹介文\""
                + "}";

        requestObject = new RequestObject(json);
        assertTrue(requestObject.is要員情報());
    }

    @Test
    void testIs要員情報False() {
        String json = "{"
                + "\"request_type\":\"LineMessage\","
                + "\"from_group\":\"group1\","
                + "\"from_id\":\"id123\","
                + "\"from_name\":\"John Doe\","
                + "\"raw_content\":\"\""
                + "}";

        requestObject = new RequestObject(json);
        assertFalse(requestObject.is要員情報());
    }

    @Test
    void testIsスキルシートTrue() {
        String json = "{"
                + "\"request_type\":\"LineFile\","
                + "\"from_group\":\"group1\","
                + "\"from_id\":\"id123\","
                + "\"from_name\":\"John Doe\","
                + "\"file_name\":\"skill_sheet.pdf\""
                + "}";

        requestObject = new RequestObject(json);
        assertTrue(requestObject.isスキルシート());
    }

    @Test
    void testIsスキルシートFalse() {
        String json = "{"
                + "\"request_type\":\"LineMessage\","
                + "\"from_group\":\"group1\","
                + "\"from_id\":\"id123\","
                + "\"from_name\":\"John Doe\""
                + "}";

        requestObject = new RequestObject(json);
        assertFalse(requestObject.isスキルシート());
    }

    @Test
    void testDownloadFileData() throws IOException, InterruptedException {
        String json = "{"
                + "\"request_type\":\"LineFile\","
                + "\"from_group\":\"group1\","
                + "\"from_id\":\"id123\","
                + "\"from_name\":\"John Doe\","
                + "\"file_id\":\"file1\""
                + "}";

        requestObject = new RequestObject(json);

        LineMessagingAPI mockClient = Mockito.mock(LineMessagingAPI.class);
        Mockito.when(mockClient.getFile("file1")).thenReturn(new byte[] {1, 2, 3});

        // 実際のメソッドをテスト
        requestObject.downloadFileData("lineChannelAccessToken");

        assertNotNull(requestObject.getFileData());
        assertArrayEquals(new byte[] {1, 2, 3}, requestObject.getFileData());
    }

    @Test
    void testToString() {
        String json = "{"
                + "\"request_type\":\"LineMessage\","
                + "\"from_group\":\"group1\","
                + "\"from_id\":\"id123\","
                + "\"from_name\":\"John Doe\","
                + "\"raw_content\":\"content\""
                + "}";

        requestObject = new RequestObject(json);
        
        String expected = "{\n"
                + "  request_type: LineMessage\n"
                + "  from_group: group1\n"
                + "  from_id: id123\n"
                + "  from_name: John Doe\n"
                + "  raw_content: content\n"
                + "  file_id: null\n"
                + "  file_name: null\n"
                + "}";
        
        assertEquals(expected, requestObject.toString());
    }
}

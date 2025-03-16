package copel.sesproductpackage.register.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApiBaseTest {
    
    private ApiBase apiBase;

    @BeforeEach
    void setUp() {
        // ApiBaseはabstractなので、テスト用の具体クラスを作成します。
        apiBase = new ApiBase() {};
    }

    @Test
    void testGetResultStatus() {
        apiBase.setResultStatus(200);
        assertEquals(200, apiBase.getResultStatus());
    }

    @Test
    void testSetResultStatus() {
        apiBase.setResultStatus(400);
        assertEquals(400, apiBase.getResultStatus());
    }

    @Test
    void testGetResultMessage() {
        apiBase.setResultMessage("Success");
        assertEquals("Success", apiBase.getResultMessage());
    }

    @Test
    void testSetResultMessage() {
        apiBase.setResultMessage("Error");
        assertEquals("Error", apiBase.getResultMessage());
    }

    @Test
    void testToString() {
        apiBase.setResultStatus(500);
        apiBase.setResultMessage("Internal Server Error");
        
        String expected = "{\n  status_code: 500\n  message: Internal Server Error\n}";
        assertEquals(expected, apiBase.toString());
    }
}

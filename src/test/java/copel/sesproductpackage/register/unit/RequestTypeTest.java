package copel.sesproductpackage.register.unit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class RequestTypeTest {

    // getEnumメソッドのテスト
    @Test
    public void testGetEnum_validCode() {
        // 有効なコードでEnumが正しく返されるか確認
        assertEquals(RequestType.LineMessage, RequestType.getEnum("11"));
        assertEquals(RequestType.LineFile, RequestType.getEnum("12"));
        assertEquals(RequestType.EmailMessage, RequestType.getEnum("21"));
        assertEquals(RequestType.EmailFile, RequestType.getEnum("22"));
        assertEquals(RequestType.OtherMessage, RequestType.getEnum("01"));
        assertEquals(RequestType.OtherFile, RequestType.getEnum("02"));
    }

    @Test
    public void testGetEnum_invalidCode() {
        // 無効なコードでOtherMessageが返されるか確認
        assertEquals(RequestType.OtherMessage, RequestType.getEnum("99"));
    }

    @Test
    public void testGetEnum_nullCode() {
        // nullコードに対してnullが返されるか確認
        assertNull(RequestType.getEnum(null));
    }

    // getCodeメソッドのテスト
    @Test
    public void testGetCode() {
        // 各Enumが持つcode値が正しいか確認
        assertEquals("11", RequestType.LineMessage.getCode());
        assertEquals("12", RequestType.LineFile.getCode());
        assertEquals("21", RequestType.EmailMessage.getCode());
        assertEquals("22", RequestType.EmailFile.getCode());
        assertEquals("01", RequestType.OtherMessage.getCode());
        assertEquals("02", RequestType.OtherFile.getCode());
    }

    // Enumのインスタンス確認
    @Test
    public void testEnumInstances() {
        // Enumのインスタンスが正しいか確認
        assertNotNull(RequestType.LineMessage);
        assertNotNull(RequestType.LineFile);
        assertNotNull(RequestType.EmailMessage);
        assertNotNull(RequestType.EmailFile);
        assertNotNull(RequestType.OtherMessage);
        assertNotNull(RequestType.OtherFile);
    }
}

package copel.sesproductpackage.register.unit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContentTest {

    private Content content;

    @BeforeEach
    void setUp() {
        // 空のコンテンツを初期化
        content = new Content();
    }

    @Test
    void testIsEmpty_EmptyContent() {
        assertTrue(content.isEmpty(), "Content should be empty");
    }

    @Test
    void testIsEmpty_NonEmptyContent() {
        content = new Content("This is some test content.");
        assertFalse(content.isEmpty(), "Content should not be empty");
    }

    @Test
    void testIs案件紹介文_案件紹介文() {
        String rawContent = "エンド 募集 勤怠 アピール 期間 就業 文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字";
        content = new Content(rawContent);
        assertTrue(content.is案件紹介文(), "Content should be judged as '案件紹介文'");
    }

    @Test
    void testIs案件紹介文_Not案件紹介文() {
        String rawContent = "氏名 稼働 応相談 応相談文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字";
        content = new Content(rawContent);
        assertFalse(content.is案件紹介文(), "Content should not be judged as '案件紹介文'");
    }

    @Test
    void testIs要員紹介文_要員紹介文() {
        String rawContent = "氏名 応相談 稼働 応相談文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字";
        content = new Content(rawContent);
        assertTrue(content.is要員紹介文(), "Content should be judged as '要員紹介文'");
    }

    @Test
    void testIs要員紹介文_Not要員紹介文() {
        String rawContent = "エンド 募集 勤怠 アピール文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字文字";
        content = new Content(rawContent);
        assertFalse(content.is要員紹介文(), "Content should not be judged as '要員紹介文'");
    }

    @Test
    void testToString() {
        String rawContent = "エンド 期間 募集";
        content = new Content(rawContent);
        assertEquals(rawContent, content.toString(), "toString() should return the raw content");
    }
}

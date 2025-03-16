package copel.sesproductpackage.register.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SkillSheetTest {

    @Mock
    private Transformer mockTransformer;

    private SkillSheet skillSheet;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        skillSheet = new SkillSheet("1", "test.docx", "");
    }

    // setFileContentFromByte() メソッドのテスト
    @Test
    public void testSetFileContentFromByte_docx() throws IOException {
        String testContent = "This is a test docx content";
        byte[] data = testContent.getBytes(StandardCharsets.UTF_8);

        skillSheet.setFileContentFromByte(data);

        assertNotNull(skillSheet.getFileContent());
        assertTrue(skillSheet.getFileContent().contains(testContent));
    }

    @Test
    public void testSetFileContentFromByte_pdf() throws IOException {
        String testContent = "This is a test pdf content";
        byte[] data = testContent.getBytes(StandardCharsets.UTF_8);

        skillSheet.setFileName("test.pdf");
        skillSheet.setFileContentFromByte(data);

        assertNotNull(skillSheet.getFileContent());
        assertTrue(skillSheet.getFileContent().contains(testContent));
    }

    @Test
    public void testSetFileContentFromByte_xlsx() throws IOException {
        String testContent = "This is a test xlsx content";
        byte[] data = testContent.getBytes(StandardCharsets.UTF_8);

        skillSheet.setFileName("test.xlsx");
        skillSheet.setFileContentFromByte(data);

        assertNotNull(skillSheet.getFileContent());
        assertTrue(skillSheet.getFileContent().contains(testContent));
    }

    @Test
    public void testSetFileContentFromByte_nullFileName() throws IOException {
        byte[] data = "Test".getBytes(StandardCharsets.UTF_8);
        skillSheet.setFileName(null);

        skillSheet.setFileContentFromByte(data);

        assertNull(skillSheet.getFileContent());  // ファイル名がnullの場合はcontentはセットされない
    }

    // generateSummary() メソッドのテスト
    @Test
    public void testGenerateSummary_success() throws IOException {
        String testContent = "This is a test content for summary generation.";
        skillSheet.setFileContent(testContent);

        String summaryContent = "Summary generated.";
        when(mockTransformer.generate(anyString())).thenReturn(summaryContent);

        skillSheet.generateSummary(mockTransformer);

        assertEquals(summaryContent, skillSheet.getFileContentSummary());
    }

    @Test
    public void testGenerateSummary_emptyFileContent() {
        skillSheet.setFileContent("");

        assertThrows(IOException.class, () -> skillSheet.generateSummary(mockTransformer));
    }

    @Test
    public void testGenerateSummary_nullFileContent() {
        skillSheet.setFileContent(null);

        assertThrows(IOException.class, () -> skillSheet.generateSummary(mockTransformer));
    }

    @Test
    public void testGenerateSummary_transformerThrowsException() throws IOException {
        String testContent = "This is a test content for summary generation.";
        skillSheet.setFileContent(testContent);

        when(mockTransformer.generate(anyString())).thenThrow(new RuntimeException("Transformer error"));

        assertThrows(RuntimeException.class, () -> skillSheet.generateSummary(mockTransformer));
    }
}

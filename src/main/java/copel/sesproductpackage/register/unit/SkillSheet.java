package copel.sesproductpackage.register.unit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

import lombok.extern.slf4j.Slf4j;

/**
 * スキルシートの情報を持つクラス.
 *
 * @author 鈴木一矢
 *
 */
@Slf4j
public class SkillSheet {
    /**
     * 要約用プロンプト.
     */
    private final static String SUMMARIZE_PROMPT = "あなたはプロの人材紹介エージェントの営業マンです。次の文章を要約し、人物の経歴書を箇条書きで作成して下さい。スキルごとに何年経験したかを合計して集計してください。また、最後にどういった分野に強味をもつ人物なのかを説明してください。ここまで合計で合計300文字以内で作成してください。";

    /**
     * ファイルID.
     */
    private String fileId;
    /**
     * ファイル名.
     */
    private String fileName;
    /**
     * ファイル内容.
     */
    private String fileContent;
    /**
     * ファイル内容要約.
     */
    private String fileContentSummary;

    /**
     * デフォルトコンストラクタ.
     */
    public SkillSheet() {}
    /**
     * コンストラクタ.
     *
     * @param fileId ファイルID 
     * @param fileName ファイル名
     * @param fileContent ファイル内容
     */
    public SkillSheet(final String fileId, final String fileName, final String fileContent) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileContent = fileContent;
    }

    /**
     * ファイルのバイナリデータからファイルに記載されているテキストデータを取得しこのクラスのfileContentにセットします.
     *
     * @param data バイナリデータ
     * @throws IOException
     */
    public void setFileContentFromByte(final byte[] data) throws IOException {
        // ファイル名が存在する場合のみ処理する
        if (this.fileName != null && data != null) {
            InputStream inputStream = new ByteArrayInputStream(data);

            // Wordファイルを処理
            if (this.fileName.endsWith(".docx")) {
                StringBuilder text = new StringBuilder();
                XWPFDocument doc = new XWPFDocument(inputStream);
                for (XWPFParagraph paragraph : doc.getParagraphs()) {
                    text.append(paragraph.getText()).append("\n");
                }
                for (XWPFTable table : doc.getTables()) {
                    for (int rowIdx = 0; rowIdx < table.getRows().size(); rowIdx++) {
                        for (int cellIdx = 0; cellIdx < table.getRow(rowIdx).getTableCells().size(); cellIdx++) {
                            XWPFTableCell cell = table.getRow(rowIdx).getCell(cellIdx);
                            text.append(cell.getText()).append("\t");
                        }
                        text.append("\n");
                    }
                }
                doc.close();
                this.fileContent = text.toString();
            }
            // Wordファイルを処理
            else if (this.fileName.endsWith(".doc")) {
                StringBuilder text = new StringBuilder();
                 HWPFDocument doc = new HWPFDocument(inputStream);
                 WordExtractor extractor = new WordExtractor(doc);
                 for (String paragraphText : extractor.getParagraphText()) {
                	 text.append(paragraphText);
                 }
                 text.append(extractor.getText());
                 this.fileContent = text.toString();
                 extractor.close();
                 doc.close();
            }
            // PDFファイルを処理
            else if (this.fileName.endsWith(".pdf")) {
                PDDocument document = PDDocument.load(inputStream);
                PDFTextStripper stripper = new PDFTextStripper();
                this.fileContent = stripper.getText(document);
                document.close();
            }
            // Excelファイルを処理
            else if (this.fileName.endsWith(".xlsx") || this.fileName.endsWith(".xls")) {
                StringBuilder text = new StringBuilder();
                Workbook workbook = this.fileName.endsWith(".xlsx") ? new XSSFWorkbook(inputStream) : new HSSFWorkbook(inputStream);
                for (Row row : workbook.getSheetAt(0)) {
                    for (Cell cell : row) {
                        CustomCell customCell = new CustomCell(cell);
                        text.append(customCell.getValue(workbook.getCreationHelper().createFormulaEvaluator())).append(",");
                    }
                    text.append("\n");
                }
                workbook.close();
                this.fileContent = text.toString();
            }
            // その他のファイルの場合
            else {
            	log.info("Word/Excel/PDF以外のファイルのため、スキルシート内容の取得処理をせずに終了します。");
            }
        }
    }

    /**
     * このスキルシートの要約を生成しfileContentSummaryにセットします.
     *
     * @throws IOException 
     * @throws RuntimeException 
     */
    public void generateSummary(final Transformer transformer) throws IOException, RuntimeException {
        if (this.fileContent != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(SUMMARIZE_PROMPT);
            stringBuilder.append(this.fileContent.replaceAll("[\\p{C}\"]", "")); // 制御文字とダブルクォーテーションを削除
            this.fileContentSummary = transformer.generate(stringBuilder.toString());
        } else {
            throw new IOException("ファイルの中身が空のため、要約の作成を中止します。");
        }
    }

    // GETTER / SETTER
    public String getFileId() {
        return fileId;
    }
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFileContent() {
        return fileContent;
    }
    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
    public String getFileContentSummary() {
        return fileContentSummary;
    }
    public void setFileContentSummary(String fileContentSummary) {
        this.fileContentSummary = fileContentSummary;
    }
}

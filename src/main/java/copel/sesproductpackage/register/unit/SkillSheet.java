package copel.sesproductpackage.register.unit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

/**
 * スキルシートの情報を持つクラス.
 *
 * @author 鈴木一矢
 *
 */
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

    public SkillSheet() {}
    public SkillSheet(final String skillsheetId, final String fileName, final String fileContent) {
        this.fileId = skillsheetId;
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
            if (this.fileName.endsWith(".docx")) {
                XWPFDocument doc = new XWPFDocument(inputStream);
                StringBuilder text = new StringBuilder();
                // パラグラフを追加
                for (XWPFParagraph paragraph : doc.getParagraphs()) {
                    text.append(paragraph.getText()).append("\n");
                }
                // テーブルがあればその内容を追加
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
            } else if (this.fileName.endsWith(".pdf")) {
                PDDocument document = PDDocument.load(inputStream);
                PDFTextStripper stripper = new PDFTextStripper();
                this.fileContent = stripper.getText(document);
                document.close();
            } else if (this.fileName.endsWith(".xlsx")) {
                Workbook workbook = new XSSFWorkbook(inputStream);
                StringBuilder text = new StringBuilder();
                for (Sheet sheet : workbook) {
                    text.append(sheet.getSheetName() + "\n");
                    for (Row row : sheet) {
                        for (Cell cell : row) {
                            CustomCell customCell = new CustomCell(cell);
                            text.append(customCell.getValue(workbook.getCreationHelper().createFormulaEvaluator())).append(",");
                        }
                        text.append("\n");
                    }
                }
                workbook.close();
                this.fileContent = text.toString();
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

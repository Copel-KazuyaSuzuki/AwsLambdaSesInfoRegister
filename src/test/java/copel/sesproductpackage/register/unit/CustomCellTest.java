package copel.sesproductpackage.register.unit;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomCellTest {

    private CustomCell customCell;
    private Cell mockCell;
    private FormulaEvaluator mockFormulaEvaluator;

    @BeforeEach
    void setUp() {
        // モックの作成
        mockCell = mock(Cell.class, RETURNS_DEEP_STUBS);
        mockFormulaEvaluator = mock(FormulaEvaluator.class);
        customCell = new CustomCell(mockCell);
    }

    @Test
    void testGetValueWithNumericCellType() {
        // NUMERIC型の場合
        when(mockCell.getCellType()).thenReturn(CellType.NUMERIC);
        when(mockCell.getNumericCellValue()).thenReturn(123.45);

        String value = customCell.getValue(mockFormulaEvaluator);
        assertEquals("123.45", value, "Numeric value should be returned as string");
    }

    @Test
    void testGetValueWithStringCellType() {
        // STRING型の場合
        when(mockCell.getCellType()).thenReturn(CellType.STRING);
        when(mockCell.getStringCellValue()).thenReturn("Test String");

        String value = customCell.getValue(mockFormulaEvaluator);
        assertEquals("Test String", value, "String value should be returned as is");
    }

    @Test
    void testGetValueWithBooleanCellType() {
        // BOOLEAN型の場合
        when(mockCell.getCellType()).thenReturn(CellType.BOOLEAN);
        when(mockCell.getBooleanCellValue()).thenReturn(true);

        String value = customCell.getValue(mockFormulaEvaluator);
        assertEquals("true", value, "Boolean value should be returned as string");
    }

    @Test
    void testGetValueWithDateCellType() {
        // 日付型の場合
        when(mockCell.getCellType()).thenReturn(CellType.NUMERIC);
        when(mockCell.getNumericCellValue()).thenReturn(44360.0); // 2021/01/01 (Excel Date)
        when(mockCell.getDateCellValue()).thenReturn(new Date(1609459200000L)); // January 1, 2021

        String value = customCell.getValue(mockFormulaEvaluator);
        assertEquals("2021/01/01", value, "Date value should be formatted as yyyy/MM/dd");
    }

    @Test
    void testGetValueWithFormulaCellType() {
        // FORMULA型の場合
        when(mockCell.getCellType()).thenReturn(CellType.FORMULA);
        
        // CellValue型をモックする
        CellValue mockCellValue = mock(CellValue.class);
        when(mockFormulaEvaluator.evaluate(mockCell)).thenReturn(mockCellValue);
        when(mockCellValue.formatAsString()).thenReturn("Formula Result");

        String value = customCell.getValue(mockFormulaEvaluator);
        assertEquals("Formula Result", value, "Formula value should be evaluated and returned as string");
    }

    @Test
    void testGetValueWithFormulaCellTypeNotImplemented() {
        // 数式が評価できない場合 (NotImplementedException)
        when(mockCell.getCellType()).thenReturn(CellType.FORMULA);
        doThrow(new NotImplementedException("Not implemented")).when(mockFormulaEvaluator).evaluate(mockCell);

        String value = customCell.getValue(mockFormulaEvaluator);
        assertEquals(mockCell.toString(), value, "If formula evaluation is not implemented, the cell's string representation should be returned");
    }

    @Test
    void testIsExcelDateTrue() {
        // Excelの日付数値判定がtrueの場合
        when(mockCell.getNumericCellValue()).thenReturn(44360.0); // Excelの2021/01/01に対応する数値

        boolean result = customCell.isExcelDate();
        assertTrue(result, "Should return true for valid Excel date");
    }

    @Test
    void testIsExcelDateFalse() {
        // Excelの日付数値判定がfalseの場合
        when(mockCell.getNumericCellValue()).thenReturn(12345.6); // 無効な日付

        boolean result = customCell.isExcelDate();
        assertFalse(result, "Should return false for invalid Excel date");
    }
}

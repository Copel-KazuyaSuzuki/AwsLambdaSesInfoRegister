package copel.sesproductpackage.register.unit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * LogicalOperators クラスのテスト.
 */
public class LogicalOperatorsTest {

    private LogicalOperators logicalOperators;

    @BeforeEach
    public void setUp() {
        // 初期設定: 論理演算子 AND と検索条件値 "value" でインスタンス作成
        logicalOperators = new LogicalOperators(LogicalOperators.論理演算子.AND, "value");
    }

    @Test
    public void testConstructorWithTwoArguments() {
        // 論理演算子と検索条件値だけでインスタンスを作成
        LogicalOperators logicalOperators = new LogicalOperators(LogicalOperators.論理演算子.OR, "value");
        assertNotNull(logicalOperators);
        assertEquals("value", logicalOperators.getValue());
    }

    @Test
    public void testConstructorWithThreeArguments() {
        // 論理演算子、カラム名、検索条件値を指定
        LogicalOperators logicalOperators = new LogicalOperators(LogicalOperators.論理演算子.NOT, "columnName", "value");
        assertNotNull(logicalOperators);
        assertEquals("value", logicalOperators.getValue());
    }

    @Test
    public void testGetLikeQuery() {
        // 正常な動作をテスト
        logicalOperators = new LogicalOperators(LogicalOperators.論理演算子.AND, "columnName", "value");
        String query = logicalOperators.getLikeQuery();
        assertNotNull(query);
        assertEquals(" AND columnName LIKE ?", query);

        // 論理演算子が null の場合
        logicalOperators = new LogicalOperators(null, "columnName", "value");
        assertNull(logicalOperators.getLikeQuery());

        // カラム名が null の場合
        logicalOperators = new LogicalOperators(LogicalOperators.論理演算子.AND, null, "value");
        assertNull(logicalOperators.getLikeQuery());
    }

    @Test
    public void testGetValue() {
        // 検索条件値が正しく返されることを確認
        assertEquals("value", logicalOperators.getValue());
    }

    @Test
    public void testSetColumnName() {
        // カラム名の設定と確認
        logicalOperators.setColumnName("newColumn");
        assertEquals("newColumn", logicalOperators.getLikeQuery().split(" ")[2]);
    }

    @Test
    public void test論理演算子Enum() {
        // 論理演算子列挙型の各値が正しく動作するかを確認
        assertEquals("AND", LogicalOperators.論理演算子.AND.name());
        assertEquals("OR", LogicalOperators.論理演算子.OR.name());
        assertEquals("NOT", LogicalOperators.論理演算子.NOT.name());
        assertEquals("NOR", LogicalOperators.論理演算子.NOR.name());
        assertEquals("XOR", LogicalOperators.論理演算子.XOR.name());
    }
}

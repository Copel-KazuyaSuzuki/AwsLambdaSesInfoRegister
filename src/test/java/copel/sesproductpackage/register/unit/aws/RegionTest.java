package copel.sesproductpackage.register.unit.aws;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RegionTest {

    @Test
    void testGetRegionCode() {
        assertEquals("us-east-1", Region.バージニア北部.getRegionCode());
        assertEquals("ap-northeast-1", Region.東京.getRegionCode());
        assertEquals("eu-central-1", Region.フランクフルト.getRegionCode());
    }

    @Test
    void testFromCode() {
        assertEquals(Region.バージニア北部, Region.fromCode("us-east-1"));
        assertEquals(Region.東京, Region.fromCode("ap-northeast-1"));
        assertEquals(Region.フランクフルト, Region.fromCode("eu-central-1"));
    }

    @Test
    void testFromCodeWithInvalidCode() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Region.fromCode("invalid-code");
        });
        assertEquals("Unknown region code: invalid-code", exception.getMessage());
    }
}

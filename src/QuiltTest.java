import javafx.fxml.FXML;
import org.junit.Assert;
import org.junit.Test;

public class QuiltTest {


    // update a block & ensure it changed & everything else is same
    @Test
    public void testUpdateGrid() {
        int width = 5;
        int height = 7;
        HashGrid hg = new HashGrid(width, height);
        hg.setCurrentData(5);
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                hg.setGridInt(w, h);
            }
        }
        hg.setCurrentData(10);
        int x = 3;
        int y = 4;
        hg.setGridInt(x, y);
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < width; h++) {
                if (w == x && h == y) {
                    Assert.assertEquals(hg.getBlockIntData(w, h), 10);
                }
                else {
                    Assert.assertEquals(hg.getBlockIntData(w,h), 5);
                }
            }
        }
    }


    // make a hash and ensure it grabs correct object
    // call setCurrentData & setBlockStringData
    @Test
    public void testHash() {
        int width = 10;
        int height = 5;
        int x = 5;
        int y = 3;
        HashGrid hg = new HashGrid(width, height);
        hg.setCurrentData(3);
        hg.setGridInt(x, y);
        int code1 = hg.getGridCode();
        hg.setGridInt(x, y);
        int code2 = hg.getGridCode();
        Assert.assertEquals(code1, code2);
        hg.setCurrentData(4);
        hg.setGridInt(x, y);
        int code3 = hg.getGridCode();
        Assert.assertNotEquals(code2, code3);
    }

    // ensure gridCode make an accumulation of all grid strings
    // & that they're correct after calling updateCode
    @Test
    public void testMakeGridCode() {
        int width = 3;
        int height = 1;
        int x = 5;
        int y = 3;
        String code1 = "123";
        String code2 = "321";
        HashGrid hg = new HashGrid(width, height);
        hg.setCurrentData(1);
        hg.setGridInt(0, 0);
        hg.setCurrentData(2);
        hg.setGridInt(1, 0);
        hg.setCurrentData(3);
        hg.setGridInt(2, 0);
        Assert.assertEquals(code1.hashCode(), hg.getGridCode());
        hg.setCurrentData(10);
        hg.setGridInt(2, 0);
        Assert.assertNotEquals(code1.hashCode(), hg.getGridCode());
    }
}


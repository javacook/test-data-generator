package com.javacook.testdatagenerator.excelreader;

import com.javacook.easyexcelaccess.ExcelCoordinate;
import com.javacook.easyexcelaccess.ExcelCoordinateSequencer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Created by vollmer on 17.12.16.
 */
public class BeanPathCalculatorTest {

    MyExcelAccessor excelAccessor;
    BeanPathCalculator beanPathCalculator;

    @Before
    public void init() throws IOException {
        excelAccessor = new MyExcelAccessor("TestdataForTest.xls");
    }

    @Test
    public void indices() {
        beanPathCalculator = new BeanPathCalculator(excelAccessor, 2, 0);
        new ExcelCoordinateSequencer()
                .from(new ExcelCoordinate("C",2))
                .to(new ExcelCoordinate("J", 11))
                .forEach(coord -> {
                    final List<Integer> actual = beanPathCalculator.indices(0, coord);
                    final String expected = (String)excelAccessor.readCell(0, coord);
                    if (expected != null) {
                        Assert.assertEquals("" + coord, ""+expected , ""+actual);
                    }
                });
    }

    /**
     * Provoziere IllegalExelFormatException mit Text:
     * "The columns consisting stricty of single name parts have different sizes (no of entries)"
     */
    @Test
    public void error1() {
        beanPathCalculator = new BeanPathCalculator(excelAccessor, 0, 0);
        new ExcelCoordinateSequencer()
                .forRow(3).fromCol("A").toCol("B")
                .forEach(coord -> {
                    try {
                        beanPathCalculator.indices(1, coord);
                        Assert.fail("Expected exception not occured: " + coord);
                    } catch (IllegalExelFormatException e) {
                        // expected
                    }
                });
    }

    /**
     * Provoziere IllegalExelFormatException mit Text:
     * "The table must contain a column whose header consists stricty of single name parts"
     */
    @Test
    public void error2() {
        beanPathCalculator = new BeanPathCalculator(excelAccessor, 0, 0);
        new ExcelCoordinateSequencer()
                .from(new ExcelCoordinate("A", 2))
                .to(new ExcelCoordinate("B", 4))
                .forEach(coord -> {
                    try {
                        beanPathCalculator.indices(2, coord);
                        Assert.fail("Expected exception not occured: " + coord);
                    } catch (IllegalExelFormatException e) {
                        // expected
                    }
                });
    }


    /**
     * Provoziere IllegalExelFormatException mit Text:
     * "The columns consisting strictly of single name parts contain elements located at different rows"
     */
    @Test
    public void error3() {
        beanPathCalculator = new BeanPathCalculator(excelAccessor, 0, 0);
        new ExcelCoordinateSequencer()
                .forCol("C").fromRow(2).toRow(6)
                .forEach(coord -> {
                    try {
                        beanPathCalculator.indices(3, coord);
                        Assert.fail("Expected exception not occured: " + coord);
                    } catch (IllegalExelFormatException e) {
                        // expected
                    }
                });
    }

}
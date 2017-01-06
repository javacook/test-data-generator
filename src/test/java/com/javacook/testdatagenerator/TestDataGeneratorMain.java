package com.javacook.testdatagenerator;

import com.javacook.easyexcelaccess.ExcelCoordinate;
import com.javacook.testdatagenerator.TestDataGenerator.ExcelCell;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by vollmer on 21.12.16.
 */
public class TestDataGeneratorMain {

    public static void main(String[] args) throws IOException {
        TestDataGenerator testDataGenerator = new TestDataGenerator("TestdataForTest.xls", 3, 1);
        final ExcelCell cell = testDataGenerator.readCell(0, new ExcelCoordinate("C", 2));
        System.out.println(cell);
    }

}
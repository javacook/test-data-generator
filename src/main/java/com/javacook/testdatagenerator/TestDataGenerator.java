package com.javacook.testdatagenerator;

import com.javacook.coordinate.CoordinateInterface;
import com.javacook.testdatagenerator.excelreader.BeanPathCalculator;
import com.javacook.testdatagenerator.excelreader.MyExcelAccessor;
import com.javacook.testdatagenerator.model.BeanPath;
import java.io.File;

import java.io.IOException;

/**
 * Created by vollmer on 21.12.16.
 */
public class TestDataGenerator {

    final BeanPathCalculator beanPathCalculator;
    final MyExcelAccessor excelAccessor;
    final Integer headerStartIndex;
    final Integer oidIndex;


    protected TestDataGenerator(MyExcelAccessor excelAccessor, Integer headerStartIndex, Integer oidIndex) throws IOException {
        this.headerStartIndex = (headerStartIndex == null)? 0 : headerStartIndex;
        this.oidIndex = oidIndex;
        this.excelAccessor = excelAccessor;
        beanPathCalculator = new BeanPathCalculator(
                excelAccessor, headerStartIndex, (oidIndex == null)? 0 : oidIndex);
    }

    public TestDataGenerator(String excelResource, int headerStartIndex, int oidIndex) throws IOException {
        this(new MyExcelAccessor(excelResource), headerStartIndex, oidIndex);
    }

    public TestDataGenerator(String excelResource, int headerStartIndex) throws IOException {
        this(new MyExcelAccessor(excelResource), headerStartIndex, null);
    }

    public TestDataGenerator(String excelResource) throws IOException {
        this(new MyExcelAccessor(excelResource), null, null);
    }

    public TestDataGenerator(File excelFile, int headerStartIndex, int oidIndex) throws IOException {
        this(new MyExcelAccessor(excelFile), headerStartIndex, oidIndex);
    }

    public TestDataGenerator(File excelFile, int headerStartIndex) throws IOException {
        this(new MyExcelAccessor(excelFile), headerStartIndex, null);
    }

    public TestDataGenerator(File excelFile) throws IOException {
        this(new MyExcelAccessor(excelFile), null, null);
    }



    public static class ExcelCell<T> {
        public BeanPath beanPath;
        public Object oid;
        public T content;

        @Override
        public String toString() {
            return "ExcelCell{" +
                    "beanPath=" + beanPath +
                    ", oid=" + oid +
                    ", content=" + content +
                    '}';
        }
    }

    public <T> ExcelCell readCell(int sheet, CoordinateInterface coord) {
        return new ExcelCell() {{
            beanPath = beanPathCalculator.beanPath(sheet, coord);
            if (oidIndex != null) oid = beanPathCalculator.oid(sheet, coord);
            content = excelAccessor.readCell(sheet, coord);
        }};
    }

    public <T> ExcelCell readCell(int sheet, CoordinateInterface coord, Class<T> clazz) {
        return new ExcelCell() {{
            beanPath = beanPathCalculator.beanPath(sheet, coord);
            if (oidIndex != null) oid = beanPathCalculator.oid(sheet, coord);
            content = excelAccessor.readCell(sheet, coord, clazz);
        }};
    }

}

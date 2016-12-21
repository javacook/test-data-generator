package com.javacook.testdatagenerator;

import com.javacook.coordinate.CoordinateInterface;
import com.javacook.testdatagenerator.excelreader.BeanPathCalculator;
import com.javacook.testdatagenerator.excelreader.MyExcelAccessor;
import com.javacook.testdatagenerator.testdatamodel.BeanPath;

import java.io.File;
import java.io.IOException;

/**
 * Created by vollmer on 21.12.16.
 */
public class TestDataGenerator {

    final BeanPathCalculator beanPathCalculator;
    final MyExcelAccessor excelAccessor;
    final Integer oidColumn;


    /**
     * @param excelAccessor
     * @param headerStartColumn starting with 1
     * @param oidColumn starting with 1
     * @throws IOException
     */
    protected TestDataGenerator(MyExcelAccessor excelAccessor, Integer headerStartColumn, Integer oidColumn) throws IOException {
        this.oidColumn = oidColumn;
        this.excelAccessor = excelAccessor;
        beanPathCalculator = new BeanPathCalculator(
                excelAccessor,
                (headerStartColumn == null)? 0 : headerStartColumn-1,
                (oidColumn == null)? 0 : oidColumn-1);
    }

    public TestDataGenerator(String excelResource, int headerStartColumn, int oidColumn) throws IOException {
        this(new MyExcelAccessor(excelResource), headerStartColumn, oidColumn);
    }

    public TestDataGenerator(String excelResource, int headerStartColumn) throws IOException {
        this(new MyExcelAccessor(excelResource), headerStartColumn, null);
    }

    public TestDataGenerator(String excelResource) throws IOException {
        this(new MyExcelAccessor(excelResource), null, null);
    }

    public TestDataGenerator(File excelFile, int headerStartColumn, int oidColumn) throws IOException {
        this(new MyExcelAccessor(excelFile), headerStartColumn, oidColumn);
    }

    public TestDataGenerator(File excelFile, int headerStartColumn) throws IOException {
        this(new MyExcelAccessor(excelFile), headerStartColumn, null);
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
            if (oidColumn != null) oid = beanPathCalculator.oid(sheet, coord);
            content = excelAccessor.readCell(sheet, coord);
        }};
    }

    public <T> ExcelCell readCell(int sheet, CoordinateInterface coord, Class<T> clazz) {
        return new ExcelCell() {{
            beanPath = beanPathCalculator.beanPath(sheet, coord);
            if (oidColumn != null) oid = beanPathCalculator.oid(sheet, coord);
            content = excelAccessor.readCell(sheet, coord, clazz);
        }};
    }

    public boolean isEmpty(int sheet, CoordinateInterface coord) {
        return excelAccessor.isEmpty(sheet, coord);
    }

    public int noRows(int sheet) {
        return excelAccessor.noRows(sheet);
    }

    public int noCols(int sheet) {
        return excelAccessor.noCols(sheet);
    }

}
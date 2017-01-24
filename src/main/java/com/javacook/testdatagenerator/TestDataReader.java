package com.javacook.testdatagenerator;

import com.javacook.coordinate.CoordinateInterface;
import com.javacook.easyexcelaccess.ExcelCoordinate;
import com.javacook.easyexcelaccess.ExcelCoordinateSequencer;
import com.javacook.easyexcelaccess.ExcelUtil;
import com.javacook.testdatagenerator.excelreader.BeanPathCalculator;
import com.javacook.testdatagenerator.excelreader.MyExcelAccessor;
import com.javacook.testdatagenerator.testdatamodel.BeanPath;
import com.javacook.testdatagenerator.testdatamodel.BeanPathTree;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vollmer on 21.12.16.
 */
public class TestDataReader {

    public final int DEFAULT_DATA_START_ROW = 2;
    public final Set<String> NIL_MARKERS = new HashSet() {{
        add("NIL");
        add("NULL");
        add("~");
    }};

    private final BeanPathCalculator beanPathCalculator;
    private final MyExcelAccessor excelAccessor;
    private int headerStartCol;
    private int oidCol;

    /**
     * @param excelFile
     * @param headerStartColumn like "A",...,"Z", "AA", "AB",...
     * @param oidColumn
     * @throws IOException
     */
    public TestDataReader(File excelFile, String headerStartColumn, String oidColumn) throws IOException {
        headerStartCol = ExcelUtil.calculateColNo(headerStartColumn); // starting with 1
        oidCol = ExcelUtil.calculateColNo(oidColumn); // starting with 1
        this.excelAccessor = new MyExcelAccessor(excelFile);
        beanPathCalculator = new BeanPathCalculator(excelAccessor, headerStartCol -1, oidCol-1);
    }

    public TestDataReader(String resourceName, String headerStartColumn, String oidColumn) throws IOException {
        headerStartCol = ExcelUtil.calculateColNo(headerStartColumn); // starting with 1
        oidCol = ExcelUtil.calculateColNo(oidColumn); // starting with 1
        this.excelAccessor = new MyExcelAccessor(resourceName);
        beanPathCalculator = new BeanPathCalculator(excelAccessor, headerStartCol -1, oidCol-1);
    }

    public TestDataReader(File excelFile, String headerStartColumn) throws IOException {
        headerStartCol = ExcelUtil.calculateColNo(headerStartColumn); // starting with 1
        this.excelAccessor = new MyExcelAccessor(excelFile);
        beanPathCalculator = new BeanPathCalculator(excelAccessor, headerStartCol -1);
    }

    public TestDataReader(String resourceName, String headerStartColumn) throws IOException {
        headerStartCol = ExcelUtil.calculateColNo(headerStartColumn); // starting with 1
        this.excelAccessor = new MyExcelAccessor(resourceName);
        beanPathCalculator = new BeanPathCalculator(excelAccessor, headerStartCol -1);
    }



    public BeanPathTree getBeanPathTree(int sheet,
                                        CoordinateInterface leftUpperCorner,
                                        CoordinateInterface rightLowerCorner) throws IOException {

        final BeanPathTree beanPathTree = new BeanPathTree();

        new ExcelCoordinateSequencer()
                .from(leftUpperCorner)
                .to(new ExcelCoordinate(rightLowerCorner))
                .forEach(coord -> {
                    final Object value = excelAccessor.readCell(sheet, coord);
                    if (value != null) {
                        final BeanPath beanPath = beanPathCalculator.beanPath(sheet, coord);
                        if (NIL_MARKERS.contains(value)) {
                            beanPathTree.nil(beanPath);
                        }
                        else {
                            beanPathTree.put(beanPath, value);
                        }
                    }
                });
        return beanPathTree;
    }


    /**
     *
     * @param sheet starting with 0
     * @return
     * @throws IOException
     */
    public BeanPathTree getBeanPathTree(int sheet) throws IOException {
        return getBeanPathTree(sheet,
                new ExcelCoordinate(headerStartCol, DEFAULT_DATA_START_ROW),
                new ExcelCoordinate(noCols(sheet), noRows(sheet)));
    }


    public int noRows(int sheet) {
        return excelAccessor.noRows(sheet);
    }

    public int noCols(int sheet) {
        return excelAccessor.noCols(sheet);
    }

}
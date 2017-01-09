package com.javacook.testdatagenerator;

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

    public final int DATA_START_ROW = 2;
    public final Set<String> NIL_MARKERS = new HashSet() {{
        add("NIL");
        add("NULL");
        add("~");
    }};

    private final BeanPathCalculator beanPathCalculator;
    private final MyExcelAccessor excelAccessor;
    private final int headStartCol;
    private final int oidCol;

    /**
     * @param excelFile
     * @param headerStartColumn like "A",...,"Z", "AA", "AB",...
     * @param oidColumn
     * @throws IOException
     */
    public TestDataReader(File excelFile, String headerStartColumn, String oidColumn) throws IOException {
        headStartCol = ExcelUtil.calculateColNo(headerStartColumn); // starting with 1
        oidCol = ExcelUtil.calculateColNo(oidColumn); // starting with 1
        this.excelAccessor = new MyExcelAccessor(excelFile);
        beanPathCalculator = new BeanPathCalculator(excelAccessor, headStartCol-1, oidCol-1);
    }

    public TestDataReader(String resourceName, String headerStartColumn, String oidColumn) throws IOException {
        headStartCol = ExcelUtil.calculateColNo(headerStartColumn); // starting with 1
        oidCol = ExcelUtil.calculateColNo(oidColumn); // starting with 1
        this.excelAccessor = new MyExcelAccessor(resourceName);
        beanPathCalculator = new BeanPathCalculator(excelAccessor, headStartCol-1, oidCol-1);
    }


    /**
     *
     * @param sheet starting with 0
     * @return
     * @throws IOException
     */
    public BeanPathTree getBeanPathTree(int sheet) throws IOException {
        final BeanPathTree beanPathTree = new BeanPathTree();

        new ExcelCoordinateSequencer()
                .from(new ExcelCoordinate(headStartCol, DATA_START_ROW))
                .to(new ExcelCoordinate(excelAccessor.noCols(sheet), excelAccessor.noRows(sheet)))
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

}
package com.javacook.testdatagenerator;

import com.javacook.easyexcelaccess.ExcelCoordinate;
import com.javacook.easyexcelaccess.ExcelCoordinateSequencer;
import com.javacook.testdatagenerator.excelreader.BeanPathCalculator;
import com.javacook.testdatagenerator.excelreader.MyExcelAccessor;
import com.javacook.testdatagenerator.testdatamodel.BeanPath;
import com.javacook.testdatagenerator.testdatamodel.BeanPathTree;

import java.io.IOException;

/**
 * Created by vollmer on 21.12.16.
 */
public class TestDataReaderAndreas {

    public static void main(String[] args) throws IOException {
        final BeanPathTree beanPathTree = new BeanPathTree();

        final MyExcelAccessor excelAccessor = new MyExcelAccessor("AndreasDataForJSON.xls");

        final BeanPathCalculator beanPathCalculator = new BeanPathCalculator(excelAccessor, 2, 0);
        new ExcelCoordinateSequencer()
                .from(new ExcelCoordinate("C",2))
                .to(new ExcelCoordinate("D", 4))
                .forEach(coord -> {
                    final String expected = (String)excelAccessor.readCell(0, coord);
                    if (expected != null) {
                        final BeanPath beanPath = beanPathCalculator.beanPath(0, coord);
//                        System.out.println(beanPath);
                        beanPathTree.put(beanPath.toString(), expected);
                    }
                });

        for (BeanPath beanPath : beanPathTree.keySet()) {
            System.out.println(beanPath + " -> " + beanPathTree.get(beanPath));
        }

        System.out.println(beanPathTree.toJSON());
    }

}
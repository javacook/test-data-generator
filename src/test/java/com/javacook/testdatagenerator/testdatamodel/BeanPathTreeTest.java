package com.javacook.testdatagenerator.testdatamodel;

import com.javacook.easyexcelaccess.ExcelCoordinate;
import com.javacook.easyexcelaccess.ExcelCoordinateSequencer;
import com.javacook.testdatagenerator.excelreader.BeanPathCalculator;
import com.javacook.testdatagenerator.excelreader.MyExcelAccessor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by vollmer on 09.01.17.
 */
public class BeanPathTreeTest {


    @Test
    public void toJSON() throws Exception {
        final int SHEET = 0;
        final BeanPathTree beanPathTree = new BeanPathTree();

        final MyExcelAccessor excelAccessor = new MyExcelAccessor("TestdataForJSON.xls");

        final BeanPathCalculator beanPathCalculator = new BeanPathCalculator(excelAccessor, 2, 0);
        new ExcelCoordinateSequencer()
                .from(new ExcelCoordinate("C",2))
                .to(new ExcelCoordinate(excelAccessor.noCols(SHEET), excelAccessor.noRows(SHEET)))
                .forEach(coord -> {
                    final String value = (String)excelAccessor.readCell(SHEET, coord);
                    if (value != null) {
                        final BeanPath beanPath = beanPathCalculator.beanPath(SHEET, coord);
                        if ("~".equals(value)) beanPathTree.nil(beanPath);
                        else beanPathTree.put(beanPath, value);
                    }
                });
        System.out.println(beanPathTree.toJSON().toJSONString());

        JSONObject base = new JSONObject();
        JSONArray kunden = new JSONArray();
        JSONObject kunde1 = new JSONObject();
        kunde1.put("Vorname", "Jörg");
        JSONArray namen = new JSONArray();
        namen.addAll(Arrays.asList("Vollmer", "Düser", "Görner", "Bratke"));
        kunde1.put("Namen", namen);

        JSONObject kunde2 = new JSONObject();
        kunden.add(kunde1);
        kunden.add(kunde2);
        base.put("Kunden", kunden);
        System.out.println(base.toJSONString());


    }

}
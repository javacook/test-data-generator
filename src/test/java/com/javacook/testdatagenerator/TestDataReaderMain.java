package com.javacook.testdatagenerator;

import com.javacook.testdatagenerator.testdatamodel.BeanPathTree;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by vollmer on 09.01.17.
 */
public class TestDataReaderMain {

    public static void main(String[] args) throws IOException {
        final TestDataReader testDataReader = new TestDataReader("TestdataForJSON.xls", "C", "A");
        final BeanPathTree beanPathTree = testDataReader.getBeanPathTree(0);
        System.out.println(beanPathTree.toJSON());
    }


}
package com.javacook.testdatagenerator;

import com.javacook.testdatagenerator.testdatamodel.BeanPath;
import com.javacook.testdatagenerator.testdatamodel.BeanPathElement;
import com.javacook.testdatagenerator.testdatamodel.BeanPathTree;

import java.io.IOException;

/**
 * Created by vollmer on 21.12.16.
 */
public class TestDataReaderAndreas {

    public static void main(String[] args) throws IOException {
        final TestDataReader testDataReader0 = new TestDataReader("AndreasDataForJSON.xls", "C", "A");
        final BeanPathTree beanPathTree0 = testDataReader0.getBeanPathTree(0);
        System.out.println(beanPathTree0.toJSON());


        final TestDataReader testDataReader1 = new TestDataReader("AndreasDataForJSON.xls", "A", "A");
        final BeanPathTree beanPathTree1 = testDataReader1.getBeanPathTree(1);
        for (BeanPathElement subTreeRoot: beanPathTree1.subtreeRoots()) {
            final BeanPathTree subtree = beanPathTree1.subtree(subTreeRoot);
            System.out.println(subtree.toJSON());
        }
    }

}
package com.javacook.testdatagenerator;

import com.javacook.testdatagenerator.testdatamodel.BeanPathElement;
import com.javacook.testdatagenerator.testdatamodel.BeanPathTree;

import java.io.IOException;

/**
 * Created by vollmer on 21.12.16.
 */
public class TestDataReaderVerzeichnis {

    public static void main(String[] args) throws IOException {
        final TestDataReader testDataReader0 = new TestDataReader("PM_TransactionsAndActivitiesOrig.xls", "C", "A");
        final BeanPathTree beanPathTree0 = testDataReader0.getBeanPathTree(0);
        System.out.println(beanPathTree0.toJSON());
    }

}
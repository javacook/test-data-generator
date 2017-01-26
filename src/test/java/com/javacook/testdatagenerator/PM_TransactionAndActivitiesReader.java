package com.javacook.testdatagenerator;


import com.javacook.testdatagenerator.testdatamodel.BeanPathElement;
import com.javacook.testdatagenerator.testdatamodel.BeanPathTree;
import org.json.simple.JSONObject;

import java.io.IOException;

/**
 * Created by vollmer on 21.12.16.
 */
public class PM_TransactionAndActivitiesReader {

    public static void main(String[] args) throws IOException {
        final TestDataReader testDataReader1 = new TestDataReader("PM_TransactionsAndActivitiesOrig.xls", "C", "A");
        final BeanPathTree beanPathTree1 = testDataReader1.getBeanPathTree(0);
        for (BeanPathElement subTreeRoot: beanPathTree1.subtreeRoots()) {
            final BeanPathTree subtree = beanPathTree1.subtree(subTreeRoot);
            final JSONObject jsonObject = subtree.toJSON();
            System.out.println(jsonObject);
        }

        final BeanPathTree beanPathTree2 = testDataReader1.getBeanPathTree(1);
        for (BeanPathElement subTreeRoot: beanPathTree2.subtreeRoots()) {
            final BeanPathTree subtree = beanPathTree2.subtree(subTreeRoot);
            final JSONObject jsonObject = subtree.toJSON();
            System.out.println(jsonObject);
        }
    }
}
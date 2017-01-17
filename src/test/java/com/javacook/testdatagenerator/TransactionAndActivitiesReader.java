package com.javacook.testdatagenerator;


import com.javacook.testdatagenerator.testdatamodel.BeanPathElement;
import com.javacook.testdatagenerator.testdatamodel.BeanPathTree;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by vollmer on 21.12.16.
 */
public class TransactionAndActivitiesReader {

    final static String FILE_PATH = "/mnt/material/Development/CrefoPortal/testdaten/testdaten-generator/output/";


    public static void main(String[] args) throws IOException {

        final TestDataReader testDataReader1 = new TestDataReader("TransactionsAndActivities.xls", "A", "A");
        final BeanPathTree beanPathTree0 = testDataReader1.getBeanPathTree(0);
//        int i = 1;
//        for (BeanPathElement subTreeRoot: beanPathTree0.subtreeRoots()) {
//            final BeanPathTree subtree = beanPathTree0.subtree(subTreeRoot);
//            final JSONObject jsonObject = subtree.toJSON();
//            jsonObject.put("@class", "de.creditreform.verzeichnis.app.common.activity.model.ActivityCtoa");
//            System.out.println(jsonObject);
//            writeToFile(jsonObject, FILE_PATH + "activities/activity_" + i++ + ".json");
//        }

        final BeanPathTree beanPathTree1 = testDataReader1.getBeanPathTree(1);
        int j = 1;
        for (BeanPathElement subTreeRoot: beanPathTree1.subtreeRoots()) {
            final BeanPathTree subtree = beanPathTree1.subtree(subTreeRoot);
            final JSONObject jsonObject = subtree.toJSON();
            jsonObject.put("@class", "de.creditreform.verzeichnis.app.common.transaction.model.TransactionCtoa");
            System.out.println(jsonObject);
            writeToFile(jsonObject, FILE_PATH + "transactions/transaction_" + j++ + ".json");
        }
    }
    
    
    public static void writeToFile(JSONObject jsonObject, String fileName)
    {
    	try {
			FileWriter writer = new FileWriter(fileName);
		    writer.write(jsonObject.toString());
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Erstellen/Befuellen der Datei fehlgeschlagen");
		}
    }

}
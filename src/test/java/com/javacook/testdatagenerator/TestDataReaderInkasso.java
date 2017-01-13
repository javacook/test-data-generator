package com.javacook.testdatagenerator;


import com.javacook.testdatagenerator.testdatamodel.BeanPathElement;
import com.javacook.testdatagenerator.testdatamodel.BeanPathTree;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by vollmer on 21.12.16.
 */
public class TestDataReaderInkasso {

    public static void main(String[] args) throws IOException {


        final TestDataReader testDataReader1 = new TestDataReader("InkassoTestdaten.xls", "C", "B");
        final BeanPathTree beanPathTree1 = testDataReader1.getBeanPathTree(0);
        for (BeanPathElement subTreeRoot: beanPathTree1.subtreeRoots()) {
            final BeanPathTree subtree = beanPathTree1.subtree(subTreeRoot);
            System.out.println(subtree.toJSON());
//            String temp = subtree.toJSON().get("id").toString();
//            writeToFile(subtree.toJSON(), temp + ".json");
        }
    }
    

    public static void writeToFile(JSONObject jsonObject, String fileName)
    {
    	try {
			FileWriter writer = new FileWriter("C:/Users/muellerp/Desktop/ITSQ/007 Projekt Excelsearcher/testdaten/testdaten-generator/src/test/resources/JSONs/" + fileName);
		    writer.write(jsonObject.toString());
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Erstellen/Bef�llen der Datei fehlgeschlagen");
		}
    }

}
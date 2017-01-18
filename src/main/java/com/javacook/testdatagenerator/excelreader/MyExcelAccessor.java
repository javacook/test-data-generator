/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javacook.testdatagenerator.excelreader;

import com.javacook.coordinate.Coordinate;
import com.javacook.coordinate.CoordinateInterface;
import com.javacook.easyexcelaccess.ExcelCoordinateAccessor;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.IOException;

import static com.javacook.easyexcelaccess.ExcelCoordinate.ROW_MAX;

/**
 *
 * @author vollmer
 */
public class MyExcelAccessor extends ExcelCoordinateAccessor {
    
    public MyExcelAccessor(String resourceName) throws IOException {

        super(resourceName);
    }
    
    public MyExcelAccessor(File file) throws IOException {
        super(file);
    }


    /**
     * Konstruiert einen Header der Excel-Tabelle betrachtet ab Spalte <code>startColumn</code>
     * (eingeschlossen).
     * @param sheet Blatt-Nummer (0,...)
     * @param startColumn Spalte, ab der der Header betrachtet werden soll
     * @param endColumn (optional) maximale Spalte, bis zu der der Header betrachtet werden soll.
     * @return {@link Header} Header der Excel-Tabelle
     */
    public Header header(final int sheet, final int startColumn, Integer endColumn) {
        Validate.isTrue(sheet >= 0, "Argument 'sheet' must be non-negativ");
        Validate.isTrue(startColumn >= 0, "Argument 'startColumn' must be non-negativ");
        Validate.isTrue(endColumn == null || endColumn >= startColumn,
                "Argument 'endColumn' must be greater than startColumn.");

        final Header header = new Header();
        String currHeaderStr;
        int col = startColumn;
        while ( (endColumn == null || col <= endColumn) &&
                (currHeaderStr = readCell(sheet, col, 0, String.class)) != null) {
            header.addHeaderElement(new HeaderElement(col++, currHeaderStr));
        }
        return header;
    }


    /**
     * Findet von Koordinate <code>coord</code> aus (<code>coord</code> eingeschlossen) die erste nicht
     * leere Zelle, wenn man in der Excel-Tabelle senkrecht hoch in Richtung Header geht.
     * Der Header zaehlt natuerlich nicht dazu. Gibt es keine nicht-leere Zelle (ausser dem Header-Element),
     * wird null geliefert.
     * @return erste nicht-leere Zelle "ueber" <code>coord</code>; null, falls es keine gibt.
     */
    public Coordinate findFirstNonEmptyCellFrom(int sheet, CoordinateInterface coord) {
        Validate.isTrue(sheet >= 0, "Argument 'sheet' must be non-negativ");
        Validate.isTrue(coord != null, "Argument 'coord' is null.");

        int currRow = coord.y();
        while (currRow > 0 && readCell(sheet, coord.x(), currRow) == null) currRow--;
        return (currRow == 0)? null : new Coordinate(coord.x(), currRow);
    }


    public Coordinate findNthNonEmptyCellInColumn(int sheet, int column, int nth) {
        Validate.isTrue(sheet >= 0, "Argument 'sheet' must be non-negativ");
        Validate.isTrue(nth > 0, "Argument 'nth' must be positive");
        Validate.isTrue(nth < ROW_MAX, "Argument 'nth' must be less than " + ROW_MAX);

        for (int row = 1; row < ROW_MAX; row++) {
            final Object o = readCell(sheet, column, row);
            if (o != null && --nth == 0) return new Coordinate(column, row);
        }
        return null;
    }


    /**
     * Zaehlt die nicht-leeren Excel-Zellen in der Spalte, die durch <code>coord</code> definiert ist,
     * von <code>coord</code> aus in Richtung Header gehend, wobei die Zeile <code>minRow</code> nicht
     * ueberschritten werden darf (sie selbst ist aber gueltig)
     * @param sheet
     * @param coord
     * @param minRow
     * @return
     */
    public int countNonEmptyCellsBetween(int sheet, CoordinateInterface coord, int minRow) {
        Validate.isTrue(sheet >= 0, "Argument 'sheet' must be non-negativ");
        Validate.isTrue(coord != null, "Argument 'coord' is null.");
        Validate.isTrue(minRow > 0, "Argument 'minRow' must be non-negativ");

        int counter = 0;
        for (int y = minRow; y <= coord.y(); y++) {
            if (readCell(sheet, coord.x(), y) != null) counter++;
        }
        return counter;
    }


    /**
     * Wie <code>countNonEmptyCellsBetween</code>, nur dass von <code>coord</code> aus bis zum
     * Header nach nicht-leeren Excel-Zellen geschaut wird.
     * @param sheet
     * @param coord
     * @return
     */
    public int countNonEmptyCells(int sheet, CoordinateInterface coord) {
        return countNonEmptyCellsBetween(sheet, coord, 1);
    }

}

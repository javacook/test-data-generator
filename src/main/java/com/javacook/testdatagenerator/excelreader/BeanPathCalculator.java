package com.javacook.testdatagenerator.excelreader;

import com.javacook.coordinate.Coordinate;
import com.javacook.coordinate.CoordinateInterface;
import com.javacook.easyexcelaccess.ExcelCoordinate;
import com.javacook.easyexcelaccess.ExcelCoordinateSequencer;
import com.javacook.testdatagenerator.testdatamodel.BeanPath;
import com.javacook.testdatagenerator.testdatamodel.BeanPathElement;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.Validate;

import java.util.*;

/**
 * Created by vollmer on 14.12.16.
 */
public class BeanPathCalculator {

    private MyExcelAccessor excelAccessor;
    private int headerStartIndex = 0;
    private Integer headerEndIndex;
    private int dataStartIndex = 1;
    private Integer dataEndIndex;
    private Integer oidIndex;

    protected void setHeaderStartIndex(int headerStartIndex) {
        Validate.isTrue(headerStartIndex >= 0, "Argument 'headerStartIndex' must be non-negative.");
        this.headerStartIndex = headerStartIndex;
    }

    protected void setHeaderEndIndex(int headerEndIndex) {
        Validate.isTrue(headerEndIndex >= headerStartIndex, "Argument 'headerEndIndex' must be greater than 'headerStartIndex'.");
        this.headerEndIndex = headerEndIndex;
    }

    protected void setDataStartIndex(int dataStartIndex) {
        Validate.isTrue(dataStartIndex >= 0, "Argument 'dataStartIndex' must be non-negative.");
        this.dataStartIndex = dataStartIndex;
    }

    protected void setDataEndIndex(int dataEndIndex) {
        Validate.isTrue(dataEndIndex >= dataStartIndex, "Argument 'dataEndIndex' must be greater than 'dataStartIndex'.");
        this.dataEndIndex = dataEndIndex;
    }

    protected void setOidIndex(int oidIndex) {
        Validate.isTrue(oidIndex >= 0, "Argument 'oidIndex' must be non-negative.");
        this.oidIndex = oidIndex;
    }


    public BeanPathCalculator(MyExcelAccessor excelAccessor, int headerStartIndex) {
        Validate.isTrue(excelAccessor != null, "Argument 'exelAccessor' is null.");
        this.excelAccessor = excelAccessor;
        setHeaderStartIndex(headerStartIndex);
    }

    public BeanPathCalculator(MyExcelAccessor excelAccessor, int headerStartIndex, int oidIndex) {
        this(excelAccessor, headerStartIndex);
        setOidIndex(oidIndex);
    }

    /**
     * @param sheet the Excel sheet no starting with 0
     * @param coord
     * @return The OID taken from the OID column for the excel element at <code>coord</code>
     */
    public Object oid(int sheet, CoordinateInterface coord) {
        Validate.isTrue(oidIndex != null, "The oid index is not set. Please use another constructor.");
        Validate.isTrue(sheet >= 0, "Argument 'sheet' must be non-negative.");
        Validate.isTrue(coord != null, "Argument 'coord' is null.");
        Validate.isTrue(coord.x() >= 0, "The value of 'coord.x' must be non-negative.");
        Validate.isTrue(coord.y() > 0, "The value of 'coord.y' must be positive.");

        final Coordinate oidCoord = oidCoordinate(sheet, coord);
        return (oidCoord == null)? null : excelAccessor.readCell(sheet, oidCoord);
    }


    public BeanPath beanPath(int sheet, CoordinateInterface coord)  {
        Validate.isTrue(sheet >= 0, "Argument 'sheet' must be non-negative.");
        Validate.isTrue(coord != null, "Argument 'coord' is null.");
        Validate.isTrue(coord.x() >= 0, "The value of 'coord.x' must be non-negative.");
        Validate.isTrue(coord.y() > 0, "The value of 'coord.y' must be positive.");

        if (excelAccessor.readCell(sheet, coord) == null) return null;
        final Header header = excelAccessor.header(sheet, headerStartIndex, headerEndIndex);
        return beanPath(header, sheet, coord);
    }


    /**
     * Eine Parent-Koordinate ist eine (nicht eindeutig bestimmte) Kooridinate eines direkten
     * Eltern-Objekts in der Excel-Tabelle. Falls ich zu einem Array gehoere, ist das dasjenige Element,
     * das das Array enthaelt. Ich kann aber auch ein einfaches Unterobjekt sein (bei kunde.adresse waere
     * das adresse). In diesem Fall ist kunde das Eltern-Objekt, und diese Methode liefert einfach die
     * Korrdinate eines bel. Kunden-Attributs, dessen Adresse ich bin.
     * <pre>
     * 1) Ich bin in einer Nur-Single-Spalte => null
     * 2) Meine Spalte enthaelt genau ein Multi-Element =>
     * 2a) Bestimme die Menge aller Spalten mit Nur-Singe-Spalten.
     * 2b) Suche dort jeweils das groesste Element kleiner gleich mir (bzgl. Zeilennummer)
     * 2c) Die Zeilennummer all derjenigen muss gleich sein.
     * 2d) Wähle eine beliebige Spalte und liefere ein Element mit dieser Eigenschaft zurueck
     * 3) Meine Spalte enthaelt mehrere Multi-Elements => rekursiv mit Sub-Tabelle
     * 3a) Bestimme den Präfix-Pfad meines Headers bis einschliesslich zum vorletzten Multi-Eintrag
     * 3b) Ermittle alle Spalten, die mit diesem Präfix starten. Genau daraus besteht meine Sub-Tabelle
     * 3c) Bestimme (rekursiv) von dieser Sub-Tabelle den Parent.
     * </pre>
     * @param header Header, bzw. Sub-Header bei der Rekursion
     * @param sheet Blattnummer (0,...)
     * @param coord Koordinate, dessen Parent bestimmt werden soll
     * @return {@link CoordinateInterface} ein Parent-Repräsentant (da nicht eindeutig bestimmt)
     */
    CoordinateInterface parentCoordinate(Header header, int sheet, CoordinateInterface coord)  {
        Validate.isTrue(header != null, "Argument 'header' is null.");
        Validate.isTrue(sheet >= 0, "Argument 'sheet' must be non-negative.");
        Validate.isTrue(coord != null, "Argument 'coord' is null.");
        Validate.isTrue(header.size() > 0, "The header must be non-empty.");

        final HeaderElement headerElementForColumn = header.headerElementForColumn(coord.x());
        Validate.isTrue(headerElementForColumn != null, "There is no header element for column coord.x = " + coord.x());

        switch (headerElementForColumn.noOfMultiElements()) {
            case 0: {
                final Header subHeaderOnlyWithSingles = header.subHeaderOnlyWithSingles();
                MultiValuedMap<Integer, Integer> colsForCountNonEmptyCells = new ArrayListValuedHashMap<>();
                for (HeaderElement currHeaderElement : subHeaderOnlyWithSingles) {
                    Coordinate runningCoord = new Coordinate(currHeaderElement.column, coord.y());
                    final int count = excelAccessor.countNonEmptyCells(sheet, runningCoord);
                    colsForCountNonEmptyCells.put(count, currHeaderElement.column);
                }
                switch (colsForCountNonEmptyCells.keySet().size()) {
                    case 0:
                        // Einerseits gibt's ein Element (headerElementForColumn) mit nur Singles, anderersiets
                        // soll die Menge der Spalten, die nur aus Singles bestehen, 0 sein.
                        throw new IllegalStateException("Software-Error: Inconsistency (code 1)");
                    case 1:
                        return null;
                    default:
                        throw new IllegalExelFormatException("The columns consisting strictly of 'single name parts' " +
                                "have different sizes (no of entries): " + colsForCountNonEmptyCells);
                }
            }
            case 1: {
                final Header subHeaderOnlyWithSingles = header.subHeaderOnlyWithSingles();
                Set<Integer> rowNumbers = new HashSet<>();
                Coordinate representative = null;
                for (HeaderElement currHeaderElement : subHeaderOnlyWithSingles) {
                    Coordinate runningCoord = new Coordinate(currHeaderElement.column, coord.y());
                    representative = excelAccessor.findFirstNonEmptyCellFrom(sheet, runningCoord);
                    // Der Fall representative == null besagt, dass die Single-Spalte ab representative.y() bis zum
                    // Header leer ist. Das wird mit 0 kodiert ("=Header"), was es im Normalfall ja nicht gibt.
                    rowNumbers.add(representative == null? 0 : representative.y());
                }
                switch (rowNumbers.size()) {
                    case 0:
                        throw new IllegalExelFormatException("The table must contain a column whose header consists " +
                                "stricty of single name parts.");
                    case 1:
                        return representative;
                    default:
                        throw new IllegalExelFormatException("The columns consisting strictly of 'single name parts' " +
                                "contain elements located at different rows: " + rowNumbers +
                                ". Columns with single elements: " + subHeaderOnlyWithSingles);
                }
            }
            default: {
                final HeaderElement prefix = headerElementForColumn.prefixUpToPenultimateMulti(); // wohnsitz.adressen[]
                final Header subHeader = header.subHeaderStartingWith(prefix);
                header = subHeader.subHeaderWithTruncatedNameParts(prefix);
                if (subHeader.size() == 0) {
                    // Da ich ja selbst diesen Praefix besitze, gehoere ich zum subHeader dazu
                    throw new IllegalStateException("Software-Error: Inconsistency (code 2)");
                }
                return parentCoordinate(header, sheet, coord);
            }
        }
    }

    List<Integer> indices(int sheet, CoordinateInterface coord)  {
        final Header header = excelAccessor.header(sheet, headerStartIndex, headerEndIndex);
        return indices(header, sheet, coord);
    }

    /**
     *
     * @param header
     * @param sheet
     * @param coord
     * @return A list of parent indices (contains at least one element)
     */
    List<Integer> indices(Header header, int sheet, CoordinateInterface coord)  {
        List<Integer> indices = new ArrayList<>();
        CoordinateInterface parentCoordinate;
        while ((parentCoordinate = parentCoordinate(header, sheet, coord)) != null) {
            final int index = excelAccessor.countNonEmptyCellsBetween(sheet, coord, parentCoordinate.y());
            indices.add(index-1);
            coord = parentCoordinate;
        }
        final int index = excelAccessor.countNonEmptyCells(sheet, coord);
        indices.add(index-1);
        Collections.reverse(indices);
        return indices;
    }


    BeanPath beanPath(Header header, int sheet, CoordinateInterface coord)  {
        final HeaderElement headerElement = header.headerElementForColumn(coord.x());
        final List<Integer> indices = indices(header, sheet, coord);
        BeanPath result = new BeanPath();
        int index = 0;
        final String sheetName = excelAccessor.sheetName(sheet);
        result.addBeanPathElement(new BeanPathElement(sheetName, indices.get(index++)));
        for (HeaderNamePart headerNamePart : headerElement.nameParts) {
            BeanPathElement beanPathElement =
                    new BeanPathElement(headerNamePart.name, headerNamePart.multi? indices.get(index++) : null);
            result.addBeanPathElement(beanPathElement);
        }
        return result;
    }


    Coordinate oidCoordinate(int sheet, CoordinateInterface coord) {
        if (excelAccessor.readCell(sheet, coord) == null) return null;
        final Header header = excelAccessor.header(sheet, headerStartIndex, headerEndIndex);
        final List<Integer> indices = indices(header, sheet, coord);
        return excelAccessor.findNthNonEmptyCellInColumn(sheet, oidIndex, indices.get(0)+1);
    }




    public static void main(String[] args) throws Exception {
        final MyExcelAccessor excelAccessor = new MyExcelAccessor("Inkasso.xls");
        final BeanPathCalculator beanPathCalculator = new BeanPathCalculator(excelAccessor, 2) {{
            setOidIndex(2);
        }};
        ExcelCoordinateSequencer sequencer = new ExcelCoordinateSequencer();
        sequencer
                .from(new ExcelCoordinate("C",2))
                .to(new ExcelCoordinate("Y", 4))
                .forEach(coord -> {
                    System.out.println(beanPathCalculator.beanPath(0, coord) + " ---> " +
                            beanPathCalculator.oid(0, coord) + " ----> " + excelAccessor.readCell(0, coord));
                });
    }


}

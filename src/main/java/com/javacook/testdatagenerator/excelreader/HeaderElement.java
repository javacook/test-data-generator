package com.javacook.testdatagenerator.excelreader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vollmer
 */
public class HeaderElement {

    public final List<HeaderNamePart> nameParts = new ArrayList<>();
    public final int column;
    

    public HeaderElement(int column) {
        this.column = column;
    }

    public HeaderElement(int column, String headerStr) {
        this(column);
        String[] split = headerStr.split("\\.");
        for (String part : split) {
            nameParts.add(new HeaderNamePart(part));
        }
    }
    

    /**
     * Prefix-Pfad, der vor dem letzten Multi-Element endet.
     * Beispiel:
     * <pre>
     * firma.kunde.adresse[].strasse -> firma.kunde
     * firma.kunde.name -> null
     * </pre>
     * @return {@link List} Prefix-Pfad; null, falls keine Multi-Element vorhanden ist
     */
    public List<HeaderNamePart> prefixBevorLastMulti() {
        int lastMultiIndex = -1;
        for (int i = 0; i < nameParts.size(); i++) {
            if (nameParts.get(i).multi) lastMultiIndex = i;
        }
        if (lastMultiIndex == -1) return null; // gar kein multi vorhanden
        List<HeaderNamePart> result = new ArrayList<>();
        for (int i = 0; i < lastMultiIndex; i++) {
            result.add(nameParts.get(i));
        }
        return result;
    }


    /**
     * Prefix-Pfad, der mit dem vorletzten Multi-Element endet.
     * Beispiel:
     * <pre>
     * firma.kunde.adresse[].strasse.nummern[].wert -> firma.kunde.adresse[]
     * firma.kunde.name -> null
     * </pre>
     * @return {@link List} Prefix-Pfad; null, falls keine Multi-Element vorhanden ist
     */
    public HeaderElement prefixUpToPenultimateMulti() {
        int counter = 0;
        int penultimateMultiIndex = -1;
        for (int i = nameParts.size() - 1; i >= 0; i--) {
            if (nameParts.get(i).multi) counter++;
            if (counter == 2) penultimateMultiIndex = i;
        }
        if (penultimateMultiIndex == -1) return null; // gar kein multi vorhanden
        HeaderElement result = new HeaderElement(column);
        for (int i = 0; i <= penultimateMultiIndex; i++) {
            result.add(nameParts.get(i));
        }
        return result;
    }


    /**
     * Prefix-Pfad, der mit dem ersten Multi-Element endet.
     * <pre>
     * firma.kunde.adresse[].strasse -> firma.kunde.adresse[]
     * "firma.kunde.name" -> null
     * </pre>
     * @return Prefix-Pfad; null, falls keine Multi-Element vorhanden ist
     */
    public HeaderElement prefixUpToFirstMulti() {
        HeaderElement result = new HeaderElement(column);
        for (HeaderNamePart namePart : nameParts) {
            if (namePart.multi) {
                result.add(namePart);
                return result;
            }
            result.add(namePart);
        }
        return null;
    }


    /**
     * Schneidet die ersten <code>noOfParts</code> NamePartElemente ab.
     * @param noOfParts Anzahl der abzuschneidenden Teile
     * @return ein neues Header-Element mit reduziertem Pfad
     */
    public HeaderElement cutOffNamePartsFromBeginning(int noOfParts) {
        HeaderElement result = new HeaderElement(column);
        for (int i = noOfParts; i < nameParts.size(); i++) {
            result.add(nameParts.get(i));
        }
        return result;
    }


    public HeaderElement cutOffPrefix(HeaderElement prefix) {
        return startsWith(prefix)? cutOffNamePartsFromBeginning(prefix.size()) : this;
    }


    public HeaderElement cutOffNamePartsUntilFirstMulti() {
        HeaderElement result = new HeaderElement(column);
        boolean addingEnabled = false;
        for (HeaderNamePart namePart : nameParts) {
            if (addingEnabled) result.add(namePart);
            if (namePart.multi) addingEnabled = true;
        }
        return result;
    }


    public void add(HeaderNamePart headerNamePart) {
        nameParts.add(headerNamePart);
    }


    public boolean isCompletelySingle() {
        for (HeaderNamePart namePart : nameParts) {
            if (namePart.multi) return false;
        }
        return true;
    }

    public int noOfMultiElements() {
        return (int)nameParts.stream().filter(t -> t.multi).count();
    }

    public int size() {
        return nameParts == null? 0 : nameParts.size();
    }

    public boolean startsWith(List<HeaderNamePart> nameParts) {
        if (this.size() < nameParts.size()) return false;
        for (int i = 0; i < nameParts.size(); i++) {
            if (!this.nameParts.get(i).equals(nameParts.get(i))) return false;
        }
        return true;
    }

    public boolean startsWith(HeaderElement headerElement) {
        return this.startsWith(headerElement.nameParts);
    }

    @Override
    public String toString() {
        return "HeaderNamePart{" +
                "nameParts=" + nameParts +
                ", column=" + column +
                '}';
    }

    public static void main(String[] args) {
        HeaderElement header1 = new HeaderElement(0, "kunde.konten[].nummer");
        HeaderElement header2 = new HeaderElement(1, "kunde.name");
        HeaderElement header3 = new HeaderElement(2, "kunde.konten[]");
//        System.out.println(header1.prefixBevorLastMulti());

    }
    
}

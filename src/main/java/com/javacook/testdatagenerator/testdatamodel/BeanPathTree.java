package com.javacook.testdatagenerator.testdatamodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by vollmerj on 12.12.16.
 */
public class BeanPathTree extends TreeMap<BeanPath, Object> {

    public final static Object NIL = new Object();

    public BeanPathTree() {
    }

    public Object put(String beanPathStr, Object element, int... indices) {
        return super.put(new BeanPath(beanPathStr, indices), element);
    }

    public Object nil(BeanPath beanPath) {
        return super.put(beanPath, NIL);
    }

    public Object nil(String beanPathStr, int... indices) {
        return nil(new BeanPath(beanPathStr, indices));
    }

    public Object get(String beanPathStr, int... indices) {
        final Object result = get(new BeanPath(beanPathStr, indices));
        if (result == null) throw new IllegalStateException("The path '" + beanPathStr + "' is not assigned.");
        else if (result == NIL) return null;
        else return result;
    }

    public boolean isNil(String beanPathStr, int... indices) {
        return isNil(new BeanPath(beanPathStr, indices));
    }

    /**
     * Der referenzierte Wert isNil, wenn er
     * 1. explizit auf NIL gesetzt wurde,
     * 2. aber auch wenn das Object nur aus Referenzen besteht, die ihrerseits isNil erfuellen.
     * Beispiel:
     * <pre>
     *     kunde.name = NIL
     *     kunde.vorname = NIL
     * </pre>
     * Dann gilt:
     * <pre>
     *     isNil(kunde.name) == true
     *     isNil(kunde.vorname) == true
     *     isNil(kunde) == true // aber auch dies
     * </pre>
     * @param beanPath
     * @return
     */
    public boolean isNil(BeanPath beanPath) {
        if (get(beanPath) == NIL) return true;

        final String LOWEST_IDENTIFIER = "";
        final String UPPER_BOUND_IDENTIFIER = String.valueOf((char)127); // hoechstes ASCII DEL

        BeanPath beanPathLowerBound = new BeanPath(beanPath)
                .addBeanPathElement(new BeanPathElement(LOWEST_IDENTIFIER));

        BeanPath beanPathUpperBound = new BeanPath(beanPath)
                .addBeanPathElement(new BeanPathElement(UPPER_BOUND_IDENTIFIER));

        final SortedMap<BeanPath, Object> map = subMap(beanPathLowerBound, beanPathUpperBound);
        if (map.size() == 0) return false;

        for (BeanPath subBeanPath : map.keySet()) {
            if (!isNil(subBeanPath)) return false;
        }
        return true;
    }

    public String getString(String beanPathStr, int... indices) {
        Object result = null;
        try {
            result = get(beanPathStr, indices);
            return (String) result;
        } catch (ClassCastException e) {
            throw new ClassCastException(result.getClass().getName() + " '" + result +
                    "' cannot be cast to java.lang.String");
        }
    }

    public Integer getInteger(String beanPathStr, int... indices) {
        Object result = null;
        try {
            result = get(beanPathStr, indices);
            return (Integer) result;
        } catch (ClassCastException e) {
            throw new ClassCastException(result.getClass().getName() + " '" + result +
                    "' cannot be cast to java.lang.Integer");
        }
    }

    public <T extends Enum<T>> T getEnum(Class<T> clazz, String beanPathStr, int... indices) {
        return Enum.valueOf(clazz, getString(beanPathStr, indices));
    }

    public Date getDate(String beanPathStr, int... indices) {
        Object result = null;
        try {
            result = get(beanPathStr, indices);
            return (Date) result;
        } catch (ClassCastException e) {
            throw new ClassCastException(result.getClass().getName() + " '" + result +
                    "' cannot be cast to java.lang.Date");
        }
    }

    public byte[] getByteArray(String beanPathStr, int... indices) {
        final String string = getString(beanPathStr, indices);
        return string.getBytes();
    }

    public Calendar getCalendar(String beanPathStr, int... indices) {
        final Calendar result = Calendar.getInstance();
        final Date date = getDate(beanPathStr, indices);
        if (date == null) return null;
        result.setTime(date);
        return result;
    }

    public Double getDouble(String beanPathStr, int... indices) {
        Object result = null;
        try {
            result = get(beanPathStr, indices);
            // Sonderfall, da Excel&Poi sich weigert, eine Double zu liefert, falls der Betrag ganzzahig ist
            if (result instanceof Integer) return (double)(int)result;
            return (Double)result;
        } catch (ClassCastException e) {
            throw new ClassCastException(result.getClass().getName() + " '" + result +
                    "' cannot be cast to java.lang.Double");
        }
    }


    public BigDecimal getBigDecimal(String beanPathStr, int... indices) {
        return new BigDecimal(getDouble(beanPathStr, indices));
    }

    public BigDecimal getBigDecimal(int nachkommastellen, String beanPathStr, int... indices) {
        final BigDecimal bigDecimal = getBigDecimal(beanPathStr, indices);
        return bigDecimal.setScale(nachkommastellen, RoundingMode.HALF_UP);
    }


    /**
     * Ermittelt, wieviele Elemente ein Array hat, das durch <code>beanPathStr</code>
     * definiert wird. Dabei muss <code>beanPathStr</code> mit "[]"enden. Beispiel:
     * <pre>
     *     beanPathStr = "kunde[].stammdaten.adressen[].hausnummern[]"
     * </pre>
     * Dann koennte der Aufruf lauten:
     * <pre>
     *     arraySize(beanPathStr, 4, 3)
     * </pre>
     * D.h. die Groesse von <code>indices</code> muss um eins kleiner sein als die
     * Anzahl der eckigen Klammerpaare.
     * Besonderheit: Falls alle Array-Elemente NIL sind, soll das Ergebnis auch 0 sein.
     * @param beanPathStr
     * @param indices
     * @return
     */
    public int arraySize(String beanPathStr, int... indices) {
        if (!beanPathStr.endsWith("[]")) {
            throw new IllegalArgumentException("Argument beanPathStr = '"+beanPathStr+"' does not reference an array.");
        }

        int[] lowerBound = Arrays.copyOf(indices, indices.length+1);
        int[] upperBound = Arrays.copyOf(indices, indices.length+1);

        int size = 0;
        do {
            lowerBound[indices.length] = size;
            upperBound[indices.length] = size+1;
            final BeanPath beanPathLower = new BeanPath(beanPathStr, lowerBound);
            final BeanPath beanPathUppper = new BeanPath(beanPathStr, upperBound);
            if (subMap(beanPathLower, beanPathUppper).size() == 0) break;
            size++;
        } while (true);

        // Besonderheit: falls alle Array-Elemente NIL sind, so soll die size auch 0 sein.
        for (int i = 0; i < size; i++) {
            final int[] extIndices = Arrays.copyOf(indices, indices.length + 1);
            extIndices[indices.length] = i;
            if (!isNil(beanPathStr, extIndices)) return size;
        }
        return 0;
    }


    public static void main(String[] args) {
        BeanPathTree bpt = new BeanPathTree();
        bpt.put("kunde.vorname[0]", "Jörg");
        bpt.put("kunde.vorname[1].erst", "Peter");
        bpt.put("kunde.vorname[2].zweit", "Armin");
        bpt.put("kunde.vorname[3]", "Claus");
        bpt.nil("kunde.name[0]");
        bpt.nil("kunde.name[1]");
        bpt.nil("kunde.name[2]");

        final NavigableMap<BeanPath, Object> map = bpt.subMap(
                new BeanPath("kunde.vorname[0]"), true,
                new BeanPath("kunde.vorname[oo]"), true);

        System.out.println(map.size());
        System.out.println(bpt.arraySize("kunde.name[]"));
        System.out.println(bpt.isNil("kunde.name"));
    }
}

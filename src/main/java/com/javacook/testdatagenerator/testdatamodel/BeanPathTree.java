package com.javacook.testdatagenerator.testdatamodel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Ein Speicher, in den Objekte anhand eines Beanpfades (als Key) abgespeichert und gelesen werden koennen.
 */
public class BeanPathTree extends TreeMap<BeanPath, Object> {

    public final static Object NIL = new Object() {
        @Override
        public String toString() { return "NIL";}
    };

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

        final SortedMap<BeanPath, Object> map = subTreeAsSortedMap(beanPath);
        if (map.size() == 0) return false;

        for (BeanPath subBeanPath : map.keySet()) {
            if (!isNil(subBeanPath)) return false;
        }
        return true;
    }


    /**
     * Checks whether i consist only of NIL-Elements
     */
    public boolean isNil() {
        for (BeanPath subBeanPath : keySet()) {
            if (!isNil(subBeanPath)) return false;
        }
        return true;
    }

    private SortedMap<BeanPath, Object> subTreeAsSortedMap(BeanPath beanPath) {
        final String LOWEST_IDENTIFIER = "";
        final String UPPER_BOUND_IDENTIFIER = String.valueOf((char)127); // hoechstes ASCII DEL

        BeanPath beanPathLowerBound = new BeanPath(beanPath)
                .addBeanPathElement(new BeanPathElement(LOWEST_IDENTIFIER));

        BeanPath beanPathUpperBound = new BeanPath(beanPath)
                .addBeanPathElement(new BeanPathElement(UPPER_BOUND_IDENTIFIER));

        return subMap(beanPathLowerBound, beanPathUpperBound);
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

    public Set<BeanPathElement> subtreeRoots() {
        Set<BeanPathElement> result = new TreeSet<>();
        for (BeanPath beanPath : keySet()) {
            if (beanPath.head() != null) {
                result.add(beanPath.head());
            }
        }
        return result;
    }

    public BeanPathTree subtree(BeanPathElement beanPathElement) {
        final BeanPathTree beanPathSubTree = new BeanPathTree();
        final SortedMap<BeanPath, Object> map = subTreeAsSortedMap(new BeanPath(beanPathElement));
        for (BeanPath beanPath : map.keySet()) {
            beanPathSubTree.put(beanPath.tail(), get(beanPath));
        }
        return beanPathSubTree;
    }

    public byte[] getByteArray(String beanPathStr, int... indices) {
        final String string = getString(beanPathStr, indices);
        return string.getBytes();
    }

    public Calendar getCalendar(String beanPathStr, int... indices) {
        final Calendar result = Calendar.getInstance();
        final Date date= getDate(beanPathStr, indices);
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


    /**
     * Marshalls the tree (structure and data) into a JSON-Object
     * @return a JSON object structured by means of the tree
     */
    public JSONObject toJSON() {
        if (isEmpty()) return null;
        JSONObject jsonObject = new JSONObject();

        LinkedHashMap<String, Object> elements = new LinkedHashMap<>();

        for (BeanPathElement subTreeRoot : subtreeRoots()) {
            if (subTreeRoot.arrayIndex == null) {
                elements.put(subTreeRoot.attrName, subTreeRoot);
            }
            else {
                if (elements.get(subTreeRoot.attrName) == null) {
                    final ArrayList<BeanPathElement> arrayElements = new ArrayList<>();
                    elements.put(subTreeRoot.attrName, arrayElements);
                }
                final List<BeanPathElement> arrayElements = (List) elements.get(subTreeRoot.attrName);
                arrayElements.add(subTreeRoot);
            }
        }

        for (String key : elements.keySet()) {
            final Object elemOrArray = elements.get(key);
            if (elemOrArray instanceof List) {
                List<BeanPathElement> arrayOfSubTreeRoots = (List)elemOrArray;
                final JSONArray jsonArray = new JSONArray();
                for (BeanPathElement subTreeRoot : arrayOfSubTreeRoots) {
                    final BeanPathTree subtree = subtree(subTreeRoot);
                    if (subtree.isEmpty()) {
                        final Object value = get(new BeanPath(subTreeRoot));
                        jsonArray.add(jsonRenderer(value));
                    }
                    // Subtrees, die nur aus NILs bestehen, sollen nicht hinzugefuegt werden
                    else if (!subtree.isNil()) {
                        jsonArray.add(subtree.toJSON());
                    }
                }
                jsonObject.put(key, jsonArray);
            }
            else {
                BeanPathElement subTreeRoot = (BeanPathElement)elemOrArray;
                final BeanPathTree subtree = subtree(subTreeRoot);
                if (subtree.isEmpty()) {
                    final Object value = get(new BeanPath(subTreeRoot));
                    jsonObject.put(key, jsonRenderer(value));
                }
                // Subtrees, die nur aus NILs bestehen, sollen nicht hinzugefuegt werden
                else if (!subtree.isNil()) {
                    jsonObject.put(key, subtree.toJSON());
                }
            }
        }
        return jsonObject;
    }

    public static final String JSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";
    public static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat(JSON_DATE_FORMAT) {{
                setTimeZone(TimeZone.getTimeZone("UTC"));
            }};

    protected Object jsonRenderer(Object excelCell) {
        if (excelCell == NIL) return null;
        if (excelCell instanceof Date) {
            return DATE_FORMAT.format((Date)excelCell);
        }
        return excelCell;
    }



    /*-----------------------------------------------------------------------------------*\
     * main                                                                              *
    \*-----------------------------------------------------------------------------------*/

    public static void main(String[] args) {
        BeanPathTree bpt = new BeanPathTree();
        bpt.put("vorname[0].erst", "Jörg");
        bpt.put("vorname[1].erst", "Peter");
        bpt.put("vorname[2].erst", "Armin");
        bpt.put("vorname[3].erst", "Claus");
        bpt.nil("kunde.name[0]");
        bpt.nil("kunde.name[1]");
        bpt.nil("kunde.name[2]");
        bpt.put("firma.adresse.plz", 58730);
        bpt.put("firma.adresse.ort", "Fröndenberg");
        bpt.put("firma.adresse.strasse", "Brückenstr.");
        bpt.put("temp[0]", 123);
        bpt.put("temp[1]", 456);
        bpt.put("temp[2]", 789);
        bpt.put("datum", new Date());

//        final NavigableMap<BeanPath, Object> map = bpt.subMap(
//                new BeanPath("kunde.vorname[0]"), true,
//                new BeanPath("kunde.vorname[oo]"), true);

//        System.out.println(map.size());
//        System.out.println(bpt.arraySize("kunde.name[]"));
//        System.out.println(bpt.isNil("kunde.name"));
//        final Set<BeanPathElement> subtreeRoots = bpt.subtreeRoots();
//        for (BeanPathElement subtreeRoot : subtreeRoots) {
//            System.out.println(bpt.subtree(subtreeRoot));
//        }
//        final BeanPathElement next = bpt.subtreeRoots().iterator().next();
//        final BeanPathTree subtree = bpt.subtree(next);
//        System.out.println(subtree);
//        final BeanPathElement next1 = subtree.subtreeRoots().iterator().next();
//        final BeanPathTree subtree1 = subtree.subtree(next1);
//        final BeanPathElement next2 = subtree1.subtreeRoots().iterator().next();
//        final BeanPathTree subtree2 = subtree1.subtree(next2);

//        System.out.println(next);
//        System.out.println(next1);
//        System.out.println(next2);
//        System.out.println(subtree1.get(new BeanPath(next2)));
//        System.out.println(subtree2.isEmpty());

        System.out.println(bpt.toJSON());

    }
}

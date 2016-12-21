package com.javacook.testdatagenerator.testdatamodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vollmerj on 12.12.16.
 */
public class BeanPath implements Comparable<BeanPath> {

    final List<BeanPathElement> beanPathElementList = new ArrayList<>();

    public BeanPath() {
    }

    /**
     * Copy-Konstruktor
     * @param beanPath
     */
    public BeanPath(BeanPath beanPath) {
        beanPathElementList.addAll(beanPath.beanPathElementList);
    }

    public BeanPath(String beanPathStr, int... indices) {
        beanPathStr = replacePlaceholders(beanPathStr, indices);
        final String[] beanPathElements = beanPathStr.split("\\.");
        for (String beanPathElementStr : beanPathElements) {
            addBeanPathElement(new BeanPathElement(beanPathElementStr));
        }
    }

    public BeanPath addBeanPathElement(BeanPathElement beanPathElement) {
        beanPathElementList.add(beanPathElement);
        return this;
    }

    protected String replacePlaceholders(final String beanPathStr, int... indices) {
        String result = beanPathStr;
        int cnt = 0;
        while (result.contains("[]")) {
            if (cnt >= indices.length) {
                throw new IllegalArgumentException("There are more placeholders '[]' than indices: " +
                        "beanPathStr='" + beanPathStr + "', indices=" + Arrays.toString(indices));
            }
            result = result.replaceFirst("\\[\\]", "[" + indices[cnt++] + "]");
        }
        if (cnt < indices.length) {
            throw new IllegalArgumentException("There are more indices than placeholders '[]': " +
                    "beanPathStr='" + beanPathStr + "', indices=" + Arrays.toString(indices));
        }
        return result;
    }

    @Override
    public String toString() {
        return beanPathElementList.stream()
                .map(Object::toString)
                .reduce("", (s1, s2) -> ("".equals(s1)? "": s1+".") + s2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeanPath beanPath = (BeanPath) o;

        return beanPathElementList.equals(beanPath.beanPathElementList);

    }

    @Override
    public int hashCode() {
        return beanPathElementList.hashCode();
    }


    @Override
    public int compareTo(BeanPath bp) {
        for (int i = 0; i < Math.min(beanPathElementList.size(), bp.beanPathElementList.size()); i++) {
            final int comp = beanPathElementList.get(i).compareTo(bp.beanPathElementList.get(i));
            if (comp < 0) return -1;
            if (comp > 0) return 1;
        }
        final int sizeDiff = beanPathElementList.size() - bp.beanPathElementList.size();
        if (sizeDiff < 0) return -1;
        if (sizeDiff > 0) return 1;
        return 0;
    }

}

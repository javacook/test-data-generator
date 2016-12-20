package com.javacook.testdatagenerator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by vollmerj on 12.12.16.
 */
public class BeanPathElement implements Comparable<BeanPathElement> {

    public final static String INFINITY = "oo";

    public final String attrName;
    public final Integer arrayIndex;

    public BeanPathElement(String attrName, Integer arrayIndex) {
        this.attrName = Objects.requireNonNull(attrName);
        this.arrayIndex = arrayIndex;
    }

    /**
     * @param beanPath z.B. "vorname" oder "vornamen[2]"
     */
    public BeanPathElement(String beanPath) {
        if (beanPath.endsWith("]")) {
            final int indexOfLeftBrace = beanPath.indexOf("[");
            if (indexOfLeftBrace < 0) {
                throw new IllegalArgumentException("Argument beanPath contains a format error: " + beanPath);
            }
            try {
                final String numberInsideBraces = beanPath.substring(indexOfLeftBrace+1, beanPath.length()-1);
                arrayIndex = INFINITY.equals(numberInsideBraces)?
                        Integer.MAX_VALUE : Integer.valueOf(numberInsideBraces);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Argument beanPath contains a format error: " + beanPath);
            }
            attrName = beanPath.substring(0, indexOfLeftBrace);
        }
        else {
            attrName = beanPath;
            arrayIndex = null;
        }
    }


    public static List<BeanPathElement> parseBeanPath(String beanPath) {
        List<BeanPathElement> result = new ArrayList<>();
        final String[] beanPathElements = beanPath.split("\\.");
        for (String beanPathElementStr : beanPathElements) {
            result.add(new BeanPathElement(beanPathElementStr));
        }
        return result;
    }


    @Override
    public String toString() {
        return attrName + (arrayIndex == null? "" : "[" + arrayIndex + "]");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeanPathElement that = (BeanPathElement) o;

        if (!attrName.equals(that.attrName)) return false;
        return arrayIndex != null ? arrayIndex.equals(that.arrayIndex) : that.arrayIndex == null;

    }

    @Override
    public int hashCode() {
        int result = attrName.hashCode();
        result = 31 * result + (arrayIndex != null ? arrayIndex.hashCode() : 0);
        return result;
    }


    @Override
    public int compareTo(BeanPathElement bpe) {
        final int compAttrName = attrName.compareTo(bpe.attrName);
        if (compAttrName == 0) {
            if (arrayIndex == null) {
                return (bpe.arrayIndex == null)? 0 : Integer.MIN_VALUE;
            }
            if (bpe.arrayIndex == null) return Integer.MAX_VALUE;
            return arrayIndex - bpe.arrayIndex;
        }
        else return compAttrName;
    }


}

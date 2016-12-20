package com.javacook.testdatagenerator.excelreader;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by vollmer on 09.12.16.
 */
public class Header implements Iterable<HeaderElement> {

    protected List<HeaderElement> headerElements = new ArrayList<>();

    @Override
    public Iterator<HeaderElement> iterator() {
        return headerElements.iterator();
    }

    public final void addHeaderElement(HeaderElement headerElement) {
        headerElements.add(headerElement);
    }

    @Override
    public void forEach(Consumer<? super HeaderElement> action) {
        // FIXME
    }

    /**
     * Liefert das Header
     * @param column
     * @return
     */
    public HeaderElement headerElementForColumn(int column) {
        final List<HeaderElement> headerElements = this.headerElements.stream()
                .filter(t -> t.column == column)
                .collect(Collectors.toList());
        final int size = headerElements.size();
        switch (size) {
            case 0: return null;
            case 1: return headerElements.get(0);
            default: throw new IllegalStateException(
                    "There are at least two column header elements with the same column " + column);
        }
    }



    public Header subHeaderStartingWith(HeaderElement headerElement) {
        final Header result = new Header();
        for (HeaderElement element : this.headerElements) {
            if (element.startsWith(headerElement)) {
                result.addHeaderElement(element);
            }
        }
        return result;
    }


    public Header subHeaderWithTruncatedNameParts(HeaderElement prefix) {
        final Header result = new Header();
        for (HeaderElement headerElement : headerElements) {
            result.addHeaderElement(headerElement.cutOffPrefix(prefix));
        }
        return result;
    }


    public Header subHeaderOnlyWithSingles() {
        final Header result = new Header();
        for (HeaderElement headerElement : headerElements) {
            if (headerElement.size() > 0 && headerElement.isCompletelySingle()) {
                result.addHeaderElement(headerElement);
            }
        }
        return result;
    }

    public int size() {
        return headerElements.size();
    }

    @Override
    public String toString() {
        return "Header{" +
                "headerElements=" + headerElements +
                '}';
    }
}

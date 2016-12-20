package com.javacook.testdatagenerator.excelreader;

import java.util.Objects;

/**
 * Created by vollmer on 14.12.16.
 */
public class HeaderNamePart {

    public final String name;
    public final boolean multi;

    public HeaderNamePart(String part) {
        multi = part.endsWith("[]");
        name = multi? part.substring(0, part.length()-2) : part;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.name);
        hash = 73 * hash + (this.multi ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final HeaderNamePart other = (HeaderNamePart) obj;
        if (this.multi != other.multi) return false;
        if (!Objects.equals(this.name, other.name)) return false;
        return true;
    }

    @Override
    public String toString() {
        return "NamePart{" +
                "name='" + name + '\'' +
                ", multi=" + multi +
                '}';
    }
}

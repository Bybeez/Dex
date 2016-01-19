package com.felkertech.dexc.data;

public class CsvFilter {
    public final static int CONTAINS = 3;
    public final static int EQUALS = 5;

    private int column;
    private int conjunction;
    private String object;
    public CsvFilter(int column, int conjunction, String object) {
        this.column = column;
        this.conjunction = conjunction;
        this.object = object;
    }

    public int getColumn() {
        return column;
    }

    public int getConjunction() {
        return conjunction;
    }

    public String getObject() {
        return object;
    }

    public boolean test(String comparator) {
        switch(getConjunction()) {
            case CONTAINS:
                return comparator.contains(getObject());
            case EQUALS:
                return comparator.equals(getObject());
        }
        return false;
    }
}

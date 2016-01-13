package com.felkertech.dexc.data;

public class CsvFilter {
    public static int CONTAINS = 3;
    public static int EQUALS = 5;

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
}

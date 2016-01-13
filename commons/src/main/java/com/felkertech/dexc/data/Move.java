package com.felkertech.dexc.data;

/**
 * Created by N on 1/2/2015.
 */
public class Move {
    private String name;
    private String id;
    private String method;
    public Move(String n, String i, String m) {
        name = n;
        id = i;
        method = m;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getMethod() {
        return method;
    }
}

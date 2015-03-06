package com.felkertech.n.dex.data;

public class Evolution {
    int prevo_id;
    int evo_id;
    String method;

    public Evolution(int species_id, int evolves_from, String m) {
        evo_id = species_id;
        prevo_id = evolves_from;
        method = m;
    }

    public int getPrevo_id() { return prevo_id; }

    public int getEvo_id() { return evo_id; }

    public String getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return prevo_id+" -> "+method+" -> "+evo_id;
    }
}

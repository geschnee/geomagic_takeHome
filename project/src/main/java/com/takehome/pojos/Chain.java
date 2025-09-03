package com.takehome.pojos;

import java.util.Set;

// Linienzug
public record Chain(int id, Set<Line> elements) {

    public float getLength() {
        return (float) elements().stream().mapToDouble(o -> Double.valueOf(o.length())).sum();
    }

    public boolean lineOverlap(Line l) {
        for (Line ele : elements()) {
            if (ele.id() == l.id()){
                return true;
            }
        }
        return false;
    }

    public void addLineToChain(Line l) {
        elements().add(l);
    }
} 
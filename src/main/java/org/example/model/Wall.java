package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Wall {
    private int length;
    private int angel;
    private List<Opening> openings;

    public Wall(int length, int angel, List<Opening> openings) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        if(angel <=0 || angel >= 360) {
            throw new IllegalArgumentException("Angle must be greater than 0 and less than 360");
        }
        if ( openings == null){
            openings = new ArrayList<Opening>();
        }
        this.length = length;
        this.angel = angel;
        this.openings = List.copyOf(openings);
    }

    public int length() {
        return length;
    }

    public int angle() {
        return angel;
    }

    public List<Opening> openings() {
        return openings;
    }

    public int totalOpeningsArea() {
        int totalArea = 0;
        for(Opening opening: openings){
            totalArea += opening.area();
        }
        return totalArea;
    }
}


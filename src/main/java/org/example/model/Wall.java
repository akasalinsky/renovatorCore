package org.example.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Wall {
    private int length;
    private int height = 2500;
    private List<WallOpening> wallOpenings = new ArrayList<WallOpening>();

    public Wall (int length){
       this(length, List.of());
    }

    public Wall (int length, List<WallOpening> wallOpenings){
        if(wallOpenings == null){wallOpenings = new ArrayList<>();}
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        this.length = length;
        int totalArea = 0;
        int totalWidth = 0;
        int maxHeight = 0;

        if(!wallOpenings.isEmpty()) {
            for (WallOpening wallOpening : wallOpenings) {
                totalArea += wallOpening.area();
                if(wallOpening.width() + wallOpening.distanceFromLeft() > length){throw  new IllegalArgumentException("Wall opening width and distanceFromLeft is too big");}
                totalWidth += wallOpening.width();
                if((wallOpening.height() + wallOpening.distanceFromFloor()) > maxHeight){ maxHeight = wallOpening.height();}
            }
            if(totalArea >= getArea()){throw  new IllegalArgumentException("Wall opening area is too big");}
            if(totalWidth > length){throw  new IllegalArgumentException("Wall opening width and distanceFromLeft is too big");}
            if(maxHeight >= height){throw  new IllegalArgumentException("Wall opening height and distanceFromFloor is too big");}
        }


        this.wallOpenings = wallOpenings;
    }

    public int getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<WallOpening> getWallOpenings() {
        return Collections.unmodifiableList(wallOpenings);
    }

    public int totalOpeningsArea() {
        int totalArea = 0;
        for(WallOpening opening: wallOpenings){
            totalArea += opening.area();
        }
        return totalArea;
    }

    public void setOpening(Opening window, int floordistance, int leftDistance, int rightDistance) {

    }

    public int getArea() {
        return height * length;
    }

    public int netArea() {
        return getArea() - totalOpeningsArea();
    }

    public Wall withOpening(WallOpening wallOpening2) {
        if(wallOpening2.area() >= netArea()){throw new IllegalArgumentException("Wall opening area is too big");}
        if(wallOpening2.distanceFromLeft() + wallOpening2.width() > getLength() ){throw  new IllegalArgumentException("Wall opening width and distanceFromLeft is too big");}
        if(wallOpening2.distanceFromFloor() + wallOpening2.height() > getHeight() ){throw  new IllegalArgumentException("Wall opening height and distanceFromFloor is too big");}

        List<WallOpening> listWallWithWallOpening2 = new java.util.ArrayList<>(getWallOpenings());
        listWallWithWallOpening2.add(wallOpening2);

        return new Wall(getLength(),listWallWithWallOpening2);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Wall wall)) return false;
        return length == wall.length && height == wall.height && Objects.equals(wallOpenings, wall.wallOpenings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, height, wallOpenings);
    }
}


package org.example.model;

public record WallOpening(
        Opening opening,
        int distanceFromLeft,
        int distanceFromFloor
) {
    public WallOpening {
        if (distanceFromLeft < 0) throw new IllegalArgumentException("Distance from left must be non-negative");
        if (distanceFromFloor < 0) throw new IllegalArgumentException("Distance from floor must be non-negative");
        if (opening == null) throw new NullPointerException("opening must not be null");
    }

    public int width() { return opening.width(); }
    public int height() { return opening.height(); }
    public int area() { return opening.getArea(); }
}
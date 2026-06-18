package org.example.model;


public record WallOpening(
        Opening opening,
        int distanceFromLeft,    // метров от левого края стены до левого края проёма
        int distanceFromFloor    // метров от пола до нижнего края проёма
) {
    public WallOpening {
        if (distanceFromLeft < 0) throw new IllegalArgumentException("Distance from left must be non-negative");
        if (distanceFromFloor < 0) throw new IllegalArgumentException("Distance from floor must be non-negative");
        if (opening == null) throw new NullPointerException("opening must not be null");
    }

    // Вычисляемые свойства (удобство)
    public int width() { return opening.getWidth(); }
    public int height() { return opening.getHeight(); }
    public int area() { return opening.getArea(); }
}
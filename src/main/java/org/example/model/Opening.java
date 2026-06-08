package org.example.model;

import java.util.Objects;

public class Opening {

    private OpeningType type;
    private double width;
    private double height;


    public Opening(OpeningType type, double width, double height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Openings area cannot be negative");
        }

        this.type = type;
        this.width = width;
        this.height = height;
    }

    public OpeningType getType() {
        return type;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double area() {
        return width * height;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Opening opening)) return false;
        return Double.compare(width, opening.width) == 0 && Double.compare(height, opening.height) == 0 && type == opening.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, width, height);
    }

    @Override
    public String toString() {
        return "Opening{" +
                "type=" + type +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}

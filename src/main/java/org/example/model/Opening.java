package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Opening {

    private final OpeningType type;
    private final int width;
    private final int height;


    public Opening(
            @JsonProperty("type") OpeningType type,
            @JsonProperty("width") int width,
            @JsonProperty("height") int height) {
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @JsonIgnore
    public int getArea() {
        return width * height;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Opening opening)) return false;
        return Integer.compare(width, opening.width) == 0 && Integer.compare(height, opening.height) == 0 && type == opening.type;
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

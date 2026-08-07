package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Opening(
        OpeningType type,
        int width,
        int height
) {
    public Opening {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Openings area cannot be negative");
        }
    }

    @JsonIgnore
    public int getArea() {
        return width() * height();
    }
}
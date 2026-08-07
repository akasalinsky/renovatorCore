package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Locale;

public record UserProjects(
        @JsonProperty("language") Locale locale,
        List<Room> rooms,
        int activeRoomIndex
) {

    public UserProjects {
        if (locale == null) {
            throw new IllegalArgumentException("locale couldn't be a null");
        }

        String lang = locale.getLanguage();
        if (!lang.equals("en") && !lang.equals("ru")) {
            throw new IllegalArgumentException("unsupported language " + lang);
        }

        if (rooms == null) {
            throw new IllegalArgumentException("rooms couldn't be a null");
        }

        if (rooms.isEmpty()) {
            activeRoomIndex = 0;
        } else {
            if (activeRoomIndex < 0 || activeRoomIndex >= rooms.size()) {
                throw new IllegalArgumentException("activeRoomIndex not defined");
            }
        }

        rooms = List.copyOf(rooms);
    }

    public UserProjects withLanguage(Locale locale) {
        return new UserProjects(locale, rooms(), activeRoomIndex());
    }

    public UserProjects withRooms(List<Room> rooms) {
        return new UserProjects(locale(), rooms, activeRoomIndex());
    }

    public UserProjects withActiveRoomIndex(int activeRoomIndex) {
        return new UserProjects(locale(), rooms(), activeRoomIndex);
    }
}
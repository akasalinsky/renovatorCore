package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class UserProjects {
    private final Locale locale;
    private final List<Room> rooms;
    private final int activeRoomIndex;

    public UserProjects(
            @JsonProperty("language") Locale locale,
            @JsonProperty("rooms") List<Room> rooms,
            @JsonProperty("activeRoomIndex") int activeRoomIndex) {
        if(locale == null){throw new IllegalArgumentException("locale couldn't be a null");}
        String lang = locale.getLanguage();
        if(!lang.equals("en") && !lang.equals("ru")) {throw new IllegalArgumentException("unsupported language" + " " + lang);}
        if(rooms == null) {throw new IllegalArgumentException("rooms couldn'r be a null");}
        if(activeRoomIndex < 0 || activeRoomIndex >= rooms.size() && !rooms.isEmpty()) {
            throw new IllegalArgumentException("activeRoomIndex not defined");
        }
        if(rooms.isEmpty()) {activeRoomIndex = 0;}

        this.locale = locale;
        this.rooms = rooms;
        this.activeRoomIndex = activeRoomIndex;
    }

    public Locale getLanguage() {
        return locale;
    }

    public List<Room> getRooms() {
        return Collections.unmodifiableList(rooms);
    }

    public int getActiveRoomIndex() {
        return activeRoomIndex;
    }

    public UserProjects withLanguage(Locale locale) {
        return new UserProjects(locale, rooms, activeRoomIndex);
    }

    public UserProjects withRooms(List<Room> rooms) {
        return new UserProjects(locale, rooms, activeRoomIndex);
    }

    public UserProjects withActiveRoomIndex(int activeRoomIndex) {
        return new UserProjects(locale, rooms, activeRoomIndex);
    }
}

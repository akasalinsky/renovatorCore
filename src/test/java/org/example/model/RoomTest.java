package org.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    @Test
    void testGetTotalWallArea() {
        // ????????: L=5.5, W=4.2, H=2.8
        // (5.5*2 + 4.2*2) * 2.8 = (11 + 8.4) * 2.8 = 19.4 * 2.8 = 54.32
        Room room = new Room("????????", 5.5, 4.2, 2.8, 0);
        assertEquals(54.32, room.getTotalWallArea(), 0.001);
    }

    @Test
    void testGetNetWallArea() {
        // ????????: ????? ??????? ???? 54.32, ?????? (3.0 + 2.1) = 5.1
        // 54.32 - 5.1 = 49.22
        Room room = new Room("????????", 5.5, 4.2, 2.8, 5.1);
        assertEquals(49.22, room.getNetWallArea(), 0.001);
    }

    @Test
    void testGetFloorArea() {
        // ????????: L=5.5, W=4.2 -> 5.5 * 4.2 = 23.1
        Room room = new Room("????????", 5.5, 4.2, 2.8, 0);
        assertEquals(23.1, room.getFloorArea(), 0.001);
    }

    @Test
    void testGetRoomName() {
        Room room = new Room("???????", 4.0, 3.0, 2.8, 0);
        assertEquals("???????", room.getRoomName());
    }

    @Test
    void testGetRoomLength() {
        Room room = new Room("???????", 3.5, 3.0, 2.8, 0);
        assertEquals(3.5, room.getRoomLength(), 0.001);
    }

    @Test
    void testGetRoomWidth() {
        Room room = new Room("?????-????????", 6.0, 4.5, 2.8, 0);
        assertEquals(4.5, room.getRoomWidth(), 0.001);
    }
}


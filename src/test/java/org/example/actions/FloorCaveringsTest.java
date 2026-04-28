package org.example.actions;

import org.example.model.Room;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FloorCaveringsTest {
    @Test
    void testFloorCalculationForLivingRoom() {
        FloorCaverings floor = new FloorCaverings();
        // ????????: L=5.5, W=4.2 -> Floor Area = 5.5 * 4.2 = 23.1
        Room room = new Room("????????", 5.5, 4.2, 2.8, 0);
        assertEquals(23.1, floor.getResult(room), 0.001);
    }

    @Test
    void testFloorCalculationForKitchen() {
        FloorCaverings floor = new FloorCaverings();
        // ?????-????????: L=6.0, W=4.5 -> Floor Area = 6.0 * 4.5 = 27.0
        Room room = new Room("?????-????????", 6.0, 4.5, 2.8, 0);
        assertEquals(27.0, floor.getResult(room), 0.001);
    }
}


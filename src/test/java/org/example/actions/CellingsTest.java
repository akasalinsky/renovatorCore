package org.example.actions;

import org.example.model.Room;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CellingsTest {
    @Test
    void testCellingCalculationForLivingRoom() {
        Cellings cellings = new Cellings();
        // ????????: L=5.5, W=4.2 -> Celling Area = 5.5 * 4.2 = 23.1
        Room room = new Room("????????", 5.5, 4.2, 2.8, 0);
        assertEquals(23.1, cellings.getResult(room), 0.001);
    }

    @Test
    void testCellingCalculationForKitchen() {
        Cellings cellings = new Cellings();
        // ?????-????????: L=6.0, W=4.5 -> Celling Area = 6.0 * 4.5 = 27.0
        Room room = new Room("?????-????????", 6.0, 4.5, 2.8, 0);
        assertEquals(27.0, cellings.getResult(room), 0.001);
    }
}


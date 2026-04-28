package org.example.actions;

import org.example.model.Room;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PrimerTest {
    @Test
    void testPrimerCalculationForLivingRoom() {
        Primer primer = new Primer();
        // ????????: Net Wall Area = 49.22
        // Liters = 49.22 * 0.15 = 7.383
        Room room = new Room("????????", 5.5, 4.2, 2.8, 5.1);
        assertEquals(7.383, primer.getResult(room), 0.001);
    }

    @Test
    void testPrimerCalculationForBedroom() {
        Primer primer = new Primer();
        // ???????: Net Wall Area = 37.7
        // Liters = 37.7 * 0.15 = 5.655
        Room room = new Room("???????", 4.0, 3.0, 2.8, 1.5);
        assertEquals(5.655, primer.getResult(room), 0.001);
    }
}


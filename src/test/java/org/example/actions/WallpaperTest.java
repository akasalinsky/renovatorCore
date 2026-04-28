package org.example.actions;

import org.example.model.Room;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WallpaperTest {
    @Test
    void testWallpaperCalculationForLivingRoom() {
        Wallpaper wallpaper = new Wallpaper();
        // ????????: L=5.5, W=4.2, H=2.8, Openings=5.1
        // Net Wall Area = 49.22
        // Strips = ceil(49.22 / (1.06 * 5.5)) = ceil(49.22 / 5.83) = ceil(8.44) = 9
        Room room = new Room("????????", 5.5, 4.2, 2.8, 5.1);
        assertEquals(9, wallpaper.getResult(room));
    }

    @Test
    void testWallpaperCalculationForBedroom() {
        Wallpaper wallpaper = new Wallpaper();
        // ???????: L=4.0, W=3.0, H=2.8, Openings=1.5
        // Total Wall Area = (4*2 + 3*2) * 2.8 = (8+6)*2.8 = 14*2.8 = 39.2
        // Net Wall Area = 39.2 - 1.5 = 37.7
        // Strips = ceil(37.7 / (1.06 * 4.0)) = ceil(37.7 / 4.24) = ceil(8.89) = 9
        Room room = new Room("???????", 4.0, 3.0, 2.8, 1.5);
        assertEquals(9, wallpaper.getResult(room));
    }
}


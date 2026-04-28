package org.example;

import org.example.actions.Cellings;
import org.example.actions.FloorCaverings;
import org.example.actions.Primer;
import org.example.actions.Wallpaper;
import org.example.model.Property;
import org.example.model.Room;

public class Main {
    public static void main(String[] args) {
        Property lanovskogo = new Property("Лановского 4-164");
        Room hall = new Room("Зал", 2.700, 3.540, 4.610, 2.837100);
        Room hallway = new Room("Коридор", 2.700, 1.200, 4.600, 7.874000);
        Room kitchen = new Room("Кухня", 2.700, 3.400, 2.740, 5.850000);
        Room bathroom = new Room("Ванная", 2.700, 2.100, 1.700, 1.340000);
        lanovskogo.addRoom(hall);
        lanovskogo.addRoom(hallway);
        lanovskogo.addRoom(kitchen);
        lanovskogo.addRoom(bathroom);

        /*System.out.println(lanovskogo.getPropertyName());
        System.out.println(lanovskogo.getRooms());
        System.out.println(lanovskogo.getTotalArea());
        System.out.println(lanovskogo.getTotalNetWallArea());
*/
        Wallpaper wallpaperCalculator = new Wallpaper();
        Primer primerCalculator = new Primer();
        FloorCaverings floorCalculator = new FloorCaverings();
        Cellings cellingCalculator = new Cellings();

        System.out.println(cellingCalculator.calculate(lanovskogo));
    }
}
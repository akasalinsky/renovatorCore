package org.example;

import org.example.model.Opening;
import org.example.model.OpeningType;
import org.example.model.Room;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        /*Property lanovskogo = new Property("Лановского 4-164");*/
        /*Room hall = new Room("Зал", 2.700, 3.540, 4.610, 2.837100);
        Room hallway = new Room("Коридор", 2.700, 1.200, 4.600, 7.874000);
        Room kitchen = new Room("Кухня", 2.700, 3.400, 2.740, 5.850000);
        Room bathroom = new Room("Ванная", 2.700, 2.100, 1.700, 1.340000);*/

        Opening hallDoor = new Opening(OpeningType.DOOR, 0.8, 2.2);
        Opening hallWindow = new Opening(OpeningType.WINDOW, 1.0, 1.0);

        Opening bathroomDoor = new Opening(OpeningType.DOOR, 0.8, 2.2);

        Opening kitchenDoor = new Opening(OpeningType.DOOR, 0.8, 2.2);
        Opening kitchenWindow = new Opening(OpeningType.WINDOW, 1.0, 1.0);

        Opening hallwayDoor1 = hallDoor;
        Opening hallwayDoor2 = bathroomDoor;
        Opening hallwayDoor3 = kitchenDoor;

        List<Opening> hallOpennigsList = List.of(hallDoor, hallWindow);
        List<Opening> bathroomOpennigsList = List.of(bathroomDoor);
        List<Opening> kitchenOpennigsList = List.of(kitchenDoor, kitchenWindow);
        List<Opening> hallwayOpennigsList = List.of(hallwayDoor1, hallwayDoor2, hallwayDoor3);


        Room hall = new Room("Hall", 4.610, 3.540, 2.700, hallOpennigsList);
        Room hallway = new Room("Hallway", 4.600, 1.200, 2.700, hallwayOpennigsList);
        Room kitchen = new Room("Kitchen", 2.740, 3.400, 2.700, kitchenOpennigsList);
        Room bathroom = new Room("Bathroom", 1.700, 2.100, 2.700, bathroomOpennigsList);


        System.out.println(hall.describe());
        System.out.println(hallway.describe());
        System.out.println(kitchen.describe());
        System.out.println(bathroom.describe());

        System.out.println(hall.getlaminatePlank());
        System.out.println(hallway.getlaminatePlank());
        System.out.println(kitchen.getlaminatePlank());
        System.out.println(bathroom.getlaminatePlank());

        /*lanovskogo.addRoom(hall);
        lanovskogo.addRoom(hallway);
        lanovskogo.addRoom(kitchen);
        lanovskogo.addRoom(bathroom);
*/
        /*System.out.println(lanovskogo.getPropertyName());
        System.out.println(lanovskogo.getRooms());
        System.out.println(lanovskogo.getTotalArea());
        System.out.println(lanovskogo.getTotalNetWallArea());
*//*
        Wallpaper wallpaperCalculator = new Wallpaper();
        Primer primerCalculator = new Primer();
        FloorCaverings floorCalculator = new FloorCaverings();
        Cellings cellingCalculator = new Cellings();

        System.out.println(cellingCalculator.calculate(lanovskogo));*/
    }
}
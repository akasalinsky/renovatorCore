package org.example;

import org.example.model.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        /*Property lanovskogo = new Property("Лановского 4-164");*/
        /*Room hall = new Room("Зал", 2.700, 3.540, 4.610, 2.837100);
        Room hallway = new Room("Коридор", 2.700, 1.200, 4.600, 7.874000);
        Room kitchen = new Room("Кухня", 2.700, 3.400, 2.740, 5.850000);
        Room bathroom = new Room("Ванная", 2.700, 2.100, 1.700, 1.340000);*/

        Opening hallDoor = new Opening(OpeningType.DOOR, 800, 2200);
        Opening hallWindow = new Opening(OpeningType.WINDOW, 1000, 1000);

        Opening bathroomDoor = new Opening(OpeningType.DOOR, 800, 2200);

        Opening kitchenDoor = new Opening(OpeningType.DOOR, 800, 2200);
        Opening kitchenWindow = new Opening(OpeningType.WINDOW, 1000, 1000);

        Opening hallwayDoor1 = hallDoor;
        Opening hallwayDoor2 = bathroomDoor;
        Opening hallwayDoor3 = kitchenDoor;


        Wall hallWall1 = new Wall(4610);
        Wall hallWall2 = new Wall(3020, List.of(new WallOpening(hallDoor, 100, 0)));
        Wall hallWall3 = new Wall(4610);
        Wall hallWall4 = new Wall(3020, List.of(new WallOpening(hallWindow, 1000, 1000)));

        Room hall = new Room("Hall", 2700, List.of(hallWall1, hallWall2, hallWall3, hallWall4), List.of(90, 90, 90, 90));


        System.out.println(hall.renderPlan(1000));


        /*List<Opening> hallOpennigsList = List.of(hallDoor, hallWindow);
        List<Opening> bathroomOpennigsList = List.of(bathroomDoor);
        List<Opening> kitchenOpennigsList = List.of(kitchenDoor, kitchenWindow);
        List<Opening> hallwayOpennigsList = List.of(hallwayDoor1, hallwayDoor2, hallwayDoor3);*/


       /* Room hall = new Room("Hall", 4610, 3540, 2700, hallOpennigsList);
        Room hallway = new Room("Hallway", 4600, 1200, 2700, hallwayOpennigsList);
        Room kitchen = new Room("Kitchen", 2740, 3400, 2700, kitchenOpennigsList);
        Room bathroom = new Room("Bathroom", 1700, 2100, 2700, bathroomOpennigsList);*/


        /*System.out.println(hall.describe());
        System.out.println(hallway.describe());
        System.out.println(kitchen.describe());
        System.out.println(bathroom.describe());

        System.out.println(hall.getlaminatePlank());
        System.out.println(hallway.getlaminatePlank());
        System.out.println(kitchen.getlaminatePlank());
        System.out.println(bathroom.getlaminatePlank());*/

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
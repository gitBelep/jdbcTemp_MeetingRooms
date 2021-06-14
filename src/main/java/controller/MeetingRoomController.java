package controller;

import entity.MeetingRoom;
import repository.RoomRepository;
import service.MeetingRoomServices;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MeetingRoomController {
    private Scanner sc = new Scanner(System.in);
    private MeetingRoomServices mrServices = new MeetingRoomServices(new RoomRepository());

    public static void main(String[] args) {
        MeetingRoomController mrc = new MeetingRoomController();
        mrc.start();
    }

    private void start(){
        String choice;
        do {
            menu();

            choice = sc.nextLine().trim();
            List<String> menuPoints = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8");
            if (menuPoints.contains(choice)) {
                doProperActionByNumber(choice);
            } else {
                System.out.println("Nincs ilyen menüpont");
            }
        } while(!choice.equals("8"));
    }

    private void doProperActionByNumber(String choice){
        if("0".equals(choice)){
            saveMeetingRoom();
        }
        if("1".equals(choice)){
            writeMeetingRoomsOrderedByName();
        }
        if("2".equals(choice)){
            writeMeetingRoomsOrderedDescByName();
        }
        if("3".equals(choice)){
            writeEverySecondMeetingRoom();
        }
        if("4".equals(choice)){
            writeAreas();
        }
        if("5".equals(choice)){
            findMeetingRoomByName();
        }
        if("6".equals(choice)){
            findMeetingRoomByPartOfName();
        }
        if("7".equals(choice)){
            findMeetingRoomAreaGreaterThan();
        }
        if("8".equals(choice)){
            System.out.println(" * Viszontlátásra! * ");
        }
    }

    private void menu(){
        System.out.println("0. Új tárgyaló rögzítése");
        System.out.println("1. Tárgyalók névsorrendben");
        System.out.println("2. Tárgyalók név alapján visszafele sorrendben");
        System.out.println("3. Minden második tárgyaló");
        System.out.println("4. Területek");
        System.out.println("5. Keresés pontos név alapján");
        System.out.println("6. Keresés névtöredék alapján");
        System.out.println("7. Keresés terület alapján");
        System.out.println("8. Kilépés");
    }

    private void saveMeetingRoom(){
        System.out.println("Kérem, adja meg a tárgyaló nevét, melyet el kíván menteni:");
        String name = sc.nextLine();
        System.out.println();
        double width = getSizeOfMeetingRoom("szélességét");
        System.out.println();
        double length = getSizeOfMeetingRoom("hosszát");
        System.out.println();

        int id = mrServices.saveMeetingRoom(new MeetingRoom(name, width, length));
        if(id > -1){
            System.out.println("Sikeresen elmentve! id: "+ id);
        }
    }

    private double getSizeOfMeetingRoom(String txt){
        int size = 0;
        while (size <= 0) {
            System.out.println("Kérem, adja meg a tárgyaló " + txt + " méterben:");
            try {
                size = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException ne){
                System.out.println("Nem megfelelő érték");
            }
        }
        return size;
    }

    private void writeMeetingRoomsOrderedByName(){
        System.out.println("write 1");
    }
    private void writeMeetingRoomsOrderedDescByName(){}
    private void writeEverySecondMeetingRoom(){}
    private void writeAreas(){}
    private void findMeetingRoomByName(){}
    private void findMeetingRoomByPartOfName(){}
    private void findMeetingRoomAreaGreaterThan(){}
}

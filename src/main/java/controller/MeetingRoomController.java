package controller;

import entity.MeetingRoom;
import repository.RoomRepository;
import service.MeetingRoomServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.UnaryOperator;

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
        if("0".equals(choice)) saveMeetingRoom();
        if("1".equals(choice)) writeMeetingRoomsOrderedByName("ASC");
        if("2".equals(choice)) writeMeetingRoomsOrderedByName("DESC");
        if("3".equals(choice)) writeEverySecondMeetingRoom();
        if("4".equals(choice)) writeAreas();
        if("5".equals(choice)) findMeetingRoomByNameOrPart("");
        if("6".equals(choice)) findMeetingRoomByNameOrPart("%");
        if("7".equals(choice)) findMeetingRoomsAreaGreaterThan();
        if("8".equals(choice)) System.out.println(" * Viszontlátásra! * ");
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

    public int saveMeetingRoom(){
        String name = getInputString("Kérem, adja meg a tárgyaló nevét, melyet el kíván menteni:");
        System.out.println();
        double width = inputSizeForCreateMeetingRoom("szélességét (X.X) ");
        System.out.println();
        double length = inputSizeForCreateMeetingRoom("hosszát (X.X) ");
        System.out.println();

        int id = mrServices.saveMeetingRoom(new MeetingRoom(name, width, length));
        if(id > -1){
            System.out.println("Sikeresen elmentve! id: "+ id);
        }
        return id;
    }

    private String getInputString(String txt){
        String in ="";
        do {
            System.out.println(txt);
            in = sc.nextLine().trim();
        } while (in.length() < 1);
        return in;
    }

    private double inputSizeForCreateMeetingRoom(String txt){
        double size = 0.0;
        while (size <= 0.0) {
            System.out.println("Kérem, adja meg a tárgyaló " + txt + "méterben:");
            try {
                size = Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException ne){
                System.out.println("Nem megfelelő érték");
            }
        }
        return size;
    }

    public void writeMeetingRoomsOrderedByName(String ordering){
        List<String> rooms = mrServices.roomsOrderedByName(ordering);
        int in = "DESC".equals(ordering) ? rooms.size() : 1;
        Function<Integer, Integer> function = "DESC".equals(ordering) ? (i -> i - 1) : (i -> i + 1);
        printList( rooms, in, function);
    }

    public void writeEverySecondMeetingRoom(){  //can I give an UnaryOp? (T==R)
        List<String> rooms = mrServices.everySecondMeetingRoom();
        UnaryOperator<Integer> function = i -> i + 1;
        printList(rooms, 1, function);
    }

    public void writeAreas(){
        List<Double> areas = mrServices.listAreas();
        UnaryOperator<Integer> function = i -> i + 1;
        printList(areas, 1, function);
    }

    private <T> void printList(List<T> list, int in, Function<Integer,Integer> function) {
        for (T t : list) {
            System.out.println(in + ". " + t);
            in = function.apply(in);
        }
    }


    public void findMeetingRoomByNameOrPart(String ifPart){
        String name = getInputString("Keresett tárgyaló neve vagy részlet:");
        List<MeetingRoom> rooms = mrServices.findRoomByName(name, ifPart);
        if(rooms.size() == 0){
            rooms = new ArrayList<>(List.of(new MeetingRoom("Nincs ilyen tárgyaló.", 0.0, 0.0)));
        }
        printMeetingRooms( rooms );
    }

    public void findMeetingRoomsAreaGreaterThan(){
        System.out.println();
        double area = inputSizeForCreateMeetingRoom("területét (X.X) négyzet");
        printMeetingRooms( mrServices.findRoomsByArea(area) );
    }

    private void printMeetingRooms(List<MeetingRoom> rooms){
        for(MeetingRoom m : rooms){
            String area = String.format("%3.2f", m.getLength() * m.getWidth());
            System.out.println( m.getId()+" ~ "+ m.getName() +": "+ area);
        }
    }

}

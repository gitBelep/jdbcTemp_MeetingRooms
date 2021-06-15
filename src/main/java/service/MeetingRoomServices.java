package service;

import entity.MeetingRoom;
import repository.RoomRepository;

import java.util.List;

public class MeetingRoomServices {
    private RoomRepository rr;

    public MeetingRoomServices(RoomRepository rr) {
        this.rr = rr;
    }



    public int saveMeetingRoom(MeetingRoom room){


        return rr.saveMeetingRoom(room);
    }

    public List<String> roomsOrderedByName(String ordering){
        return rr.roomsOrderedByName(ordering);
    }

    public List<String> everySecondMeetingRoom(){
        return rr.everySecondMeetingRoom();
    }

    public List<Double> listAreas(){
        return rr.listAreas();
    }

    public List<MeetingRoom> findRoomByName(String s){
        return rr.findRoomsByNameOrPart(s);
    }

    public List<MeetingRoom> findRoomsByArea(double area){
        return rr.findRoomsByArea(area);
    }

}

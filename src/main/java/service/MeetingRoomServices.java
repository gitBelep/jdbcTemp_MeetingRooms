package service;

import entity.Meeting;
import entity.MeetingRoom;
import repository.RoomRepository;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class MeetingRoomServices {
    private RoomRepository rr;

    public MeetingRoomServices(RoomRepository rr) {
        this.rr = rr;
    }


    public int saveMeetingRoom(MeetingRoom room){
        return rr.saveMeetingRoomAndGetId(room);
    }

    public List<String> roomsOrderedByName(String ordering){
        return rr.roomsOrderedByNameB(ordering);
    }

    public List<String> everySecondMeetingRoom(){
        try{
            return rr.everySecondMeetingRoom();
        } catch (SQLException se){
            return Collections.singletonList(se.getMessage());
        }
    }

    public List<Double> listAreas(){
        return rr.listAreas();
    }

    public List<MeetingRoom> findRoomByName(String name, String ifPart){
        return rr.findRoomsByNameOrPart(name, ifPart);
    }

    public List<MeetingRoom> findRoomsByArea(double area){
        return rr.findRoomsByArea(area);
    }

    public void saveMeetingRoomsAndMeetings(MeetingRoom room){
        rr.saveMeetingRoomsAndMeetings(room);
    }

    public List<Meeting> listMeetingsToRoom(long roomId){
        return rr.listMeetingsToRoom(roomId);
    }

    public List<MeetingRoom> loadMeetingRoomsWithMeetings(){
        return rr.loadMeetingRoomsWithMeetings();
    }

}

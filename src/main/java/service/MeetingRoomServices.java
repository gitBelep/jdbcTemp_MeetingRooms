package service;

import entity.MeetingRoom;
import repository.RoomRepository;

public class MeetingRoomServices {
    private RoomRepository rr;

    public MeetingRoomServices(RoomRepository rr) {
        this.rr = rr;
    }



    public int saveMeetingRoom(MeetingRoom room){


        return rr.saveMeetingRoom(room);
    }

}

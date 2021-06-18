package entity;

import repository.RoomRepository;

import java.util.ArrayList;
import java.util.List;

public class MeetingRoom {
    private int id;
    private String name;
    private double width;
    private double length;
    private List<Meeting> meetings = new ArrayList<>();

    public MeetingRoom(String name, double width, double length) {
        this.name = name;
        this.width = width;
        this.length = length;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<Meeting> meetings) {
        this.meetings = meetings;
    }

    public boolean addMeeting(Meeting meeting){
        meetings.add(meeting);
        RoomRepository rr = new RoomRepository();
        return rr.saveMeeting(meeting);
    }

}

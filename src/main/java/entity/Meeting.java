package entity;

import java.time.LocalDateTime;

public class Meeting {
    private String owner;
    private LocalDateTime start;
    private int durationMin;
    private int roomId;

    public Meeting(String owner, LocalDateTime start, int durationMin) {
        this.owner = owner;
        this.start = start;
        this.durationMin = durationMin;
    }

    public Meeting(String owner, LocalDateTime start, int durationMin, int roomId) {
        this.owner = owner;
        this.start = start;
        this.durationMin = durationMin;
        this.roomId = roomId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public int getDurationMin() {
        return durationMin;
    }

    public void setDurationMin(int durationMin) {
        this.durationMin = durationMin;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

}

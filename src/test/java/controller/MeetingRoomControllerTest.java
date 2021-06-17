package controller;

import entity.Meeting;
import entity.MeetingRoom;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.RoomRepository;
import service.MeetingRoomServices;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MeetingRoomControllerTest {
    private MeetingRoomServices mrs;

    @BeforeEach
    void setUp() {
        RoomRepository rr = new RoomRepository();
        mrs = new MeetingRoomServices(rr);
        Flyway fw = Flyway.configure()
                .locations("/db/migration/MeetingRoom")
                .dataSource(rr.getDataSource()).load();
        fw.clean();
        fw.migrate();

        mrs.saveMeetingRoom(new MeetingRoom("Egy", 1.1, 1.2));
        mrs.saveMeetingRoom(new MeetingRoom("Kettő", 22.2, 2.2));
        mrs.saveMeetingRoom(new MeetingRoom("Három", 3.3, 3.2));
        mrs.saveMeetingRoom(new MeetingRoom("Négy", 4.4, 4.2));
    }


    @Test
    void writeMeetingRoomsOrderedByName() {
        List<String> ascending = mrs.roomsOrderedByName("ASC");
        List<String> descending = mrs.roomsOrderedByName("DESC");

        assertEquals("Egy", ascending.get(0));
        assertEquals("Három", ascending.get(1));
        assertEquals("Négy", descending.get(0));
    }

    @Test
    void writeEverySecondMeetingRoom() {
        List<String> seconds = mrs.everySecondMeetingRoom();

        assertEquals("2 Kettő", seconds.get(0));
        assertEquals("4 Négy", seconds.get(1));

        new RoomRepository().deleteAll();

        assertEquals("No meeting rooms are available", mrs.everySecondMeetingRoom().get(0));
    }

    @Test
    void writeAreas() {
        List<Double> areas = mrs.listAreas();

        assertEquals(1.32, areas.get(0), 0.01);
        assertEquals(48.84, areas.get(3), 0.01);
    }

    @Test
    void findMeetingRoomByNameOrPart() {
        MeetingRoom room1 = mrs.findRoomByName("Kettő", "").get(0);
        MeetingRoom roomByPart2 = mrs.findRoomByName("ttő", "%").get(0);
        MeetingRoom roomByPart3 = mrs.findRoomByName("Hár", "%").get(0);

        assertEquals(22.2, room1.getWidth(),0.01);
        assertEquals(2, room1.getId());
        assertEquals(22.2, roomByPart2.getWidth(),0.01);
        assertEquals(2, roomByPart2.getId());
        assertEquals(3.3, roomByPart3.getWidth(),0.01);
        assertEquals(3, roomByPart3.getId());
    }

    @Test
    void findMeetingRoomsAreaGreaterThan() {
        List<MeetingRoom> rooms10 = mrs.findRoomsByArea(10.0);
        List<MeetingRoom> rooms15 = mrs.findRoomsByArea(15.0);
        List<MeetingRoom> rooms20 = mrs.findRoomsByArea(20.0);

        // 1.32, 10.56, 18.480000000000004, 48.84
        assertEquals(3, rooms10.size());
        assertEquals(2, rooms15.size());
        assertEquals("Négy", rooms15.get(1).getName());
        assertEquals(1, rooms20.size());
    }

    @Test
    void findMeetingRoomsAndMeeting() {
        Meeting mA1 = new Meeting("Alma", LocalDateTime.of(2021,7,1,13,0,0), 30);
        Meeting mA2 = new Meeting("Abu", LocalDateTime.of(2021,7,1,14,30,0), 60);
        List<Meeting> lA = List.of(mA2, mA1);
        MeetingRoom rA = new MeetingRoom("Ötűs", 5.1, 5.2);
        rA.setMeetings( lA );
        mrs.saveMeetingRoomsAndMeetings( rA );

        Meeting mB1 = new Meeting("Banya", LocalDateTime.of(2021,7,1,13,0,0), 90);
        Meeting mB2 = new Meeting("Bősze", LocalDateTime.of(2021,7,1,14,30,0), 120);
        List<Meeting> lB = List.of(mB2, mB1);
        MeetingRoom rB = new MeetingRoom("Hatós", 6.1, 6.2);
        rB.setMeetings( lB );
        mrs.saveMeetingRoomsAndMeetings( rB );

        List<MeetingRoom> rooms = mrs.loadMeetingRoomsWithMeetings();

        assertEquals(6, rooms.size());
        assertEquals(30, rooms.get(4).getMeetings().get(0).getDurationMin());
        assertEquals("Abu", rooms.get(4).getMeetings().get(1).getOwner());
        assertEquals(90, rooms.get(5).getMeetings().get(0).getDurationMin());
        assertEquals("Bősze", rooms.get(5).getMeetings().get(1).getOwner());
    }

}

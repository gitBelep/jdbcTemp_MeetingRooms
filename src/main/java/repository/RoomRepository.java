package repository;

import entity.MeetingRoom;
import org.flywaydb.core.Flyway;
import org.mariadb.jdbc.MariaDbDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class RoomRepository {
    private JdbcTemplate jdbcTemplate;
    private MariaDbDataSource dataSource;

    public RoomRepository() {
        try {
            //filebol
            dataSource = new MariaDbDataSource();
            dataSource.setUrl("jdbc:mariadb://localhost:3306/meetingrooms?useUnicode=true");
            dataSource.setUser("meetingrooms");
            dataSource.setPassword("meetingrooms");

            Flyway fw = Flyway.configure().dataSource(dataSource).load();
            fw.migrate();

            jdbcTemplate = new JdbcTemplate(dataSource);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot create DataSource.", sqle);
        }
    }

    public int saveMeetingRoom(MeetingRoom room){
        // --
        return -1;
    }

    public List<String> roomsOrderedByName(String ordering){
        // --
        return Collections.singletonList("No meeting rooms are available");
    }

    public List<String> everySecondMeetingRoom(){
        // --
        return Collections.singletonList("No meeting rooms are available");
    }

    public List<Double> listAreas(){
        // --
        return Collections.singletonList(0.0);
    }

    public List<MeetingRoom> findRoomsByNameOrPart(String prefix){
        // --
        return Collections.singletonList(new MeetingRoom("a", 1.1, 1.1));
    }

    public List<MeetingRoom> findRoomsByArea(double area){
        // --
        return Collections.singletonList(new MeetingRoom("a", 1.1, 1.1));
    }



}

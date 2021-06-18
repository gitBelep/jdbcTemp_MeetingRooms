package repository;

import entity.Meeting;
import entity.MeetingRoom;
import org.flywaydb.core.Flyway;
import org.mariadb.jdbc.MariaDbDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class RoomRepository {
    private JdbcTemplate jdbcTemplate;
    private MariaDbDataSource ds;

    public RoomRepository() {
        try (InputStream is = RoomRepository.class.getResourceAsStream("/company.properties")) {
            Properties pr = new Properties();
            pr.load(is);
            ds = new MariaDbDataSource();
            ds.setUrl(pr.getProperty("Url"));
            ds.setUser(pr.getProperty("User"));
            ds.setPassword(pr.getProperty("Password"));
            jdbcTemplate = new JdbcTemplate(ds);
//            Flyway fw = Flyway.configure()
//                    .locations("/db/migration/MeetingRoom")
//                    .dataSource(ds).load();
//            fw.migrate();
        } catch (IOException | SQLException e) {
            throw new IllegalStateException("Cannot create DataSource.", e);
        }
    }


    public MariaDbDataSource getDataSource() {
        return ds;
    }

    public int saveMeetingRoomAndGetId(MeetingRoom room) {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO rooms(`r_name`, `r_length`, `r_width`) VALUES(?,?,?)",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, room.getName());
                    ps.setDouble(2, room.getLength());
                    ps.setDouble(3, room.getWidth());
                    return ps;
                }, holder);
        return holder.getKey().intValue();
    }

    public List<String> roomsOrderedByNameB(String ordering) {
        String statement = "ASC".equals(ordering) ?
                "SELECT r_name FROM rooms ORDER BY r_name ASC;" :
                "SELECT r_name FROM rooms ORDER BY r_name DESC;";
        return jdbcTemplate.query(statement,
                (rs, i) -> rs.getString("r_name"));
    }

    public List<String> everySecondMeetingRoom() throws SQLException{
        try(Connection conn = ds.getConnection();
        Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = st.executeQuery("SELECT id, r_name FROM rooms ORDER BY id")
        ) {
            List<String> names = new ArrayList<>();
            if (!rs.next()) {
                return Collections.singletonList("No meeting rooms are available");
            }
            rs.relative(1);         //move  foreward from the '1'
            names.add(rs.getInt("id") +" "+ rs.getString("r_name"));
            while (rs.relative(2)){
                names.add(rs.getInt("id") +" "+ rs.getString("r_name"));
            }
            return names;
        }
    }

    public void deleteAll(){
        jdbcTemplate.update("DELETE FROM rooms;");
    }

    public List<Double> listAreas() {
        return jdbcTemplate.query(
                "SELECT (r_width * r_length) AS a FROM rooms ORDER BY a;",
                (rs, i) -> rs.getDouble("a"));
    }

    public List<MeetingRoom> findRoomsByNameOrPart(String name, String ifPart) {
        String searchForText = ifPart + name + ifPart;
        String statement;
        if("%".equals(ifPart)){
            statement = "SELECT id, r_name, r_width, r_length FROM rooms WHERE r_name LIKE ?";
        } else{
            statement = "SELECT id, r_name, r_width, r_length FROM rooms WHERE r_name = ?";
        }
        return jdbcTemplate.query(statement,
                new RowMapper<MeetingRoom>() {
                    @Override
                    public MeetingRoom mapRow(ResultSet rs, int i) throws SQLException {
                        String name = rs.getString("r_name");
                        Double w = rs.getDouble("r_width");
                        Double l = rs.getDouble("r_length");
                        MeetingRoom m = new MeetingRoom(name, w, l);
                        m.setId( rs.getInt("id") );
                        return m;
                    }
                }, searchForText);
    }

    public List<MeetingRoom> findRoomsByArea(double minimumArea) {
        return jdbcTemplate.query(
                "SELECT id, r_name, r_width, r_length FROM rooms WHERE (r_width * r_length) > ?;",
                new RowMapper<MeetingRoom>() {
                    @Override
                    public MeetingRoom mapRow(ResultSet rs, int i) throws SQLException {
                        String name = rs.getString("r_name");
                        Double w = rs.getDouble("r_width");
                        Double l = rs.getDouble("r_length");
                        MeetingRoom m = new MeetingRoom(name, w, l);
                        m.setId( rs.getInt("id") );
                        return m;
                    }
                }, minimumArea);
    }

    public void saveMeetingRoomsAndMeetings(MeetingRoom room){
        try(Connection conn = ds.getConnection()){
            conn.setAutoCommit( false );
            try{
                long id = saveMeetingRoom(room, conn);       // 1 Room
                saveMeetings(id, room.getMeetings(), conn);  // 2 Meetings
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new IllegalStateException("Transaction not succeeded", e);
            }
        } catch (SQLException se) {
            throw new IllegalStateException("Cannot save Room and Meetings", se);
        }
    }

    private long saveMeetingRoom(MeetingRoom room, Connection conn) throws SQLException{
        try(PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO rooms(`r_name`, `r_length`, `r_width`) VALUES(?,?,?)",
                Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, room.getName());
            ps.setDouble(2, room.getLength());
            ps.setDouble(3, room.getWidth());
            ps.executeUpdate();
            long id = -1;
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    id = rs.getLong(1);
                }
            }
            return id;
        }
    }

    private void saveMeetings(long roomId, List<Meeting> meetings, Connection conn)  throws SQLException{
        try(PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO meetings(room_id, owner, start_time, duration) VALUES (?,?,?,?)")){
            for(Meeting actual : meetings) {
                ps.setLong(1, roomId);
                ps.setString(2, actual.getOwner());
                ps.setTimestamp(3, Timestamp.valueOf(actual.getStart()));
                ps.setInt(4, actual.getDurationMin());
                ps.executeUpdate();
            }
        }
    }

    public List<MeetingRoom> loadMeetingRoomsWithMeetings(){
        try(Connection conn = ds.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM rooms ORDER BY id")){
            return selectRoomsByPreparedStatement(ps);
        } catch (SQLException se) {
            throw new IllegalStateException("Cannot list Rooms and Meetings", se);
        }
    }

    private List<MeetingRoom> selectRoomsByPreparedStatement(PreparedStatement ps) throws SQLException{
        try(ResultSet rs = ps.executeQuery()){
            List<MeetingRoom> rooms = new ArrayList<>();
            while(rs.next()){
                long id = rs.getLong("id");
                String name = rs.getString("r_name");
                Double l = rs.getDouble("r_length");
                Double w = rs.getDouble("r_width");
                MeetingRoom actual = new MeetingRoom(name, w, l);

                actual.setMeetings( listMeetingsToRoom(id) );
                rooms.add(actual);
            }
            return rooms;
        }
    }

    public List<Meeting> listMeetingsToRoom(long roomId){
        try(Connection conn = ds.getConnection();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM meetings WHERE room_id = ? ORDER BY start_time;")){
            ps.setLong(1, roomId);
            return getMeetingsByPs(ps);
        }catch (SQLException se) {
            throw new IllegalStateException("Cannot list Meetings", se);
        }
    }

    private List<Meeting> getMeetingsByPs(PreparedStatement ps) throws SQLException{
        try(ResultSet rs = ps.executeQuery()){
            List<Meeting> meetings = new ArrayList<>();
            while(rs.next()){
                long id = rs.getLong("m_id");
                String owner = rs.getString("owner");
                LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
                int duration = rs.getInt("duration");
                meetings.add( new Meeting(owner, startTime, duration) );
            }
            return meetings;
        }
    }

    public boolean saveMeeting(Meeting actual){
        jdbcTemplate.update(
                "INSERT INTO meetings(room_id, owner, start_time, duration) VALUES (?,?,?,?)",
                actual.getRoomId(), actual.getOwner(),
                Timestamp.valueOf(actual.getStart()), actual.getDurationMin());
        return true;
    }

}

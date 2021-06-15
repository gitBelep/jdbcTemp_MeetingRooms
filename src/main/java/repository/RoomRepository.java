package repository;

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

    public List<MeetingRoom> findRoomsByArea(double area) {
        // --
        return Collections.singletonList(new MeetingRoom("a", 1.1, 1.1));
    }


}

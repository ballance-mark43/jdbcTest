import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.sql2o.*;
import java.util.List;

public class MyDatabase {
    private Sql2o sql2o;

    public MyDatabase() {
        String connectionUrl = "jdbc:mysql://localhost:3306/SCORING?autoReconnect=false&useSSL=false";
        this.sql2o = new Sql2o(connectionUrl, "root", "Jellybean01!");
    }

    public void inputEntry(String fileName, int score) {
        String insertSql =
                "INSERT INTO entries VALUES (:id, :score, :fileName, :dateScored);";
        DateTime date = new DateTime(DateTimeZone.UTC);

        try (Connection con = sql2o.open()) {
            con.createQuery(insertSql)
                    .addParameter("id", 0)
                    .addParameter("score", score)
                    .addParameter("fileName", fileName)
                    .addParameter("dateScored", date)
                    .executeUpdate();
        }
    }

    // gets all entries of a specific file in the database
    public List<Entry> getAllEntries(String target) {
        String sql = "SELECT * FROM entries WHERE fileName = :fileName";

        try(Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("fileName", target)
                    .executeAndFetch(Entry.class);
        }
    }

    //gets all entries in a custom date range
    public List<Entry> getEntriesBetweenDates(String fromDate, String toDate){
        String sql = "SELECT * FROM entries " +
                "WHERE dateScored >= :fromDate  AND dateScored <= :toDate";

        try(Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("fromDate", fromDate)
                    .addParameter("toDate", toDate)
                    .executeAndFetch(Entry.class);
        }
    }

    // gets maximum score in a database
    public List<Entry> getMaxScore(String target) {
        String sql = "SELECT MAX(score) FROM entries WHERE fileName = :fileName";

        try(Connection con = sql2o.open()) {

            return con.createQuery(sql)
                    .addParameter("fileName", target)
                    .executeAndFetch(Entry.class);
        }
    }

    // gets minimum score in a database
    public List<Entry> getMinScore(String target) {
        String sql = "SELECT MIN(score) FROM entries WHERE fileName = :fileName";

        try(Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("fileName", target)
                    .executeAndFetch(Entry.class);
        }
    }

    public List<Entry> getAvgScore(String target) {
        String sql = "SELECT Avg(score) FROM entries WHERE fileName = :fileName";

        try(Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("fileName", target)
                    .executeAndFetch(Entry.class);
        }
    }
}


import java.sql.*;
import java.util.Properties;

public class HugeTableGenerator {

    public static void main(String[] args) throws SQLException {

        // MySQL database connection
        final String mysqlUrl = System.getenv("MYSQL_URL");
        final String mysqlUser = System.getenv("MYSQL_USER");
        final String mysqlPassword = System.getenv("MYSQL_PASSWORD");

        // Oracle database connection (uncomment if necessary)
        //final String oracleUrl = System.getenv("ORACLE_URL");
        //final String oracleUser = System.getenv("ORACLE_USER");
        //final String oraclePassword = System.getenv("ORACLE_PASSWORD");

        final String url = mysqlUrl; // Change this to oracleUrl if connecting to Oracle
        final Properties props = new Properties();
        props.setProperty("user", mysqlUser); // Change this to oracleUser if connecting to Oracle
        props.setProperty("password", mysqlPassword); // Change this to oraclePassword if connecting to Oracle

        try (Connection conn = DriverManager.getConnection(url, props)) {
            conn.setAutoCommit(true);

            try (PreparedStatement stmt = conn.prepareStatement(
                        "CREATE TABLE test.BigTable (table_key integer, some_string varchar(64), create_time timestamp, primary key(table_key))")) {
                stmt.execute();
            }
            // 10kk records
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO test.BigTable(table_key, some_string, create_time) values(?,?,?)")) {
                for (int i = 0; i < 20000000; i++) {
                    stmt.setInt(1, i);
                    stmt.setString(2, "Row " + i);
                    stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    stmt.execute();
                    if (i % 100000 == 0) {
                        conn.commit();
                        System.out.println(i + " records");
                    }
                }
                conn.commit();
            }
        }
    }
}

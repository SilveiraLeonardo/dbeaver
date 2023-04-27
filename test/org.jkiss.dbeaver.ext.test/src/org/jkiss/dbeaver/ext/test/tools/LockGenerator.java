
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockGenerator {

    public static final int MAX_SESSIONS = 79;

    public static final int MIN_CHAIN_SIZE = 2;
    public static final int MAX_CHAIN_SIZE = 4;
    
    public static final int MAX_LEVEL_ITEMS = 2;

    private static final Logger LOGGER = LoggerFactory.getLogger(LockGenerator.class);

    private static int getPid(Connection conn) throws SQLException {
        String query = "SELECT pg_backend_pid()";
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet res = stmt.executeQuery()) {
            res.next();
            return res.getInt(1);
        }
    }

    public static void main(String[] args) {
        final String url = "jdbc:postgresql://localhost/postgres";
        final Properties props = new Properties();
        props.setProperty("user", "");
        props.setProperty("password", "");

        try (Connection conn = DriverManager.getConnection(url, props)) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement("SELECT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = current_schema AND table_name = 'usr')");
                 ResultSet res = stmt.executeQuery()) {
                res.next();
                if (!res.getBoolean(1)) {
                    LOGGER.error("Table not found");
                    
                    try (PreparedStatement stmtCreate = conn.prepareStatement("create table usr(field INTEGER,v INTEGER, s VARCHAR)")) {
                        stmtCreate.execute();
                    }
                    
                    try (PreparedStatement stmtInsert = conn.prepareStatement("insert into usr(field,s) SELECT b,(SELECT string_agg(x, '')FROM (SELECT chr(ascii('A') + (random() * 25)::integer) FROM generate_series(1, 1024 + b * 0)) AS y(x)) s FROM generate_series(1,10000) as a(b)")) {
                        stmtInsert.execute();
                    }
                    
                    try (PreparedStatement stmtAlter = conn.prepareStatement("alter table usr add primary key (field)")) {
                        stmtAlter.execute();
                    }

                    conn.commit();
                    LOGGER.error("Table created");
                }
            }

            ExecutorService service = Executors.newFixedThreadPool(MAX_SESSIONS);

            int sessionCount = 0;
            int field = 1;

            while (sessionCount < MAX_SESSIONS) {
                final int fieldVal = field;

                service.submit(() -> updateField(url, props, fieldVal));

                sessionCount++;

                if ((MAX_SESSIONS - sessionCount) > MIN_CHAIN_SIZE) {
                    int chainCount = ThreadLocalRandom.current().nextInt(MIN_CHAIN_SIZE, MAX_CHAIN_SIZE + 1);

                    if ((MAX_SESSIONS - sessionCount) >= chainCount) {
                        for (int i = 0; i < chainCount; i++) {
                            final int level = i;

                            int levelCount = ThreadLocalRandom.current().nextInt(1, MAX_LEVEL_ITEMS + 1);

                            for (int j = 0; j < levelCount; j++) {
                                final int levelNo = j;
                                service.submit(() -> processLevel(url, props, fieldVal, level, levelNo));
                                sessionCount++;

                                if (sessionCount >= MAX_SESSIONS) {
                                    break;
                                }
                            }

                            if (sessionCount >= MAX_SESSIONS) {
                                break;
                            }
                        }
                    }
                }

                field++;
            }

            LOGGER.info("Submitted {}", sessionCount);
            service.shutdown();
            service.awaitTermination(1, TimeUnit.HOURS);

        } catch (SQLException | InterruptedException e) {
            LOGGER.error("Error: ", e);
        }
    }

    private static void updateField(String url, Properties props, int fieldVal) {
        try (Connection c = DriverManager.getConnection(url, props)) {
            String pid = String.valueOf(getPid(c));
            LOGGER.info("[{}] Submitted root session for {}", pid, fieldVal);
            c.setAutoCommit(false);
            try (PreparedStatement s = c.prepareStatement("/*ROOT " + fieldVal + " */ update usr set v = 100500 where field = ?")) {
                s.setInt(1, fieldVal);
                s.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error("Error updating field: " + fieldVal, e);
        }
    }

    private static void processLevel(String url, Properties props, int fieldVal, int level, int levelNo) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            LOGGER.error("Error: ", e);
        }

        try (Connection c = DriverManager.getConnection(url, props)) {
            c.setAutoCommit(false);
            int pid = getPid(c);
            if (levelNo > 0) {
                int sublock = MAX_SESSIONS + (level * MAX_CHAIN_SIZE);
                String query = String.format("/*[%d] Sublock %d for %d -> %d (%d) */ update usr set v = 100500 where field = ?", pid, sublock, fieldVal, level, levelNo);
                try (PreparedStatement s = c.prepareStatement(query)) {
                    LOGGER.info("Sublock for [{}] Sublock {} for {} -> {} ({})", pid, sublock, fieldVal, level, levelNo);
                    s.setInt(1, sublock);
                    s.executeUpdate();
                }
            }

            String query = String.format("/*[%d] %d->%d (%d) */ update usr set v = 100500 where field = ?", pid, fieldVal, level, levelNo);
            try (PreparedStatement s = c.prepareStatement(query)) {
                LOGGER.info("Wait session for [{}] {}->{} ({})", pid, fieldVal, level, levelNo);
                s.setInt(1, fieldVal);
                s.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error("Error processing level {} field: {}", level, fieldVal, e);
        }
    }
}

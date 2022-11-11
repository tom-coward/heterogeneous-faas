package resourcemanager.repositories;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import java.net.InetSocketAddress;

public class CassandraFunctionRepository implements IFunctionRepository {
    private static final String CASSANDRA_SERVER_ADDR = "127.0.0.1";
    private static final int CASSANDRA_SERVER_PORT = 9042;

    private final CqlSession cqlSession;

    public CassandraFunctionRepository() {
        cqlSession = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(CASSANDRA_SERVER_ADDR, CASSANDRA_SERVER_PORT))
                .build();
    }


    public void get() {
        ResultSet rs = cqlSession.execute("select release_version from system.local");
        Row row = rs.one();
        System.out.println(row.getString("release_version"));
    }
}

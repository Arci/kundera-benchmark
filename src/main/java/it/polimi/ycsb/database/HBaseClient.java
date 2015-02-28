package it.polimi.ycsb.database;

import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.DBException;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

/**
 * @author Fabio Arcidiacono.
 */
@Slf4j
public class HBaseClient extends DB {

    public static final int OK = 0;
    public static final int ERROR = -1;
    public static final String NODE = "localhost";
    public static final String PORT = "2181";
    private static final String ZOOKEEPER_NODE = NODE;
    private static final String ZOOKEEPER_PORT = PORT;
    public HTableInterface hTable;
    public byte[] columnFamilyBytes;
    private SecureRandom random = new SecureRandom();

    public HBaseClient() throws DBException {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.master", NODE + ":" + PORT);
        config.set("hbase.zookeeper.quorum", ZOOKEEPER_NODE);
        config.set("hbase.zookeeper.property.clientPort", ZOOKEEPER_PORT);

        try {
            HBaseAdmin admin = new HBaseAdmin(config);
            if (!admin.tableExists("usertable")) {
                HTableDescriptor table = new HTableDescriptor("usertable");
                table.addFamily(new HColumnDescriptor("user"));
                admin.createTable(table);
            }
        } catch (Exception e) {
            throw new DBException(e);
        }

        columnFamilyBytes = Bytes.toBytes("user");
        this.hTable = new HTablePool(config, 100).getTable("usertable");
    }

    /**
     * Initialize any state for this DB. Called once per DB instance; there is one DB instance per client thread.
     */
    public void init() throws DBException {
        /*
         * connection initialized before to be more comparable with Kundera version
         * in which connection is created in the ClientFactory initialized before the init() method
         */
    }

    /**
     * Cleanup any state for this DB. Called once per DB instance; there is one DB instance per client thread.
     */
    public void cleanup() throws DBException {
        hTable = null;
        columnFamilyBytes = null;
    }

    public int read(String table, String key, Set<String> fields, HashMap<String, ByteIterator> result) {
        try {
            Get g = new Get(Bytes.toBytes(key));
            final Object o = hTable.get(g);
            if (o == null) {
                log.error("object is null, table: [" + table + "], key: [" + key + "]");
                return ERROR;
            }
            return OK;
        } catch (Exception e) {
            return ERROR;
        }
    }

    public int scan(String table, String startkey, int recordcount, Set<String> fields, Vector<HashMap<String, ByteIterator>> result) {
        return OK;
    }

    public int update(String table, String key, HashMap<String, ByteIterator> values) {
        return insert(table, key, values);
    }

    public int insert(String table, String key, HashMap<String, ByteIterator> values) {
        if (table == null || key == null) {
            log.error("table: [" + table + "], key: [" + key + "]");
            return ERROR;
        }
        try {
            Put p = new Put(Bytes.toBytes(key));
            // for (Map.Entry<String, ByteIterator> entry : values.entrySet()) {
            //     p.add(columnFamilyBytes, Bytes.toBytes(entry.getKey()), Bytes.toBytes(nextString()));
            // }
            p.add(columnFamilyBytes, Bytes.toBytes("NAME"), Bytes.toBytes(nextString()));
            p.add(columnFamilyBytes, Bytes.toBytes("SURNAME"), Bytes.toBytes(nextString()));
            p.add(columnFamilyBytes, Bytes.toBytes("AGE"), Bytes.toBytes(nextString()));
            p.add(columnFamilyBytes, Bytes.toBytes("ADDRESS"), Bytes.toBytes(nextString()));
            hTable.put(p);
            return OK;
        } catch (Exception e) {
            return ERROR;
        }
    }

    public int delete(String table, String key) {
        if (table == null || key == null) {
            log.error("table: [" + table + "], key: [" + key + "]");
            return ERROR;
        }
        try {
            Delete d = new Delete(Bytes.toBytes(key));
            hTable.delete(d);
            return OK;
        } catch (Exception e) {
            return ERROR;
        }
    }

    public String nextString() {
        return new BigInteger(130, random).toString(32);
    }
}

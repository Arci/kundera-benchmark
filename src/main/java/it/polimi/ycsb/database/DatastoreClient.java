package it.polimi.ycsb.database;

import com.google.appengine.api.datastore.*;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.DBException;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

/**
 * YCSB client that uses plain Google Datastore API.
 *
 * @author Fabio Arcidiacono.
 */
@Slf4j
public class DatastoreClient extends DB {

    private static final int OK = 0;
    private static final int ERROR = -1;
    private RemoteApiInstaller installer;
    private DatastoreService datastore;
    private SecureRandom random;

    public void init() throws DBException {
        random = new SecureRandom();
        try {
            String url = getProperties().getProperty("url");
            int port = Integer.valueOf(getProperties().getProperty("port"));
            String username = getProperties().getProperty("username");
            String password = getProperties().getProperty("password");

            RemoteApiOptions options = new RemoteApiOptions().server(url, port).credentials(username, password);
            this.installer = new RemoteApiInstaller();
            this.installer.install(options);

            DatastoreServiceConfig config = DatastoreServiceConfig.Builder.withDefaults();
            datastore = DatastoreServiceFactory.getDatastoreService(config);
        } catch (Exception e) {
            throw new DBException(e);
        }
    }

    public void cleanup() throws DBException {
        if (installer != null) {
            installer.uninstall();
        }
        datastore = null;
    }

    @Override
    public int read(String table, String key, Set<String> fields, HashMap<String, ByteIterator> result) {
        if (table == null || key == null) {
            log.error("table: [" + table + "], key: [" + key + "]");
            return ERROR;
        }
        try {
            final Object o = datastore.get(KeyFactory.createKey(table, key));
            if (o == null) {
                log.error("object is null, table: [" + table + "], key: [" + key + "]");
                return ERROR;
            }
            return OK;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ERROR;
        }
    }

    @Override
    public int scan(String table, String startkey, int recordcount, Set<String> fields, Vector<HashMap<String, ByteIterator>> result) {
        return OK;
    }

    @Override
    public int update(String table, String key, HashMap<String, ByteIterator> values) {
        return insert(table, key, values);
    }

    @Override
    public int insert(String table, String key, HashMap<String, ByteIterator> values) {
        if (table == null || key == null) {
            log.error("table: [" + table + "], key: [" + key + "]");
            return ERROR;
        }
        try {
            Entity gaeEntity = new Entity(table, key);
            //for (Map.Entry<String, ByteIterator> entry : values.entrySet()) {
            //    gaeEntity.setProperty(entry.getKey(), entry.getValue().toArray());
            //}
            gaeEntity.setProperty("USER_ID", key);
            gaeEntity.setProperty("NAME", nextString());
            gaeEntity.setProperty("SURNAME", nextString());
            gaeEntity.setProperty("AGE", nextString());
            gaeEntity.setProperty("ADDRESS", nextString());
            datastore.put(gaeEntity);
            return OK;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ERROR;
        }
    }

    @Override
    public int delete(String table, String key) {
        if (table == null || key == null) {
            log.error("table: [" + table + "], key: [" + key + "]");
            return ERROR;
        }
        try {
            datastore.delete(KeyFactory.createKey(table, key));
            return OK;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ERROR;
        }
    }

    public String nextString() {
        return new BigInteger(130, random).toString(32);
    }
}

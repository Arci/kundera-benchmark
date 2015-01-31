package it.polimi.ycsb.database;

import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.DBException;
import it.polimi.ycsb.entities.DatastoreUser;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

/**
 * YCSB client that uses Kundera Datastore extension to interact with Google Datastore.
 *
 * @author Fabio Arcidiacono.
 */
@Slf4j
public class KunderaDatastoreClient extends DB {

    private static final int OK = 0;
    private static final int ERROR = -1;
    public static final int MAX_ENTITIES = 5000;
    private static final String PU = "kundera_datastore_pu";
    private static EntityManagerFactory emf;
    private EntityManager em;
    private SecureRandom random;
    private int entities;

    public void init() throws DBException {
        emf = Persistence.createEntityManagerFactory(PU);
        em = emf.createEntityManager();
        random = new SecureRandom();
        entities = 1;
    }

    public void cleanup() throws DBException {
        em.clear();
        em.close();
    }

    @Override
    public int read(String table, String key, Set<String> fields, HashMap<String, ByteIterator> result) {
        if (em == null || !em.isOpen()) {
            em = emf.createEntityManager();
        }
        if (table == null || key == null) {
            log.error("table: [" + table + "], key: [" + key + "]");
            return ERROR;
        }
        try {
            final Object o = em.find(DatastoreUser.class, key);
            if (o == null) {
                log.error("object is null, table: [" + table + "], key: [" + key + "]");
                return ERROR;
            }
            if (timeToClearEntityManager()) {
                em.clear();
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
        if (em == null || !em.isOpen()) {
            em = emf.createEntityManager();
        }
        if (table == null || key == null) {
            log.error("table: [" + table + "], key: [" + key + "]");
            return ERROR;
        }
        try {
            DatastoreUser user = new DatastoreUser(key, nextString(), nextString(), nextString(), nextString());
            em.persist(user);
            if (timeToClearEntityManager()) {
                em.clear();
            }
            return OK;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ERROR;
        }
    }

    @Override
    public int delete(String table, String key) {
        try {
            em.remove(em.find(DatastoreUser.class, key));
            return OK;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ERROR;
        }
    }

    private boolean timeToClearEntityManager() {
        entities++;
        return entities % MAX_ENTITIES == 0;
    }

    public String nextString() {
        return new BigInteger(130, random).toString(32);
    }
}

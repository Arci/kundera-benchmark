package it.polimi.ycsb.database;

import com.microsoft.windowsazure.services.core.storage.CloudStorageAccount;
import com.microsoft.windowsazure.services.table.client.*;
import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.DBException;
import it.polimi.kundera.client.azuretable.DynamicEntity;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

/**
 * YCSB client that uses plain Azure Table API.
 *
 * @author Fabio Arcidiacono.
 */
@Slf4j
public class AzureTableClient extends DB {

    private static final int OK = 0;
    private static final int ERROR = -1;
    private CloudTableClient tableClient;
    private SecureRandom random;

    public void init() throws DBException {
        random = new SecureRandom();
        try {
            String storageConnectionString = buildConnectionString(getProperties());
            tableClient = CloudStorageAccount.parse(storageConnectionString).createCloudTableClient();

            CloudTable table = tableClient.getTableReference("usertable");
            table.createIfNotExist();
        } catch (Exception e) {
            throw new DBException(e);
        }
    }

    private String buildConnectionString(Properties properties) {
        String useEmulator = properties.getProperty("emulator");
        String accountName = properties.getProperty("account.name");
        String accountKey = properties.getProperty("account.key");
        String protocol = properties.getProperty("protocol");

        if (useEmulator != null && useEmulator.equalsIgnoreCase("true")) {
            return "UseDevelopmentStorage=true;DevelopmentStorageProxyUri=http://127.0.0.1";
        }
        return "DefaultEndpointsProtocol=" + protocol + ";AccountName=" + accountName + ";AccountKey=" + accountKey;
    }

    public void cleanup() throws DBException {
        tableClient = null;
    }

    @Override
    public int read(String table, String key, Set<String> fields, HashMap<String, ByteIterator> result) {
        if (table == null || key == null) {
            log.error("table: [" + table + "], key: [" + key + "]");
            return ERROR;
        }
        try {
            TableOperation retrieveOperation = TableOperation.retrieve(table, key, DynamicEntity.class);
            final Object o = tableClient.execute(table, retrieveOperation).getResultAsType();
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
            DynamicEntity tableEntity = new DynamicEntity(table, key);
            // for (Map.Entry<String, ByteIterator> entry : values.entrySet()) {
            //    tableEntity.setProperty(entry.getKey(), new EntityProperty(entry.getValue().toArray()));
            // }
            tableEntity.setProperty("USER_ID", new EntityProperty(key));
            tableEntity.setProperty("NAME", nextString());
            tableEntity.setProperty("SURNAME", nextString());
            tableEntity.setProperty("AGE", nextString());
            tableEntity.setProperty("ADDRESS", nextString());
            TableOperation insertOperation = TableOperation.insertOrReplace(tableEntity);
            tableClient.execute(table, insertOperation);
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
            TableQuery<DynamicEntity> query = TableQuery.from(table, DynamicEntity.class).where(TableQuery.combineFilters(
                    TableQuery.generateFilterCondition("PartitionKey", TableQuery.QueryComparisons.EQUAL, table),
                    TableQuery.Operators.AND,
                    TableQuery.generateFilterCondition("RowKey", TableQuery.QueryComparisons.EQUAL, key)));
            for (DynamicEntity entity : tableClient.execute(query)) {
                TableOperation deleteOperation = TableOperation.delete(entity);
                tableClient.execute(table, deleteOperation);
            }
            return OK;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ERROR;
        }
    }

    public EntityProperty nextString() {
        return new EntityProperty(new BigInteger(130, random).toString(32));
    }
}

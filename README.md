#kundera-benchmark

Benchmarks utilities to test Kundera extension with YCSB.

The available YCSB adapters are:

- Azure Table with low-level API
- Azure Table with Kundera and Kundea-azure-table extension
- GAE Datastore with low-level API
- GAE Datastore with Kundera and Kundea-gae-datastore extension


To start the test:

Install kundera-benchmark with maven:

```
mvn clean install
```

Lunch the command

```
mvn dependency:copy-dependencies
```
this will create a direectory called `dependency` in the `target` directory containing all the jars of the dependencies.

To lunch the YCSB benchmark for the low-level API version use:

```
java -cp kundera-benchmark-jar-location:path-to-depencenvy-folder/*
com.yahoo.ycsb.Client -t -db database-adapter-class-to-use
-P path-to-workload
```

Available adapter classes are:

- `it.polimi.ycsb.database.AzureTableClient` for Azure Table
- `it.polimi.ycsb.database.DatastoreClient` for GAE Datastore

To lunch the YCSB benchmark for the Kundera client version you also need to specify the property file location:

```
java -cp kundera-benchmark-jar-location:path-to-depencenvy-folder/*
com.yahoo.ycsb.Client -t -db database-adapter-class-to-use
-P path-to-workload
-P path-to-property-file
```
Available adapter classes are:

- `it.polimi.ycsb.database.KunderaAzureTableClient` for  kundera-azure-table extension
- `it.polimi.ycsb.database.DatastoreClient` for kundera-gae-datastore extension

Property files must provide information to locate the database to test.
For GAE Datastore the required properties are:

- `url`
- `port`
- `username`
- `password` (can be blank if tests are against localhost)

For Azure Table the required properties are:

- `emulator` [true|false]
- `account.name` from azure portal
- `account.key` from azure portal
- `protocol` [http|https]

if `emulator` is set to `true` the remaining properties are ignored.

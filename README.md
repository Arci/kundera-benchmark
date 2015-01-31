#kundera-benchmark

Benchmarks utilities to test Kundera extension with YCSB.

The available YCSB adapters are:

- Azure Table with low-level API
- Azure Table with Kundera and Kundea-azure-table extension
- GAE Datastore with low-level API
- GAE Datastore with Kundera and Kundea-gae-datastore extension

##Preliminary Operations
The code for the Kundera extensions are available on GitHub:

- [Azure Table extension](https://github.com/Arci/kundera-azure-table)
- [GAE Datastore extension](https://github.com/Arci/kundera-gae-datastore)

you need to download them and install them locally through maven install.

Since YCSB is not available in any maven repository, you have also to [download](https://github.com/brianfrankcooper/YCSB/) it and install it locally through maven install.

Now all the required dependency for kundera-benchmark should be resolved so is possible to install it with maven:

```
mvn clean install
```

then lunch the command:

```
mvn dependency:copy-dependencies
```
this will create a directory called `dependency` in the `target` directory containing all the jars of the dependencies.

##Run the benchmarks
To lunch the YCSB benchmark for the low-level API version use:

```
java -cp kundera-benchmark-jar-location:path-to-dependency-folder/*
com.yahoo.ycsb.Client -t -db database-adapter-class-to-use
-P path-to-workload
```

Available adapter classes are:

- `it.polimi.ycsb.database.AzureTableClient` for Azure Table
- `it.polimi.ycsb.database.DatastoreClient` for GAE Datastore

To lunch the YCSB benchmark for the Kundera client version you also need to specify the property file location:

```
java -cp kundera-benchmark-jar-location:path-to-dependency-folder/*
com.yahoo.ycsb.Client -t -db database-adapter-class-to-use
-P path-to-workload
-P path-to-property-file
```
Available adapter classes are:

- `it.polimi.ycsb.database.KunderaAzureTableClient` for  kundera-azure-table extension
- `it.polimi.ycsb.database.DatastoreClient` for kundera-gae-datastore extension

##Property Files
Property files must provide information to locate the database to test.

####GAE Datastore
The required properties are:

- `url`
- `port`
- `username`
- `password` (can be blank if tests are against localhost)

####Azure Table
The required properties are:

- `emulator` [true|false]
- `account.name` from azure portal
- `account.key` from azure portal
- `protocol` [http|https]

if `emulator` is set to `true` the remaining properties are ignored.

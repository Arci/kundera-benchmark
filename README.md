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
Note that the azure client tests require a reachable storage emulator on Windows so if you do not want to execute test while build run `mvn clean install -DskipTests`.
Test for datastore extension can be executed without further configuration as they're executed thought google in-memory Datastore stub.

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
java -cp KUNDERA-BENCHMARK-JAR-LOCATION:PATH-TO-DEPENDENCY-FOLDER/*
com.yahoo.ycsb.Client -t -db DATABASE-ADAPTER-CLASS-TO-USE
-P PATH-TO-WORKLOAD
```

Available adapter classes are:

- `it.polimi.ycsb.database.AzureTableClient` for Azure Table
- `it.polimi.ycsb.database.DatastoreClient` for GAE Datastore

To lunch the YCSB benchmark for the Kundera client version you also need to specify the property file location:

```
java -cp KUNDERA-BENCHMARK-JAR-LOCATION:PATH-TO-DEPENDENCY-FOLDER/*
com.yahoo.ycsb.Client -t -db DATABASE-ADAPTER-CLASS-TO-USE
-P PATH-TO-WORKLOAD
-P PATH-TO-PROPERTY-FILE
```
Available adapter classes are:

- `it.polimi.ycsb.database.KunderaAzureTableClient` for  kundera-azure-table extension
- `it.polimi.ycsb.database.KunderaDatastoreClient` for kundera-gae-datastore extension
- `it.polimi.ycsb.database.KunderaHBaseClient` for kundera-gae-datastore extension

##Property Files
Property files must provide information to locate the database to test when running the benchmarks on the low-level API versions.

####GAE Datastore
The available properties are:

- `url` _required_
- `port` _optional_ default is __443__
- `username`  _required_ the username of an admin on the remote application
- `password` _required_ can be omitted if tests are against localhost

####Azure Table
The available properties are:

- `emulator` [true|false]
- `account.name` _required_ if not using emulator (available from azure portal)
- `account.key` _required_ if not using emulator (available from azure portal)
- `protocol` [http|https] _optional_ default is __https__

if `emulator` is set to `true` the remaining properties are ignored.

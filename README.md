#kundera-benchmark

Benchmarks utilities to test Kundera extension with YCSB.

The available YCSB adapters are:

- Azure Table with low-level API
- Azure Table with Kundera and Kundea-azure-table extension
- GAE Datastore with low-level API
- GAE Datastore with Kundera and Kundea-gae-datastore extension
- HBase with low-level API
- HBase with Kundera and Kundera-hbase extension

##Preliminary Operations
The code for the Kundera extensions are available on GitHub:

- [Azure Table extension](https://github.com/Arci/kundera-azure-table)
- [GAE Datastore extension](https://github.com/Arci/kundera-gae-datastore)
- [Hbase extension](https://github.com/impetus-opensource/Kundera)

You need to download them and install the one for azure table and the one for google datastore locally through maven install since are not available iun any public maven repository.  
Note that the azure table extension tests to run require a reachable storage emulator on Windows so if you do not want to execute test while build run `mvn clean install -DskipTests`.
Test for datastore extension can be executed without any configuration as they're executed thought google in-memory Datastore stub.  
Since also YCSB is not available in any maven repository, you have also to [download](https://github.com/brianfrankcooper/YCSB/) it and install it locally through maven install.

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
Benchmarks for YCSB run in two distinct phases:

- __load__ that load the data in the target database
- __transaction__ that actually execute the workload test on the loaded data

To execute the __load__ phase use the `-load` option, for the __transaction__ phase use the `-t` option.

##Available datastore
###low-level API versions
Those benchmarks are executed through the command:

```
java -cp KUNDERA-BENCHMARK-JAR-LOCATION:PATH-TO-DEPENDENCY-FOLDER/*
com.yahoo.ycsb.Client -t -db DATABASE-ADAPTER-CLASS-TO-USE
-P PATH-TO-WORKLOAD -s -threads NUMBER-OF-THREAD-TO-USE -PHASE > OUTPUT_FILE
```
where `PHASE` should be `load` for __load__ phase or `t` for __transaction__ phase.

Available adapter classes are:

- `it.polimi.ycsb.database.AzureTableClient` for Azure Table
- `it.polimi.ycsb.database.DatastoreClient` for GAE Datastore
- `it.polimi.ycsb.database.HBaseClient` for Hbase

###Kundera client version
Those benchmarks are executed similarly to the low-level API version with the only difference that you also need to specify the property file location:

```
java -cp KUNDERA-BENCHMARK-JAR-LOCATION:PATH-TO-DEPENDENCY-FOLDER/*
com.yahoo.ycsb.Client -t -db DATABASE-ADAPTER-CLASS-TO-USE
-P PATH-TO-WORKLOAD -P PATH-TO-PROPERTY-FILE
-s -threads NUMBER-OF-THREAD-TO-USE -PHASE > OUTPUT_FILE
```
where `PHASE` should be `load` for __load__ phase or `t` for __transaction__ phase.

Available adapter classes are:

- `it.polimi.ycsb.database.KunderaAzureTableClient` for  kundera-azure-table extension
- `it.polimi.ycsb.database.KunderaDatastoreClient` for kundera-gae-datastore extension
- `it.polimi.ycsb.database.KunderaHBaseClient` for kundera-hbase extension

##Property Files
Property files must provide information to locate the database to test when running the benchmarks on the low-level API versions.

###GAE Datastore
The available properties are:

- `url` _required_
- `port` _optional_, default is __443__
- `username`  _required_, the username of an admin on the remote application
- `password` _required_, can be omitted if tests are against localhost

###Azure Table
The available properties are:

- `emulator` [true|false]
- `account.name` _required_ if not using emulator, available from azure portal
- `account.key` _required_ if not using emulator, available from azure portal
- `protocol` [http|https] _optional_, default is __https__

if `emulator` is set to `true` the remaining properties are ignored.

###Hbase
The properties must be configured inside the []() file because to be more accurate w.r.t. the Kundera client, connection cannot be done in the `init()` method.
The properties can be set modifying the constants:

- `node`, the master node location
- `port`, the master node port
- `zookeeper.node`, the node location for `hbase.zookeeper.quorum`
- `zookeeper.port`, the node port for `hbase.zookeeper.property.clientPort`

Since property file is not needed for Hbase, it does not need to be specified while [running the benchmarks](#low-level-api-version).

##Persistence.xml configuration
In the [persistence.xml](https://github.com/Arci/kundera-benchmark/blob/master/src/main/resources/META-INF/persistence.xml) file, each persistence unit must be configured to locate the database to test.

For specific documentation for the extensions please refer to:

- gae-datastore extension [configuration](https://github.com/Arci/kundera-gae-datastore#configuration)
- azure-table extension [configuration](https://github.com/Arci/kundera-azure-table#configuration)
- hbase extension [configuration](https://github.com/impetus-opensource/Kundera/wiki/Common-Configuration)

__Note:__ hbase configuration make use of a datastore specific property file [hbase-properties.xml](https://github.com/Arci/kundera-benchmark/blob/master/src/main/resources/hbase-properties.xml) in which can be configured the value for `hbase.zookeeper.quorum`  and `hbase.zookeeper.property.clientPort`.
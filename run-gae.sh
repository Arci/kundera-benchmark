#!/bin/bash

printf '\nExcuting benchmark over GAE Datastore [low-level API]\n'
# execute benchmark over GAE datastore low level API
java -cp target/kundera-benchmarks-1.0-SNAPSHOT.jar:target/dependency/* com.yahoo.ycsb.Client -t -db it.polimi.ycsb.database.DatastoreClient -P src/main/resources/workloads/workloadinsert100K -P src/main/resources/datastore.properties -s -threads 30 -load > ../load_lowlevelAPI_datastore
java -cp target/kundera-benchmarks-1.0-SNAPSHOT.jar:target/dependency/* com.yahoo.ycsb.Client -t -db it.polimi.ycsb.database.DatastoreClient -P src/main/resources/workloads/workloadinsert100K -P src/main/resources/datastore.properties -s -threads 30 -t > ../load_lowlevelAPI_datastore

printf '\n\nExcuting benchmark over GAE Datastore [Kundera]\n'
# execute benchmark over GAE datastore kundera version
java -cp target/kundera-benchmarks-1.0-SNAPSHOT.jar:target/dependency/* com.yahoo.ycsb.Client -t -db it.polimi.ycsb.database.KunderaDatastoreClient -P src/main/resources/workloads/workloadinsert100K -s -threads 30 -load > ../load_kundera_datastore
java -cp target/kundera-benchmarks-1.0-SNAPSHOT.jar:target/dependency/* com.yahoo.ycsb.Client -t -db it.polimi.ycsb.database.KunderaDatastoreClient -P src/main/resources/workloads/workloadinsert100K -s -threads 30 -t > ../transaction_kundera_datastore


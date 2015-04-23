#!/bin/bash

# maven install YCSB
cd YCSB/
mvn clean install -DskipTests
cd ..

# maven install azure table kundera client
cd kundera-azure-table/
mvn clean install -DskipTests
cd ..

# maven install GAE datastore kundera client
cd kundera-gae-datastore/
mvn clean install -DskipTests
cd ..

# maven install YCSB adapters
cd kundera-benchmark/
mvn clean install -DskipTests
mvn dependency:copy-dependencies

printf '\n\nExcuting benchmark over Azure Tables [low-level API]'
# execute benchmark over azure tables low level API
java -cp target/kundera-benchmarks-1.0-SNAPSHOT.jar:target/dependency/* com.yahoo.ycsb.Client -t -db it.polimi.ycsb.database.AzureTableClient -P src/main/resources/workloads/workloadinsert100K -P src/main/resources/azure.properties -s -threads 30 -load > ../load_lowlevelAPI_azure
java -cp target/kundera-benchmarks-1.0-SNAPSHOT.jar:target/dependency/* com.yahoo.ycsb.Client -t -db it.polimi.ycsb.database.AzureTableClient -P src/main/resources/workloads/workloadinsert100K -P src/main/resources/azure.properties -s -threads 30 -t > ../load_lowlevelAPI_azure

printf '\n\nExcuting benchmark over Azure Tables [Kundera]'
# execute benchmark over azure tables kundera version
java -cp target/kundera-benchmarks-1.0-SNAPSHOT.jar:target/dependency/* com.yahoo.ycsb.Client -t -db it.polimi.ycsb.database.KunderaAzureTableClient -P src/main/resources/workloads/workloadinsert100K -s -threads 30 -load > ../load_kundera_azure
java -cp target/kundera-benchmarks-1.0-SNAPSHOT.jar:target/dependency/* com.yahoo.ycsb.Client -t -db it.polimi.ycsb.database.KunderaAzureTableClient -P src/main/resources/workloads/workloadinsert100K -s -threads 30 -t > ../transaction_kundera_azure

printf '\n\nExcuting benchmark over GAE Datastore [low-level API]'
# execute benchmark over GAE datastore low level API
java -cp target/kundera-benchmarks-1.0-SNAPSHOT.jar:target/dependency/* com.yahoo.ycsb.Client -t -db it.polimi.ycsb.database.DatastoreTableClient -P src/main/resources/workloads/workloadinsert100K -P src/main/resources/datastore.properties -s -threads 30 -load > ../load_lowlevelAPI_datastore
java -cp target/kundera-benchmarks-1.0-SNAPSHOT.jar:target/dependency/* com.yahoo.ycsb.Client -t -db it.polimi.ycsb.database.DatastoreTableClient -P src/main/resources/workloads/workloadinsert100K -P src/main/resources/datastore.properties -s -threads 30 -t > ../load_lowlevelAPI_datastore

printf '\n\nExcuting benchmark over GAE Datastore [Kundera]\n'
# execute benchmark over GAE datastore kundera version
java -cp target/kundera-benchmarks-1.0-SNAPSHOT.jar:target/dependency/* com.yahoo.ycsb.Client -t -db it.polimi.ycsb.database.KunderaDatastoreTableClient -P src/main/resources/workloads/workloadinsert100K -s -threads 30 -load > ../load_kundera_datastore
java -cp target/kundera-benchmarks-1.0-SNAPSHOT.jar:target/dependency/* com.yahoo.ycsb.Client -t -db it.polimi.ycsb.database.KunderaDatastoreTableClient -P src/main/resources/workloads/workloadinsert100K -s -threads 30 -t > ../transaction_kundera_datastore

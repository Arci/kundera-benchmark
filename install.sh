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

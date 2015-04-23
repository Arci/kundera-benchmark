#!/bin/bash

# install git
sudo apt-get -y install git

# clone git repository
git clone https://github.com/brianfrankcooper/YCSB.git
git clone https://github.com/Arci/kundera-benchmark.git
git clone https://github.com/deib-polimi/kundera-azure-table.git
git clone https://github.com/deib-polimi/kundera-gae-datastore.git

#install jdk and maven
sudo apt-get -y install default-jdk
sudo apt-get -y install maven

printf '\n\nbenchmark environment ready, update the following file'
printf '\n\tkundera-benchmark/src/main/resources/azure.properties'
printf '\n\tkundera-benchmark/src/main/resources/datastore.properties'
printf '\n\tkundera-benchmark/src/main/resources/META-INF/persistence.xml\n'

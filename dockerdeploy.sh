docker container rm -f hbase-webshell
mvn clean package
./dockerbuild.sh
./dockerrun.sh
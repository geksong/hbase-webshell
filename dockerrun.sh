docker run -d --network private --name hbase-webshell -p 18080:8080 -p 18088:8888 --add-host hbase-docker:172.18.0.2 org.sixpence.hbase-webshell:0.0.1-SNAPSHOT
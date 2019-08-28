#!/usr/bin/env bash
export PAUSE=30

docker kill flutter
pkill -f flutter
sleep $PAUSE
docker run --rm  -p 9042:9042 --name flutter -d cassandra:latest
sleep $PAUSE
docker exec -i flutter cqlsh <<EOF
CREATE KEYSPACE flutter WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}  AND durable_writes = true;

CREATE TABLE flutter.user (
    id uuid,
    username text,
    email text,
    enable_notifications boolean,
    created_dttm bigint,
   PRIMARY KEY (id)
);
EOF
mvn clean
nohup mvn -pl service-registry spring-boot:run &
sleep $PAUSE
nohup mvn -pl user-dao spring-boot:run &
sleep $PAUSE
nohup mvn -pl user-service spring-boot:run &
sleep $PAUSE
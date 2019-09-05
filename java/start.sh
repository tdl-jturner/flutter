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
    username text,
    email text,
    enable_notifications boolean,
    created_dttm bigint,
   PRIMARY KEY (username)
);

CREATE TABLE flutter.follow (
    follower text,
    author text,
   PRIMARY KEY (follower,author)
);

CREATE TABLE flutter.message (
    id uuid,
    author text,
    message text,
    created_dttm bigint,
   PRIMARY KEY (id)
);

CREATE TABLE flutter.message_by_author (
    author text,
    created_dttm bigint,
    id uuid,
    message text,
   PRIMARY KEY (author,created_dttm,id)
);

CREATE TABLE flutter.timeline (
    user text,
    author text,
    created_dttm bigint,
    message_id uuid,
    message text,
   PRIMARY KEY (user,author,created_dttm)
);
EOF

rm *.nohup

mvn clean
mvn -pl common clean install
nohup mvn -pl service-registry spring-boot:run &> nohup.service-registry.out&
sleep $PAUSE

nohup mvn -pl user-dao spring-boot:run &> nohup.user-dao.out&
nohup mvn -pl follow-dao spring-boot:run &> nohup.follow-dao.out&
nohup mvn -pl message-dao spring-boot:run &> nohup.message-dao.out&
nohup mvn -pl timeline-dao spring-boot:run &> nohup.message-dao.out&

nohup mvn -pl user-service spring-boot:run &> nohup.user-service.out&
nohup mvn -pl follow-service spring-boot:run &> nohup.follow-service.out&
nohup mvn -pl message-service spring-boot:run &> nohup.message-service.out&
nohup mvn -pl timeline-service spring-boot:run &> nohup.message-service.out&
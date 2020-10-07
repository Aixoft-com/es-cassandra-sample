## Cassandra

To run cassandra node (automatically performs migration):

    docker-compose -f src/main/docker/cassandra.yml up [-d]
    
To run cassandra container which automatically apply CQL migrations scripts (no need to restart cassandra):
    
    docker-compose -f src/main/docker/cassandra-migration.yml up [-d]

To scale cluster on X+1 nodes run following command:

    docker-compose -f src/main/docker/cassandra-cluster.yml scale greceipt-cassandra-node=X

CQLs files from docker/cassandra/schema/changelog will be executed in file name alphabetical order. 
Rules:
 
    1. Naming convention for changelog files yyyy-MM-dd_{6_digit_id}_{description}.cql
    2. Do not remove changelogs in already on prod ( cassandra-migration is not detecting that).


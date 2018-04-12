****************
Solr
****************

How to prepare solr 5.5.1 instance:

1. Add these libraries to solr/server/lib/ish:
jts-1.13.jar
mysql-connector-java-5.1.38.jar
spatial4j-0.5.jar

2. Add these libraries to solr/server/lib

jetty-jndi-9.2.13.v20150730.jar
jetty-plus-9.2.13.v20150730.jar

4. adjust [lib] section in file solr/server/modules/server.mod
[lib]
lib/*.jar
lib/ext/*.jar
lib/ish/*.jar
resources/

5. configure jndi for the db connection in solr/server/etc/jetty.xml:
  <New id="onCourseRef" class="org.eclipse.jetty.plus.jndi.Resource">
    <Arg></Arg>
    <Arg>java:comp/env/jdbc/oncourse</Arg>
    <Arg>
      <New class="com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource">
        <Set name="url">jdbc:mysql://localhost:3306/$DBNAME?autoReconnect=true&amp;zeroDateTimeBehavior=convertToNull</Set>
        <Set name="user">$DBUSER</Set>
        <Set name="password">$DBPASSWORD</Set>
      </New>
    </Arg>
  </New>

****************
Zookeeper
****************

How to configure zookeeper.

1. zookeeper/conf/zoo.cfg
tickTime=2000
initLimit=10
syncLimit=5
dataDir=<data files dir>
clientPort=2181
#maxClientCnxns=60
#autopurge.snapRetainCount=3
#autopurge.purgeInterval=1

server.1=127.0.0.1:2888:3888 # zookeeper server 1, dataDir for this server should have file myid and the file should contain: 1
server.2=127.0.0.1:2889:3889 # ip address and port of the other servers
server.3=127.0.0.1:2890:3890


****************
Configuration
****************

How to use solr+zookeeper.

1. Start the zookeeper-cluster

2. Start solr instances:
# ./bin/solr start -m 1g -c -z localhost:2181,localhost:2182,localhost:2183 -p <port> -s <solr.home>port - tcp port for this solr server: 7001,7002 .....
 solr.home - path to dir where solr.xml is located

3. upload  core configs to zookeeper:
# pushd solr/server/scripts/cloud-scripts
# ./zkcli.sh -zkhost localhost:2181 -cmd upconfig -confdir  /courses/conf -confname courses
# ./zkcli.sh -zkhost localhost:2181 -cmd upconfig -confdir  /tags/conf -confname tags
# ./zkcli.sh -zkhost localhost:2181 -cmd upconfig -confdir  /suburbs/conf -confname suburbs
# popd

4. create a new collection for every config:
# curl 'http://localhost:7001/solr/admin/collections?action=CREATE&name=courses&numShards=1&replicationFactor=1&maxShardsPerNode=1&collection.configName=courses'
# curl 'http://localhost:7001/solr/admin/collections?action=CREATE&name=tags&numShards=1&replicationFactor=1&maxShardsPerNode=1&collection.configName=tags'
# curl 'http://localhost:7001/solr/admin/collections?action=CREATE&name=suburbs&numShards=1&replicationFactor=1&maxShardsPerNode=1&collection.configName=suburbs'


****************
Updating
****************

How to update solr configuration.

1. Edit config files

2. execute the command:
# ./zkcli.sh -zkhost localhost:2181 -cmd upconfig -confdir  /courses/conf -confname courses

I have not found how to configure zookeeper to monitor the conf folder and update config files automatically
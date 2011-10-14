Build project:

mvn install:install-file -DgroupId=org.apache.solr -DartifactId=solr-webapp -Dversion=1.4.1 -Dpackaging=war -Dfile=/path/apache-solr-1.4.1.war

To Start server:

    mvn -Dsolr.data.dir=/Users/anton/willow/index -Dsolr.solr.home=/Users/anton/willow/code/searchapp/src/main/resources/solr jetty:run-exploded

To Stop server:

    mvn jetty:stop
    

Build index from database:
	
	GET request to http://{solr_url}/solr/dataimport?command=full-import


Run webapp project:

	add JVM paam -Dsolr.server=http://localhost:8081/solr
	
	
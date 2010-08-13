To Start server:

    mvn install:install-file -DgroupId=org.apache.solr -DartifactId=solr-webapp -Dversion=1.4.1 -Dpackaging=war -Dfile=/path/apache-solr-1.4.1.war
    mvn jetty:run-exploded

To Stop server:

    mvn jetty:stop
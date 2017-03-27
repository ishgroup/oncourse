package ish.oncourse.willow

class Configuration {

    static final String CONFIG_NAME = 'application.properties'
    static final String BD_URL = 'jdbc:mysql://%s:%s/%s?autoReconnect=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf8'

    static configure() {
        
        System.out.println(System.getProperties().get('user.dir'))

        File propFile = new File((System.getProperties().get('user.dir') as String) +'/'+ CONFIG_NAME)
        if (propFile.exists()) {
            Properties prop = new Properties()
            prop.load(new FileInputStream(propFile))
            System.setProperty('bq.jetty.connector.port', prop.get('port') as String)
            System.setProperty('bq.jetty.connector.host', prop.get('host') as String)
            System.setProperty('bq.jdbc.ish.url', String.format(BD_URL, prop.get('db_host'), prop.get('db_port'), prop.get('db_name')))
            System.setProperty('bq.jdbc.ish.username', prop.get('db_user') as String)
            System.setProperty('bq.jdbc.ish.password', prop.get('db_pass') as String)
        } else {
            throw new IllegalArgumentException("application.properties file not found")
        }

    }
}
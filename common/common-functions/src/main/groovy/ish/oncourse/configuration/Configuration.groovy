package ish.oncourse.configuration

class Configuration {

    static final String CONFIG_FILE_NAME = 'application.properties'
    static final String BD_URL = 'jdbc:mysql://%s:%s/%s?autoReconnect=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf8&useSSL=false'
    static final String ZK_HOST_PROPERTY = 'zk.host.property'
    static final String JETTY_PORT_PROPERTY = 'bq.jetty.connector.port'
    static final String JETTY_HOST_PROPERTY = 'bq.jetty.connector.host'
    static final String JDBC_URL_PROPERTY = 'bq.jdbc.willow.url'
    static final String JDBC_USERNAME_PROPERTY = 'bq.jdbc.willow.username'
    static final String JDBC_PASSWORD_PROPERTY = 'bq.jdbc.willow.password'
    static final String JETTY_CONTEXT_PROPERTY = 'bq.jetty.context'


    static configure() {
        String userDir = System.getProperties().get('user.dir') as String
        File propFile = new File(userDir+'/'+ CONFIG_FILE_NAME)
        if (propFile.exists()) {
            Properties prop = new Properties()
            prop.load(new FileInputStream(propFile))
            System.setProperty(JETTY_PORT_PROPERTY, prop.get('port') as String)
            System.setProperty(JETTY_HOST_PROPERTY, prop.get('host') as String)
            System.setProperty(JDBC_URL_PROPERTY, String.format(BD_URL, prop.get('db_host'), prop.get('db_port'), prop.get('db_name')))
            System.setProperty(JDBC_USERNAME_PROPERTY, prop.get('db_user') as String)
            System.setProperty(JDBC_PASSWORD_PROPERTY, prop.get('db_pass') as String)
            
            if (prop.get('path')) {
                System.setProperty(JETTY_CONTEXT_PROPERTY, prop.get('path') as String)
            }
            
            if (prop.get('zk_host')) {
                System.setProperty(ZK_HOST_PROPERTY, prop.get('zk_host') as String)
            }
            
        } else {
            throw new IllegalArgumentException("application.properties file not found")
        }

    }
}
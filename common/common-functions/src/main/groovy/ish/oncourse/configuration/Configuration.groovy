package ish.oncourse.configuration

class Configuration {

    /**
     * property names which supported into 'application.properties' files
     */
    static final String SUPPORTED_PROPERTY_PORT = 'port'
    static final String SUPPORTED_PROPERTY_HOST = 'host'
    static final String SUPPORTED_PROPERTY_DB_HOST = 'db_host'
    static final String SUPPORTED_PROPERTY_DB_PORT = 'db_port'
    static final String SUPPORTED_PROPERTY_DB_NAME = 'db_name'
    static final String SUPPORTED_PROPERTY_DB_USER = 'db_user'
    static final String SUPPORTED_PROPERTY_DB_PASS = 'db_pass'
    static final String SUPPORTED_PROPERTY_PATH = 'path'
    static final String SUPPORTED_PROPERTY_ZK_HOST = 'zk_host'
    static final String SUPPORTED_PROPERTY_LOGS_PATH = 'logs_path'
    static final String SUPPORTED_PROPERTY_SMTP = 'smtp'
    
    static final String USER_DIR = 'user.dir'
    static final String CONFIG_FILE_NAME = 'application.properties'
    
    static final String BD_URL = 'jdbc:mysql://%s:%s/%s?autoReconnect=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf8&useSSL=false'
    
    public static final String ZK_HOST_PROPERTY = 'zk.host.property'
    static final String JETTY_PORT_PROPERTY = 'bq.jetty.connector.port'
    static final String JETTY_HOST_PROPERTY = 'bq.jetty.connector.host'
    static final String JDBC_URL_PROPERTY = 'bq.jdbc.willow.url'
    static final String JDBC_USERNAME_PROPERTY = 'bq.jdbc.willow.username'
    static final String JDBC_PASSWORD_PROPERTY = 'bq.jdbc.willow.password'
    static final String JETTY_CONTEXT_PROPERTY = 'bq.jetty.context'
    static final String LOGS_PATH_PROPERTY = 'logs.path'
    public static final String SYSTEM_PROPERTY_SMTP_HOST = "mail.smtp.host"



    static configure() {
        String userDir = System.getProperties().get(USER_DIR) as String
        File propFile = new File(userDir+'/'+ CONFIG_FILE_NAME)
        if (propFile.exists()) {
            Properties prop = new Properties()
            prop.load(new FileInputStream(propFile))
            System.setProperty(JETTY_PORT_PROPERTY, prop.get(SUPPORTED_PROPERTY_PORT) as String)
            System.setProperty(JETTY_HOST_PROPERTY, prop.get(SUPPORTED_PROPERTY_HOST) as String)
            System.setProperty(JDBC_URL_PROPERTY, String.format(BD_URL, prop.get(SUPPORTED_PROPERTY_DB_HOST), prop.get(SUPPORTED_PROPERTY_DB_PORT), prop.get(SUPPORTED_PROPERTY_DB_NAME)))
            System.setProperty(JDBC_USERNAME_PROPERTY, prop.get(SUPPORTED_PROPERTY_DB_USER) as String)
            System.setProperty(JDBC_PASSWORD_PROPERTY, prop.get(SUPPORTED_PROPERTY_DB_PASS) as String)
            
            if (prop.get(SUPPORTED_PROPERTY_PATH)) {
                System.setProperty(JETTY_CONTEXT_PROPERTY, prop.get(SUPPORTED_PROPERTY_PATH) as String)
            }
            
            if (prop.get(SUPPORTED_PROPERTY_ZK_HOST)) {
                String zkHostPort = prop.get(SUPPORTED_PROPERTY_ZK_HOST) as String
                System.setProperty(ZK_HOST_PROPERTY, zkHostPort)
                InitZKRootNode.valueOf(zkHostPort).init()
            }
            
            if (prop.get(SUPPORTED_PROPERTY_LOGS_PATH)) {
                System.setProperty(LOGS_PATH_PROPERTY, prop.get(SUPPORTED_PROPERTY_LOGS_PATH) as String)
            } else {
                System.setProperty(LOGS_PATH_PROPERTY, "${userDir}/logs/")
            }

            if (prop.get(SUPPORTED_PROPERTY_SMTP)) {
                System.setProperty(SYSTEM_PROPERTY_SMTP_HOST, prop.get(SUPPORTED_PROPERTY_SMTP) as String)
            }
            
        } else {
            throw new IllegalArgumentException("application.properties file not found")
        }

    }
}
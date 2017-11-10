package ish.oncourse.configuration

import static ish.oncourse.configuration.Configuration.AppProperty.*

class Configuration {
    
    static final String USER_DIR = 'user.dir'
    static final String CONFIG_FILE_NAME = 'application.properties'
    public static final String JDBC_URL_PROPERTY = 'bq.jdbc.willow.url'
    static final String BD_URL = 'jdbc:mysql://%s:%s/%s?autoReconnect=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf8&useSSL=false'
    
    static configure(IProperty... extendedProps = null) {
        String userDir = System.getProperties().get(USER_DIR) as String
        File propFile = new File("$userDir/$CONFIG_FILE_NAME")
        if (propFile.exists()) {
            Properties prop = new Properties()
            prop.load(new FileInputStream(propFile))
            
            if (!LOGS_PATH.init(prop)) {
                System.setProperty(LOGS_PATH.systemProperty, "${userDir}/logs/")
            }
            PORT.init(prop)
            HOST.init(prop)
            PATH.init(prop)
            System.setProperty(JDBC_URL_PROPERTY, String.format(BD_URL, prop.get(DB_HOST.key), prop.get(DB_PORT.key), prop.get(DB_NAME.key)))
            DB_PASS.init(prop)
            DB_USER.init(prop)
            SMTP.init(prop)

            if (ZK_HOST.init(prop)) {
                String zkHostPort = prop.get(ZK_HOST.key) as String
                InitZKRootNode.valueOf(zkHostPort).init()
            }
            if (extendedProps) {
                extendedProps.each { it.init(prop) }
            }
        } else {
            throw new IllegalArgumentException("application.properties file not found")
        }
    }

    /**
     * properties which supported into 'application.properties' files
     */
    static enum AppProperty implements IProperty {
        
        PORT('port', 'bq.jetty.connector.port'),
        HOST('host', 'bq.jetty.connector.host'),
        DB_HOST('db_host', null),
        DB_PORT('db_port', null),
        DB_NAME('db_name', null),
        DB_USER('db_user', 'bq.jdbc.willow.username'),
        DB_PASS('db_pass', 'bq.jdbc.willow.password'),
        PATH('path', 'bq.jetty.context'),
        ZK_HOST('zk_host', 'zk.host.property'),
        LOGS_PATH('logs_path', 'logs.path'),
        SMTP('smtp', 'mail.smtp.host')
        
        private AppProperty(String key, String systemProperty) {
            this.key = key
            this.systemProperty = systemProperty
        }
        
    }
}
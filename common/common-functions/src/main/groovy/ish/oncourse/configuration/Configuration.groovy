package ish.oncourse.configuration

import static ish.oncourse.configuration.Configuration.AppProperty.*

class Configuration {
    
    static final String USER_DIR = 'user.dir'
    static final String CONFIG_FILE_NAME = 'application.properties'
    public static final String JDBC_URL_PROPERTY = 'bq.jdbc.willow.url'
    static final String BD_URL = 'jdbc:mysql://%s:%s/%s?autoReconnect=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf8&useSSL=false'
    
    static configure(IProperty... extendedProps = null) {
        
        String userDir = System.getProperties().get(USER_DIR) as String
        Properties props = loadPropertyFile("$userDir/$CONFIG_FILE_NAME")
        if (props) {
            if (!init(props, LOGS_PATH)) {
                System.setProperty(LOGS_PATH.systemProperty, "${userDir}/logs/")
            }
            init(props, PORT)
            init(props, HOST)
            init(props, PATH)
            System.setProperty(JDBC_URL_PROPERTY, String.format(BD_URL, props.get(DB_HOST.key), props.get(DB_PORT.key), props.get(DB_NAME.key)))
            init(props, DB_PASS)
            init(props, DB_USER)
            init(props, SMTP)

            if (init(props, ZK_HOST)) {
                String zkHostPort = props.get(ZK_HOST.key) as String
                InitZKRootNode.valueOf(zkHostPort).init()
            }
            if (extendedProps) {
                extendedProps.each { init(props, it) }
            }
        }
    }

    static Properties loadPropertyFile(String path) {
        Properties props
        FileInputStream stream
        try {
            stream = new FileInputStream(path)
            props = new Properties()
            props.load(stream)
        } catch (Exception ex) {
            throw new IllegalArgumentException("Exception during reading application.properties file", ex)
        } finally {
            try {
                stream.close()
            } catch (IOException ex) {}
        }
        props
    }


    static boolean init(Properties props, IProperty prop) {
        if (props.get(prop.key)) {
            System.setProperty(prop.systemProperty, props.get(prop.key) as String)
            return true
        }
        return false
    }
    
    static String getValue(IProperty prop) {
        System.getProperty(prop.systemProperty)
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

        private String key
        private String systemProperty
        
        private AppProperty(String key, String systemProperty) {
            this.key = key
            this.systemProperty = systemProperty
        }

        String getKey() {
            key
        }

        String getSystemProperty() {
            systemProperty
        }
    }
}
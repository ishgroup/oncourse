package ish.oncourse.configuration

import java.util.regex.Pattern

import static ish.oncourse.configuration.Configuration.AppProperty.*

class Configuration {
    
    static final String USER_DIR = 'user.dir'
    static final String CONFIG_FILE_NAME = 'application.properties'
    static final Pattern DB_URL_PATTERN = Pattern.compile('(\\w+:)+\\/\\/.+\\/\\w+')
    
    static final String BD_URL_PARAMS = 'autoReconnect=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf8&useSSL=false'

    static configureOnly(IProperty... extendedProps) {
        String userDir = System.getProperties().get(USER_DIR) as String
        Properties props = loadProperties()

        if (props) {
            if (!init(props, LOGS_PATH)) {
                System.setProperty(LOGS_PATH.systemProperty, "${userDir}/logs/")
            }
            extendedProps.each { it
                init(props, it)

            }
        }
    }


    static configure(IProperty... extendedProps = null) {
        
        String userDir = System.getProperties().get(USER_DIR) as String
        Properties props = loadProperties()
        if (props) {
            if (!init(props, LOGS_PATH)) {
                System.setProperty(LOGS_PATH.systemProperty, "${userDir}/logs/")
            }
            init(props, PORT)
            init(props, HOST)
            init(props, PATH)
            init(props, DB_URL)
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

    static Properties loadProperties() {
        String userDir = System.getProperties().get(USER_DIR) as String

        Properties props = null
        FileInputStream stream
        try {
            stream = new FileInputStream("$userDir/$CONFIG_FILE_NAME")
            props = new Properties()
            props.load(stream)
        } catch (Exception ex) {
            throw new IllegalArgumentException("Exception during reading application.properties file", ex)
        } finally {
            try {
                if (stream) {
                    stream.close()
                }
            } catch (IOException ignore) {}
        }
        props
    }


    static boolean init(Properties props, IProperty prop) {
        if (props.get(prop.key)) {
            String value =  props.get(prop.key) as String
            if (prop == DB_URL && DB_URL_PATTERN.matcher(value).matches()) {
                value = "$value?$BD_URL_PARAMS"
            }
            System.setProperty(prop.systemProperty, value)
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
        PORT('port', 'bq.jetty.connectors[0].port'),
        HOST('host', 'bq.jetty.connectors[0].host'),
        DB_URL('db_url', 'bq.jdbc.willow.url'),
        DB_USER('db_user', 'bq.jdbc.willow.username'),
        DB_PASS('db_pass', 'bq.jdbc.willow.password'),
        PATH('path', 'bq.jetty.context'),
        ZK_HOST('zk_host', 'zk.host.property'),
        LOGS_PATH('logs_path', 'logs.path'),
        SMTP('smtp', 'mail.smtp.host'),
        USI_LOCATION('usi_location', 'usi.location')

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


    /**
     * extra property for willow admin/billing apps
     */
    static enum AdminProperty implements IProperty {
        S_ROOT('editor_files', 's.root'),
        STORAGE_ACCESS_ID('storage_access_id', 'storage.access.id'),
        STORAGE_ACCESS_KEY('storage_access_key', 'storage.access.key'),
        DEPLOY_SCRIPT_PATH('editor_deploy', 'editor.script.deploy'),

        SVN_URL('svn_url', 'svn.url'),
        SVN_USER('svn_user', 'svn.user'),
        SVN_PASS('svn_pass', 'svn.pass')
        
        private String key
        private String systemProperty

        private AdminProperty(String key, String systemProperty) {
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
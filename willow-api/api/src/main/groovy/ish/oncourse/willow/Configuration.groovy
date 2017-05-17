package ish.oncourse.willow

class Configuration {

    static final String CONFIG_FILE_NAME = 'application.properties'
    static final String VERSION_FILE_NAME = 'VERSION'
    static final String BD_URL = 'jdbc:mysql://%s:%s/%s?autoReconnect=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf8'
    static final String API_VERSION = 'ish.api.version'
    static final String ZK_HOST_PROPERTY = 'zk.host.property'


    static configure() {
        
        System.out.println(System.getProperties().get('user.dir'))
        String userDir = System.getProperties().get('user.dir') as String

        File propFile = new File(userDir+'/'+ CONFIG_FILE_NAME)
        File versionFile = new File(userDir +'/'+ VERSION_FILE_NAME)
        if (propFile.exists()) {
            Properties prop = new Properties()
            prop.load(new FileInputStream(propFile))
            System.setProperty('bq.jetty.connector.port', prop.get('port') as String)
            System.setProperty('bq.jetty.connector.host', prop.get('host') as String)
            System.setProperty('bq.jdbc.ish.url', String.format(BD_URL, prop.get('db_host'), prop.get('db_port'), prop.get('db_name')))
            System.setProperty('bq.jdbc.ish.username', prop.get('db_user') as String)
            System.setProperty('bq.jdbc.ish.password', prop.get('db_pass') as String)
            System.setProperty(ZK_HOST_PROPERTY, prop.get('zk_host') as String)
            
            if (prop.get('path')) {
                System.setProperty('bq.cxf.urlPattern',prop.get('path') as String)
            }



            if (versionFile.exists()) {
                System.setProperty(API_VERSION, versionFile.newReader().readLine())
            }
            
        } else {
            throw new IllegalArgumentException("application.properties file not found")
        }

    }
}
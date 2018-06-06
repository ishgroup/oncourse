package ish.oncourse.test

import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.mariadb.jdbc.Driver

import static java.lang.System.getProperty

/**
 * User: akoiro
 * Date: 30/10/17
 */
class MariaDB {
    private static final Logger logger = LogManager.getLogger();

    String url
    String user
    String password
    String dbName
    String driver = 'org.mariadb.jdbc.Driver'
    Class driverClass = Driver

    static MariaDB valueOf(String url, String user, String password) {
        MariaDB result = new MariaDB()
        result.url = url
        result.user = user
        result.password = password
        String[] parts = StringUtils.split(url, "?")
        String[] urlParts = StringUtils.split(parts[0], "/")
        result.dbName = urlParts[2]
        return result
    }

    static MariaDB valueOf(String schema) {
        String url = "jdbc:mariadb://localhost:3306/$schema?autoReconnect=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf8"
        String user = "root"
        String password = "whatsup"
        return valueOf(url, user, password)
    }

    static MariaDB valueOf() {
        String url = getProperty('oncourse.jdbc.url')
        String user = getProperty('oncourse.jdbc.user')
        String password = getProperty('oncourse.jdbc.password')

        logger.warn("mariadb parameters: URL: ${url};\n" +
                "USER: ${user};\n" +
                "PASSWORD: ${password}")

        return valueOf(url,
                user,
                password)
    }

}

package ish.oncourse.test

import org.apache.commons.lang3.StringUtils
import org.mariadb.jdbc.Driver

/**
 * User: akoiro
 * Date: 30/10/17
 */
class MariaDB {

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

    static MariaDB valueOf() {
        return valueOf(System.getProperty("oncourse.jdbc.url"),
                System.getProperty("oncourse.jdbc.user"),
                System.getProperty("oncourse.jdbc.password"))
    }

}

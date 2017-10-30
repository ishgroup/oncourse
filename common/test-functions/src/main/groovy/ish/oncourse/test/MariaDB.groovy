package ish.oncourse.test

import org.apache.commons.lang3.StringUtils

/**
 * User: akoiro
 * Date: 30/10/17
 */
class MariaDB {

    String url
    String user
    String password
    String dbName
    String  driver = 'org.gjt.mm.mysql.Driver'

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

}

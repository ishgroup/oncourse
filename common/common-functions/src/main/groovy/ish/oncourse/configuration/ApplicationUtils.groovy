package ish.oncourse.configuration

import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class ApplicationUtils {
    private static final Logger logger = LogManager.getLogger()

    static final String VERSION_DEVELOPMENT = 'DEVELOPMENT'
    static final String VERSION_FILE_NAME = 'VERSION'

    static String getAppVersion() {
        String userDir = System.getProperties().get('user.dir') as String
        File versionFile = new File("${userDir}/${VERSION_FILE_NAME}")
        if (versionFile.exists()) {
            Reader reader = null
            try {
                reader = versionFile.newReader()
                return reader.readLine()
            } catch (Throwable e) {
                logger.error(e)
                return VERSION_DEVELOPMENT
            } finally {
                IOUtils.closeQuietly(reader)
            }
        } else {
            return VERSION_DEVELOPMENT
        }

    }
}

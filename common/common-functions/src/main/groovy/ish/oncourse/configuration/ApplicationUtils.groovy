package ish.oncourse.configuration

class ApplicationUtils {
    static final String VERSION_DEVELOPMENT = 'DEVELOPMENT'
    static final String VERSION_FILE_NAME = 'VERSION'
    
    static String getAppVersion() {
        String userDir = System.getProperties().get('user.dir') as String
        File versionFile = new File( "${userDir}/${VERSION_FILE_NAME}")

        if (versionFile.exists()) {
           return versionFile.newReader().readLine()
        } else {
            return VERSION_DEVELOPMENT
        }
    }
}

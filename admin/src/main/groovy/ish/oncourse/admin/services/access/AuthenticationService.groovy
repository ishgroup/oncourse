package ish.oncourse.admin.services.access

import ish.oncourse.services.authentication.AuthenticationResult
import ish.oncourse.services.authentication.AuthenticationStatus
import ish.oncourse.services.authentication.IAuthenticationService

class AuthenticationService implements IAuthenticationService {

    static final String PROP_FILE_NAME = 'application.properties'
    static final String USER_DIR_PROP = 'user.dir'
    static final String ALLOWED_USERS_ROLES_PROP = 'users'

    private Map<String, String> rolesForUsers = new HashMap<>()
    private Map<String, String> passwordsForUsers = new HashMap<>()

    AuthenticationService() {
        String propertyFilePath = "${System.getProperty(USER_DIR_PROP)}/${PROP_FILE_NAME}"

        Properties props = new Properties()
        props.load(new FileInputStream(propertyFilePath))
        String propertyLine = props.getProperty(ALLOWED_USERS_ROLES_PROP)
        propertyLine.split(",").each { userCredentials ->
            String[] creds = userCredentials.split(":")
            rolesForUsers.put(creds[0], creds[2])
            passwordsForUsers.put(creds[0], creds[1])
        }
    }

    AuthenticationResult authenticate(String userName, String password, boolean persist) {
        AuthenticationStatus status = AuthenticationStatus.UNAUTHORIZED

        String availablePassword = passwordsForUsers.get(userName)
        if (availablePassword) {
            if (availablePassword == password) {
                status = AuthenticationStatus.SUCCESS
            } else {
                status = AuthenticationStatus.INVALID_CREDENTIALS
            }
        } else {
            status = AuthenticationStatus.NO_MATCHING_USER
        }
        AuthenticationResult.valueOf(status, userName, password)
    }

    boolean authorise(String pathResource) {
        return true
    }

    void logout() {
    }
}

package ish.oncourse.admin.services.access

import ish.oncourse.services.authentication.AuthenticationResult
import ish.oncourse.services.authentication.AuthenticationStatus
import ish.oncourse.services.authentication.IAuthenticationService
import ish.oncourse.configuration.Configuration
import static ish.oncourse.configuration.Configuration.USER_DIR
import static ish.oncourse.configuration.Configuration.CONFIG_FILE_NAME

class AuthenticationService implements IAuthenticationService {

    static final String USERS = 'users'
    static final String ROOT_ROLE = 'root'
    static final String DELETE_PATH = '/college/web.deletesite/'

    private Map<String, String> roles = new HashMap<>()
    private Map<String, String> passwords = new HashMap<>()

    AuthenticationService() {
        String propertyPath = "${System.getProperty(USER_DIR)}/${CONFIG_FILE_NAME}"

        String propertyLine = Configuration.loadPropertyFile(propertyPath).getProperty(USERS)
        propertyLine.split(",").each { userCredentials ->
            String[] creds = userCredentials.split(":")
            roles.put(creds[0], creds[2])
            passwords.put(creds[0], creds[1])
        }
    }

    AuthenticationResult authenticate(String userName, String password, boolean persist) {
        AuthenticationStatus status = AuthenticationStatus.UNAUTHORIZED

        String availablePassword = passwords.get(userName)
        if (availablePassword) {
            if (availablePassword.equals(password)) {
                status = AuthenticationStatus.SUCCESS
            } else {
                status = AuthenticationStatus.INVALID_CREDENTIALS
            }
        } else {
            status = AuthenticationStatus.NO_MATCHING_USER
        }
        AuthenticationResult.valueOf(status, userName, password)
    }

    boolean authorise(String userName, String contextPath, String path) {
        if (path.startsWith("${contextPath}${DELETE_PATH}")) {
            if (ROOT_ROLE.equals(roles.get(userName))) {
                return true
            } else {
                return false
            }
        }
        return true
    }

    void logout() {
    }
}

package ish.oncourse.services.authentication

class AuthenticationResult {
    private AuthenticationStatus status
    private String firstName
    private String lastName
    private String sessionToken
    

    void setStatus(AuthenticationStatus status) {
        this.status = status
    }

    void setFirstName(String firstName) {
        this.firstName = firstName
    }

    void setLastName(String lastName) {
        this.lastName = lastName
    }

    void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken
    }

    AuthenticationStatus getStatus() {
        return status
    }

    String getFirstName() {
        return firstName
    }

    String getLastName() {
        return lastName
    }
    
    String getSessionToken() {
        return sessionToken
    }
    
    static AuthenticationResult valueOf(AuthenticationStatus status, String firstName = null, String lastName = null, String sessionToken = null) {
        AuthenticationResult result = new AuthenticationResult()
        result.status = status
        result.firstName = firstName
        result.lastName = lastName
        result.sessionToken = sessionToken
        result
    }
}

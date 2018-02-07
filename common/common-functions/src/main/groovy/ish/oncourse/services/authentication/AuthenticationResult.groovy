package ish.oncourse.services.authentication

class AuthenticationResult {
    private AuthenticationStatus status
    private String firstName
    private String lastName

    void setStatus(AuthenticationStatus status) {
        this.status = status
    }

    void setFirstName(String firstName) {
        this.firstName = firstName
    }

    void setLastName(String lastName) {
        this.lastName = lastName
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

    static AuthenticationResult valueOf(AuthenticationStatus status, String firstName = null, String lastName = null) {
        AuthenticationResult result = new AuthenticationResult()
        result.status = status
        result.firstName = firstName
        result.lastName = lastName
        result
    }
}

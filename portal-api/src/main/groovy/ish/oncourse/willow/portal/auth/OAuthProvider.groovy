package ish.oncourse.willow.portal.auth

import ish.oncourse.configuration.Configuration

abstract class OAuthProvider {
    
    protected abstract String getSecretFileName()
    abstract String getClientId()
    abstract SSOCredantials authorize(String activationCode, String redirectUrl, String codeVerifier)


        protected FileInputStream readSecret() {
        String userDir = System.getProperties().get(Configuration.USER_DIR) as String
        try {
            return new FileInputStream("$userDir/$secretFileName")
        } catch (Exception ex) {
            throw new IllegalArgumentException("Exception during reading application.properties file", ex)
        }
    }
}

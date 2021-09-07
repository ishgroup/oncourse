package ish.oncourse.willow.portal.auth

import ish.oncourse.configuration.Configuration
import ish.oncourse.willow.portal.v1.model.ClientId
import ish.oncourse.willow.portal.v1.model.Platform
import ish.oncourse.willow.portal.v1.model.SSOproviders

abstract class OAuthProvider {

    abstract SSOCredantials authorize(String activationCode, String redirectUrl, String codeVerifier)
    abstract List<ClientId> getClientIds()
    
    protected FileInputStream readSecret(SSOproviders provider, Platform platform) {
        String userDir = System.getProperties().get(Configuration.USER_DIR) as String
        try {
            return new FileInputStream("${userDir}/${provider}_${platform}.json".toLowerCase())
        } catch (Exception ex) {
            throw new IllegalArgumentException("Exception during reading application.properties file", ex)
        }
    }
}

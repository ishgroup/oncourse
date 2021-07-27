package ish.oncourse.willow.portal.auth

import ish.common.types.SSOProviderType

class SSOCredantials {
    SSOProviderType providerType
    String email 
    String profilePicture 
    String accessToken 
    String refreshToken 
}

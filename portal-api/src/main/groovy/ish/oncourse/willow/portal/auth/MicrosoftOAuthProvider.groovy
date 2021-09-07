package ish.oncourse.willow.portal.auth

import com.google.inject.Inject
import com.microsoft.aad.msal4j.AuthenticationResult
import com.microsoft.aad.msal4j.AuthorizationCodeParameters
import com.microsoft.aad.msal4j.ClientCredentialFactory
import com.microsoft.aad.msal4j.ClientCredentialParameters
import com.microsoft.aad.msal4j.ConfidentialClientApplication
import com.microsoft.aad.msal4j.IAuthenticationResult
import com.microsoft.aad.msal4j.IClientSecret
import groovy.json.JsonSlurper
import groovy.transform.CompileDynamic
import ish.common.types.SSOProviderType
import ish.oncourse.willow.portal.v1.model.ClientId
import ish.oncourse.willow.portal.v1.model.SSOproviders

import java.util.concurrent.Future

import static ish.oncourse.willow.portal.v1.model.Platform.WEB
import static ish.oncourse.willow.portal.v1.model.SSOproviders.GOOGLE
import static ish.oncourse.willow.portal.v1.model.SSOproviders.MICROSOFT


class MicrosoftOAuthProvider extends OAuthProvider {
    
    static Set<String> scopes = ['openid',
    'profile',
    'email',
    'offline_access',
    'Files.Read',
    'Files.Read.All',
    'Files.ReadWrite',
    'Files.ReadWrite.All',
    'Files.ReadWrite.AppFolder',
    'Files.Read.Selected',
    'Files.ReadWrite.Selected',
    'Calendars.ReadWrite'].toSet()
    
    private String clientId

    ConfidentialClientApplication app
    @Inject
    MicrosoftOAuthProvider() {
        
        FileInputStream inputStream = readSecret(MICROSOFT, WEB)
        Map<String, Object> secretJson = new JsonSlurper().parse(inputStream) as Map<String, Object>
        clientId = secretJson['web']['client_id'] as String
        IClientSecret secret = ClientCredentialFactory.createFromSecret(secretJson['web']['client_secret'] as String)
        app = ConfidentialClientApplication.builder(clientId, secret).build()
        
    }

    @Override
    List<ClientId> getClientIds() {
        return [new ClientId(ssOProvider: MICROSOFT, platform: WEB, clientId: clientId)]
    }

    @Override
    @CompileDynamic
    SSOCredantials authorize(String activationCode, String redirectUrl, String codeVerifier) {
        AuthorizationCodeParameters parameters = AuthorizationCodeParameters.builder(
                activationCode,
                new URI(redirectUrl))
                .scopes(scopes)
                .codeVerifier(codeVerifier)
                .build()

        IAuthenticationResult result = app.acquireToken(parameters).get() 
        SSOCredantials credantials = new SSOCredantials()
        credantials.accessToken= result.accessToken()
        credantials.accessToken= result.refreshToken()
        credantials.email= result.account().username()
        credantials.userId= result.account().homeAccountId()

        credantials.providerType = SSOProviderType.MICROSOFT

        return credantials
    }
    
}

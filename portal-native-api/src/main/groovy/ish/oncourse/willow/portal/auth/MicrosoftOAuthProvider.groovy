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

import java.util.concurrent.Future


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


        FileInputStream inputStream = readSecret()
        Map<String, Object> secretJson = new JsonSlurper().parse(inputStream) as Map<String, Object>
        clientId = secretJson['web']['client_id'] as String
        IClientSecret secret = ClientCredentialFactory.createFromSecret(secretJson['web']['client_secret'] as String)
        app = ConfidentialClientApplication.builder(clientId, secret).build()
        
    }

    @Override
    String getSecretFileName() {
        return "microsoft_secret.json"
    }
    @Override
    String getClientId() {
        return clientId
    }

    @Override
    @CompileDynamic
    SSOCredantials authorize(String activationCode, String redirectUrl) {
        AuthorizationCodeParameters parameters = AuthorizationCodeParameters.builder(
                activationCode,
                new URI(redirectUrl))
                .scopes(scopes)
                .build()

        IAuthenticationResult result = app.acquireToken(parameters).get() 
        SSOCredantials credantials = new SSOCredantials()
        credantials.accessToken= result.accessToken()
        credantials.accessToken= result.refreshToken()
        credantials.email= result.account().username()
        credantials.providerType = SSOProviderType.MICROSOFT

        return credantials
    }
    
}

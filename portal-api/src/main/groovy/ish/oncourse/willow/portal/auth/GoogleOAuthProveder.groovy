package ish.oncourse.willow.portal.auth

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.drive.DriveScopes
import com.google.api.services.oauth2.Oauth2Scopes
import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.common.types.SSOProviderType
import ish.oncourse.configuration.Configuration

@CompileStatic
class GoogleOAuthProveder extends OAuthProvider {
    private static final JsonFactory jsonFactory = GsonFactory.defaultInstance
    private static final HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    
    private GoogleAuthorizationCodeFlow flow
    private GoogleClientSecrets clientSecrets
    
    
    @Inject
    GoogleOAuthProveder() {

        clientSecrets = GoogleClientSecrets.load(jsonFactory,
                new InputStreamReader(readSecret()))
        flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport , jsonFactory, clientSecrets, [Oauth2Scopes.OPENID, Oauth2Scopes.USERINFO_PROFILE,Oauth2Scopes.USERINFO_EMAIL, CalendarScopes.CALENDAR_EVENTS, DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_METADATA])
                .setAccessType("offline")
                .build()
    }

    SSOCredantials authorize(String activationCode, String redirectUrl, String codeVerifier) {
        //
        GoogleTokenResponse resp = flow.newTokenRequest(activationCode)
                .setGrantType('authorization_code')
                .set('code_verifier', codeVerifier)
                .setRedirectUri(redirectUrl).execute()
        GoogleIdToken token =  resp.parseIdToken()
        SSOCredantials credantials = new SSOCredantials()
        credantials.providerType = SSOProviderType.GOOGLE
        credantials.email = token.payload.getEmail()
        credantials.userId = token.payload.getSubject()
        credantials.profilePicture = token.payload.get('picture')
        credantials.accessToken = resp.getAccessToken()
        credantials.refreshToken = resp.getRefreshToken()

        return credantials
    }

    String getWebClientId() {
        clientSecrets.getWeb().getClientId()
    }

    @Override
    String getAndroidClientId() {
        return clientSecrets.getInstalled().getClientId()
    }

    @Override
    String getSecretFileName() {
        return "google_secret.json"
    }
}

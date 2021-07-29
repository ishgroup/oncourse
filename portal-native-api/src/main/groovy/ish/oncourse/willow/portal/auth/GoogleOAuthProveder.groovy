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
class GoogleOAuthProveder {
    private static final JsonFactory jsonFactory = GsonFactory.defaultInstance
    private static final HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    
    private GoogleAuthorizationCodeFlow flow
    private GoogleClientSecrets clientSecrets
    
    
    
    @Inject
    GoogleOAuthProveder() {
        String userDir = System.getProperties().get(Configuration.USER_DIR) as String

        FileInputStream stream
        try {
            stream = new FileInputStream("$userDir/client_secret.json")
          } catch (Exception ex) {
            throw new IllegalArgumentException("Exception during reading application.properties file", ex)
        } 

        clientSecrets = GoogleClientSecrets.load(jsonFactory,
                new InputStreamReader(stream))
        flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport , jsonFactory, clientSecrets, [Oauth2Scopes.OPENID, Oauth2Scopes.USERINFO_PROFILE,Oauth2Scopes.USERINFO_EMAIL, CalendarScopes.CALENDAR_EVENTS, DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_METADATA])
                .setAccessType("offline")
                .build()
    }

    SSOCredantials authorize(String activationCode, String redirectUrl) {
        //
        GoogleTokenResponse resp = flow.newTokenRequest(activationCode)
                .setGrantType('authorization_code')
                .setRedirectUri(redirectUrl).execute()
        GoogleIdToken token =  resp.parseIdToken()
        SSOCredantials credantials = new SSOCredantials()
        credantials.providerType = SSOProviderType.GOOGLE
        credantials.email = token.payload.getEmail()
        credantials.profilePicture = token.payload.get('picture')
        credantials.accessToken = resp.getAccessToken()
        credantials.refreshToken = resp.getRefreshToken()

        return credantials
    }

    String getClientId() {
        clientSecrets.getWeb().getClientId()
    }
    
}

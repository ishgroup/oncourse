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
import ish.oncourse.willow.portal.v1.model.ClientId

import static ish.oncourse.willow.portal.v1.model.Platform.ANDROID
import static ish.oncourse.willow.portal.v1.model.Platform.WEB
import static ish.oncourse.willow.portal.v1.model.SSOproviders.FACEBOOK
import static ish.oncourse.willow.portal.v1.model.SSOproviders.GOOGLE

@CompileStatic
class GoogleOAuthProveder extends OAuthProvider {
    private static final JsonFactory jsonFactory = GsonFactory.defaultInstance
    private static final HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    
    private GoogleAuthorizationCodeFlow webFlow
    private GoogleClientSecrets webSecrets
    private GoogleAuthorizationCodeFlow androidFlow
    private GoogleClientSecrets androidSecret
    
    
    @Inject
    GoogleOAuthProveder() {

        webSecrets = GoogleClientSecrets.load(jsonFactory,
                new InputStreamReader(readSecret(GOOGLE, WEB)))
        webFlow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport , jsonFactory, webSecrets, [Oauth2Scopes.OPENID, Oauth2Scopes.USERINFO_PROFILE,Oauth2Scopes.USERINFO_EMAIL, CalendarScopes.CALENDAR_EVENTS, DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_METADATA])
                .setAccessType("offline")
                .build()
        androidSecret = GoogleClientSecrets.load(jsonFactory,
                new InputStreamReader(readSecret(GOOGLE, ANDROID)))
        androidFlow =  new GoogleAuthorizationCodeFlow.Builder(
                httpTransport , jsonFactory, androidSecret, [Oauth2Scopes.OPENID, Oauth2Scopes.USERINFO_PROFILE,Oauth2Scopes.USERINFO_EMAIL, CalendarScopes.CALENDAR_EVENTS, DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_METADATA])
                .setAccessType("offline")
                .build()
    }

 
    SSOCredantials authorize(String activationCode, String codeVerifier) {
        GoogleAuthorizationCodeFlow codeFlow
        //need to work out the criteria from which place we get auth request: browser or android app or ios app
        //android redirect URL is ish.oncourse.willow.portal:/oauthredirect
        codeFlow = webFlow
        String redirect = webSecrets.getWeb().getRedirectUris()[0]

        GoogleTokenResponse resp = codeFlow.newTokenRequest(activationCode)
                .setGrantType('authorization_code')
                .set('code_verifier', codeVerifier)
                .setRedirectUri(redirect).execute()
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

    @Override
    List<ClientId> getClientIds() {
        return [new ClientId(ssOProvider: GOOGLE, platform: WEB, clientId: webSecrets.getWeb().getClientId()),
                new ClientId(ssOProvider: GOOGLE, platform: ANDROID, clientId: androidSecret.getInstalled().getClientId())]
    }
}

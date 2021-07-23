package ish.oncourse.willow.portal.auth

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
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
import ish.oncourse.services.persistence.ICayenneService

@CompileStatic
class GoogleOAuthProveder {
    private static final JsonFactory jsonFactory = GsonFactory.defaultInstance
    private static final HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    
    private GoogleAuthorizationCodeFlow flow
    private GoogleClientSecrets clientSecrets

    private ICayenneService cayenneService
    
    
    @Inject
    GoogleOAuthProveder(ICayenneService cayenneService) {
        this.cayenneService = cayenneService

        clientSecrets = GoogleClientSecrets.load(jsonFactory,
                new InputStreamReader(this.class.getResourceAsStream('client_secrets.json')))
        flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport , jsonFactory, clientSecrets, [Oauth2Scopes.OPENID, Oauth2Scopes.USERINFO_PROFILE,Oauth2Scopes.USERINFO_EMAIL, CalendarScopes.CALENDAR_EVENTS, DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_METADATA])
                .setAccessType("offline")
                .build()
    }

    GoogleTokenResponse authorize(String activationCode) {
        GoogleTokenResponse resp = flow.newTokenRequest(activationCode).execute()
    }

    
}

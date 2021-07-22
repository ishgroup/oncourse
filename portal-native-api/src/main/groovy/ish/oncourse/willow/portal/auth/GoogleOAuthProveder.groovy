package ish.oncourse.willow.portal.auth

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.drive.DriveScopes
import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.services.persistence.ICayenneService

@CompileStatic
class GoogleOAuthProveder {

    private GoogleAuthorizationCodeFlow flow

    
    
    @Inject
    GoogleOAuthProveder(ICayenneService cayenneService) {
        // load client secrets
        JsonFactory jsonFactory = GsonFactory.defaultInstance
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
                new InputStreamReader(this.class.getResourceAsStream('client_secrets.json')))
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, clientSecrets, [CalendarScopes.CALENDAR_EVENTS, DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_METADATA])
                .setAccessType("offline")
                .build()
    }

    GoogleTokenResponse authorize(String activationCode) {
        GoogleTokenResponse resp = flow.newTokenRequest(activationCode).execute()
    }
    
}

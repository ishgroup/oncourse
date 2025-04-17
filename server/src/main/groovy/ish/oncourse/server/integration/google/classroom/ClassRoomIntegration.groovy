/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.integration.google.classroom

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.classroom.ClassroomScopes
import ish.common.types.IntegrationType
import ish.oncourse.server.api.v1.model.ValidationErrorDTO
import ish.oncourse.server.cayenne.IntegrationConfiguration
import ish.oncourse.server.integration.OnSave
import ish.oncourse.server.integration.Plugin
import ish.oncourse.server.integration.PluginTrait
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

@Plugin(type = IntegrationType.GOOGLE_CLASSROOM)
class ClassRoomIntegration implements PluginTrait {

    public static final String CLASSROOM_CLIENT_ID = "clientId"
    public static final String CLASSROOM_CLIENT_SECRET = "clientSecret"
    public static final String CLASSROOM_ACTIVATION_CODE = "activationCode"
    public static final String CLASSROOM_REDIRECT_URI= "redirectUri"

    public static final String CLASSROOM_ACCESS_TOKEN = "access_token"
    public static final String CLASSROOM_REFRESH_TOKEN = "refresh_token"

    String clientId
    String clientSecret

    String access_token
    String refresh_token

    private static Logger logger = LogManager.logger

    ClassRoomIntegration(Map args) {
        loadConfig(args)

        this.clientId = configuration.getIntegrationProperty(CLASSROOM_CLIENT_ID).value
        this.clientId = configuration.getIntegrationProperty(CLASSROOM_CLIENT_SECRET).value
    }

    void authenticate(String activationCode, String origin) {
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.defaultInstance, clientId, clientSecret, ClassroomScopes.all())
                .setAccessType("offline")
                .build()
        GoogleTokenResponse resp = flow.newTokenRequest(activationCode).setRedirectUri(origin).execute()
        this.access_token = resp.getAccessToken()
        this.refresh_token = resp.getRefreshToken()
    }

    @OnSave
    static void onSave(IntegrationConfiguration configuration, Map<String, String> props) {
        String activationCode = props[CLASSROOM_ACTIVATION_CODE]
        String clientId = props[CLASSROOM_CLIENT_ID]
        String clientSecret = props[CLASSROOM_CLIENT_SECRET]
        String redirectUri = props[CLASSROOM_REDIRECT_URI]
        configuration.setProperty(CLASSROOM_CLIENT_ID, clientId)
        configuration.setProperty(CLASSROOM_CLIENT_SECRET, clientSecret)

        ClassRoomIntegration classRoom = new ClassRoomIntegration(configuration: configuration)
        try {
            classRoom.authenticate(activationCode, redirectUri)
        } catch (Exception e) {
            logger.error("Can not authentificate google class room, $activationCode, $clientId, $clientSecret")
            logger.catching(e)
            throw new ClientErrorException(Response.status(Response.Status.BAD_REQUEST).entity(new ValidationErrorDTO(errorMessage: 'Can not authenticate Googel ClassRoom.')).build())
        }
        configuration.setProperty(CLASSROOM_ACCESS_TOKEN, classRoom.access_token)
        configuration.setProperty(CLASSROOM_REFRESH_TOKEN, classRoom.refresh_token)
    }

}

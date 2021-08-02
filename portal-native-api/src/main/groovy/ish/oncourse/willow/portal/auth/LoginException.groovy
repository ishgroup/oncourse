package ish.oncourse.willow.portal.auth

import ish.oncourse.willow.portal.v1.model.ErrorResponse

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

class LoginException extends BadRequestException {

    LoginException(String message) {
        super(Response.status(400).entity(new ErrorResponse(message: message)).build())
    }
}

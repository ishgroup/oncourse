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

package ish.oncourse.server.api.validation

import ish.oncourse.server.api.v1.model.ValidationErrorDTO

import javax.ws.rs.ClientErrorException
import javax.ws.rs.ForbiddenException
import javax.ws.rs.core.Response

class EntityValidator {

    static void validateLength(Long id, String value, String attribute, int maxLength) {
        if (value != null && value.length() > maxLength ) {
            throwClientErrorException(id, attribute, "The maximum length for this field is $maxLength.")
        }
    }

    static void validateBoolean(String value, String attribute) {
        if (value != Boolean.TRUE.toString() && value != Boolean.FALSE.toString()) {
            throwClientErrorException(attribute, "Wrong attribute vale: $value")
        }
    }

    static void throwClientErrorException(String propertyName, String errorMessage) {
        ValidationErrorDTO error = new ValidationErrorDTO(null, propertyName, errorMessage)
        throwClientErrorException(error)
    }

    static void throwClientErrorException(Object id, String propertyName, String errorMessage) {
        ValidationErrorDTO error = new ValidationErrorDTO(id?.toString(), propertyName, errorMessage)
        throwClientErrorException(error)
    }

    static void throwClientErrorException(ValidationErrorDTO validationError) {
        Response response = Response
                .status(Response.Status.BAD_REQUEST)
                .entity(validationError)
                .build()

        throw new ClientErrorException(response)
    }

    static void throwForbiddenErrorException(Object id, String propertyName, String errorMessage) {
        ValidationErrorDTO error = new ValidationErrorDTO(id?.toString(), propertyName, errorMessage)
        throwForbiddenErrorException(error)
    }

    static void throwForbiddenErrorException(ValidationErrorDTO validationError) {
        Response response = Response
                .status(Response.Status.FORBIDDEN)
                .entity(validationError)
                .build()

        throw new ForbiddenException(response)
    }
}

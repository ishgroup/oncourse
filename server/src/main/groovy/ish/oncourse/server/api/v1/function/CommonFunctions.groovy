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

package ish.oncourse.server.api.v1.function

import ish.oncourse.server.api.v1.model.ValidationErrorDTO

import javax.ws.rs.ClientErrorException
import javax.ws.rs.core.Response

class CommonFunctions {

    static ClientErrorException badRequest(String errorMessage) {
        new ClientErrorException(Response.status(Response.Status.BAD_REQUEST)
                .entity(new ValidationErrorDTO(null, null, errorMessage)).build())
    }
}

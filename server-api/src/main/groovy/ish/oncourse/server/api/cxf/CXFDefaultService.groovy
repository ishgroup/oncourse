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

package ish.oncourse.server.api.cxf

import javax.inject.Inject

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

class CXFDefaultService {

    private CXFModuleConfig config

    @Inject
    CXFDefaultService(CXFModuleConfig config) {
        this.config = config
    }

    @GET()
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    String get() {
        return config.welcomeText
    }
}

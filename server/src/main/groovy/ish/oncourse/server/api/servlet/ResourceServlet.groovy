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

package ish.oncourse.server.api.servlet

import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.util.resource.Resource


class ResourceServlet extends DefaultServlet {

    private static final int MAX_AGE = 60 * 60 * 24 // day
    private static final String INDEX_HTML = '/index.html'
    private static final String PATTERN = /.*\/([^\/]+\.)+\w+(#\w+)?(\?.+)?/

    @Override
    String getInitParameter(String name) {
        switch (name) {
            case 'cacheControl':
                return "max-age=$MAX_AGE"
            case 'etags':
                return true
            default:
                return super.getInitParameter(name)
        }
    }

    @Override
    Resource getResource(String pathInContext) {
        super.getResource(pathInContext ==~ PATTERN ? pathInContext : INDEX_HTML)
    }
}

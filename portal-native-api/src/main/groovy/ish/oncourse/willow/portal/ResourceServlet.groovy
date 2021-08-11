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

package ish.oncourse.willow.portal

import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.util.resource.Resource


class ResourceServlet extends DefaultServlet {

    private static final String INDEX_HTML = '/index.html'
    private static final String PATTERN = /.*\/([^\/]+\.)+\w+(#\w+)?(\?.+)?/


    /**
     * Return index page in all cases except contex path is file name
     * / -> index.html
     * /classRoll -> index.html
     * /session/200 -> index.html
     * /sessions.js -> sessions.js
     * /font.ttf -> font.ttf 
     * @param pathInContext
     * @return
     */
    @Override
    Resource getResource(String pathInContext) {
        super.getResource(pathInContext ==~ PATTERN ? pathInContext : INDEX_HTML)
    }
}

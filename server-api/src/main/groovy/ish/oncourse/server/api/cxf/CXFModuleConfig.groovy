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

import io.bootique.annotation.BQConfig
import io.bootique.annotation.BQConfigProperty

@BQConfig("Configures the servlet that is an entry point to CXF REST API engine.")
class CXFModuleConfig {

    private String urlPattern
    private String welcomeText

    @BQConfigProperty
    void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern
    }

    String getUrlPattern() {
        return this.urlPattern ? this.urlPattern : '/*'
    }

    @BQConfigProperty
    void setWelcomeText(String welcomeText) {
        this.welcomeText = welcomeText
    }


    String getWelcomeText() {
        return this.welcomeText ? this.welcomeText : 'CXF REST API Module'
    }
}

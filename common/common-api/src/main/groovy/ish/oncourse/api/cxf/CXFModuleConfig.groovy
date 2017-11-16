package ish.oncourse.api.cxf

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

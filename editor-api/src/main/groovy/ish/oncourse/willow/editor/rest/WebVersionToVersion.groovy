package ish.oncourse.willow.editor.rest

import ish.oncourse.model.WebSiteVersion
import ish.oncourse.willow.editor.v1.model.Version

import java.time.ZoneOffset

class WebVersionToVersion {

    private WebSiteVersion webSiteVersion

    private WebVersionToVersion(){}

    static WebVersionToVersion valueOf(WebSiteVersion webSiteVersion) {
        WebVersionToVersion serializer = new WebVersionToVersion()
        serializer.webSiteVersion = webSiteVersion
        serializer
    }

    Version getVersion() {
        return new Version(webSiteVersion.siteVersion.intValue(),
                (webSiteVersion.deployedBy ?  "$webSiteVersion.deployedBy.firstName $webSiteVersion.deployedBy.surname".toString() : null),
                null,
                webSiteVersion.deployedOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime(),
                null)
    }
    
}

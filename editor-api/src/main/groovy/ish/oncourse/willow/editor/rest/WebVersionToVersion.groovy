package ish.oncourse.willow.editor.rest

import ish.oncourse.model.WebSiteVersion
import ish.oncourse.willow.editor.model.Version

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
        new Version().with { version ->
            version.id = webSiteVersion.id.intValue()
            version.published = webSiteVersion.deployedOn != null
            version.author = webSiteVersion.deployedBy ?  "$webSiteVersion.deployedBy.firstName $webSiteVersion.deployedBy.surname" : null
            version.datetime = webSiteVersion.deployedOn?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
            version
        }
    }
    
}

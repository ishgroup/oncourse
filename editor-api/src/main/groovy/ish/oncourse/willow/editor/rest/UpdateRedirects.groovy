package ish.oncourse.willow.editor.rest

import ish.oncourse.linktransform.URLPath
import ish.oncourse.model.WebSiteVersion
import ish.oncourse.model.WebUrlAlias
import ish.oncourse.util.ISHUrlValidator
import ish.oncourse.willow.editor.v1.model.RedirectItem
import ish.oncourse.willow.editor.v1.model.Redirects
import ish.oncourse.willow.editor.website.WebSiteVersionFunctions
import ish.oncourse.willow.editor.website.WebUrlAliasFunctions
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ObjectId
import org.eclipse.jetty.server.Request

class UpdateRedirects extends AbstractUpdate<Redirects> {

  
    private List<String> errors = []
    private ArrayList<ObjectId> deletedAliasIds = new ArrayList<ObjectId>()
    private WebSiteVersion version
    
    List<String> getErrors() {
        return errors
    }
    
    private UpdateRedirects() {}
    
    static UpdateRedirects valueOf(Redirects redirects, ObjectContext context, Request request) {
        UpdateRedirects updater = new UpdateRedirects()
        updater.init(redirects, context, request)
        updater.version = WebSiteVersionFunctions.getCurrentVersion(request, context)
        return updater
    }


    @Override
    UpdateRedirects update() {
        
        Map<String, RedirectItem> providedUrlsMap = resourceToSave.rules
                .collect { redirect -> redirect.from(URLPath.valueOf(redirect.from).encodedPath) }
                .collectEntries { redirect -> [("${redirect.from}-${redirect.to}".toString()) : redirect]}
        

        List<WebUrlAlias> persistentAliases = WebUrlAliasFunctions.getRedirects(request, context)
        
        new ArrayList<WebUrlAlias>(persistentAliases).each { alias ->
            RedirectItem redirect  = providedUrlsMap.remove("${alias.urlPath}-${alias.redirectTo}".toString())
            if (!redirect) {
                //remove redirect if no present in save request
                deleteAlias(alias)
            }
        }
        //create new aliases (not presented in persistent objects list)
        providedUrlsMap.values().each { createAlias(it) }

        return this
    }

    private void createAlias(RedirectItem redirect) {
        if (validate(redirect)) {
            WebUrlAlias alias = context.newObject(WebUrlAlias)
            alias.webSiteVersion = version
            alias.urlPath = redirect.from
            alias.redirectTo  = redirect.to
        }
    }
    
    private void deleteAlias(WebUrlAlias alias) {
        deletedAliasIds << alias.objectId
        context.deleteObject(alias)
    }

    private boolean validate(RedirectItem redirectItem) {
        ISHUrlValidator validator = new ISHUrlValidator('http', 'https')
        if (!validator.isValidOnlyPath(redirectItem.from)) {
            redirectItem.error = "Invalid redirect, from: ${redirectItem.from}, to: ${redirectItem.to}.  The from address must be a valid path within the site starting with /"
            errors << redirectItem.error
            return false
        }
        WebUrlAlias fWebUrl = WebUrlAliasFunctions.getAliasByPath(redirectItem.from, request, context)
        if (fWebUrl != null && !(fWebUrl.objectId in deletedAliasIds)) {
            if (fWebUrl.webNode) {
                redirectItem.error = "Invalid redirect, from: ${redirectItem.from}, to: ${redirectItem.to}. To create redirects to pages within this CMS, go to that page and add an additional URL in the page options."
                errors << redirectItem.error
            } else {
                redirectItem.error = "Invalid redirect, from: ${redirectItem.from}, to: ${redirectItem.to}. This URL is already being redirected to ${fWebUrl.redirectTo}"
                errors << redirectItem.error
            }
            return false
        }
        if (!validator.isValid(redirectItem.to) && !validator.isValidOnlyPath(redirectItem.to)) {
            redirectItem.error = "Invalid redirect, from: ${redirectItem.from}, to: ${redirectItem.to}. The to address must be a valid URL or partial URL starting with /"
            errors << redirectItem.error
            return false
        }
        return true
    }
}

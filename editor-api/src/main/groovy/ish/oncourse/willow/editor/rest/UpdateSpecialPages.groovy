package ish.oncourse.willow.editor.rest

import groovy.transform.CompileStatic
import ish.oncourse.linktransform.URLPath
import ish.oncourse.model.WebSiteVersion
import ish.oncourse.model.WebUrlAlias
import ish.oncourse.specialpages.RequestMatchType
import ish.oncourse.specialpages.SpecialWebPage
import ish.oncourse.util.ISHUrlValidator
import ish.oncourse.willow.editor.utils.BidiMap
import ish.oncourse.willow.editor.v1.model.SpecialPage
import ish.oncourse.willow.editor.v1.model.SpecialPageItem
import ish.oncourse.willow.editor.v1.model.URLMatchRule
import ish.oncourse.willow.editor.website.WebSiteVersionFunctions
import ish.oncourse.willow.editor.website.WebUrlAliasFunctions
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ObjectId
import org.eclipse.jetty.server.Request

@CompileStatic
class UpdateSpecialPages extends AbstractUpdate<List<SpecialPageItem>> {


    private List<String> errors = []
    private ArrayList<ObjectId> deletedAliasIds = new ArrayList<ObjectId>()
    private WebSiteVersion version

    public static BidiMap<URLMatchRule, RequestMatchType> matchTypeRuleMapping = new BidiMap<>()
    public static BidiMap<SpecialPage, SpecialWebPage> specialPageMapping = new BidiMap<>()

    static{
        matchTypeRuleMapping.put(URLMatchRule.EXACT, RequestMatchType.EXACT)
        matchTypeRuleMapping.put(URLMatchRule.STARTS_WITH, RequestMatchType.STARTS_WITH)

        specialPageMapping.put(SpecialPage.TUTORS, SpecialWebPage.TUTORS)
    }


    List<String> getErrors() {
        return errors
    }

    private UpdateSpecialPages() {}

    static UpdateSpecialPages valueOf(List<SpecialPageItem> specialPages, ObjectContext context, Request request) {
        UpdateSpecialPages updater = new UpdateSpecialPages()
        updater.init(specialPages, context, request)
        updater.version = WebSiteVersionFunctions.getCurrentVersion(request, context)
        return updater
    }


    @Override
    UpdateSpecialPages update() {

        Map<String, SpecialPageItem> providedUrlsMap = resourceToSave
                .collect { redirect -> redirect.from(URLPath.valueOf(redirect.from).encodedPath) }
                .collectEntries { redirect -> [("${redirect.from}-${redirect.specialPage}".toString()) : redirect]}

        List<WebUrlAlias> persistentAliases = WebUrlAliasFunctions.getSpecialPages(request, context)

        new ArrayList<WebUrlAlias>(persistentAliases).each { alias ->
            SpecialPageItem specialPage  = providedUrlsMap.remove("${alias.urlPath}-${alias.specialPage}".toString())
            if (!specialPage) {
                deleteAlias(alias)
            }
        }
        //create new aliases (not presented in persistent objects list)
        providedUrlsMap.values().each { createAlias(it) }

        return this
    }

    private void createAlias(SpecialPageItem item) {
        if (validate(item)) {
            WebUrlAlias alias = context.newObject(WebUrlAlias)
            alias.webSiteVersion = version
            alias.urlPath = item.from
            alias.specialPage = specialPageMapping.get(item.specialPage)
            alias.matchType = matchTypeRuleMapping.get(item.matchType)
        }
    }

    private void deleteAlias(WebUrlAlias alias) {
        deletedAliasIds << alias.objectId
        context.deleteObject(alias)
    }

    private boolean validate(SpecialPageItem item) {
        ISHUrlValidator validator = new ISHUrlValidator('http', 'https')
        if (!validator.isValidOnlyPath(item.from)) {
            item.error = "Invalid special redirect, from: ${item.from}, special page: ${item.specialPage}, match rule: ${item.matchType}.  The from address must be a valid path within the site starting with /"
            errors << item.error
            return false
        }

        WebUrlAlias fWebUrl = WebUrlAliasFunctions.getAliasByPath(item.from, request, context)

        if (fWebUrl && !(fWebUrl.objectId in deletedAliasIds)) {
            if (fWebUrl.webNode) {
                item.error = "Invalid special redirect, from: ${item.from}, special page: ${item.specialPage}, match rule: ${item.matchType}. To create redirects to pages within this CMS, go to that page and add an additional URL in the page options."
                errors << item.error
            } else {
                item.error = "Invalid special redirect, from: ${item.from}, special page: ${item.specialPage}, match rule: ${item.matchType}. This URL is already being redirected to ${fWebUrl.redirectTo}"
                errors << item.error
            }
            return false
        }

        if (!item.specialPage) {
            item.error = "Invalid special redirect, from: ${item.from}. Special page should defined."
            errors << item.error
            return false
        }

        if (!item.matchType) {
            item.error = "Invalid special redirect, from: ${item.from}. Match rule should defined."
            errors << item.error
            return false
        }
        return true
    }
}

package ish.oncourse.linktransform;

import ish.oncourse.model.WebSite;
import ish.oncourse.model.WebSiteVersion;
import ish.oncourse.model.WebUrlAlias;
import ish.oncourse.services.site.GetDeployedVersion;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;

import static ish.oncourse.specialpages.RequestMatchType.EXACT;
import static ish.oncourse.specialpages.RequestMatchType.STARTS_WITH;

public class SpecialWebPageMatcher {

    private ObjectContext context;
    private QueryCacheStrategy strategy;
    private WebSite webSite;
    private String requestPath;

    private SpecialWebPageMatcher() {}

    public static SpecialWebPageMatcher valueOf(ObjectContext context, QueryCacheStrategy strategy, WebSite webSite, String requestPath) {
        SpecialWebPageMatcher obj = new SpecialWebPageMatcher();
        obj.context = context;
        obj.strategy = strategy;
        obj.webSite = webSite;
        obj.requestPath = requestPath;
        return obj;
    }

    public String get() {
        String templatePath = aliasByQualifier(context, strategy, webSite, WebUrlAlias.MATCH_TYPE.eq(EXACT).andExp(WebUrlAlias.URL_PATH.likeIgnoreCase(requestPath)));
        if (templatePath == null) {
            templatePath = aliasByQualifier(context, strategy, webSite, WebUrlAlias.MATCH_TYPE.eq(STARTS_WITH).andExp(WebUrlAlias.URL_PATH.startsWithIgnoreCase(requestPath)));
        }
        return templatePath;
    }

    private String aliasByQualifier(ObjectContext context, QueryCacheStrategy strategy, WebSite webSite,
                                    Expression filterExpr) {
        WebUrlAlias alias = ObjectSelect.query(WebUrlAlias.class)
                .where(siteQualifier(context, strategy, webSite).andExp(filterExpr))
                .selectFirst(context);
        return alias != null ? alias.getSpecialPage().getTemplatePath() : null;
    }

    private Expression siteQualifier(ObjectContext context, QueryCacheStrategy strategy, WebSite webSite) {
        return (webSite == null) ?
                WebUrlAlias.WEB_SITE_VERSION.dot(WebSiteVersion.WEB_SITE).dot(WebSite.COLLEGE).eq(webSite.getCollege()) :
                WebUrlAlias.WEB_SITE_VERSION.eq(GetDeployedVersion.valueOf(context, webSite, strategy != null).get());
    }
}

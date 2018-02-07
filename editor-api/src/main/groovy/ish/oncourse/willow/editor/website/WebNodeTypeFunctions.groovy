package ish.oncourse.willow.editor.website

import ish.oncourse.model.WebNodeType
import ish.oncourse.model.WebSiteVersion
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.eclipse.jetty.server.Request

class WebNodeTypeFunctions {

    static WebNodeType getDefaultWebNodeType(WebSiteVersion version) {
        return getWebNodeType(WebNodeType.NAME.eq(WebNodeType.PAGE), version, version.objectContext)
    }

    static WebNodeType getWebNodeTypeById(Long id, Request request, ObjectContext context) {
        WebSiteVersion version = WebSiteVersionFunctions.getCurrentVersion(request, context)
        return getWebNodeType(ExpressionFactory.matchDbExp(WebNodeType.ID_PK_COLUMN, id), version, context)
    }

    static WebNodeType getWebNodeTypeByName(String name, Request request, ObjectContext context) {
        WebSiteVersion version = WebSiteVersionFunctions.getCurrentVersion(request, context)
        return getWebNodeType(WebNodeType.NAME.eq(name), version, context)
    }

    static WebNodeType getWebNodeType(Expression selectQualifier, WebSiteVersion webSiteVersion, ObjectContext context) {
        return (ObjectSelect.query(WebNodeType).where(selectQualifier)
                &  WebNodeType.WEB_SITE_VERSION.eq(webSiteVersion)).
                selectOne(context)
    }
    
    static WebNodeType createNewWebNodeType(Request request, ObjectContext ctx) {
        WebSiteVersion webSiteVersion = WebSiteVersionFunctions.getCurrentVersion(request, ctx)
        WebNodeType newWebNodeType = ctx.newObject(WebNodeType)
        newWebNodeType.webSiteVersion = webSiteVersion
        newWebNodeType.name = ResourceNameUtil.getAvailableName(ResourceNameUtil.Name.THEME_NAME, ctx, WebNodeType.WEB_SITE_VERSION.eq(webSiteVersion))
        newWebNodeType.webSiteLayout = WebSiteLayoutFunctions.getDefaultLayout(webSiteVersion, ctx)
        newWebNodeType.webSiteLayout.webSiteVersion = webSiteVersion
        return newWebNodeType
    }

    static List<WebNodeType> getWebNodeTypes(Request request, ObjectContext ctx) {
        WebSiteVersion version = WebSiteVersionFunctions.getCurrentVersion(request, ctx)
        return ObjectSelect.query(WebNodeType)
                .where(WebNodeType.WEB_SITE_VERSION.eq(version))
                .orderBy(WebNodeType.MODIFIED.desc())
                .select(ctx)
    }
}

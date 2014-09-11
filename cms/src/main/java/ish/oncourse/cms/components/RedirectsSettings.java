package ish.oncourse.cms.components;

import ish.oncourse.model.WebUrlAlias;
import ish.oncourse.services.alias.IWebUrlAliasService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.util.ISHUrlValidator;
import ish.oncourse.util.URLUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.util.TextStreamResponse;

import java.util.List;

public class RedirectsSettings {


    @Inject
    private ICayenneService cayenneService;

    @Inject
    private IWebSiteVersionService webSiteVersionService;

    @Inject
    private IWebUrlAliasService webUrlAliasService;

    @Inject
    private Request request;

    @Inject
    private Response response;

    @Inject
    private Block redirectItem;


    @OnEvent(value = "newItem")
    public Block newItem() {
        return redirectItem;
    }

    @OnEvent(value = "deleteItem")
    public void deleteItem() {
        String id = request.getParameter(WebUrlAlias.ID_PK_COLUMN);
        if (StringUtils.isNumeric(id)) {
            ObjectContext context = cayenneService.newContext();
            WebUrlAlias webUrl = deserialize(context);
            context.deleteObjects(webUrl);
            context.commitChanges();
        }
    }

    @OnEvent(value = "saveItem")
    public Object saveItem() throws Exception {
        ObjectContext context = cayenneService.newContext();

        WebUrlAlias webUrl = deserialize(context);


        JSONObject result = new JSONObject();

        JSONObject validation = validate(webUrl);
        if (validation != null) {

            result.put("error", validation);
        } else {
            context.commitChanges();
            result.put("value", getJSONWebUrl(webUrl));
        }
        return new TextStreamResponse("text/json", result.toString());
    }

    @OnEvent(value = "loadItems")
    public Object loadItems() {
        ObjectContext context = cayenneService.newContext();
        JSONArray result = new JSONArray();

        Expression expression = ExpressionFactory.matchExp(WebUrlAlias.WEB_SITE_VERSION_PROPERTY, webSiteVersionService.getCurrentVersion());
        expression = expression.andExp(ExpressionFactory.matchExp(WebUrlAlias.WEB_NODE_PROPERTY, null));

        SelectQuery query = new SelectQuery(WebUrlAlias.class, expression);
        query.addOrdering(new Ordering(WebUrlAlias.MODIFIED_PROPERTY, SortOrder.DESCENDING));
        List<WebUrlAlias> aliases = context.performQuery(query);
        for (WebUrlAlias webUrlAlias : aliases) {
            result.put(getJSONWebUrl(webUrlAlias));
        }
        return new TextStreamResponse("text/json", result.toString());
    }

    private WebUrlAlias deserialize(ObjectContext context) {
        String id = request.getParameter(WebUrlAlias.ID_PK_COLUMN);
        String urlPath = request.getParameter(WebUrlAlias.URL_PATH_PROPERTY);
        String redirectTo = request.getParameter(WebUrlAlias.REDIRECT_TO_PROPERTY);

        WebUrlAlias webUrl;
        if (StringUtils.isNumeric(id)) {
            webUrl = Cayenne.objectForPK(context, WebUrlAlias.class, Long.valueOf(id));
        } else {
            webUrl = context.newObject(WebUrlAlias.class);
            webUrl.setWebSiteVersion(context.localObject(webSiteVersionService.getCurrentVersion()));
        }

        webUrl.setUrlPath(urlPath);
        webUrl.setRedirectTo(redirectTo);
        return webUrl;
    }

    private JSONObject validate(WebUrlAlias webUrl) {
        JSONObject result = new JSONObject();

        ISHUrlValidator validator = URLUtils.HTTP_URL_VALIDATOR;
        if (!validator.isValidOnlyPath(webUrl.getUrlPath())) {
            result.put("key", WebUrlAlias.URL_PATH_PROPERTY);
            result.put("message", "The from address must be a valid path within the site starting with /");
            return result;
        }

        WebUrlAlias fWebUrl = webUrlAliasService.getAliasByPath(webUrl.getUrlPath());
        if (fWebUrl != null && !fWebUrl.getObjectId().equals(webUrl.getObjectId())) {
            result.put("key", WebUrlAlias.URL_PATH_PROPERTY);
            result.put("message", "To create redirects to pages within this CMS, go to that page and add an additional URL in the page options.");
            return result;
        }


        if (!validator.isValid(webUrl.getRedirectTo()) && !validator.isValidOnlyPath(webUrl.getRedirectTo())) {
            result.put("key", WebUrlAlias.REDIRECT_TO_PROPERTY);
            result.put("message", "The to address must be a valid URL or partial URL starting with /");
            return result;
        }
        return null;
    }

    private JSONObject getJSONWebUrl(WebUrlAlias webUrlAlias) {
        JSONObject jWebUrl = new JSONObject();
        jWebUrl.put(WebUrlAlias.ID_PK_COLUMN, webUrlAlias.getId());
        jWebUrl.put(WebUrlAlias.URL_PATH_PROPERTY, webUrlAlias.getUrlPath());
        jWebUrl.put(WebUrlAlias.REDIRECT_TO_PROPERTY, webUrlAlias.getRedirectTo());
        return jWebUrl;
    }
}

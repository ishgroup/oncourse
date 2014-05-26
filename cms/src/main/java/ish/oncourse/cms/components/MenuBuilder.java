package ish.oncourse.cms.components;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebUrlAlias;
import ish.oncourse.services.alias.IWebUrlAliasService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

public class MenuBuilder extends ish.oncourse.ui.components.Menu {
	
	@SuppressWarnings("all")
	@Property
	@Component(id = "menuItem", parameters = { "menu=currentMenu",
			"childPosition=currentChildPosition" })
	private MenuNode menuItem;

	@SuppressWarnings("all")
	@Inject
	private IWebUrlAliasService aliasService;

	@Inject
	private IWebNodeService webNodeService;
	
	@Inject
	private IWebSiteService webSiteService;
	
	@Inject
	private IWebSiteVersionService webSiteVersionService;

	public String getSuggestResults() {

		JSONArray array = new JSONArray();

		for (WebUrlAlias alias : webSiteVersionService.getCurrentVersion(webSiteService.getCurrentWebSite()).getWebURLAliases()) {
			JSONObject obj = new JSONObject();

			obj.append("value", alias.getUrlPath());
			obj.append("label", alias.getUrlPath());
			obj.append("title", alias.getWebNode().getName());

			array.put(obj);
		}

		for (WebNode node : webNodeService.getNodes()) {
			JSONObject obj = new JSONObject();

			obj.append("value", node.getNodeNumber());
			obj.append("label", "/page/" + node.getNodeNumber());
			obj.append("title", node.getName());

			array.put(obj);
		}

		return array.toString();
	}
}

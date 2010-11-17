package ish.oncourse.cms.components;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebUrlAlias;
import ish.oncourse.services.alias.IWebUrlAliasService;
import ish.oncourse.services.node.IWebNodeService;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

public class MenuBuilder extends ish.oncourse.ui.components.Menu {
	
	@Property
	@Component(id = "menuItem", parameters = { "menu=currentMenu",
			"childPosition=currentChildPosition" })
	private MenuNode menuItem;

	@Inject
	private IWebUrlAliasService aliasService;

	@Inject
	private IWebNodeService webNodeService;

	public String getSuggestResults() {

		JSONArray array = new JSONArray();

		for (WebUrlAlias alias : aliasService.loadForCurrentSite()) {
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

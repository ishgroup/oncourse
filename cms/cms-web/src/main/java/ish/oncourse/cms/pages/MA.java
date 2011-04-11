package ish.oncourse.cms.pages;

import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.menu.IWebMenuService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

/**
 * The ajax backed page for Menu editor in CMS. This page is responsible for persisting 
 * @author anton
 *
 */
public class MA {

	@Inject
	private Request request;

	@Inject
	private IWebMenuService webMenuService;

	@Inject
	private IWebNodeService webNodeService;

	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private ICayenneService cayenneService;

	@Property
	private String message;

	private enum OPER {
		n, u
	};

	/**
	 * Invoked upon user click on 'New menu item' button.
	 * 
	 * @return
	 */
	StreamResponse onActionFromNewPage() {

		ObjectContext ctx = cayenneService.newContext();
		WebMenu menu = ctx.newObject(WebMenu.class);
		menu.setWebSite((WebSite) ctx.localObject(webSiteService.getCurrentWebSite().getObjectId(), null));
		ctx.commitChanges();

		JSONObject obj = new JSONObject();
		obj.put("id", menu.getId());

		return new TextStreamResponse("text/json", obj.toString());
	}
	
	/**
	 * Ajax call to handle in-place edit of menu item. 
	 * @return
	 */
	StreamResponse onActionFromSave() {
		String[] id = request.getParameter("id").split("_");
		String value = request.getParameter("value");

		WebMenu menu = (WebMenu) cayenneService.newContext().localObject(webMenuService.findById(Long.parseLong(id[1])).getObjectId(), null);

		switch (OPER.valueOf(id[0])) {
		case n:
			menu.setName(value);
			break;
		case u:
			if (value.startsWith("/page")) {
				String nodeId = value.substring(6);
				WebNode node = webNodeService.getNodeForNodeNumber(Integer.parseInt(nodeId));
				menu.setWebNode(node);
			} else {
				menu.setUrl(value);
			}
			break;
		}

		menu.getObjectContext().commitChanges();

		return new TextStreamResponse("text/html", value);
	}

	/**
	 * Handles ajax call to remove menu item.
	 * @return json-status
	 */
	StreamResponse onActionFromRemove() {

		String id = request.getParameter("id");

		ObjectContext ctx = cayenneService.newContext();

		WebMenu menu = (WebMenu) ctx.localObject(webMenuService.findById(Long.parseLong(id)).getObjectId(), null);
		if(!menu.getChildrenMenus().isEmpty()){
			return new TextStreamResponse("text/json", "{status: 'FAILED'}");
		}
		ctx.deleteObject(menu);

		ctx.commitChanges();

		return new TextStreamResponse("text/json", "{status: 'OK'}");
	}

	/**
	 * Handles ajax call to sort menu items. Done when user sorts items with drag&drop.
	 * @return
	 */
	StreamResponse onActionFromSort() {

		String id = request.getParameter("id");
		String pid = request.getParameter("pid");

		int weight = Integer.parseInt(request.getParameter("w"));

		ObjectContext ctx = cayenneService.newContext();

		WebMenu item = (WebMenu) ctx.localObject(webMenuService.findById(Long.parseLong(id)).getObjectId(), null);
		WebMenu pItem = (WebMenu) ctx.localObject((("root".equalsIgnoreCase(pid)) ? webMenuService.getRootMenu() : webMenuService.findById(Long.parseLong(pid))).getObjectId(), null);

		item.setParentWebMenu(pItem);
		item.updateWeight(weight);

		ctx.commitChanges();

		return new TextStreamResponse("text/json", "{status: 'OK'}");
	}
}

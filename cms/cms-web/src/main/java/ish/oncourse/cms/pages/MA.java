package ish.oncourse.cms.pages;

import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.menu.IWebMenuService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.site.IWebSiteService;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

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

		WebMenu menu = cayenneService.newContext().newObject(WebMenu.class);
		menu.setWebSite(webSiteService.getCurrentWebSite());
		menu.getObjectContext().commitChanges();

		JSONObject obj = new JSONObject();
		obj.put("id", menu.getId());

		return new TextStreamResponse("text/json", obj.toString());
	}

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

	StreamResponse onActionFromRemove() {

		String id = request.getParameter("id");

		ObjectContext ctx = cayenneService.newContext();

		WebMenu menu = (WebMenu) ctx.localObject(webMenuService.findById(Long.parseLong(id)).getObjectId(), null);
		ctx.deleteObject(menu);

		ctx.commitChanges();

		return new TextStreamResponse("text/json", "{status: 'OK'}");
	}

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

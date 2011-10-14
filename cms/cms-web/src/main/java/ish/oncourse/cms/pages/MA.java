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
 * The ajax backed page for Menu editor in CMS. This page is responsible for
 * persisting
 * 
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
		JSONObject obj = new JSONObject();
		if (request.getSession(false) == null) {
			obj.put("status", "session timeout");
			return new TextStreamResponse("text/json", obj.toString());
		}

		ObjectContext ctx = cayenneService.newContext();
		WebMenu menu = ctx.newObject(WebMenu.class);
		menu.setWebSite((WebSite) ctx.localObject(webSiteService.getCurrentWebSite().getObjectId(), null));
		menu.setParentWebMenu((WebMenu) ctx.localObject(webMenuService.getRootMenu().getObjectId(), null));
		ctx.commitChanges();

		obj.put("id", menu.getId());
		obj.put("warning", menu.getWarning());

		return new TextStreamResponse("text/json", obj.toString());
	}

	/**
	 * Ajax call to handle in-place edit of menu item.
	 * 
	 * @return
	 */
	StreamResponse onActionFromSave() {

		if (request.getSession(false) == null) {
			return new TextStreamResponse("text/html",
					"<script type=\"text/javascript\">window.location.reload();</script>");
		}

		String[] id = request.getParameter("id").split("_");
		String value = request.getParameter("value");

		WebMenu menu = (WebMenu) cayenneService.newContext().localObject(
				webMenuService.findById(Long.parseLong(id[1])).getObjectId(), null);

		switch (OPER.valueOf(id[0])) {
		case n:
			menu.setName(value);
			break;
		case u:
			if (!value.startsWith("http://") && !value.startsWith("https://") && !value.startsWith("www.")
					&& !value.startsWith("/")) {
				value = "/" + value;
			}
			WebNode node = null;
			if (value.matches("/page/\\d+")) {
				String nodeId = value.substring(6);

				node = webNodeService.getNodeForNodeNumber(Integer.parseInt(nodeId));

			} else {
				node = webNodeService.getNodeForNodePath(value);
			}
			if (node != null) {
				node = (WebNode) menu.getObjectContext().localObject(node.getObjectId(), null);
			}

			menu.setWebNode(node);

			menu.setUrl(node == null ? value : null);

			break;
		}

		menu.getObjectContext().commitChanges();

		JSONObject obj = new JSONObject();
		obj.put("id", menu.getId());
		obj.put("value", value);
		obj.put("warning", menu.getWarning());
		return new TextStreamResponse("text/json", obj.toString());
	}

	/**
	 * Handles ajax call to remove menu item.
	 * 
	 * @return json-status
	 */
	StreamResponse onActionFromRemove() {

		if (request.getSession(false) == null) {
			return new TextStreamResponse("text/json", "{status: 'session timeout'}");
		}

		String id = request.getParameter("id");

		ObjectContext ctx = cayenneService.newContext();

		WebMenu menu = (WebMenu) ctx.localObject(webMenuService.findById(Long.parseLong(id)).getObjectId(), null);
		if (!menu.getChildrenMenus().isEmpty()) {
			return new TextStreamResponse("text/json", "{status: 'FAILED'}");
		}
		ctx.deleteObject(menu);

		ctx.commitChanges();

		return new TextStreamResponse("text/json", "{status: 'OK'}");
	}

	/**
	 * Handles ajax call to sort menu items. Done when user sorts items with
	 * drag&drop.
	 * 
	 * @return
	 */
	StreamResponse onActionFromSort() {

		if (request.getSession(false) == null) {
			return new TextStreamResponse("text/json", "{status: 'session timeout'}");
		}

		String id = request.getParameter("id");
		String pid = request.getParameter("pid");

		int weight = Integer.parseInt(request.getParameter("w"));

		ObjectContext ctx = cayenneService.newContext();

		WebMenu item = (WebMenu) ctx.localObject(webMenuService.findById(Long.parseLong(id)).getObjectId(), null);
		WebMenu pItem = (WebMenu) ctx.localObject((("root".equalsIgnoreCase(pid)) ? webMenuService.getRootMenu()
				: webMenuService.findById(Long.parseLong(pid))).getObjectId(), null);

		item.setParentWebMenu(pItem);
		item.updateWeight(weight);

		ctx.commitChanges();

		return new TextStreamResponse("text/json", "{status: 'OK'}");
	}
}

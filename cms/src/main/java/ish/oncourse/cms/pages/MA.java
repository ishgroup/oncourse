package ish.oncourse.cms.pages;

import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.menu.IWebMenuService;
import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.util.List;

/**
 * The ajax backed page for Menu editor in CMS. This page is responsible for
 * persisting
 * 
 * @author anton
 */
public class MA {

	static final String MENU_ELEMENT_VALUE_PARAMETER = "value";
	static final String MENU_ELEMENT_ID_PARAMETER = "id";

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

	@SuppressWarnings("all")
	@Property
	private String message;

	private enum OPER {
		n, u
	}

	/**
	 * Invoked upon user click on 'New menu item' button.
	 * 
	 * @return
	 */
	StreamResponse onActionFromNewPage() {
        if (!request.isXHR())
        {
            return null;
        }

		JSONObject obj = new JSONObject();
		if (request.getSession(false) == null) {
			obj.put("status", "session timeout");
			return new TextStreamResponse("text/json", obj.toString());
		}

		ObjectContext ctx = cayenneService.newContext();
		WebSite webSite = ctx.localObject(webSiteService.getCurrentWebSite());
		WebMenu menu = webMenuService.createMenu(webSite);
		ctx.commitChanges();

		obj.put(MENU_ELEMENT_ID_PARAMETER, menu.getId());
		obj.put(MENU_ELEMENT_VALUE_PARAMETER, menu.getName());
		obj.put("warning", menu.getWarning());

		return new TextStreamResponse("text/json", obj.toString());
	}

	/**
	 * Ajax call to handle in-place edit of menu item.
	 */
	StreamResponse onActionFromSave() {
        if (!request.isXHR())
        {
            return null;
        }

		StringBuilder warning = new StringBuilder();

		if (request.getSession(false) == null) {
			return new TextStreamResponse("text/html",
					"<script type=\"text/javascript\">window.location.reload();</script>");
		}

		String[] id = request.getParameter(MENU_ELEMENT_ID_PARAMETER).split("_");
		String value = request.getParameter(MENU_ELEMENT_VALUE_PARAMETER);
		if (request.getParameterNames().size() > 2) {
			StringBuilder agregatedValue = new StringBuilder(value);
			//this mean that & character linked with request and JQuery post value parameter as few parameters
			for (String parameter : request.getParameterNames()) {
				//skip the expected parameters out of process
				if (!MENU_ELEMENT_ID_PARAMETER.equals(parameter) && !MENU_ELEMENT_VALUE_PARAMETER.equals(parameter)) {
					agregatedValue.append("&").append(parameter.replaceAll("amp;", StringUtils.EMPTY)).append("=").append(request.getParameter(parameter));
				}
			}
			value = agregatedValue.toString();
		}

		WebMenu menu = (WebMenu) cayenneService.newContext().localObject(
				webMenuService.findById(Long.parseLong(id[1])).getObjectId(), null);

		switch (OPER.valueOf(id[0])) {
			case n:
				if (webMenuService.getMenuByNameAndParentMenu(value, menu.getParentWebMenu()) == null) {
					menu.setName(value);
				} else {
					warning.append(menu.getNonUniqueNameWarning());
					value = menu.getName();
				}
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
		obj.put(MENU_ELEMENT_ID_PARAMETER, menu.getId());
		obj.put(MENU_ELEMENT_VALUE_PARAMETER, value);
		warning.append(" "+menu.getWarning());
		obj.put("warning", warning.toString());

		return new TextStreamResponse("text/json", obj.toString());
	}

	/**
	 * Handles ajax call to remove menu item.
	 * 
	 * @return json-status
	 */
	StreamResponse onActionFromRemove() {
        if (!request.isXHR())
        {
           return null;
        }

		if (request.getSession(false) == null) {
			return new TextStreamResponse("text/json", "{status: 'session timeout'}");
		}

		String id = request.getParameter(MENU_ELEMENT_ID_PARAMETER);

		ObjectContext ctx = cayenneService.newContext();

		WebMenu menu = ctx.localObject(webMenuService.findById(Long.parseLong(id)));
		if (!menu.getChildrenMenus().isEmpty()) {
			return new TextStreamResponse("text/json", "{status: 'FAILED'}");
		}
		WebMenu parentWebMenu = menu.getParentWebMenu();
		parentWebMenu.removeFromChildrenMenus(menu);
		List<WebMenu> webMenus = parentWebMenu.getWebMenus();
		//update weights for the rest of menus
		if (!webMenus.isEmpty()) {
			webMenus.get(0).updateWeight(0, parentWebMenu);
		}
		ctx.deleteObjects(menu);

		ctx.commitChanges();

		return new TextStreamResponse("text/json", "{status: 'OK'}");
	}

	/**
	 * Handles ajax call to sort menu items. Done when user sorts items with
	 * drag&drop.
	 */
	StreamResponse onActionFromSort() {
        if (!request.isXHR())
        {
            return null;
        }

		if (request.getSession(false) == null) {
			return new TextStreamResponse("text/json", "{status: 'session timeout'}");
		}

		String id = request.getParameter(MENU_ELEMENT_ID_PARAMETER);
		String pid = request.getParameter("pid");

		int weight = Integer.parseInt(request.getParameter("w"));

		ObjectContext ctx = cayenneService.newContext();

		WebMenu item = ctx.localObject(webMenuService.findById(Long.parseLong(id)));
		WebMenu pItem = ctx.localObject((("root".equalsIgnoreCase(pid)) ? webMenuService.getRootMenu()
				: webMenuService.findById(Long.parseLong(pid))));
		
		if (webMenuService.getMenuByNameAndParentMenu(item.getName(), pItem) == null) {
			WebMenu oldParent = item.getParentWebMenu();
			item.setParentWebMenu(pItem);
			item.updateWeight(weight, oldParent);

			ctx.commitChanges();
			return new TextStreamResponse("text/json", "{status: 'OK'}");
		} else {
			ctx.rollbackChanges();
			return new TextStreamResponse("text/json", "{status: 'FAILED'}");
		}	
	}
}

package ish.oncourse.cms.pages;

import ish.oncourse.model.WebMenu;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.menu.IWebMenuService;
import ish.oncourse.services.node.IWebNodeService;

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
	private ICayenneService cayenneService;

	@Property
	private String message;

	private enum OPER {
		n, u
	};

	StreamResponse onActionFromNewPage() {
		WebMenu menu = webMenuService.newMenu();

		JSONObject obj = new JSONObject();
		obj.put("id", menu.getId());

		return new TextStreamResponse("text/json", obj.toString());
	}

	StreamResponse onActionFromSave() {
		String[] id = request.getParameter("id").split("_");
		String value = request.getParameter("value");

		WebMenu menu = webMenuService.loadByIds(id[1]).get(0);

		switch (OPER.valueOf(id[0])) {
		case n:
			menu.setName(value);
			break;
		case u:
			if (value.startsWith("/page")) {
				String nodeId = value.substring(6);
				WebNode node = webNodeService.getNodeForNodeNumber(Integer
						.parseInt(nodeId));
				menu.setWebNode(node);
			} else {
				menu.setUrl(value);
			}
			break;
		}

		cayenneService.sharedContext().commitChanges();

		return new TextStreamResponse("text/html", value);
	}

	StreamResponse onActionFromRemove() {

		String id = request.getParameter("id");

		WebMenu menu = webMenuService.loadByIds(id).get(0);

		cayenneService.sharedContext().deleteObject(menu);
		cayenneService.sharedContext().commitChanges();

		return new TextStreamResponse("text/json", "{status: 'OK'}");
	}

	StreamResponse onActionFromSort() {
		String id = request.getParameter("id");
		String pid = request.getParameter("pid");

		int w = Integer.parseInt(request.getParameter("w"));

		WebMenu item = webMenuService.loadByIds(id).get(0);
		WebMenu pItem = ("root".equalsIgnoreCase(pid)) ? null : webMenuService
				.loadByIds(pid).get(0);

		for (WebMenu m : pItem.getChildrenMenus()) {
			int weight = m.getWeight();
			if (weight >= w) {
				m.setWeight(weight + 1);
			}
		}

		item.setParentWebMenu(pItem);
		item.setWeight(w);

		cayenneService.sharedContext().commitChanges();

		return new TextStreamResponse("text/json", "{status: 'OK'}");
	}
}

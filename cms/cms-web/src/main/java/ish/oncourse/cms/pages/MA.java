package ish.oncourse.cms.pages;

import ish.oncourse.model.WebMenu;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.menu.IWebMenuService;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

public class MA {

	@Inject
	private Request request;

	@Inject
	private IWebMenuService webMenuService;

	@Inject
	private ICayenneService cayenneService;

	@Property
	private String message;

	private enum OPER {
		n, u
	};
	
	StreamResponse onActionFromSave() {
		String[] id = request.getParameter("id").split("_");
		String value = request.getParameter("value");

		WebMenu menu = webMenuService.loadByIds(id[1]).get(0);

		switch (OPER.valueOf(id[0])) {
		case n:
			menu.setName(value);
			break;
		case u:
			menu.setUrl(value);
			break;
		}

		cayenneService.sharedContext().commitChanges();
		
		return new TextStreamResponse("text/html", value);
	}
	
	StreamResponse onActionFromDelete() {
		return new TextStreamResponse("text/json", "");
	}
	
	StreamResponse onActionFromSuggest() {
		return new TextStreamResponse("text/json", "");
	}
}

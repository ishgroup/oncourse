package ish.oncourse.cms.components;

import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.visitor.LastEditedVisitor;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PageTypes {

	@Inject
	@Property
	private IWebNodeTypeService webNodeTypeService;

	@Inject
	private Messages messages;

	@Inject
	private IWebSiteService webSiteService;

	@Property
	private WebNodeType webNodeType;

	@Property
	@Persist
	private WebNodeType selectedPageType;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private Block editPageTypeBlock;

	@Property
	@Component
	private Zone pageTypeZone;

	public String getLastEdited() {
		return webNodeType.accept(new LastEditedVisitor(messages));
	}

	private Object onActionFromNewPageType() {
		ObjectContext ctx = cayenneService.newContext();
		this.selectedPageType = ctx.newObject(WebNodeType.class);
		selectedPageType.setWebSite((WebSite) ctx.localObject(webSiteService
				.getCurrentWebSite().getObjectId(), null));
		return editPageTypeBlock;
	}

	private Object onActionFromEditPageType(String id) {
		this.selectedPageType = webNodeTypeService.findById(Long.parseLong(id));
		return editPageTypeBlock;
	}

	private Object onActionFromDeletePageType(String id) {
		ObjectContext ctx = cayenneService.newContext();
		WebNodeType themeToDelete = webNodeTypeService.findById(Long
				.parseLong(id));
		if (themeToDelete != null) {
			themeToDelete = (WebNodeType) ctx.localObject(
					themeToDelete.getObjectId(), null);
			ctx.deleteObject(themeToDelete);
			ctx.commitChanges();
		}
		return pageTypeZone.getBody();
	}

	public String getEditPageTypeUrl() {
		return "http://" + webSiteService.getCurrentDomain().getName()
				+ "/cms/site.pagetypes.editpagetype/";
	}
}

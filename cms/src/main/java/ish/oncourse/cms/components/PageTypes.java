package ish.oncourse.cms.components;

import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.site.IWebSiteVersionService;
import ish.oncourse.ui.pages.internal.Page;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class PageTypes {
	
	static final String CAN_NOT_DELETE_PAGE_MESSAGE = "This theme %s cannot be deleted since it is has been used by a page.";

	@Inject
	private Request request;
	
	@Inject
	private IWebSiteService webSiteService;

	@Inject
	private IWebSiteVersionService webSiteVersionService;
	
	@Property
	private WebNodeType webNodeType;

	@Property
	@Persist
	private WebNodeType selectedPageType;

	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	@Property
	private IWebNodeTypeService webNodeTypeService;

	@Inject
	private Block editPageTypeBlock;

	@Component
	private Zone pageTypeZone;
	
	private String problemMessage;
	
	@InjectPage
	private Page page;

	Object onActionFromNewPageType() {
		if(request.getSession(false)==null){
			return page.getReloadPageBlock();
		}
		ObjectContext ctx = cayenneService.newContext();
		WebNodeType newTheme = ctx.newObject(WebNodeType.class);
		newTheme.setWebSiteVersion(
				webSiteVersionService.getCurrentVersion(ctx.localObject(webSiteService.getCurrentWebSite())));
		changeSelectedPageType(newTheme);
		return getEditPageTypeBlock();
	}

	public String getEditPageTypeUrl() {
		return "http://" + request.getServerName() + "/cms/site.pagetypes.editpagetype/";
	}
	
	public String getProblemMessage() {
		Object pm = request.getAttribute("problemMessage");
		if(pm != null){
			return (String) pm;
		}
		return problemMessage;
	}
	
	void setProblemMessage(String problemMessage) {
		this.problemMessage = problemMessage;
	}

	public Block getEditPageTypeBlock() {
		return editPageTypeBlock;
	}

	void changeSelectedPageType(WebNodeType selectedPageType) {
		this.selectedPageType = selectedPageType;
	}

	public Zone getPageTypeZone() {
		return pageTypeZone;
	}
	
}

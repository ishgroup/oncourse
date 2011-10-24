package ish.oncourse.cms.components;

import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.visitor.LastEditedVisitor;
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

	@Inject
	private Request request;
	
	@Inject
	@Property
	private IWebNodeTypeService webNodeTypeService;

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
	
	private String problemMessage;
	
	@InjectPage
	private Page page;

	public String getLastEdited() {
		return webNodeType.accept(new LastEditedVisitor());
	}
	
	private Object onActionFromNewPageType() {
		if(request.getSession(false)==null){
			return page.getReloadPageBlock();
		}
		ObjectContext ctx = cayenneService.newContext();
		this.selectedPageType = ctx.newObject(WebNodeType.class);
		selectedPageType.setWebSite((WebSite) ctx.localObject(webSiteService
				.getCurrentWebSite().getObjectId(), null));
		return editPageTypeBlock;
	}

	private Object onActionFromEditPageType(String id) {
		if(request.getSession(false)==null){
			return page.getReloadPageBlock();
		}
		this.selectedPageType = webNodeTypeService.findById(Long.parseLong(id));
		return editPageTypeBlock;
	}

	private Object onActionFromDeletePageType(String id) {
		if(request.getSession(false)==null){
			return page.getReloadPageBlock();
		}
		ObjectContext ctx = cayenneService.newContext();
		WebNodeType themeToDelete = webNodeTypeService.findById(Long
				.parseLong(id));

		if (themeToDelete != null) {
			themeToDelete = (WebNodeType) ctx.localObject(
					themeToDelete.getObjectId(), null);
			if (themeToDelete.getWebNodes().size() > 0) {
				this.problemMessage = "This theme " + themeToDelete.getName() + " cannot be deleted since it is has been used by a page.";
			} else {
				ctx.deleteObject(themeToDelete);
				ctx.commitChanges();
			}
		}
		return pageTypeZone.getBody();
	}

	public String getEditPageTypeUrl() {
		return "http://" + request.getServerName()
				+ "/cms/site.pagetypes.editpagetype/";
	}
	
	public String getProblemMessage() {
		Object pm = request.getAttribute("problemMessage");
		if(pm != null){
			return (String) pm;
		}
		return problemMessage;
	}
	
}

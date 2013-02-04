package ish.oncourse.cms.components;

import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.visitor.LastEditedVisitor;
import ish.oncourse.ui.pages.internal.Page;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class ThemeItem {
	
	@Parameter
	@Property
	private WebNodeType webNodeType;
	
	@Inject
	private Request request;
	
	@InjectPage
	private Page page;
	
	@Inject
	@Property
	private IWebNodeTypeService webNodeTypeService;
	
	@Inject
	private ComponentResources componentResources;
	
	@Inject
	private ICayenneService cayenneService;
	
	public String getLastEdited() {
		return webNodeType.accept(new LastEditedVisitor());
	}
	
	public boolean getIsSpecialType() {
		return WebNodeType.PAGE.equals(webNodeType.getName());
	}
	
	Object onActionFromEditPageType(String id) {
		if(request.getSession(false)==null){
			return page.getReloadPageBlock();
		}
		PageTypes parentComponent = (PageTypes) componentResources.getContainer();
		parentComponent.changeSelectedPageType(webNodeTypeService.findById(Long.parseLong(id)));
		return parentComponent.getEditPageTypeBlock();
	}
		
	Object onActionFromDeletePageType(String id) {
		if(request.getSession(false)==null){
			return page.getReloadPageBlock();
		}
		ObjectContext ctx = cayenneService.newContext();
		WebNodeType themeToDelete = webNodeTypeService.findById(Long.parseLong(id));

		PageTypes parentComponent = (PageTypes) componentResources.getContainer();
		if (themeToDelete != null) {
			themeToDelete = (WebNodeType) ctx.localObject(
					themeToDelete.getObjectId(), null);
			boolean canNotBeDeleted = themeToDelete.getWebNodes().size() > 0;
			if (canNotBeDeleted) {
				parentComponent.setProblemMessage(String.format(PageTypes.CAN_NOT_DELETE_PAGE_MESSAGE, themeToDelete.getName()));
			} else {
				ctx.deleteObject(themeToDelete);
				ctx.commitChanges();
			}
		}
		return parentComponent.getPageTypeZone().getBody();
	}

}

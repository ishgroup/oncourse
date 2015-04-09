package ish.oncourse.cms.components;

import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.node.IWebNodeTypeService;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.visitor.LastEditedVisitor;
import ish.oncourse.ui.pages.internal.Page;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

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
	
	public boolean canRemove() {
		return !webNodeType.isDefaultPageTheme() && !webNodeType.isThemeUsedInPages();
	}
		
	Object onActionFromEditPageType(String id) {
		if(request.getSession(false)==null){
			return page.getReloadPageBlock();
		}
		PageTypes parentComponent = (PageTypes) componentResources.getContainer();
		parentComponent.changeSelectedPageType(webNodeTypeService.findById(Long.parseLong(id)));
		return parentComponent.getEditPageTypeBlock();
	}
	
	StreamResponse onActionFromDeletePageType(String id) {
		if(request.getSession(false)==null){
			return new TextStreamResponse("text/json", "{status: 'session timeout'}");
		}
		ObjectContext ctx = cayenneService.newContext();
		WebNodeType themeToDelete = webNodeTypeService.findById(Long.parseLong(id));
		if (themeToDelete != null) {
			themeToDelete = ctx.localObject(themeToDelete);
			if (themeToDelete.isThemeUsedInPages()) {
				return new TextStreamResponse("text/json", "{status: 'FAILED',themeName:'" + themeToDelete.getName() + "'}");
			} else {
				ctx.deleteObject(themeToDelete);
				ctx.commitChanges();
			}
		}
		return new TextStreamResponse("text/json", "{status: 'OK'}");
	}

}

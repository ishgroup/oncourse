package ish.oncourse.cms.components;

import java.util.List;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebNodeType;
import ish.oncourse.services.content.IWebContentService;
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

public class BlockItem {
	
	@Parameter
	@Property
	private WebContent block;
	
	@Inject
	private Request request;
	
	@InjectPage
	private Page page;
	
	@Property
	@Inject
	private IWebContentService webContentService;
	
	@Inject
	private ICayenneService cayenneService;
	
	@Inject
	private ComponentResources componentResources;
	
	public String getLastEdited() {
		return block.accept(new LastEditedVisitor());
	}
	
	public boolean getIsUsedInPages() {
		boolean result = false;
		List<WebContentVisibility> visibilities = block.getWebContentVisibilities();
		if (visibilities.size() > 0) {
			for (WebContentVisibility visibility : visibilities) {
				if (visibility.getWebNodeType() != null) {
					//not unassigned
					WebNodeType theme = visibility.getWebNodeType();
					if (theme.isThemeUsedInPages()) {
						return true;
					}
				}
			}
		}
		return result;
	}
	
	Object onActionFromEditBlock(String id) {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		Blocks parentComponent = (Blocks) componentResources.getContainer();
		parentComponent.changeSelectedBlock(webContentService.findById(Long.parseLong(id)));
		return parentComponent.getEditBlock();
	}
	
	Object onActionFromDeleteBlock(String id) {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		ObjectContext ctx = cayenneService.newContext();
		WebContent blockToDelete = webContentService.findById(Long.parseLong(id));
		if (blockToDelete != null) {
			blockToDelete = (WebContent) ctx.localObject(blockToDelete.getObjectId(), null);
			ctx.deleteObject(blockToDelete);
			ctx.commitChanges();
		}
		Blocks parentComponent = (Blocks) componentResources.getContainer();
		return parentComponent.getBlockZone().getBody();
	}

}

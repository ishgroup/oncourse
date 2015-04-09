package ish.oncourse.cms.components;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.services.content.IWebContentService;
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

import java.util.List;

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
		return isBlockAssigned(block);
	}
	
	static boolean isBlockAssigned(WebContent block) {
		List<WebContentVisibility> visibilities = block.getWebContentVisibilities();
		if (visibilities.size() > 0) {
			for (WebContentVisibility visibility : visibilities) {
				if (visibility.getWebNodeType() != null) {
					//not unassigned
					return true;
				}
			}
		}
		return false;
	}
	
	Object onActionFromEditBlock(String id) {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		Blocks parentComponent = (Blocks) componentResources.getContainer();
		parentComponent.changeSelectedBlock(webContentService.findById(Long.parseLong(id)));
		return parentComponent.getEditBlock();
	}
	
	StreamResponse onActionFromDeleteBlock(String id) {
		if (request.getSession(false) == null) {
			return new TextStreamResponse("text/json", "{status: 'session timeout'}");
		}
		ObjectContext ctx = cayenneService.newContext();
		WebContent blockToDelete = webContentService.findById(Long.parseLong(id));
		if (blockToDelete != null && !isBlockAssigned(blockToDelete)) {
			blockToDelete = ctx.localObject(blockToDelete);
			ctx.deleteObject(blockToDelete);
			ctx.commitChanges();
		} else {
			return new TextStreamResponse("text/json", "{status: 'FAILED'}");
		}
		return new TextStreamResponse("text/json", "{status: 'OK'}");
	}

}

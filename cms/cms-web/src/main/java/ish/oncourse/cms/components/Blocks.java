package ish.oncourse.cms.components;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.WebSite;
import ish.oncourse.services.content.IWebContentService;
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

public class Blocks {

	@InjectPage
	private Page page;

	@Inject
	private Request request;

	@Property
	@Inject
	private IWebContentService webContentService;

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@Property
	private WebContent block;

	@Property
	@Persist
	private WebContent selectedBlock;

	@Inject
	private Block editBlock;

	@Property
	@Component
	private Zone blockZone;

	public String getLastEdited() {
		return block.accept(new LastEditedVisitor());
	}

	private Object onActionFromNewBlock() {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		ObjectContext ctx = cayenneService.newContext();
		selectedBlock = ctx.newObject(WebContent.class);

		WebContentVisibility visibility = ctx.newObject(WebContentVisibility.class);
		visibility.setRegionKey(RegionKey.unassigned);
		visibility.setWebContent(selectedBlock);

		selectedBlock.setWebSite((WebSite) ctx.localObject(webSiteService.getCurrentWebSite().getObjectId(), null));

		return editBlock;
	}

	private Object onActionFromEditBlock(String id) {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		selectedBlock = webContentService.findById(Long.parseLong(id));
		return editBlock;
	}

	private Object onActionFromDeleteBlock(String id) {
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
		return blockZone.getBody();
	}

	public String getEditBlockUrl() {
		return "http://" + webSiteService.getCurrentDomain().getName() + "/cms/site.blocks.editblock/";
	}
}

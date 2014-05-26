package ish.oncourse.cms.components;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.services.content.IWebContentService;
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
	
	@Inject
	private IWebSiteVersionService webSiteVersionService;

	@Property
	private WebContent block;

	@Property
	@Persist
	private WebContent selectedBlock;

	@Inject
	private Block editBlock;

	@Component
	private Zone blockZone;

	Object onActionFromNewBlock() {
		if (request.getSession(false) == null) {
			return page.getReloadPageBlock();
		}
		ObjectContext ctx = cayenneService.newContext();
		changeSelectedBlock(ctx.newObject(WebContent.class));

		WebContentVisibility visibility = ctx.newObject(WebContentVisibility.class);
		visibility.setRegionKey(RegionKey.unassigned);
		visibility.setWebContent(selectedBlock);

		selectedBlock.setWebSiteVersion(
				webSiteVersionService.getCurrentVersion(ctx.localObject(webSiteService.getCurrentWebSite())));

		return getEditBlock();
	}

	public String getEditBlockUrl() {
		return "http://" + request.getServerName() + "/cms/site.blocks.editblock/";
	}

	public Block getEditBlock() {
		return editBlock;
	}

	public Zone getBlockZone() {
		return blockZone;
	}

	void changeSelectedBlock(WebContent selectedBlock) {
		this.selectedBlock = selectedBlock;
	}
	
}

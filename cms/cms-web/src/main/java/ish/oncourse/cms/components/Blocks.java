package ish.oncourse.cms.components;

import ish.oncourse.model.RegionKey;
import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebContentVisibility;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.services.content.IWebContentService;
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

public class Blocks {

	@Property
	@Inject
	private IWebContentService webContentService;
	
	@Inject
	private ICayenneService cayenneService;

	@Inject
	private Messages messages;
	
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
		return block.accept(new LastEditedVisitor(messages));
	}

	private Object onActionFromNewBlock() {
		ObjectContext ctx = cayenneService.newContext();
		selectedBlock = ctx.newObject(WebContent.class);
		
		WebContentVisibility visibility = ctx.newObject(WebContentVisibility.class);
		visibility.setRegionKey(RegionKey.unassigned);
		visibility.setWebContent(selectedBlock);

		return editBlock;
	}
	
	private Object onActionFromEditBlock(String id) {
		selectedBlock = webContentService.findById(Long.parseLong(id));
		return editBlock;
	}
	
	public String getEditBlockUrl() {
		return "http://" + webSiteService.getCurrentDomain().getName() + "/cms/site.blocks.editblock/";
	}
}

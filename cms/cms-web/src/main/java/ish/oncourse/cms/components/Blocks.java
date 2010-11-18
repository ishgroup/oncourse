package ish.oncourse.cms.components;

import ish.oncourse.model.WebContent;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.services.visitor.LastEditedVisitor;

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
		this.selectedBlock = webContentService.newWebContent();
		return editBlock;
	}
	
	private Object onActionFromEditBlock(String id) {
		this.selectedBlock = webContentService.loadByIds(id).get(0);
		return editBlock;
	}
	
	public String getEditBlockUrl() {
		return "http://" + webSiteService.getCurrentDomain().getName() + "/cms/site.blocks.editblock/";
	}
}

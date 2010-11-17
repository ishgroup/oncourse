package ish.oncourse.ui.pages;

import ish.oncourse.model.WebContent;
import ish.oncourse.services.content.IWebContentService;
import ish.oncourse.ui.utils.EmptyRenderable;

import org.apache.log4j.Logger;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;

public class Page extends GenericPage {

	private static final Logger LOGGER = Logger.getLogger(Page.class);

	@Property
	@Component(id = "regionForm")
	private Form regionForm;

	@Inject
	private IWebContentService webContentService;

	@Inject
	private Block editorBlock;

	@Inject
	@Property
	private Block regionContentBlock;

	Object onActionFromEditRegion(String id) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(String.format("Edit region with id: %s", id));
		}
		
		WebContent region = webContentService.loadByIds(id).get(0);
		setCurrentRegion(region);

		return editorBlock;
	}

	Object onSuccessFromRegionForm() {
		getCurrentRegion().getObjectContext().commitChanges();
		return new MultiZoneUpdate("editorZone", new EmptyRenderable()).add(
				getCurrentZoneKey(), regionContentBlock);
	}

	public String getCurrentZoneKey() {
		return "z_"
				+ getCurrentRegion().getWebContentVisibility().getRegionKey();
	}
}

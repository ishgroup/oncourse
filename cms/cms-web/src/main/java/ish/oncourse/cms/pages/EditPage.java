package ish.oncourse.cms.pages;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.WebNode;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.ui.pages.Page;
import ish.oncourse.ui.utils.EmptyRenderable;

import org.apache.cayenne.DataObjectUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;

public class EditPage extends Page {
	
	@Property
	@Component(id = "regionForm")
	private Form regionForm;
	
	@Inject
	private Block editorBlock;

	@Inject
	private Block regionContentBlock;

	@Inject
	private ICayenneService cayenneService;

	@Property
	private String webNodeId;

	void onActivate(String webNodeId) {
		this.webNodeId = webNodeId;
	}

	@SetupRender
	public void beforeRender() {
		WebNode node = DataObjectUtils.objectForPK(cayenneService.newContext(),
				WebNode.class, webNodeId);
		setCurrentNode(node);
	}

	Object onActionFromEditRegion(String id) {
		WebContent region = DataObjectUtils.objectForPK(getCurrentNode()
				.getObjectContext(), WebContent.class, id);
		setCurrentRegion(region);

		return editorBlock;
	}

	Object onActionFromEditRegionNew(String id) {
		return onActionFromEditRegion(id);
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

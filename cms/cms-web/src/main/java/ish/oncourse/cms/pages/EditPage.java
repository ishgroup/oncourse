package ish.oncourse.cms.pages;

import ish.oncourse.model.WebNode;
import ish.oncourse.model.WebNodeContent;
import ish.oncourse.model.services.persistence.ICayenneService;
import ish.oncourse.ui.pages.Page;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.Renderable;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

public class EditPage extends Page {

	@Persist
	@Property
	private WebNodeContent editorRegion;

	@Component
	private Zone contentZone;

	@Component
	private Zone editorZone;

	@Inject
	private Block editorBlock;

	@Property
	@Component(id = "regionForm")
	private Form regionForm;

	@Inject
	private ICayenneService cayenneService;

	void onActivate(String webNodeID) {
		WebNode node = DataObjectUtils.objectForPK(cayenneService.newContext(),
				WebNode.class, webNodeID);
		setCurrentNode(node);
	}

	Object onActionFromEditRegion(String id) {
		//TODO commented till the question with the layouts regions will be resolved
		/*this.editorRegion = ExpressionFactory
				.matchExp(WebNodeContent.REGION_KEY_PROPERTY, id)
				.filterObjects(getCurrentNode().getWebNodeContents()).get(0);*/

		return editorBlock;
	}

	Object onSuccessFromRegionForm() {
		this.editorRegion.getObjectContext().commitChanges();
		return new MultiZoneUpdate("contentZone", contentZone).add(
				"editorZone", new EmptyRenderable());
	}

	public boolean isEditRegionSelected() {
		return this.editorRegion != null;
	}

	private static class EmptyRenderable implements Renderable {
		public void render(MarkupWriter writer) {

		}
	}
}

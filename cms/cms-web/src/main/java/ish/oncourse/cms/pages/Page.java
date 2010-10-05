package ish.oncourse.cms.pages;

import ish.oncourse.model.WebNodeContent;

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

public class Page extends ish.oncourse.ui.pages.Page {

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

	@Override
	public void beforeRender() {
		if (currentNode() == null) {
			super.beforeRender();
		}
	}

	Object onActionFromEditRegion(String id) {

		this.editorRegion = ExpressionFactory
				.matchExp(WebNodeContent.REGION_KEY_PROPERTY, id)
				.filterObjects(currentNode().getWebNodeContents()).get(0);

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

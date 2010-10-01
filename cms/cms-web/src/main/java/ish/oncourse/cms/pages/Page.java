package ish.oncourse.cms.pages;

import ish.oncourse.model.WebNodeContent;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ajax.MultiZoneUpdate;

public class Page extends ish.oncourse.ui.pages.Page {

	@Property
	private WebNodeContent editRegion;

	@Component
	private Zone editorZone;
	
	@Component
	private Form regionForm;

	public void selectEditRegion(WebNodeContent region) {
		this.editRegion = region;
	}

	Object onActionFromEditRegion(String id) {

		WebNodeContent activeRegion = ExpressionFactory
				.matchExp(WebNodeContent.REGION_KEY_PROPERTY, id)
				.filterObjects(getCurrentNode().getWebNodeContents()).get(0);

		selectEditRegion(activeRegion);

		return editorZone.getBody();
	}
	
	Object onActionFromSave() {
		//ObjectContext ctx = editRegion.getObjectContext();
		//ctx.commitChanges();
		selectEditRegion(null);
		return null;//new MultiZoneUpdate("editorZone", editorZone).add("regionZone", regionZone());
	}

	public boolean isEditRegionSelected() {
		return this.editRegion != null;
	}
}

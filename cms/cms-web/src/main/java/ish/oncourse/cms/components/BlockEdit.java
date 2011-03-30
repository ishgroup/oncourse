package ish.oncourse.cms.components;

import ish.oncourse.model.WebContent;
import ish.oncourse.services.persistence.ICayenneService;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

public class BlockEdit {
	
	@Parameter(required=true)
	@Property
	private WebContent block;
	
	@Property
	@Persist
	private WebContent editBlock;
	
	@Parameter
	@Property
	private Zone updateZone;
	
	@Property
	@Component
	private Form blockEditForm;
	
	@Inject
	private ICayenneService cayenneService;
	
	@SetupRender
	public void beforeRender() {
		editBlock = (WebContent) cayenneService.newContext().localObject(block.getObjectId(), block);
	}
	
	Object onSuccessFromBlockEditForm() {
		editBlock.getObjectContext().commitChanges();
		return updateZone.getBody();
	}
	
	Object onActionFromCancel() {
		return updateZone.getBody();
	}
	
	Object onActionFromDelete() {
		ObjectContext ctx = editBlock.getObjectContext();
		ctx.deleteObject(editBlock);
		ctx.commitChanges();
		return updateZone.getBody();
	}
}

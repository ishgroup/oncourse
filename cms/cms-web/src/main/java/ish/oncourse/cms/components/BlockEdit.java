package ish.oncourse.cms.components;

import ish.oncourse.model.WebContent;
import ish.oncourse.model.services.persistence.ICayenneService;

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
		this.editBlock = block;
	}
	
	Object onSuccessFromBlockEditForm() {
		cayenneService.sharedContext().commitChanges();
		return updateZone.getBody();
	}
	
	Object onActionFromCancel() {
		return updateZone.getBody();
	}
	
	Object onActionFromDelete() {
		
		cayenneService.sharedContext().deleteObject(editBlock);
		cayenneService.sharedContext().commitChanges();
		
		return updateZone.getBody();
	}
}

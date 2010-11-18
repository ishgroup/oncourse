package ish.oncourse.cms.components;

import ish.oncourse.model.WebNodeType;
import ish.oncourse.model.services.persistence.ICayenneService;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

public class PageTypeEdit {
	
	@Parameter(required=true)
	@Property
	private WebNodeType pageType;
	
	@Property
	@Persist
	private WebNodeType editPageType;
	
	@Parameter
	@Property
	private Zone updateZone;
	
	@Inject
	private ICayenneService cayenneService;
	
	@SetupRender
	public void beforeRender() {
		this.editPageType = pageType;
	}
	
	Object onSuccessFromBlockEditForm() {
		cayenneService.sharedContext().commitChanges();
		return updateZone.getBody();
	}
	
	Object onActionFromCancel() {
		return updateZone.getBody();
	}
	
	Object onActionFromDelete() {
		
		cayenneService.sharedContext().deleteObject(editPageType);
		cayenneService.sharedContext().commitChanges();
		
		return updateZone.getBody();
	}
}

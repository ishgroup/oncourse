package ish.oncourse.admin.pages;

import ish.oncourse.admin.services.ntis.INTISUpdater;
import ish.oncourse.model.Qualification;

import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class NTIS {
	
	private boolean isUpdate;
	
	@Inject
	private INTISUpdater ntisUpdater;

	@SetupRender
	void setupRender() {
		
	}
	
	@OnEvent(component = "update", value = "selected")
	void onSelectedUpdate() {
		this.isUpdate = true;
	}
	
	@OnEvent(component = "updateForm", value = "success")
	void submitted() {
		ntisUpdater.doUpdate(Qualification.class);
	}
}

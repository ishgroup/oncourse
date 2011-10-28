package ish.oncourse.admin.pages;

import ish.oncourse.admin.services.ntis.INTISUpdater;

import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class NTIS {
	
	@Inject
	private INTISUpdater ntisUpdater;

	@SetupRender
	void setupRender() {
		
	}
}

package ish.oncourse.admin.pages;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import au.gov.training.services.trainingcomponent.ITrainingComponentService;
import au.gov.training.services.trainingcomponent.ITrainingComponentServiceGetServerTimeValidationFaultFaultFaultMessage;

public class NTIS {
	
	@Property
	private XMLGregorianCalendar serverDate;
	
	@Inject
	private ITrainingComponentService port;

	@SetupRender
	void setupRender() throws ITrainingComponentServiceGetServerTimeValidationFaultFaultFaultMessage {
		serverDate = port.getServerTime();
	}

}

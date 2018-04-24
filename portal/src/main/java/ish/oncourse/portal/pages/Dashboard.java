/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.pages;


import ish.oncourse.cache.ICacheProvider;
import ish.oncourse.model.Contact;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.dashboard.*;
import ish.oncourse.services.preference.PreferenceController;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;


public class Dashboard {

	@Inject
	@Property
	private IPortalService portalService;

	@Property
	private Contact contact;

	@Property
	private CalculateAttendancePercent attendanceCalculater;

	@Property
	private CalculatePaymentDue paymentDuecalculater;
	
	@Property
	private GetEnrolmentToSurvey getEnrolmentToSurvey;
	
	@Property
	private GetSessionToMarkRoll getSessionToMarkRoll;
	
	@Property
	private GetClassToMarkOutcomes getClassToMarkOutcomes;
	
	@Property
	private GetApplicationToStudy getApplicationToStudy;
	
	@Property
	private GetClassToApproval getClassToApproval;
	
	@Property
	private UsiRequired usiRequired;

	@Inject
	private PreferenceController preferenceController;

	@Property
	private GetNextSession getNextSession;

	@Inject
	private ICacheProvider cacheProvider;

	@SetupRender
	public void setupRender() {
		contact = portalService.getContact();
		
		getClassToApproval = new GetClassToApproval(contact, portalService);
		attendanceCalculater =  new CalculateAttendancePercent(contact.getStudent(), cacheProvider);
		getEnrolmentToSurvey = new GetEnrolmentToSurvey(contact.getStudent());
		getApplicationToStudy = new GetApplicationToStudy(contact.getStudent());
		getSessionToMarkRoll = new GetSessionToMarkRoll(contact.getTutor());
		getClassToMarkOutcomes = new GetClassToMarkOutcomes(contact.getTutor(), preferenceController);
		paymentDuecalculater = new CalculatePaymentDue(contact);
		getNextSession = new GetNextSession(contact, portalService);
		usiRequired = new UsiRequired(contact);
	}
}

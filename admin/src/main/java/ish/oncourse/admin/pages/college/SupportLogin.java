/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.admin.pages.college;

import ish.oncourse.admin.pages.Index;
import ish.oncourse.model.College;
import ish.oncourse.model.Contact;
import ish.oncourse.model.SupportPassword;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.system.ICollegeService;
import ish.util.SecurityUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.Date;
import java.util.List;


public class SupportLogin {
	
	@Persist
	@Property
	private boolean isCompany;

	@InjectComponent
	@Property
	private Form loginForm;
	
	@Persist
	@Property
	private String companyName;
	
	@Persist
	@Property
	private String firstName;
	
	@Persist
	@Property
	private String lastName;
	
	@Persist
	@Property
	private String email;
	
	@Property
	private College college;
	
	@Persist
	@Property
	private Contact contact;

	@Persist
	@Property
	private SupportPassword supportPassword;
	
	@Inject
	private Request request;
	
	@Inject
	private ICollegeService collegeService;
	
	@Inject
	private ICayenneService cayenneService;

	@InjectPage
	private Index indexPage;
	
	private static final long TWO_MINUTES = 120000;
	private static final String PORTAL_URL_TEMPLATE = "https://%s/portal/login?oneTimePassword=%s";
	private static final String SLYLING_DISPLAY_NONE = "none";
	private static final String CONTACT_NOT_EXIST_MESSAGE = "Contact with these credentials does not exist for %s college";
	private static final String CONTACT_DUPLICATE_MESSAGE="There is more than one contact with such credentials for %s college";

	void onActivate(Long id) {
		this.college = collegeService.findById(id);
	}

	Object onPassivate() {
		return college.getId();
	}


	@SetupRender
	void setupRender() {

		
		ObjectContext context = cayenneService.newContext();

		this.college = context.localObject(college);
		
	}
	
	@AfterRender
	void afterRender() {
		contact = null;
		supportPassword = null;
	}
	

	void submitted() {
		if (contact != null) {
			ObjectContext context = cayenneService.newContext();
			
			if (contact.getSupportPassword() != null) {
				supportPassword = context.localObject(contact.getSupportPassword());								
			} else {
				
				supportPassword = context.newObject(SupportPassword.class);
				supportPassword.setContact(context.localObject(contact));
				supportPassword.setCreatedOn(new Date());
			}

			supportPassword.setModifiedOn(new Date());
			supportPassword.setPassword(SecurityUtil.generateRandomPassword(16));
			
			Date currentDate = new Date();
			Long time = currentDate.getTime();
			time = time + TWO_MINUTES;
			
			supportPassword.setExpiresOn(new Date(time));
			context.commitChanges();

			/**
			 *  we are removing all SupportPassword which have already expired 
			 */
			List<SupportPassword> oldPasswords = ObjectSelect.query(SupportPassword.class).
					where(SupportPassword.EXPIRES_ON.lte(new Date())).
					select(context);

			context.deleteObjects(oldPasswords);
			context.commitChanges();
			
		}
	}
	
	@OnEvent(component = "loginForm", value = "validate")
	void validate() {
		ObjectContext context = cayenneService.sharedContext();
				
		supportPassword = null;
		contact = null;
		loginForm.clearErrors();
		
		ObjectSelect<Contact> query = ObjectSelect.query(Contact.class).
				where(Contact.EMAIL_ADDRESS.eq(email)).
				and(Contact.COLLEGE.eq(college));
		if (isCompany) {
			query = query.and(Contact.FAMILY_NAME.eq(companyName));
		} else {
			query = query.and(Contact.GIVEN_NAME.eq(firstName));
			query = query.and(Contact.FAMILY_NAME.eq(lastName));
		}
		List<Contact> users = query.select(context);
		
		if ( users == null || users.isEmpty()) {
			loginForm.recordError(String.format(CONTACT_NOT_EXIST_MESSAGE, college.getName()));
		} else if (users.size() > 1) {
			loginForm.recordError(String.format(CONTACT_DUPLICATE_MESSAGE, college.getName()));
		} else if (users.size() == 1 ) {
			contact = users.get(0);
			submitted();
		}
	}

	public Object onException(Throwable cause){
		//redirect to index page when session was expired and persist properties got null value
		if (college == null) {
			return indexPage;
		} else {
			throw new IllegalStateException(cause);
		}
	}
	
	public String getSupportURL() {
		String url;

		url =String.format(PORTAL_URL_TEMPLATE, request.getServerName(), supportPassword.getPassword());
		
		return url;
	}
	
	public String getCompanyShow() {
		return isCompany ? StringUtils.EMPTY : SLYLING_DISPLAY_NONE;
	}

	public String getUserShow() {
		return isCompany ? SLYLING_DISPLAY_NONE : StringUtils.EMPTY;
	}

}

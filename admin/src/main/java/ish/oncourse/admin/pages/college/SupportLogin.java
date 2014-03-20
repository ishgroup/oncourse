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
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
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
			 *  we are removing all SupportPassword with expires-property lesses  or equels current time. 
			 */
			SelectQuery query = new SelectQuery(SupportPassword.class);
			query.andQualifier(ExpressionFactory.lessOrEqualExp(SupportPassword.EXPIRES_ON_PROPERTY, new Date()));

			context.deleteObjects(context.performQuery(query));
			context.commitChanges();
			
		}
	}
	
	@OnEvent(component = "loginForm", value = "validate")
	void validate() {
		
		supportPassword = null;
		contact = null;
		loginForm.clearErrors();
		
		SelectQuery query = new SelectQuery(Contact.class);

		query.andQualifier(ExpressionFactory.matchExp(Contact.EMAIL_ADDRESS_PROPERTY, email));
		if (isCompany) {
			query.andQualifier(ExpressionFactory.matchExp(Contact.FAMILY_NAME_PROPERTY, companyName));
		} else {
			query.andQualifier(ExpressionFactory.matchExp(Contact.GIVEN_NAME_PROPERTY, firstName));
			query.andQualifier(ExpressionFactory.matchExp(Contact.FAMILY_NAME_PROPERTY, lastName));
		}

		query.andQualifier(ExpressionFactory.matchExp(Contact.COLLEGE_PROPERTY, college));

		List<Contact> users = cayenneService.sharedContext().performQuery(query);
		
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

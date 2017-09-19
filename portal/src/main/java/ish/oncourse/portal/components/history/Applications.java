/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.history;

import ish.common.types.ApplicationStatus;
import ish.oncourse.model.Application;
import ish.oncourse.model.Student;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.application.IPortalApplicationService;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;

import java.util.LinkedList;
import java.util.List;

public class Applications {

	@Property
	private List<Application> applications = new LinkedList<>();

	@Property
	private Application application;

	@Inject
	private IPortalApplicationService applicationService;

	@Inject
	private ICayenneService cayenneService;
	
	@InjectComponent
	private ApplicationItem applicationItem;

	@Inject
	private IPortalService portalService;

	@Inject
	private Request request;

	@SetupRender
	public void setupRender() {
		Student student = portalService.getContact().getStudent();
		if (student != null) {
			applications = applicationService.getAllApplicationsBy(student);
		}
	}

	@OnEvent(value = "reject")
	public Object reject(Long id) {
		if(!request.isXHR()) {
			return null;
		}
		Application application = ObjectSelect.query(Application.class).and(ExpressionFactory.matchDbExp(Application.ID_PK_COLUMN, id)).selectOne(cayenneService.newContext());
		
		if (ApplicationStatus.OFFERED.equals(application.getStatus())) {
			application.setStatus(ApplicationStatus.WITHDRAWN);
			application.getObjectContext().commitChanges();
		}
		
		applicationItem.setApplication(application);
		return applicationItem;
	}

	@OnEvent(value = "enrolNow")
	public Object enrolNow(Long id) {
		if(!request.isXHR()) {
			return null;
		}
		Application application = ObjectSelect.query(Application.class).and(ExpressionFactory.matchDbExp(Application.ID_PK_COLUMN, id)).selectOne(cayenneService.sharedContext());

		JSONObject data = new JSONObject();
		if (ApplicationStatus.OFFERED.equals(application.getStatus())) {
			
			String redirectUrl = String.format("%s?student=%s", portalService.getUrlBy(application.getCourse()), application.getStudent().getContact().getUniqueCode());
			data.put("redirectUrl",redirectUrl);
			
			return new TextStreamResponse("text/json", data.toString());
		} else {
			applicationItem.setApplication(application);
			return applicationItem;
		}
	}
	
	
	
}

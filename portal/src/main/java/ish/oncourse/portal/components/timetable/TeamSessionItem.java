/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.timetable;

import ish.oncourse.model.Contact;
import ish.oncourse.model.Session;
import ish.oncourse.portal.services.attendance.ContactUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.annotations.Parameter;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.util.List;

public class TeamSessionItem extends AbstractSessionItem  {

	@Parameter
	@Property
	private List<Contact> children;
	
	@Property
	private Contact child;
	
	public List<Contact> getChildrenForSession(Session session) {
		ObjectContext context = children.get(0).getObjectContext();
		return ContactUtils.getChildrenForSession(children,context.localObject(session));
	}

}

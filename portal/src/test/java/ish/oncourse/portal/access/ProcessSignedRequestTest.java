package ish.oncourse.portal.access;

import ish.oncourse.model.Contact;
import org.apache.tapestry5.services.Request;
import org.junit.Test;

import static ish.oncourse.portal.access.ProcessSignedRequest.Case.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class ProcessSignedRequestTest {

	@Test
	public void testGetCase() {
		Request request = mock(Request.class);
		when(request.isXHR()).thenReturn(false);

		Contact currentContact = mock(Contact.class);
		when(currentContact.getId()).thenReturn(1l);
		String currentPath = "/class/1";

		//test FIRST
		assertEquals(ProcessSignedRequest.GetCase.valueOf(null, null, currentPath, currentContact, request).getCase(), FIRST);

		//test REFRESH
		Contact prevContact = mock(Contact.class);
		String prevPath = "/class/1";
		when(prevContact.getId()).thenReturn(1l);
		assertEquals(ProcessSignedRequest.GetCase.valueOf(prevPath, prevContact, currentPath, currentContact, request).getCase(), REFRESH);
		assertEquals(ProcessSignedRequest.GetCase.valueOf(prevPath, prevContact, currentPath, null, request).getCase(), REFRESH);


		//test ANOTHER
		prevContact = mock(Contact.class);
		prevPath = "/class/1";
		when(prevContact.getId()).thenReturn(2l);
		assertEquals(ProcessSignedRequest.GetCase.valueOf(prevPath, prevContact, currentPath, currentContact, request).getCase(), ANOTHER);

		prevPath = "/class/2";
		when(prevContact.getId()).thenReturn(1l);
		assertEquals(ProcessSignedRequest.GetCase.valueOf(prevPath, prevContact, currentPath, currentContact, request).getCase(), ANOTHER);

		when(prevContact.getId()).thenReturn(2l);
		assertEquals(ProcessSignedRequest.GetCase.valueOf(prevPath, prevContact, currentPath, currentContact, request).getCase(), ANOTHER);
		assertEquals(ProcessSignedRequest.GetCase.valueOf(null, prevContact, currentPath, currentContact, request).getCase(), ANOTHER);

		prevPath = "/class/2";
		when(prevContact.getId()).thenReturn(1l);
		assertEquals(ProcessSignedRequest.GetCase.valueOf(prevPath, prevContact, currentPath, null, request).getCase(), INVALID);

		when(prevContact.getId()).thenReturn(1l);
		assertEquals(ProcessSignedRequest.GetCase.valueOf(null, prevContact, currentPath, null, request).getCase(), REGULAR);
		assertEquals(ProcessSignedRequest.GetCase.valueOf(null, null, currentPath, null, request).getCase(), REGULAR);

		when(request.isXHR()).thenReturn(true);
		assertEquals(ProcessSignedRequest.GetCase.valueOf(prevPath, prevContact, currentPath, currentContact, request).getCase(), AJAX);
	}
}

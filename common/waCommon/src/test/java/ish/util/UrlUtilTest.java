/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UrlUtilTest {
	
	@Test
	public void testCreatePortalUsiLink() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date expiry = format.parse("01/01/2114");
		
		String url = UrlUtil.createPortalUsiLink("uniqueCode", expiry, "saltstring");
		
		assertTrue(url.startsWith("https://skillsoncourse.com.au/portal/usi"));
		
		assertTrue(UrlUtil.validatePortalUsiLink(url, "saltstring", format.parse("01/01/2015")));
	}
	
	@Test
	public void testValidatePortalUsiLink_Expired() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		String url = UrlUtil.createPortalUsiLink("uniqueCode", format.parse("31/12/2014"), "saltstring");

		assertFalse(UrlUtil.validatePortalUsiLink(url, "saltstring", format.parse("01/01/2015")));
	}

	@Test
	public void testValidatePortalUsiLink_InvalidSignature() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		String url = UrlUtil.createPortalUsiLink("uniqueCode", format.parse("31/12/2014"), "wrongsalt");

		assertFalse(UrlUtil.validatePortalUsiLink(url, "saltstring", format.parse("01/01/2015")));
	}

	@Test
	public void testSignUrl() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date expiry = format.parse("01/01/2114");

		String url = UrlUtil.signUrl("https://skillsoncourse.com.au/portal/survey/1531", expiry, "saltstring");

		assertTrue(url.startsWith("https://skillsoncourse.com.au/portal/survey/1531"));

		assertTrue(UrlUtil.validateSignedUrl(url, "saltstring", format.parse("01/01/2015")));
	}

	@Test
	public void testSignUrlWithParams() throws Exception{
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date expiry = format.parse("01/01/2115");

		String url = UrlUtil.signUrl("https://skillsoncourse.com.au/portal/survey/1531?param=test", expiry, "saltstring");

		assertTrue(url.startsWith("https://skillsoncourse.com.au/portal/survey/1531?param=test"));

		assertTrue(UrlUtil.validateSignedUrl(url, "saltstring", format.parse("01/01/2015")));
	}

	@Test
	public void testSignatureExpired() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		String url = UrlUtil.signUrl("https://skillsoncourse.com.au/portal/survey/1531", format.parse("31/12/2014"), "saltstring");

		assertFalse(UrlUtil.validateSignedUrl(url, "saltstring", format.parse("01/01/2015")));
	}

	@Test
	public void testInvalidSignature() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		String url = UrlUtil.signUrl("https://skillsoncourse.com.au/portal/survey/1531",format.parse("31/12/2014"), "wrongsalt");

		assertFalse(UrlUtil.validateSignedUrl(url, "saltstring", format.parse("01/01/2015")));
	}
}

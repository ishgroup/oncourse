/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UrlUtilTest {
	
	@Test
	public void testCreatePortalUsiLink() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date expiry = format.parse("01/01/2114");
		
		String url = UrlUtil.createPortalUsiLink("uniqueCode", expiry, "saltstring");
		
		Assertions.assertTrue(url.startsWith("https://www.skillsoncourse.com.au/portal/usi"));
		
		Assertions.assertTrue(UrlUtil.validatePortalUsiLink(url, "saltstring", format.parse("01/01/2015")));
	}
	
	@Test
	public void testValidatePortalUsiLink_Expired() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		String url = UrlUtil.createPortalUsiLink("uniqueCode", format.parse("31/12/2014"), "saltstring");

		Assertions.assertFalse(UrlUtil.validatePortalUsiLink(url, "saltstring", format.parse("01/01/2015")));
	}

	@Test
	public void testValidatePortalUsiLink_InvalidSignature() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		String url = UrlUtil.createPortalUsiLink("uniqueCode", format.parse("31/12/2014"), "wrongsalt");

		Assertions.assertFalse(UrlUtil.validatePortalUsiLink(url, "saltstring", format.parse("01/01/2015")));
	}

	@Test
	public void testSignUrl() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date expiry = format.parse("01/01/2114");

		String url = UrlUtil.createSignedPortalUrl("survey/1531", expiry, "saltstring");

		Assertions.assertTrue(url.startsWith("https://www.skillsoncourse.com.au/portal/survey/1531"));

		Assertions.assertTrue(UrlUtil.validateSignedPortalUrl(url, "saltstring", format.parse("01/01/2015")));
	}

	@Test
	public void testSignUrlWithParams() throws Exception{
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date expiry = format.parse("01/01/2115");

		String url = UrlUtil.createSignedPortalUrl("/survey/1531?param=test", expiry, "saltstring");

		Assertions.assertTrue(url.startsWith("https://www.skillsoncourse.com.au/portal/survey/1531?param=test"));

		Assertions.assertTrue(UrlUtil.validateSignedPortalUrl(url, "saltstring", format.parse("01/01/2015")));
	}

	@Test
	public void testSignatureExpired() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		String url = UrlUtil.createSignedPortalUrl("/survey/1531", format.parse("31/12/2014"), "saltstring");

		Assertions.assertFalse(UrlUtil.validateSignedPortalUrl(url, "saltstring", format.parse("01/01/2015")));
	}

	@Test
	public void testInvalidSignature() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		String url = UrlUtil.createSignedPortalUrl("survey/1531",format.parse("31/12/2014"), "wrongsalt");

		Assertions.assertFalse(UrlUtil.validateSignedPortalUrl(url, "saltstring", format.parse("01/01/2015")));
	}

	@Test
	public void test() throws ParseException {
		Assertions.assertTrue(UrlUtil.validateSignedPortalUrl("https://www.skillsoncourse.com.au/portal/class/5034179?contactId=cvKcLp3qQabLXhf6&valid=20160303&key=CAnB0aQQpCLqN6nJB3fEQerjjm4",
				"oJRJarnFPk9xoPVQ", new SimpleDateFormat("yyy-MM-dd").parse("2016-03-02")));

		Assertions.assertFalse(UrlUtil.validateSignedPortalUrl("https://www.skillsoncourse.com.au/portal/class/5034179?contactId=cvKcLp3qQabLXhf6&valid=20160303&key=CAnB0aQQpCLqN6nJB3fEQerjjm4",
				"oJRJarnFPk9xoPVQ", new SimpleDateFormat("yyy-MM-dd").parse("2016-03-04")));
	}
}

/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
package ish.util

import groovy.transform.CompileStatic
import ish.DatabaseSetup
import ish.TestWithDatabase
import ish.oncourse.entity.services.TagService
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.PreferenceController
import ish.oncourse.server.cayenne.Preference
import ish.oncourse.server.integration.PluginsPrefsService
import ish.oncourse.server.license.LicenseService
import ish.oncourse.server.services.ISystemUserService
import ish.persistence.CommonPreferenceController
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.Select
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.when

@CompileStatic
class UrlUtilTest {


	@Test
    void testCreatePortalUsiLink() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy")
        Date expiry = format.parse("01/01/2114")

        String url = UrlUtil.createPortalUsiLink("uniqueCode", expiry, "saltstring", "domain")

        Assertions.assertTrue(url.startsWith("https://domain.skillsoncourse.com.au/portal/usi"))

        Assertions.assertTrue(UrlUtil.validatePortalUsiLink(url, "saltstring", format.parse("01/01/2015")))
    }

	@Test
    void testValidatePortalUsiLink_Expired() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy")

        String url = UrlUtil.createPortalUsiLink("uniqueCode", format.parse("31/12/2014"), "saltstring", "domain")

        Assertions.assertFalse(UrlUtil.validatePortalUsiLink(url, "saltstring", format.parse("01/01/2015")))
    }

	@Test
    void testValidatePortalUsiLink_InvalidSignature() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy")

        String url = UrlUtil.createPortalUsiLink("uniqueCode", format.parse("31/12/2014"), "wrongsalt", "domain")

        Assertions.assertFalse(UrlUtil.validatePortalUsiLink(url, "saltstring", format.parse("01/01/2015")))
    }

	@Test
    void testSignUrl() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy")
        Date expiry = format.parse("01/01/2114")

        String url = UrlUtil.createSignedPortalUrl( "survey/1531", expiry, "saltstring", "domain")

        Assertions.assertTrue(url.startsWith("https://domain.skillsoncourse.com.au/portal/survey/1531"))

        Assertions.assertTrue(UrlUtil.validateSignedPortalUrl(url, "saltstring", format.parse("01/01/2015")))
    }

	@Test
    void testSignUrlWithParams() throws Exception{
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy")
        Date expiry = format.parse("01/01/2115")

        String url = UrlUtil.createSignedPortalUrl("/survey/1531?param=test", expiry, "saltstring", "domain")

        Assertions.assertTrue(url.startsWith("https://domain.skillsoncourse.com.au/portal/survey/1531?param=test"))

        Assertions.assertTrue(UrlUtil.validateSignedPortalUrl(url, "saltstring", format.parse("01/01/2015")))
    }

	@Test
    void testSignatureExpired() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy")

        String url = UrlUtil.createSignedPortalUrl("/survey/1531", format.parse("31/12/2014"), "saltstring", "domain")

        Assertions.assertFalse(UrlUtil.validateSignedPortalUrl(url, "saltstring", format.parse("01/01/2015")))
    }

	@Test
    void testInvalidSignature() throws Exception {
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy")

        String url = UrlUtil.createSignedPortalUrl("survey/1531",format.parse("31/12/2014"), "wrongsalt", "domain")

        Assertions.assertFalse(UrlUtil.validateSignedPortalUrl(url, "saltstring", format.parse("01/01/2015")))
    }

	@Test
    void test() throws ParseException {
		Assertions.assertTrue(UrlUtil.validateSignedPortalUrl("https://domain.skillsoncourse.com.au/portal/class/5034179?contactId=cvKcLp3qQabLXhf6&valid=20160303&key=CAnB0aQQpCLqN6nJB3fEQerjjm4",
				"oJRJarnFPk9xoPVQ", new SimpleDateFormat("yyy-MM-dd").parse("2016-03-02")))

        Assertions.assertFalse(UrlUtil.validateSignedPortalUrl("https://domain.skillsoncourse.com.au/portal/class/5034179?contactId=cvKcLp3qQabLXhf6&valid=20160303&key=CAnB0aQQpCLqN6nJB3fEQerjjm4",
				"oJRJarnFPk9xoPVQ", new SimpleDateFormat("yyy-MM-dd").parse("2016-03-04")))
    }

    private PreferenceController initPreferenceController(Preference preference) {
        ICayenneService iCayenneService = Mockito.mock(ICayenneService.class)

        DataContext dataContext = Mockito.mock(DataContext.class)
        when(iCayenneService.getNewContext()).thenReturn(dataContext)

        when(dataContext.selectFirst(any(Select))).thenReturn(preference)
        when(dataContext.localObject(any(Preference))).thenReturn(preference)

        ISystemUserService systemUserService = Mockito.mock(ISystemUserService.class)
        LicenseService licenseService = Mockito.mock(LicenseService.class)
        PluginsPrefsService pluginsService = Mockito.mock(PluginsPrefsService.class)
        TagService tagService = Mockito.mock(TagService.class)

        return new PreferenceController(iCayenneService, systemUserService, licenseService, pluginsService, tagService)
    }
}

package ish.oncourse.services.preference;

import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.ServiceTest;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;

import static ish.oncourse.services.preference.PreferenceController.ContactFiledsSet.*;
import static ish.oncourse.services.preference.PreferenceController.FieldDescriptor.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PreferenceControllerTest extends ServiceTest {

	static final String REQUIRE_CONTACT_ADDRESS_ENROLMENT = "enrolment.contact.address.required";
	static final String REQUIRE_CONTACT_SUBURB_ENROLMENT = "enrolment.contact.suburb.required";
	static final String REQUIRE_CONTACT_STATE_ENROLMENT = "enrolment.contact.state.required";
	static final String REQUIRE_CONTACT_POSTCODE_ENROLMENT = "enrolment.contact.postcode.required";
	static final String REQUIRE_CONTACT_HOME_PHONE_ENROLMENT = "enrolment.contact.homephone.required";
	static final String REQUIRE_CONTACT_BUSINESS_PHONE_ENROLMENT = "enrolment.contact.businessphone.required";
	static final String REQUIRE_CONTACT_FAX_ENROLMENT = "enrolment.contact.fax.required";
	static final String REQUIRE_CONTACT_MOBILE_ENROLMENT = "enrolment.contact.mobile.required";
	static final String REQUIRE_CONTACT_DATE_OF_BIRTH_ENROLMENT = "enrolment.contact.birth.required";

	private static final String REQUIRE_CONTACT_ADDRESS_WAITING_LIST = "waitinglist.contact.address.required";
	private static final String REQUIRE_CONTACT_SUBURB_WAITING_LIST = "waitinglist.contact.suburb.required";
	private static final String REQUIRE_CONTACT_STATE_WAITING_LIST = "waitinglist.contact.state.required";
	private static final String REQUIRE_CONTACT_POSTCODE_WAITING_LIST = "waitinglist.contact.postcode.required";
	private static final String REQUIRE_CONTACT_HOME_PHONE_WAITING_LIST = "waitinglist.contact.homephone.required";
	private static final String REQUIRE_CONTACT_BUSINESS_PHONE_WAITING_LIST = "waitinglist.contact.businessphone.required";
	private static final String REQUIRE_CONTACT_FAX_WAITING_LIST = "waitinglist.contact.fax.required";
	private static final String REQUIRE_CONTACT_MOBILE_WAITING_LIST = "waitinglist.contact.mobile.required";
	private static final String REQUIRE_CONTACT_DATE_OF_BIRTH_WAITING_LIST = "waitinglist.contact.birth.required";

	private static final String REQUIRE_CONTACT_ADDRESS_MAILING_LIST = "mailinglist.contact.address.required";
	private static final String REQUIRE_CONTACT_SUBURB_MAILING_LIST = "mailinglist.contact.suburb.required";
	private static final String REQUIRE_CONTACT_STATE_MAILING_LIST = "mailinglist.contact.state.required";
	private static final String REQUIRE_CONTACT_POSTCODE_MAILING_LIST = "mailinglist.contact.postcode.required";
	private static final String REQUIRE_CONTACT_HOME_PHONE_MAILING_LIST = "mailinglist.contact.homephone.required";
	private static final String REQUIRE_CONTACT_BUSINESS_PHONE_MAILING_LIST = "mailinglist.contact.businessphone.required";
	private static final String REQUIRE_CONTACT_FAX_MAILING_LIST = "mailinglist.contact.fax.required";
	private static final String REQUIRE_CONTACT_MOBILE_MAILING_LIST = "mailinglist.contact.mobile.required";
	private static final String REQUIRE_CONTACT_DATE_OF_BIRTH_MAILING_LIST = "mailinglist.contact.birth.required";
	
	private PreferenceController prefController;
	
	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "", ServiceTestModule.class);
		
		InputStream st = PreferenceControllerTest.class.getClassLoader().getResourceAsStream(
				"ish/oncourse/services/preference/preferenceDataSet.xml");

		FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder().build(st);
		DataSource dataSource = getDataSource("jdbc/oncourse");
		DatabaseOperation.CLEAN_INSERT.execute(new DatabaseConnection(dataSource.getConnection(), null), dataSet);
		
		ICayenneService cayenneService = getService(ICayenneService.class);
		IWebSiteService webSiteService = getService(IWebSiteService.class);
		
		prefController = new PreferenceController(cayenneService, webSiteService);
	}
	
	@Test
	public void testGetValue() throws Exception {
		boolean smsLicense = prefController.getLicenseSms();
		assertTrue("Check sms license.", smsLicense);
		String smtpHost = prefController.getEmailSMTPHost();
		assertEquals("Check smtp host.", smtpHost, "smtp.text.ish.com");
	}
	
	@Test
	public void testSetValue() throws Exception {
		
		prefController.setAvetmissPostcode("12345");
		assertEquals("Check avetmiss post code", prefController.getAvetmissPostcode(), "12345");
		
		prefController.setLicenseEmail(true);
		assertTrue("Check license email.", prefController.getLicenseEmail());
	}

	@Test
	public void testContactPrefences() throws Exception {
		assertEquals(street.getPreferenceNameBy(enrolment),REQUIRE_CONTACT_ADDRESS_ENROLMENT);
		assertEquals(suburb.getPreferenceNameBy(enrolment),REQUIRE_CONTACT_SUBURB_ENROLMENT);
		assertEquals(state.getPreferenceNameBy(enrolment),REQUIRE_CONTACT_STATE_ENROLMENT);
		assertEquals(postcode.getPreferenceNameBy(enrolment),REQUIRE_CONTACT_POSTCODE_ENROLMENT);
		assertEquals(homePhoneNumber.getPreferenceNameBy(enrolment),REQUIRE_CONTACT_HOME_PHONE_ENROLMENT);
		assertEquals(businessPhoneNumber.getPreferenceNameBy(enrolment),REQUIRE_CONTACT_BUSINESS_PHONE_ENROLMENT);
		assertEquals(faxNumber.getPreferenceNameBy(enrolment),REQUIRE_CONTACT_FAX_ENROLMENT);
		assertEquals(mobilePhoneNumber.getPreferenceNameBy(enrolment),REQUIRE_CONTACT_MOBILE_ENROLMENT);
		assertEquals(dateOfBirth.getPreferenceNameBy(enrolment),REQUIRE_CONTACT_DATE_OF_BIRTH_ENROLMENT);

		assertEquals(street.getPreferenceNameBy(mailinglist),REQUIRE_CONTACT_ADDRESS_MAILING_LIST);
		assertEquals(suburb.getPreferenceNameBy(mailinglist),REQUIRE_CONTACT_SUBURB_MAILING_LIST);
		assertEquals(state.getPreferenceNameBy(mailinglist),REQUIRE_CONTACT_STATE_MAILING_LIST);
		assertEquals(postcode.getPreferenceNameBy(mailinglist),REQUIRE_CONTACT_POSTCODE_MAILING_LIST);
		assertEquals(homePhoneNumber.getPreferenceNameBy(mailinglist),REQUIRE_CONTACT_HOME_PHONE_MAILING_LIST);
		assertEquals(businessPhoneNumber.getPreferenceNameBy(mailinglist),REQUIRE_CONTACT_BUSINESS_PHONE_MAILING_LIST);
		assertEquals(faxNumber.getPreferenceNameBy(mailinglist),REQUIRE_CONTACT_FAX_MAILING_LIST);
		assertEquals(mobilePhoneNumber.getPreferenceNameBy(mailinglist),REQUIRE_CONTACT_MOBILE_MAILING_LIST);
		assertEquals(dateOfBirth.getPreferenceNameBy(mailinglist),REQUIRE_CONTACT_DATE_OF_BIRTH_MAILING_LIST);

		assertEquals(street.getPreferenceNameBy(waitinglist),REQUIRE_CONTACT_ADDRESS_WAITING_LIST);
		assertEquals(suburb.getPreferenceNameBy(waitinglist),REQUIRE_CONTACT_SUBURB_WAITING_LIST);
		assertEquals(state.getPreferenceNameBy(waitinglist),REQUIRE_CONTACT_STATE_WAITING_LIST);
		assertEquals(postcode.getPreferenceNameBy(waitinglist),REQUIRE_CONTACT_POSTCODE_WAITING_LIST);
		assertEquals(homePhoneNumber.getPreferenceNameBy(waitinglist),REQUIRE_CONTACT_HOME_PHONE_WAITING_LIST);
		assertEquals(businessPhoneNumber.getPreferenceNameBy(waitinglist),REQUIRE_CONTACT_BUSINESS_PHONE_WAITING_LIST);
		assertEquals(faxNumber.getPreferenceNameBy(waitinglist),REQUIRE_CONTACT_FAX_WAITING_LIST);
		assertEquals(mobilePhoneNumber.getPreferenceNameBy(waitinglist),REQUIRE_CONTACT_MOBILE_WAITING_LIST);
		assertEquals(dateOfBirth.getPreferenceNameBy(waitinglist),REQUIRE_CONTACT_DATE_OF_BIRTH_WAITING_LIST);
	}


	@After
	public void tearDown() throws Exception {
		prefController = null;
	}
}

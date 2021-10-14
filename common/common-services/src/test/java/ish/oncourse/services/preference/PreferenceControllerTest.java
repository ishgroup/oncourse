package ish.oncourse.services.preference;

import ish.oncourse.services.ServiceTestModule;
import ish.oncourse.services.cache.NoopQueryCache;
import ish.oncourse.services.courseclass.ClassAge;
import ish.oncourse.services.courseclass.ClassAgeType;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import ish.oncourse.test.LoadDataSet;
import ish.oncourse.test.tapestry.ServiceTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static ish.oncourse.services.preference.PreferenceController.FieldDescriptor.*;
import static org.junit.Assert.*;

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
	

	private PreferenceController prefController;
	
	@Before
	public void setup() throws Exception {
		initTest("ish.oncourse.services", "", new NoopQueryCache(), ServiceTestModule.class);

		new LoadDataSet().dataSetFile("ish/oncourse/services/preference/preferenceDataSet.xml")
				.load(testContext.getDS());

		ICayenneService cayenneService = getService(ICayenneService.class);
		IWebSiteService webSiteService = getService(IWebSiteService.class);
		
		prefController = new PreferenceController(cayenneService, webSiteService);
	}
	
	
	@Test
	public void testSetValue() throws Exception {
		
		prefController.setAvetmissPostcode("12345");
		assertEquals("Check avetmiss post code", prefController.getAvetmissPostcode(), "12345");
		
		prefController.setValue("testKey", false, "testValue");
		assertEquals("testValue", prefController.getValue("testKey", false));

		prefController.setValue("testKey", false, "   test Value     ");
		assertEquals("test Value", prefController.getValue("testKey", false));
	}

	@Test
	public void testContactPrefences() throws Exception {
		assertEquals(street.getPreferenceNameBy(),REQUIRE_CONTACT_ADDRESS_ENROLMENT);
		assertEquals(suburb.getPreferenceNameBy(),REQUIRE_CONTACT_SUBURB_ENROLMENT);
		assertEquals(state.getPreferenceNameBy(),REQUIRE_CONTACT_STATE_ENROLMENT);
		assertEquals(postcode.getPreferenceNameBy(),REQUIRE_CONTACT_POSTCODE_ENROLMENT);
		assertEquals(homePhoneNumber.getPreferenceNameBy(),REQUIRE_CONTACT_HOME_PHONE_ENROLMENT);
		assertEquals(businessPhoneNumber.getPreferenceNameBy(),REQUIRE_CONTACT_BUSINESS_PHONE_ENROLMENT);
		assertEquals(faxNumber.getPreferenceNameBy(),REQUIRE_CONTACT_FAX_ENROLMENT);
		assertEquals(mobilePhoneNumber.getPreferenceNameBy(),REQUIRE_CONTACT_MOBILE_ENROLMENT);
		assertEquals(dateOfBirth.getPreferenceNameBy(),REQUIRE_CONTACT_DATE_OF_BIRTH_ENROLMENT);


	}

	@Test
	public void testClassAge() throws Exception {
		ClassAge hideClassOnWebsiteAge = prefController.getHideClassOnWebsiteAge();
		assertNotNull(hideClassOnWebsiteAge);
		assertEquals(0, hideClassOnWebsiteAge.getDays());
		assertEquals(ClassAgeType.beforeClassEnds, hideClassOnWebsiteAge.getType());


		ClassAge stopWebEnrolmentsAge = prefController.getStopWebEnrolmentsAge();
		assertNotNull(stopWebEnrolmentsAge);
		assertEquals(0, stopWebEnrolmentsAge.getDays());
		assertEquals(ClassAgeType.beforeClassEnds, stopWebEnrolmentsAge.getType());


		prefController.setHideClassOnWebsiteAge(ClassAge.valueOf(1, ClassAgeType.afterClassEnds));
		hideClassOnWebsiteAge = prefController.getHideClassOnWebsiteAge();
		assertNotNull(hideClassOnWebsiteAge);
		assertEquals(1, hideClassOnWebsiteAge.getDays());
		assertEquals(ClassAgeType.afterClassEnds, hideClassOnWebsiteAge.getType());


		prefController.setStopWebEnrolmentsAge(ClassAge.valueOf(2, ClassAgeType.beforeClassStarts));
		stopWebEnrolmentsAge = prefController.getStopWebEnrolmentsAge();
		assertNotNull(stopWebEnrolmentsAge);
		assertEquals(2, stopWebEnrolmentsAge.getDays());
		assertEquals(ClassAgeType.beforeClassStarts, stopWebEnrolmentsAge.getType());
	}



	@After
	public void tearDown() throws Exception {
		prefController = null;
	}
}

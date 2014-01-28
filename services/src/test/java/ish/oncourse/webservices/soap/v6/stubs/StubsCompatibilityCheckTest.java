package ish.oncourse.webservices.soap.v6.stubs;

import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.util.*;
import ish.oncourse.webservices.v6.stubs.replication.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import javax.xml.bind.annotation.XmlElement;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Stub compatibility check for V6 stubs.
 * This test should care of stub health till V6 stubs will be supported.
 * Test stubsPropertyMap definition should not be updated at all, because only if new fields will be added into stub test execution result will be OK.
 * @author vdavidovich
 */
public class StubsCompatibilityCheckTest extends ServiceTest {
	private static final String REPLICATION_STUB_FIELD_NAME = "replicationStub";
	private static final String GENERIC_ATTENDANCE_OR_BINARY_DATA_OR_BINARY_INFO_METHOD_NAME = "genericAttendanceOrBinaryDataOrBinaryInfo";
	private static final String ATTENDANCE_OR_BINARY_DATA_OR_BINARY_INFO_METHOD_NAME = "attendanceOrBinaryDataOrBinaryInfo";
	/**
	 * Map with defined stub's fields paramethers.
	 */
	private Map<String,List<ReplicationStubFieldParameter>> stubsPropertyMap = new HashMap<>();

	private static String getStubName(Class<? extends GenericReplicationStub> clazz) {
		return getName(clazz);
	}

	private static String getName(@SuppressWarnings("rawtypes") Class clazz) {
		int index = clazz.getName().lastIndexOf(".") + 1;
		return clazz.getName().substring(index);
	}

	@Override
	public void cleanup() throws Exception {}

	@Before
	public void setupDataSet() throws Exception {
		final List<ReplicationStubFieldParameter> attendanceParamethers = fillDefaultReplicationStubFields();
		attendanceParamethers.add(new ReplicationStubFieldParameter("attendanceType", Integer.class));
		attendanceParamethers.add(new ReplicationStubFieldParameter("markerId", Long.class));
		attendanceParamethers.add(new ReplicationStubFieldParameter("sessionId", Long.class));
		attendanceParamethers.add(new ReplicationStubFieldParameter("studentId", Long.class));
		stubsPropertyMap.put(getStubName(AttendanceStub.class), attendanceParamethers);
		final List<ReplicationStubFieldParameter> binaryDataParamethers = fillDefaultReplicationStubFields();
		binaryDataParamethers.add(new ReplicationStubFieldParameter("binaryInfoId", Long.class));
		binaryDataParamethers.add(new ReplicationStubFieldParameter("content", byte[].class));
		stubsPropertyMap.put(getStubName(BinaryDataStub.class), binaryDataParamethers);
		final List<ReplicationStubFieldParameter> binaryInfoParamethers = fillDefaultReplicationStubFields();
		binaryInfoParamethers.add(new ReplicationStubFieldParameter("byteSize", Long.class));
		binaryInfoParamethers.add(new ReplicationStubFieldParameter("webVisible", Integer.class));
		binaryInfoParamethers.add(new ReplicationStubFieldParameter("mimeType", String.class));
		binaryInfoParamethers.add(new ReplicationStubFieldParameter("name", String.class));
		binaryInfoParamethers.add(new ReplicationStubFieldParameter("pixelHeight", Integer.class));
		binaryInfoParamethers.add(new ReplicationStubFieldParameter("pixelWidth", Integer.class));
		binaryInfoParamethers.add(new ReplicationStubFieldParameter("referenceNumber", Integer.class));
		binaryInfoParamethers.add(new ReplicationStubFieldParameter("fileUUID", String.class));
		binaryInfoParamethers.add(new ReplicationStubFieldParameter("thumbnail", byte[].class));
		stubsPropertyMap.put(getStubName(BinaryInfoStub.class), binaryInfoParamethers);
		final List<ReplicationStubFieldParameter> concessionTypeParamethers = fillDefaultReplicationStubFields();
		concessionTypeParamethers.add(new ReplicationStubFieldParameter("credentialExpiryDays", Integer.class));
		concessionTypeParamethers.add(new ReplicationStubFieldParameter("hasConcessionNumber", Boolean.class));
		concessionTypeParamethers.add(new ReplicationStubFieldParameter("hasExpiryDate", Boolean.class));
		concessionTypeParamethers.add(new ReplicationStubFieldParameter("isConcession", Boolean.class));
		concessionTypeParamethers.add(new ReplicationStubFieldParameter("isEnabled", Boolean.class));
		concessionTypeParamethers.add(new ReplicationStubFieldParameter("name", String.class));
		concessionTypeParamethers.add(new ReplicationStubFieldParameter("requiresCredentialCheck", Boolean.class));
		stubsPropertyMap.put(getStubName(ConcessionTypeStub.class), concessionTypeParamethers);
		final List<ReplicationStubFieldParameter> contactParamethers = fillDefaultReplicationStubFields();
		contactParamethers.add(new ReplicationStubFieldParameter("businessPhoneNumber", String.class));
		contactParamethers.add(new ReplicationStubFieldParameter("cookieHash", String.class));
		contactParamethers.add(new ReplicationStubFieldParameter("countryId", Long.class));
		contactParamethers.add(new ReplicationStubFieldParameter("dateOfBirth", Date.class));
		contactParamethers.add(new ReplicationStubFieldParameter("emailAddress", String.class));
		contactParamethers.add(new ReplicationStubFieldParameter("familyName", String.class));
		contactParamethers.add(new ReplicationStubFieldParameter("faxNumber", String.class));
		contactParamethers.add(new ReplicationStubFieldParameter("givenName", String.class));
		contactParamethers.add(new ReplicationStubFieldParameter("homePhoneNumber", String.class));
		contactParamethers.add(new ReplicationStubFieldParameter("company", Boolean.class));
		contactParamethers.add(new ReplicationStubFieldParameter("male", Boolean.class));
		contactParamethers.add(new ReplicationStubFieldParameter("marketingViaEmailAllowed", Boolean.class));
		contactParamethers.add(new ReplicationStubFieldParameter("marketingViaPostAllowed", Boolean.class));
		contactParamethers.add(new ReplicationStubFieldParameter("marketingViaSMSAllowed", Boolean.class));
		contactParamethers.add(new ReplicationStubFieldParameter("mobilePhoneNumber", String.class));
		contactParamethers.add(new ReplicationStubFieldParameter("password", String.class));
		contactParamethers.add(new ReplicationStubFieldParameter("passwordHash", String.class));
		contactParamethers.add(new ReplicationStubFieldParameter("postcode", String.class));
		contactParamethers.add(new ReplicationStubFieldParameter("state", String.class));
		contactParamethers.add(new ReplicationStubFieldParameter("street", String.class));
		contactParamethers.add(new ReplicationStubFieldParameter("suburb", String.class));
		contactParamethers.add(new ReplicationStubFieldParameter("taxFileNumber", String.class));
		contactParamethers.add(new ReplicationStubFieldParameter("uniqueCode", String.class));
		contactParamethers.add(new ReplicationStubFieldParameter("studentId", Long.class));
		contactParamethers.add(new ReplicationStubFieldParameter("tutorId", Long.class));
		stubsPropertyMap.put(getStubName(ContactStub.class), contactParamethers);

		List<ReplicationStubFieldParameter> courseClassParameters = fillDefaultReplicationStubFields();
		courseClassParameters.add(new ReplicationStubFieldParameter("cancelled", Boolean.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("code", String.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("countOfSessions", Integer.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("deliveryMode", Integer.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("detail", String.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("detailTextile", String.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("endDate", Date.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("feeExGst", BigDecimal.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("feeGst", BigDecimal.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("webVisible", Boolean.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("materials", String.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("materialsTextile", String.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("maximumPlaces", Integer.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("minimumPlaces", Integer.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("minutesPerSession", Integer.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("sessionDetail", String.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("sessionDetailTextile", String.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("startDate", Date.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("startingMinutePerSession", Integer.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("timeZone", String.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("courseId", Long.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("roomId", Long.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("distantLearningCourse", Boolean.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("maximumDays", Integer.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("expectedHours", BigDecimal.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("feeHelpClass", Boolean.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("attendanceType", Integer.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("reportingPeriod", Integer.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("censusDate", Date.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("fullTimeLoad", String.class));
		stubsPropertyMap.put(getStubName(CourseClassStub.class), courseClassParameters);

		final List<ReplicationStubFieldParameter> courseParamethers = fillDefaultReplicationStubFields();
		courseParamethers.add(new ReplicationStubFieldParameter("allowWaitingList", Boolean.class));
		courseParamethers.add(new ReplicationStubFieldParameter("code", String.class));
		courseParamethers.add(new ReplicationStubFieldParameter("detail", String.class));
		courseParamethers.add(new ReplicationStubFieldParameter("detailTextile", String.class));
		courseParamethers.add(new ReplicationStubFieldParameter("fieldOfEducation", String.class));
		courseParamethers.add(new ReplicationStubFieldParameter("sufficientForQualification", Boolean.class));
		courseParamethers.add(new ReplicationStubFieldParameter("vetCourse", "VETCourse",Boolean.class));
		courseParamethers.add(new ReplicationStubFieldParameter("webVisible", Boolean.class));
		courseParamethers.add(new ReplicationStubFieldParameter("name", String.class));
		courseParamethers.add(new ReplicationStubFieldParameter("nominalHours", BigDecimal.class));
		courseParamethers.add(new ReplicationStubFieldParameter("searchText", String.class));
		courseParamethers.add(new ReplicationStubFieldParameter("qualificationId", Long.class));
		stubsPropertyMap.put(getStubName(CourseStub.class), courseParamethers);
		final List<ReplicationStubFieldParameter> discountParameters = fillDefaultReplicationStubFields();
		discountParameters.add(new ReplicationStubFieldParameter("code", String.class));
		discountParameters.add(new ReplicationStubFieldParameter("detail", String.class));
		discountParameters.add(new ReplicationStubFieldParameter("combinationType", Boolean.class));
		discountParameters.add(new ReplicationStubFieldParameter("discountAmount", BigDecimal.class));
		discountParameters.add(new ReplicationStubFieldParameter("discountRate", BigDecimal.class));
		discountParameters.add(new ReplicationStubFieldParameter("maximumDiscount", BigDecimal.class));
		discountParameters.add(new ReplicationStubFieldParameter("minimumDiscount", BigDecimal.class));
		discountParameters.add(new ReplicationStubFieldParameter("name", String.class));
		discountParameters.add(new ReplicationStubFieldParameter("roundingMode", Integer.class));
		discountParameters.add(new ReplicationStubFieldParameter("studentAge", Integer.class));
		discountParameters.add(new ReplicationStubFieldParameter("studentAgeOperator", String.class));
		discountParameters.add(new ReplicationStubFieldParameter("studentEnrolledWithinDays", Integer.class));
		discountParameters.add(new ReplicationStubFieldParameter("studentPostcodes", String.class));
		discountParameters.add(new ReplicationStubFieldParameter("validFrom", Date.class));
		discountParameters.add(new ReplicationStubFieldParameter("validTo", Date.class));
		discountParameters.add(new ReplicationStubFieldParameter("discountType", Integer.class));
		discountParameters.add(new ReplicationStubFieldParameter("hideOnWeb", Boolean.class));
		discountParameters.add(new ReplicationStubFieldParameter("availableOnWeb", Boolean.class));
		stubsPropertyMap.put(getStubName(DiscountStub.class), discountParameters);

		List<ReplicationStubFieldParameter> enrolmentParameters = fillDefaultReplicationStubFields();
		enrolmentParameters.add(new ReplicationStubFieldParameter("reasonForStudy", Integer.class));
		enrolmentParameters.add(new ReplicationStubFieldParameter("status", String.class));
		enrolmentParameters.add(new ReplicationStubFieldParameter("source", String.class));
		enrolmentParameters.add(new ReplicationStubFieldParameter("studentId", Long.class));
		enrolmentParameters.add(new ReplicationStubFieldParameter("courseClassId", Long.class));
		enrolmentParameters.add(new ReplicationStubFieldParameter("invoiceLineId", Long.class));
		enrolmentParameters.add(new ReplicationStubFieldParameter("feeHelpStatus", Integer.class));
		enrolmentParameters.add(new ReplicationStubFieldParameter("creditOfferedValue", String.class));
		enrolmentParameters.add(new ReplicationStubFieldParameter("creditProvider", String.class));
		enrolmentParameters.add(new ReplicationStubFieldParameter("creditUsedValue", String.class));
		enrolmentParameters.add(new ReplicationStubFieldParameter("creditType", Integer.class));
		enrolmentParameters.add(new ReplicationStubFieldParameter("creditFoeId", String.class));
		enrolmentParameters.add(new ReplicationStubFieldParameter("creditLevel", Integer.class));
		enrolmentParameters.add(new ReplicationStubFieldParameter("creditProviderType", Integer.class));
		enrolmentParameters.add(new ReplicationStubFieldParameter("feeStatus", Integer.class));
		enrolmentParameters.add(new ReplicationStubFieldParameter("creditTotal", Integer.class));
		stubsPropertyMap.put(getStubName(EnrolmentStub.class), enrolmentParameters);

		final List<ReplicationStubFieldParameter> invoiceLineDiscountParamethers = fillDefaultReplicationStubFields();
		invoiceLineDiscountParamethers.add(new ReplicationStubFieldParameter("discountId", Long.class));
		invoiceLineDiscountParamethers.add(new ReplicationStubFieldParameter("invoiceLineId", Long.class));
		stubsPropertyMap.put(getStubName(InvoiceLineDiscountStub.class), invoiceLineDiscountParamethers);
		final List<ReplicationStubFieldParameter> invoiceLineParamethers = fillDefaultReplicationStubFields();
		invoiceLineParamethers.add(new ReplicationStubFieldParameter("description", String.class));
		invoiceLineParamethers.add(new ReplicationStubFieldParameter("discountEachExTax", BigDecimal.class));
		invoiceLineParamethers.add(new ReplicationStubFieldParameter("priceEachExTax", BigDecimal.class));
		invoiceLineParamethers.add(new ReplicationStubFieldParameter("quantity", BigDecimal.class));
		invoiceLineParamethers.add(new ReplicationStubFieldParameter("taxEach", BigDecimal.class));
		invoiceLineParamethers.add(new ReplicationStubFieldParameter("title", String.class));
		invoiceLineParamethers.add(new ReplicationStubFieldParameter("unit", String.class));
		invoiceLineParamethers.add(new ReplicationStubFieldParameter("enrolmentId", Long.class));
		invoiceLineParamethers.add(new ReplicationStubFieldParameter("invoiceId", Long.class));
		invoiceLineParamethers.add(new ReplicationStubFieldParameter("sortOrder", Integer.class, false));
		stubsPropertyMap.put(getStubName(InvoiceLineStub.class), invoiceLineParamethers);
		final List<ReplicationStubFieldParameter> invoiceParameters = fillDefaultReplicationStubFields();
		invoiceParameters.add(new ReplicationStubFieldParameter("amountOwing", BigDecimal.class));
		invoiceParameters.add(new ReplicationStubFieldParameter("billToAddress", String.class));
		invoiceParameters.add(new ReplicationStubFieldParameter("customerPO", String.class));
		invoiceParameters.add(new ReplicationStubFieldParameter("customerReference", String.class));
		invoiceParameters.add(new ReplicationStubFieldParameter("dateDue", Date.class));
		invoiceParameters.add(new ReplicationStubFieldParameter("description", String.class));
		invoiceParameters.add(new ReplicationStubFieldParameter("invoiceDate", Date.class));
		invoiceParameters.add(new ReplicationStubFieldParameter("invoiceNumber", Long.class));
		invoiceParameters.add(new ReplicationStubFieldParameter("publicNotes", String.class));
		invoiceParameters.add(new ReplicationStubFieldParameter("shippingAddress", String.class));
		invoiceParameters.add(new ReplicationStubFieldParameter("source", String.class));
		invoiceParameters.add(new ReplicationStubFieldParameter("status", String.class));
		invoiceParameters.add(new ReplicationStubFieldParameter("totalExGst", BigDecimal.class));
		invoiceParameters.add(new ReplicationStubFieldParameter("totalGst", BigDecimal.class));
		invoiceParameters.add(new ReplicationStubFieldParameter("contactId", Long.class));
		invoiceParameters.add(new ReplicationStubFieldParameter("corporatePassId", Long.class));
		stubsPropertyMap.put(getStubName(InvoiceStub.class), invoiceParameters);
		final List<ReplicationStubFieldParameter> messagePersonParamethers = fillDefaultReplicationStubFields();
		messagePersonParamethers.add(new ReplicationStubFieldParameter("destinationAddress", String.class));
		messagePersonParamethers.add(new ReplicationStubFieldParameter("numberOfAttempts", Integer.class));
		messagePersonParamethers.add(new ReplicationStubFieldParameter("response", String.class));
		messagePersonParamethers.add(new ReplicationStubFieldParameter("status", Integer.class));
		messagePersonParamethers.add(new ReplicationStubFieldParameter("timeOfDelivery", Date.class));
		messagePersonParamethers.add(new ReplicationStubFieldParameter("type", Integer.class));
		messagePersonParamethers.add(new ReplicationStubFieldParameter("contactId", Long.class));
		messagePersonParamethers.add(new ReplicationStubFieldParameter("messageId", Long.class));
		messagePersonParamethers.add(new ReplicationStubFieldParameter("studentId", Long.class));
		messagePersonParamethers.add(new ReplicationStubFieldParameter("tutorId", Long.class));
		stubsPropertyMap.put(getStubName(MessagePersonStub.class), messagePersonParamethers);
		final List<ReplicationStubFieldParameter> outcomeParamethers = fillDefaultReplicationStubFields();
		outcomeParamethers.add(new ReplicationStubFieldParameter("deliveryMode", Integer.class));
		outcomeParamethers.add(new ReplicationStubFieldParameter("fundingSource", Integer.class));
		outcomeParamethers.add(new ReplicationStubFieldParameter("reportableHours", BigDecimal.class));
		outcomeParamethers.add(new ReplicationStubFieldParameter("hoursAttended", Integer.class));
		outcomeParamethers.add(new ReplicationStubFieldParameter("enrolmentId", Long.class));
		outcomeParamethers.add(new ReplicationStubFieldParameter("moduleId", Long.class));
		outcomeParamethers.add(new ReplicationStubFieldParameter("startDate", Date.class));
		outcomeParamethers.add(new ReplicationStubFieldParameter("endDate", Date.class));
		outcomeParamethers.add(new ReplicationStubFieldParameter("status", Integer.class));
		stubsPropertyMap.put(getStubName(OutcomeStub.class), outcomeParamethers);
		final List<ReplicationStubFieldParameter> paymentInLineParamethers = fillDefaultReplicationStubFields();
		paymentInLineParamethers.add(new ReplicationStubFieldParameter("amount", BigDecimal.class));
		paymentInLineParamethers.add(new ReplicationStubFieldParameter("invoiceId", Long.class));
		paymentInLineParamethers.add(new ReplicationStubFieldParameter("paymentInId", Long.class));
		stubsPropertyMap.put(getStubName(PaymentInLineStub.class), paymentInLineParamethers);
		final List<ReplicationStubFieldParameter> paymentInParamethers = fillDefaultReplicationStubFields();
		paymentInParamethers.add(new ReplicationStubFieldParameter("amount", BigDecimal.class));
		paymentInParamethers.add(new ReplicationStubFieldParameter("contactId", Long.class));
		paymentInParamethers.add(new ReplicationStubFieldParameter("source", String.class));
		paymentInParamethers.add(new ReplicationStubFieldParameter("status", Integer.class));
		paymentInParamethers.add(new ReplicationStubFieldParameter("type", Integer.class));
		paymentInParamethers.add(new ReplicationStubFieldParameter("privateNotes", String.class));
		paymentInParamethers.add(new ReplicationStubFieldParameter("gatewayReference", String.class));
		paymentInParamethers.add(new ReplicationStubFieldParameter("gatewayResponse", String.class));
		paymentInParamethers.add(new ReplicationStubFieldParameter("sessionId", String.class));
		paymentInParamethers.add(new ReplicationStubFieldParameter("creditCardExpiry", String.class));
		paymentInParamethers.add(new ReplicationStubFieldParameter("creditCardName", String.class));
		paymentInParamethers.add(new ReplicationStubFieldParameter("creditCardNumber", String.class));
		paymentInParamethers.add(new ReplicationStubFieldParameter("creditCardType", String.class));
		paymentInParamethers.add(new ReplicationStubFieldParameter("dateBanked", Date.class));
		stubsPropertyMap.put(getStubName(PaymentInStub.class), paymentInParamethers);
		final List<ReplicationStubFieldParameter> paymentOutParamethers = fillDefaultReplicationStubFields();
		paymentOutParamethers.add(new ReplicationStubFieldParameter("amount", BigDecimal.class));
		paymentOutParamethers.add(new ReplicationStubFieldParameter("contactId", Long.class));
		paymentOutParamethers.add(new ReplicationStubFieldParameter("paymentInTxnReference", String.class));
		paymentOutParamethers.add(new ReplicationStubFieldParameter("type", Integer.class));
		paymentOutParamethers.add(new ReplicationStubFieldParameter("source", String.class));
		paymentOutParamethers.add(new ReplicationStubFieldParameter("status", Integer.class));
		paymentOutParamethers.add(new ReplicationStubFieldParameter("dateBanked", Date.class));
		paymentOutParamethers.add(new ReplicationStubFieldParameter("datePaid", Date.class));
		stubsPropertyMap.put(getStubName(PaymentOutStub.class), paymentOutParamethers);
		final List<ReplicationStubFieldParameter> preferenceParamethers = fillDefaultReplicationStubFields();
		preferenceParamethers.add(new ReplicationStubFieldParameter("name", String.class));
		preferenceParamethers.add(new ReplicationStubFieldParameter("value", byte[].class));
		preferenceParamethers.add(new ReplicationStubFieldParameter("valueString", String.class));
		stubsPropertyMap.put(getStubName(PreferenceStub.class), preferenceParamethers);
		final List<ReplicationStubFieldParameter> roomParamethers = fillDefaultReplicationStubFields();
		roomParamethers.add(new ReplicationStubFieldParameter("capacity", Integer.class));
		roomParamethers.add(new ReplicationStubFieldParameter("directions", String.class));
		roomParamethers.add(new ReplicationStubFieldParameter("directionsTextile", String.class));
		roomParamethers.add(new ReplicationStubFieldParameter("facilities", String.class));
		roomParamethers.add(new ReplicationStubFieldParameter("facilitiesTextile", String.class));
		roomParamethers.add(new ReplicationStubFieldParameter("name", String.class));
		roomParamethers.add(new ReplicationStubFieldParameter("siteId", Long.class));
		stubsPropertyMap.put(getStubName(RoomStub.class), roomParamethers);
		final List<ReplicationStubFieldParameter> siteParameters = fillDefaultReplicationStubFields();
		siteParameters.add(new ReplicationStubFieldParameter("drivingDirections", String.class));
		siteParameters.add(new ReplicationStubFieldParameter("webVisible", Boolean.class));
		siteParameters.add(new ReplicationStubFieldParameter("latitude", BigDecimal.class));
		siteParameters.add(new ReplicationStubFieldParameter("longitude", BigDecimal.class));
		siteParameters.add(new ReplicationStubFieldParameter("name", String.class));
		siteParameters.add(new ReplicationStubFieldParameter("postcode", String.class));
		siteParameters.add(new ReplicationStubFieldParameter("publicTransportDirections", String.class));
		siteParameters.add(new ReplicationStubFieldParameter("specialInstructions", String.class));
		siteParameters.add(new ReplicationStubFieldParameter("state", String.class));
		siteParameters.add(new ReplicationStubFieldParameter("street", String.class));
		siteParameters.add(new ReplicationStubFieldParameter("suburb", String.class));
		siteParameters.add(new ReplicationStubFieldParameter("timeZone", String.class));
		siteParameters.add(new ReplicationStubFieldParameter("countryId", Long.class));
		siteParameters.add(new ReplicationStubFieldParameter("virtual", Boolean.class));
		stubsPropertyMap.put(getStubName(SiteStub.class), siteParameters);
		final List<ReplicationStubFieldParameter> studentConcessionParamethers = fillDefaultReplicationStubFields();
		studentConcessionParamethers.add(new ReplicationStubFieldParameter("authorisationExpiresOn", Date.class));
		studentConcessionParamethers.add(new ReplicationStubFieldParameter("authorisedOn", Date.class));
		studentConcessionParamethers.add(new ReplicationStubFieldParameter("concessionNumber", String.class));
		studentConcessionParamethers.add(new ReplicationStubFieldParameter("expiresOn", Date.class));
		studentConcessionParamethers.add(new ReplicationStubFieldParameter("timeZone", String.class));
		studentConcessionParamethers.add(new ReplicationStubFieldParameter("concessionTypeId", Long.class));
		studentConcessionParamethers.add(new ReplicationStubFieldParameter("studentId", Long.class));
		stubsPropertyMap.put(getStubName(StudentConcessionStub.class), studentConcessionParamethers);

		List<ReplicationStubFieldParameter> studentParameters = fillDefaultReplicationStubFields();
		studentParameters.add(new ReplicationStubFieldParameter("concessionType", Integer.class));
		studentParameters.add(new ReplicationStubFieldParameter("disabilityType", Integer.class));
		studentParameters.add(new ReplicationStubFieldParameter("englishProficiency", Integer.class));
		studentParameters.add(new ReplicationStubFieldParameter("highestSchoolLevel", Integer.class));
		studentParameters.add(new ReplicationStubFieldParameter("indigenousStatus", Integer.class));
		studentParameters.add(new ReplicationStubFieldParameter("overseasClient", Boolean.class));
		studentParameters.add(new ReplicationStubFieldParameter("stillAtSchool", Boolean.class));
		studentParameters.add(new ReplicationStubFieldParameter("priorEducationCode", Integer.class));
		studentParameters.add(new ReplicationStubFieldParameter("labourForceType", Integer.class));
		studentParameters.add(new ReplicationStubFieldParameter("yearSchoolCompleted", Integer.class));
		studentParameters.add(new ReplicationStubFieldParameter("contactId", Long.class));
		studentParameters.add(new ReplicationStubFieldParameter("languageId", Long.class));
		studentParameters.add(new ReplicationStubFieldParameter("countryOfBirthId", Long.class));
		studentParameters.add(new ReplicationStubFieldParameter("languageHomeId", Long.class));
		studentParameters.add(new ReplicationStubFieldParameter("chessn", String.class));
		studentParameters.add(new ReplicationStubFieldParameter("feeHelpEligible", Boolean.class));
		studentParameters.add(new ReplicationStubFieldParameter("specialNeedsAssistance", String.class));
		studentParameters.add(new ReplicationStubFieldParameter("citizenship", Integer.class));
		studentParameters.add(new ReplicationStubFieldParameter("disabilitySupportRequested", Boolean.class));
		stubsPropertyMap.put(getStubName(StudentStub.class), studentParameters);

		final List<ReplicationStubFieldParameter> systemUserParamethers = fillDefaultReplicationStubFields();
		systemUserParamethers.add(new ReplicationStubFieldParameter("editCMS", Boolean.class));
		systemUserParamethers.add(new ReplicationStubFieldParameter("editTara", Boolean.class));
		systemUserParamethers.add(new ReplicationStubFieldParameter("email", String.class));
		systemUserParamethers.add(new ReplicationStubFieldParameter("firstName", String.class));
		systemUserParamethers.add(new ReplicationStubFieldParameter("surname", String.class));
		systemUserParamethers.add(new ReplicationStubFieldParameter("password", String.class));
		systemUserParamethers.add(new ReplicationStubFieldParameter("login", String.class));
		systemUserParamethers.add(new ReplicationStubFieldParameter("lastLoginIP", String.class));
		systemUserParamethers.add(new ReplicationStubFieldParameter("lastLoginOn", Date.class));
		systemUserParamethers.add(new ReplicationStubFieldParameter("isActive", Boolean.class));
		systemUserParamethers.add(new ReplicationStubFieldParameter("isAdmin", Boolean.class));
		systemUserParamethers.add(new ReplicationStubFieldParameter("defaultAdministrationCentreId", Long.class));
		stubsPropertyMap.put(getStubName(SystemUserStub.class), systemUserParamethers);
		final List<ReplicationStubFieldParameter> tagRelationParamethers = fillDefaultReplicationStubFields();
		tagRelationParamethers.add(new ReplicationStubFieldParameter("entityAngelId", Long.class));
		tagRelationParamethers.add(new ReplicationStubFieldParameter("entityName", String.class));
		tagRelationParamethers.add(new ReplicationStubFieldParameter("entityWillowId", Long.class));
		tagRelationParamethers.add(new ReplicationStubFieldParameter("tagId", Long.class));
		stubsPropertyMap.put(getStubName(TagRelationStub.class), tagRelationParamethers);
		final List<ReplicationStubFieldParameter> courseClassTutorParamethers = fillDefaultReplicationStubFields();
		courseClassTutorParamethers.add(new ReplicationStubFieldParameter("courseClassId", Long.class));
		courseClassTutorParamethers.add(new ReplicationStubFieldParameter("tutorId", Long.class));
		courseClassTutorParamethers.add(new ReplicationStubFieldParameter("confirmedOn", Date.class));
		courseClassTutorParamethers.add(new ReplicationStubFieldParameter("inPublicity", Boolean.class));
		stubsPropertyMap.put(getStubName(CourseClassTutorStub.class), courseClassTutorParamethers);
		final List<ReplicationStubFieldParameter> tutorParamethers = fillDefaultReplicationStubFields();
		tutorParamethers.add(new ReplicationStubFieldParameter("finishDate", Date.class));
		tutorParamethers.add(new ReplicationStubFieldParameter("resume", String.class));
		tutorParamethers.add(new ReplicationStubFieldParameter("resumeTextile", String.class));
		tutorParamethers.add(new ReplicationStubFieldParameter("startDate", Date.class));
		tutorParamethers.add(new ReplicationStubFieldParameter("contactId", Long.class));
		stubsPropertyMap.put(getStubName(TutorStub.class), tutorParamethers);
		final List<ReplicationStubFieldParameter> waitingListParamethers = fillDefaultReplicationStubFields();
		waitingListParamethers.add(new ReplicationStubFieldParameter("detail", String.class));
		waitingListParamethers.add(new ReplicationStubFieldParameter("studentCount", Integer.class));
		waitingListParamethers.add(new ReplicationStubFieldParameter("courseId", Long.class));
		waitingListParamethers.add(new ReplicationStubFieldParameter("studentId", Long.class));
		stubsPropertyMap.put(getStubName(WaitingListStub.class), waitingListParamethers);
		final List<ReplicationStubFieldParameter> certificateParamethers = fillDefaultReplicationStubFields();
		certificateParamethers.add(new ReplicationStubFieldParameter("certificateNumber", Long.class));
		certificateParamethers.add(new ReplicationStubFieldParameter("endDate", Date.class));
		certificateParamethers.add(new ReplicationStubFieldParameter("fundingSource", Integer.class));
		certificateParamethers.add(new ReplicationStubFieldParameter("qualification", Boolean.class));
		certificateParamethers.add(new ReplicationStubFieldParameter("printedWhen", Date.class));
		certificateParamethers.add(new ReplicationStubFieldParameter("privateNotes", String.class));
		certificateParamethers.add(new ReplicationStubFieldParameter("publicNotes", String.class));
		certificateParamethers.add(new ReplicationStubFieldParameter("qualificationId", Long.class));
		certificateParamethers.add(new ReplicationStubFieldParameter("revokedWhen", Date.class));
		certificateParamethers.add(new ReplicationStubFieldParameter("studentFirstName", String.class));
		certificateParamethers.add(new ReplicationStubFieldParameter("studentLastName", String.class));
		certificateParamethers.add(new ReplicationStubFieldParameter("studentId", Long.class));
		stubsPropertyMap.put(getStubName(CertificateStub.class), certificateParamethers);
		final List<ReplicationStubFieldParameter> certificateOutcomeParamethers = fillDefaultReplicationStubFields();
		certificateOutcomeParamethers.add(new ReplicationStubFieldParameter("certificateId", Long.class));
		certificateOutcomeParamethers.add(new ReplicationStubFieldParameter("outcomeId", Long.class));
		stubsPropertyMap.put(getStubName(CertificateOutcomeStub.class), certificateOutcomeParamethers);
		final List<ReplicationStubFieldParameter> deletedStubParamethers = fillDefaultReplicationStubFields();
		stubsPropertyMap.put(getStubName(DeletedStub.class), deletedStubParamethers);
		final List<ReplicationStubFieldParameter> hollowStubParamethers = fillDefaultReplicationStubFields();
		stubsPropertyMap.put(getStubName(HollowStub.class), hollowStubParamethers);
		final List<ReplicationStubFieldParameter> queuedStatisticParamethers = fillDefaultReplicationStubFields();
		queuedStatisticParamethers.add(new ReplicationStubFieldParameter("stackedTransactionsCount", Long.class));
		queuedStatisticParamethers.add(new ReplicationStubFieldParameter("stackedCount", Long.class));
		queuedStatisticParamethers.add(new ReplicationStubFieldParameter("stackedEntityIdentifier", String.class));
		queuedStatisticParamethers.add(new ReplicationStubFieldParameter("receivedTimestamp", Date.class));
		queuedStatisticParamethers.add(new ReplicationStubFieldParameter("cleanupStub", Boolean.class));
		stubsPropertyMap.put(getStubName(QueuedStatisticStub.class), queuedStatisticParamethers);
		final List<ReplicationStubFieldParameter> voucherProductCourseParamethers = fillDefaultReplicationStubFields();
		voucherProductCourseParamethers.add(new ReplicationStubFieldParameter("courseId", Long.class));
		voucherProductCourseParamethers.add(new ReplicationStubFieldParameter("voucherProductId", Long.class));
		stubsPropertyMap.put(getStubName(VoucherProductCourseStub.class), voucherProductCourseParamethers);
		final List<ReplicationStubFieldParameter> productItemParamethers = fillProductItemStubFields(fillDefaultReplicationStubFields());
		stubsPropertyMap.put(getStubName(ProductItemStub.class), productItemParamethers);
		final List<ReplicationStubFieldParameter> productParamethers = fillProductStubFields(fillDefaultReplicationStubFields());
		stubsPropertyMap.put(getStubName(ProductStub.class), productParamethers);
		final List<ReplicationStubFieldParameter> voucherParamethers = fillProductItemStubFields(fillDefaultReplicationStubFields());
		voucherParamethers.add(new ReplicationStubFieldParameter("expiryDate", Date.class));
		voucherParamethers.add(new ReplicationStubFieldParameter("key", String.class));
		voucherParamethers.add(new ReplicationStubFieldParameter("contactId", Long.class));
		voucherParamethers.add(new ReplicationStubFieldParameter("code", String.class));
		voucherParamethers.add(new ReplicationStubFieldParameter("redeemedCoursesCount", Integer.class));
		voucherParamethers.add(new ReplicationStubFieldParameter("redemptionValue", BigDecimal.class));
		voucherParamethers.add(new ReplicationStubFieldParameter("status", Integer.class));
		voucherParamethers.add(new ReplicationStubFieldParameter("source", String.class));
		stubsPropertyMap.put(getStubName(VoucherStub.class), voucherParamethers);
		final List<ReplicationStubFieldParameter> voucherProductParamethers = fillProductStubFields(fillDefaultReplicationStubFields());
		voucherProductParamethers.add(new ReplicationStubFieldParameter("expiryDays", Integer.class));
		voucherProductParamethers.add(new ReplicationStubFieldParameter("expiryType", Integer.class));
		voucherProductParamethers.add(new ReplicationStubFieldParameter("value", BigDecimal.class));
		voucherProductParamethers.add(new ReplicationStubFieldParameter("maxCoursesRedemption", Integer.class));
		stubsPropertyMap.put(getStubName(VoucherProductStub.class), voucherProductParamethers);
		final List<ReplicationStubFieldParameter> membershipParamethers = fillProductItemStubFields(fillDefaultReplicationStubFields());
		membershipParamethers.add(new ReplicationStubFieldParameter("expiryDate", Date.class));
		membershipParamethers.add(new ReplicationStubFieldParameter("contactId", Long.class));
		stubsPropertyMap.put(getStubName(MembershipStub.class), membershipParamethers);
		final List<ReplicationStubFieldParameter> voucherPaymentInParamethers = fillDefaultReplicationStubFields();
		voucherPaymentInParamethers.add(new ReplicationStubFieldParameter("paymentInId", Long.class));
		voucherPaymentInParamethers.add(new ReplicationStubFieldParameter("voucherId", Long.class));
		voucherPaymentInParamethers.add(new ReplicationStubFieldParameter("enrolmentsCount", Integer.class));
		voucherPaymentInParamethers.add(new ReplicationStubFieldParameter("status", Integer.class));
		voucherPaymentInParamethers.add(new ReplicationStubFieldParameter("invoiceLineId", Long.class));
		stubsPropertyMap.put(getStubName(VoucherPaymentInStub.class), voucherPaymentInParamethers);
		final List<ReplicationStubFieldParameter> sessionModuleParamethers = fillDefaultReplicationStubFields();
		sessionModuleParamethers.add(new ReplicationStubFieldParameter("moduleId", Long.class));
		sessionModuleParamethers.add(new ReplicationStubFieldParameter("sessionId", Long.class));
		stubsPropertyMap.put(getStubName(SessionModuleStub.class), sessionModuleParamethers);
		final List<ReplicationStubFieldParameter> membershipProductParamethers = fillProductStubFields(fillDefaultReplicationStubFields());
		membershipProductParamethers.add(new ReplicationStubFieldParameter("expiryDays", Integer.class));
		membershipProductParamethers.add(new ReplicationStubFieldParameter("expiryType", Integer.class));
		stubsPropertyMap.put(getStubName(MembershipProductStub.class), membershipProductParamethers);
		final List<ReplicationStubFieldParameter> surveyParamethers = fillDefaultReplicationStubFields();
		surveyParamethers.add(new ReplicationStubFieldParameter("comment", String.class));
		surveyParamethers.add(new ReplicationStubFieldParameter("courseScore", Integer.class));
		surveyParamethers.add(new ReplicationStubFieldParameter("tutorScore", Integer.class));
		surveyParamethers.add(new ReplicationStubFieldParameter("venueScore", Integer.class));
		surveyParamethers.add(new ReplicationStubFieldParameter("uniqueCode", String.class));
		surveyParamethers.add(new ReplicationStubFieldParameter("enrolmentId", Long.class));
		stubsPropertyMap.put(getStubName(SurveyStub.class), surveyParamethers);

		List<ReplicationStubFieldParameter> entityRelationParameters = fillDefaultReplicationStubFields();
		entityRelationParameters.add(new ReplicationStubFieldParameter("fromEntityIdentifier", Integer.class));
		entityRelationParameters.add(new ReplicationStubFieldParameter("toEntityIdentifier", Integer.class));
		entityRelationParameters.add(new ReplicationStubFieldParameter("fromEntityAngelId", Long.class));
		entityRelationParameters.add(new ReplicationStubFieldParameter("toEntityAngelId", Long.class));
		entityRelationParameters.add(new ReplicationStubFieldParameter("fromEntityWillowId", Long.class));
		entityRelationParameters.add(new ReplicationStubFieldParameter("toEntityWillowId", Long.class));
		stubsPropertyMap.put(getStubName(EntityRelationStub.class), entityRelationParameters);

		List<ReplicationStubFieldParameter> corporatePassParameters = fillDefaultReplicationStubFields();
		corporatePassParameters.add(new ReplicationStubFieldParameter("expiryDate", Date.class));
		corporatePassParameters.add(new ReplicationStubFieldParameter("invoiceEmail", String.class));
		corporatePassParameters.add(new ReplicationStubFieldParameter("password", String.class));
		corporatePassParameters.add(new ReplicationStubFieldParameter("contactId", Long.class));
		stubsPropertyMap.put(getStubName(CorporatePassStub.class), corporatePassParameters);

		List<ReplicationStubFieldParameter> corporatePassCourseClassParameters = fillDefaultReplicationStubFields();
		corporatePassCourseClassParameters.add(new ReplicationStubFieldParameter("corporatePassId", Long.class));
		corporatePassCourseClassParameters.add(new ReplicationStubFieldParameter("courseClassId", Long.class));
		stubsPropertyMap.put(getStubName(CorporatePassCourseClassStub.class), corporatePassCourseClassParameters);

		//TODO: add new stubs here
		final List<ReplicationStubFieldParameter> replicationStubParamethers = fillDefaultReplicationStubFields();
		stubsPropertyMap.put(getStubName(ReplicationStub.class), replicationStubParamethers);
		final List<ReplicationStubFieldParameter> replicationResultParamethers = new ArrayList<>();
		replicationResultParamethers.add(new ReplicationStubFieldParameter("replicatedRecord", true, ReplicatedRecord.class));
		replicationResultParamethers.add(new ReplicationStubFieldParameter("genericReplicatedRecord", "replicatedRecord", List.class, true, null,
				ReplicatedRecord.class));
		stubsPropertyMap.put(getName(ReplicationResult.class), replicationResultParamethers);
		final List<ReplicationStubFieldParameter> replicationRecordsParamethers = new ArrayList<>();
		replicationRecordsParamethers.add(new ReplicationStubFieldParameter("groups", true, TransactionGroup.class));
		replicationRecordsParamethers.add(new ReplicationStubFieldParameter("genericGroups", "groups", List.class, true, null,
				TransactionGroup.class));
		stubsPropertyMap.put(getName(ReplicationRecords.class), replicationRecordsParamethers);
		final List<ReplicationStubFieldParameter> replicatedRecordParamethers = new ArrayList<>();
		replicatedRecordParamethers.add(new ReplicationStubFieldParameter("status", Status.class));
		replicatedRecordParamethers.add(new ReplicationStubFieldParameter("message", String.class));
		replicatedRecordParamethers.add(new ReplicationStubFieldParameter("stub", HollowStub.class));
		replicatedRecordParamethers.add(new ReplicationStubFieldParameter("failedStatus", boolean.class, "status"));
		replicatedRecordParamethers.add(new ReplicationStubFieldParameter("successStatus", boolean.class, "status"));
		stubsPropertyMap.put(getName(ReplicatedRecord.class), replicatedRecordParamethers);
		final List<ReplicationStubFieldParameter> parameterEntryParamethers = new ArrayList<>();
		parameterEntryParamethers.add(new ReplicationStubFieldParameter("name", String.class));
		parameterEntryParamethers.add(new ReplicationStubFieldParameter("value", String.class));
		stubsPropertyMap.put(getName(ParameterEntry.class), parameterEntryParamethers);
		final List<ReplicationStubFieldParameter> parametersMapParamethers = new ArrayList<>();
		parametersMapParamethers.add(new ReplicationStubFieldParameter("entry", true, false, ParameterEntry.class));
		parametersMapParamethers.add(new ReplicationStubFieldParameter("genericEntry", "entry", List.class, false, null, ParameterEntry.class));
		stubsPropertyMap.put(getName(ParametersMap.class), parametersMapParamethers);
		final List<ReplicationStubFieldParameter> instructionStubParamethers = new ArrayList<>();
		instructionStubParamethers.add(new ReplicationStubFieldParameter("id", Long.class));
		instructionStubParamethers.add(new ReplicationStubFieldParameter("message", String.class));
		instructionStubParamethers.add(new ReplicationStubFieldParameter("parameters", ParametersMap.class, false));
		stubsPropertyMap.put(getName(InstructionStub.class), instructionStubParamethers);
		final List<ReplicationStubFieldParameter> instructionsResponseParamethers = new ArrayList<>();
		instructionsResponseParamethers.add(new ReplicationStubFieldParameter("_return", "return", List.class, false, null, InstructionStub.class));
		stubsPropertyMap.put(getName(GetInstructionsResponse.class), instructionsResponseParamethers);
		final List<ReplicationStubFieldParameter> faultReasonParamethers = new ArrayList<>();
		faultReasonParamethers.add(new ReplicationStubFieldParameter("detailMessage", String.class, false));
		faultReasonParamethers.add(new ReplicationStubFieldParameter("faultCode", Integer.class, false));
		stubsPropertyMap.put(getName(FaultReason.class), faultReasonParamethers);
		final List<ReplicationStubFieldParameter> transactionGroupParamethers = new ArrayList<>();
		transactionGroupParamethers.add(new ReplicationStubFieldParameter("transactionKeys", List.class));
		final ReplicationStubFieldParameter replicationStub = new ReplicationStubFieldParameter(
				REPLICATION_STUB_FIELD_NAME, List.class, false);
		@SuppressWarnings("rawtypes")
		final List<Class> replicationStubAvailableClasses = new ArrayList<>();
		replicationStubAvailableClasses.add(ReplicationStub.class);

		replicationStub.getAvailableClasses().addAll(replicationStubAvailableClasses);
		transactionGroupParamethers.add(replicationStub);
		final ReplicationStubFieldParameter genericAttendanceOrBinaryDataOrBinaryInfo = new ReplicationStubFieldParameter(
				GENERIC_ATTENDANCE_OR_BINARY_DATA_OR_BINARY_INFO_METHOD_NAME, REPLICATION_STUB_FIELD_NAME, List.class, false, null);
		genericAttendanceOrBinaryDataOrBinaryInfo.getAvailableClasses().addAll(replicationStubAvailableClasses);
		transactionGroupParamethers.add(genericAttendanceOrBinaryDataOrBinaryInfo);
		stubsPropertyMap.put(getName(TransactionGroup.class), transactionGroupParamethers);
	}

	@Test
	public void testEntityRelationStub() {
		GenericReplicationStub stub = new EntityRelationStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testCorporatePassStub() {
		GenericReplicationStub stub = new CorporatePassStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testCorporatePassCourseClassStub() {
		GenericReplicationStub stub = new CorporatePassCourseClassStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testSurveyStub() {
		final GenericReplicationStub stub = new SurveyStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testVoucherPaymentInStub() {
		final GenericReplicationStub stub = new VoucherPaymentInStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testSessionModuleStub() {
		final GenericReplicationStub stub = new SessionModuleStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testMembershipStub() {
		final GenericReplicationStub stub = new MembershipStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testMembershipProductStub() {
		final GenericReplicationStub stub = new MembershipProductStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testProductStub() {
		final GenericReplicationStub stub = new ProductStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testProductItemStub() {
		final GenericReplicationStub stub = new ProductItemStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testVoucherProductCourseStub() {
		final GenericReplicationStub stub = new VoucherProductCourseStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testVoucherStub() {
		final GenericReplicationStub stub = new VoucherStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testVoucherProductStub() {
		final GenericReplicationStub stub = new VoucherProductStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testAttendanceStub() {
		final GenericReplicationStub stub = new AttendanceStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testBinaryDataStub() {
		final GenericReplicationStub stub = new BinaryDataStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testBinaryInfoStub() {
		final GenericReplicationStub stub = new BinaryInfoStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testConcessionTypeStub() {
		final GenericReplicationStub stub = new ConcessionTypeStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testContactStub() {
		final GenericReplicationStub stub = new ContactStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testCourseClassStub() {
		final GenericReplicationStub stub = new CourseClassStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testCourseStub() {
		final GenericReplicationStub stub = new CourseStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testDiscountStub() {
		final GenericReplicationStub stub = new DiscountStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testEnrolmentStub() {
		final GenericReplicationStub stub = new EnrolmentStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testInvoiceLineDiscountStub() {
		final GenericReplicationStub stub = new InvoiceLineDiscountStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testInvoiceLineStub() {
		final GenericReplicationStub stub = new InvoiceLineStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testInvoiceStub() {
		final GenericReplicationStub stub = new InvoiceStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testMessagePersonStub() {
		final GenericReplicationStub stub = new MessagePersonStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testOutcomeStub() {
		final GenericReplicationStub stub = new OutcomeStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testPaymentInLineStub() {
		final GenericReplicationStub stub = new PaymentInLineStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testPaymentInStub() {
		final GenericReplicationStub stub = new PaymentInStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testPaymentOutStub() {
		final GenericReplicationStub stub = new PaymentOutStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testPreferenceStub() {
		final GenericReplicationStub stub = new PreferenceStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testRoomStub() {
		final GenericReplicationStub stub = new RoomStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testSiteStub() {
		final GenericReplicationStub stub = new SiteStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testStudentConcessionStub() {
		final GenericReplicationStub stub = new StudentConcessionStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testStudentStub() {
		final GenericReplicationStub stub = new StudentStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testSystemUserStub() {
		final GenericReplicationStub stub = new SystemUserStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testTagRelationStub() {
		final GenericReplicationStub stub = new TagRelationStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testCourseClassTutorStub() {
		final GenericReplicationStub stub = new CourseClassTutorStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testTutorStub() {
		final GenericReplicationStub stub = new TutorStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testWaitingListStub() {
		final GenericReplicationStub stub = new WaitingListStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testCertificateStub() {
		final GenericReplicationStub stub = new CertificateStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testCertificateOutcomeStub() {
		final GenericReplicationStub stub = new CertificateOutcomeStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testTransactionGroup() {
		final GenericTransactionGroup group = new TransactionGroup();
		testCollectorDefinition(group, stubsPropertyMap.get(getName(group.getClass())));
	}

	@Test
	public void testFaultReason() {
		final FaultReason reason = new FaultReason();
		testStubDefinition(reason, stubsPropertyMap.get(getName(reason.getClass())));
	}

	@Test
	public void testDeletedStub() {
		final GenericReplicationStub stub = new DeletedStub();
		assertTrue("Delete stub shoul implement GenericDeletedStub interface.", (stub instanceof GenericDeletedStub));
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testHollowStub() {
		final GenericReplicationStub stub = new HollowStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testGetInstructionsResponse() {
		final GetInstructionsResponse response = new GetInstructionsResponse();
		testCollectorDefinition(response, stubsPropertyMap.get(getName(response.getClass())));
	}

	@Test
	public void testInstructionStub() {
		final GenericInstructionStub stub = new InstructionStub();
		testStubDefinition(stub, stubsPropertyMap.get(getName(stub.getClass())));
	}

	@Test
	public void testQueuedStatisticStub() {
		final GenericReplicationStub stub = new QueuedStatisticStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testParametersMap() {
		final GenericParametersMap result = new ParametersMap();
		testCollectorDefinition(result, stubsPropertyMap.get(getName(result.getClass())));
	}

	@Test
	public void testParameterEntry() {
		final GenericParameterEntry result = new ParameterEntry();
		testStubDefinition(result, stubsPropertyMap.get(getName(result.getClass())));
	}

	@Test
	public void testReplicatedRecord() {
		final GenericReplicatedRecord result = new ReplicatedRecord();
		testCollectorDefinition(result, stubsPropertyMap.get(getName(result.getClass())));
	}

	@Test
	public void testReplicationRecords() {
		final GenericReplicationRecords result = new ReplicationRecords();
		testCollectorDefinition(result, stubsPropertyMap.get(getName(result.getClass())));
	}

	@Test
	public void testReplicationResult() {
		final GenericReplicationResult result = new ReplicationResult();
		testCollectorDefinition(result, stubsPropertyMap.get(getName(result.getClass())));
	}

	@Test
	public void testReplicationStub() {
		final GenericReplicationStub stub = new ReplicationStub() {};
		assertTrue("ReplicationStub should have abstract modifier", Modifier.isAbstract(ReplicationStub.class.getModifiers()));
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(ReplicationStub.class)));
	}

	private List<ReplicationStubFieldParameter> fillDefaultReplicationStubFields() {
		final List<ReplicationStubFieldParameter> paramethers = new ArrayList<>();
		paramethers.add(new ReplicationStubFieldParameter("angelId", Long.class));
		paramethers.add(new ReplicationStubFieldParameter("willowId", Long.class));
		paramethers.add(new ReplicationStubFieldParameter("entityIdentifier", String.class));
		paramethers.add(new ReplicationStubFieldParameter("created", Date.class));
		paramethers.add(new ReplicationStubFieldParameter("modified", Date.class));
		return paramethers;
	}

	private List<ReplicationStubFieldParameter> fillProductItemStubFields(final List<ReplicationStubFieldParameter> preparedParamethers) {
		preparedParamethers.add(new ReplicationStubFieldParameter("type", Integer.class));
		preparedParamethers.add(new ReplicationStubFieldParameter("invoiceLineId", Long.class));
		preparedParamethers.add(new ReplicationStubFieldParameter("productId", Long.class));
		preparedParamethers.add(new ReplicationStubFieldParameter("status", Integer.class));
		return preparedParamethers;
	}

	private List<ReplicationStubFieldParameter> fillProductStubFields(final List<ReplicationStubFieldParameter> preparedParamethers) {
		preparedParamethers.add(new ReplicationStubFieldParameter("sku", String.class));
		preparedParamethers.add(new ReplicationStubFieldParameter("description", String.class));
		preparedParamethers.add(new ReplicationStubFieldParameter("incomeAccountId", Long.class));
		preparedParamethers.add(new ReplicationStubFieldParameter("isOnSale", Boolean.class));
		preparedParamethers.add(new ReplicationStubFieldParameter("isWebVisible", Boolean.class));
		preparedParamethers.add(new ReplicationStubFieldParameter("name", String.class));
		preparedParamethers.add(new ReplicationStubFieldParameter("notes", String.class));
		preparedParamethers.add(new ReplicationStubFieldParameter("priceExTax", BigDecimal.class));
		preparedParamethers.add(new ReplicationStubFieldParameter("taxAdjustment", BigDecimal.class));
		preparedParamethers.add(new ReplicationStubFieldParameter("taxAmount", BigDecimal.class));
		preparedParamethers.add(new ReplicationStubFieldParameter("taxId", Long.class));
		preparedParamethers.add(new ReplicationStubFieldParameter("type", Integer.class));
		return preparedParamethers;
	}

	private ReplicationStubFieldParameter getReplicationParametherForReplacement(final List<ReplicationStubFieldParameter> paramethers,
																				 final String replacementName) {
		for (final ReplicationStubFieldParameter paramether : paramethers) {
			if (!paramether.isEmptyReplacement() && paramether.getReplacementName().equalsIgnoreCase(replacementName)) {
				paramether.setFound(true);
				return paramether;
			}
		}
		return null;
	}

	private ReplicationStubFieldParameter getReplicationParametherForDependendFields(final List<ReplicationStubFieldParameter> paramethers,
																					 final String descriptorName) {
		for (final ReplicationStubFieldParameter paramether : paramethers) {
			if (StringUtils.trimToNull(paramether.getDependentFieldName()) != null && paramether.getName().equals(descriptorName)) {
				assertNotNull(String.format("Dependent field with name %s not exist for paramather %s ", paramether.getDependentFieldName(),
						paramether.getName()), paramether.getDependentToField());
				return paramether;
			}
		}
		return null;
	}

	private void fillFieldDependency(final List<ReplicationStubFieldParameter> paramethers, final Object stub) {
		for (final PropertyDescriptor descriptor : PropertyUtils.getPropertyDescriptors(stub)) {
			if ("class".equals(descriptor.getName())) {
				continue;
			}
			Field field = ReflectionUtils.findField(stub.getClass(), descriptor.getName());
			if (field == null) {
				ReplicationStubFieldParameter paramether = getReplicationParametherForReplacement(paramethers, descriptor.getName());
				if (paramether != null) {
					field = ReflectionUtils.findField(stub.getClass(), paramether.getName());
					assertNotNull(String.format("No field could be loaded for descriptor with name %s for stub %s with alias %s", descriptor.getName(),
							stub.getClass().getName(), paramether.getName()), field);
				}
			}
			if (field != null) {
				for (final ReplicationStubFieldParameter paramether : paramethers) {
					//fill the dependent fields if required
					if (StringUtils.trimToNull(paramether.getDependentFieldName()) != null && paramether.getDependentToField() == null
							&& field.getName().equals(paramether.getDependentFieldName())) {
						paramether.setDependentToField(field);
					}
				}
			}
		}
	}

	/**
	 * Method which check current container definition for backward compatibility.
	 * @param stub - container for test.
	 * @param paramethers defined for this stub.
	 */
	private void testCollectorDefinition(final Object stub, final List<ReplicationStubFieldParameter> paramethers) {
		assertNotNull(String.format("No paramether defined for stub %s", stub.getClass().getSimpleName()), paramethers);
		final PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(stub);
		fillFieldDependency(paramethers, stub);
		for (final PropertyDescriptor descriptor : descriptors) {
			//we should skipp class
			// and attendanceOrBinaryDataOrBinaryInfo because in V6 this property replaced with replicationStub
			// but method should exists to have backward compatibility with  V4 and V5
			if ("class".equals(descriptor.getName()) || ATTENDANCE_OR_BINARY_DATA_OR_BINARY_INFO_METHOD_NAME.equals(descriptor.getName())) {
				continue;
			}
			Field field = ReflectionUtils.findField(stub.getClass(), descriptor.getName());
			boolean useReplacement = false;
			if (field == null) {
				ReplicationStubFieldParameter paramether = getReplicationParametherForReplacement(paramethers, descriptor.getName());
				if (paramether != null) {
					useReplacement = true;
					field = ReflectionUtils.findField(stub.getClass(), paramether.getName());
					assertNotNull(String.format("No field could be loaded for descriptor with name %s for stub %s with alias %s", descriptor.getName(),
							stub.getClass().getName(), paramether.getName()), field);
				} else if (descriptor.getName().startsWith("generic")) {
					paramether = getReplicationParametherForReplacement(paramethers, descriptor.getName()
						.replaceFirst(GENERIC_ATTENDANCE_OR_BINARY_DATA_OR_BINARY_INFO_METHOD_NAME, REPLICATION_STUB_FIELD_NAME).replaceFirst("generic", StringUtils.EMPTY));
					if (paramether != null) {
						field = ReflectionUtils.findField(stub.getClass(), paramether.getReplacementName());
						assertNotNull(String.format("No field could be loaded for generic descriptor with name %s for stub %s with alias %s",
								descriptor.getName(), stub.getClass().getName(), paramether.getName()), field);
					}
				} else {
					paramether = getReplicationParametherForDependendFields(paramethers, descriptor.getName());
					if (paramether != null) {
						field = paramether.getDependentToField();
					}
				}
			}
			assertNotNull(String.format("No field could be loaded for descriptor with name %s for stub %s possible alias used", descriptor.getName(),
					stub.getClass().getName()), field);
			final XmlElement elementAnotation = field.getAnnotation(XmlElement.class);
			final boolean requiredField = elementAnotation != null && elementAnotation.required();
			boolean founded = false;
			for (final ReplicationStubFieldParameter paramether : paramethers) {
				if (descriptor.getName().equals(useReplacement? paramether.getReplacementName() : paramether.getName())) {
					founded = true;
					if (descriptor.getName().startsWith("generic")) {
						useReplacement = true;
					}
					assertFalse(String.format("Not used alias defined for paramether with name %s and alias %s", paramether.getName(),
							paramether.getReplacementName()), !useReplacement && !paramether.isEmptyReplacement());
					paramether.setFound(founded);
					//we should check this descriptor
					Method readMethod = descriptor.getReadMethod();
					if (readMethod == null) {
						if (paramether.getType().equals(Boolean.class)) {
							String readMethodName = "is" + WordUtils.capitalize(descriptor.getName());
							readMethod = ReflectionUtils.findMethod(stub.getClass(), readMethodName);
						}
					}
					assertNotNull(String.format("There are no getter for %s field found on %s stub", descriptor.getName(), stub.getClass().getName()), readMethod);
					assertEquals(String.format("test property - %s return type incompatible with defined type %s", descriptor.getName(),
							paramether.getType()), paramether.getType(), readMethod.getReturnType());
					if (paramether.isListWithDefinedParamethers()) {
						ParameterizedType entityType = (ParameterizedType) field.getGenericType();
						@SuppressWarnings("rawtypes")
						Class clazz = (Class) entityType.getActualTypeArguments()[0];
						assertEquals(String.format("Incorrect definition of generics for %s paramether. ", paramether.getName()),
								paramether.getParameterizedType(), clazz);
					}
					if (paramether.isList()) {
						assertEquals(String.format("test property - %s should have no setter", descriptor.getName()), null,
								descriptor.getWriteMethod());
					} else {
						Method writeMethod = descriptor.getWriteMethod();
						boolean withParam = true;
						if (writeMethod == null) {
							if (paramether.getDependentToField() != null) {
								withParam = false;
								String writeMethodName = "set" + WordUtils.capitalize(descriptor.getName());
								writeMethod = ReflectionUtils.findMethod(stub.getClass(), writeMethodName);
							}
						}
						if (writeMethod != null) {
							if (withParam) {
								assertEquals(String.format("test property - %s should have one paramether for setter", descriptor.getName()), 1,
										writeMethod.getParameterTypes().length);
							} else {
								assertEquals(String.format("test property - %s should have no paramether for setter", descriptor.getName()), 0,
										writeMethod.getParameterTypes().length);
							}
						}
					}
					assertTrue(String.format("Method modifiers should be public for property %s", descriptor.getName()),
							(readMethod.getModifiers() == Modifier.PUBLIC || readMethod.getModifiers() == 17 ||
									readMethod.getModifiers() == 1025/*public final*/ || readMethod.getModifiers() == 4161/*public abstract with override*/));
					assertTrue(String.format("Required anotation should be equal to defined for property %s", descriptor.getName()),
							paramether.isRequerdedField() == requiredField);
				}
			}
			if (requiredField) {
				assertTrue(String.format("Required field descriptor not defined in property map %s", descriptor.getName()), founded);
			} else {
				assertTrue(String.format("Advise field descriptor not defined in property map %s", descriptor.getName()), founded);
			}
		}
		for (final ReplicationStubFieldParameter paramether : paramethers) {
			assertTrue(String.format("Stub %s have not defined field %s", stub.getClass().getName(), paramether.getName()), paramether.isFound());
			if (StringUtils.trimToNull(paramether.getDependentFieldName()) != null) {
				assertNotNull(String.format("Dependent field with name %s should be loaded.", paramether.getDependentFieldName()),
						paramether.getDependentToField());
			}
		}
	}

	private void testReplicationStubDefinition(final GenericReplicationStub stub, final List<ReplicationStubFieldParameter> paramethers) {
		testStubDefinition(stub, paramethers);
	}

	/**
	 * Method which check current stub definition for backward compatibility.
	 * @param stub for test.
	 * @param paramethers defined for this stub.
	 */
	private void testStubDefinition(final Object stub, final List<ReplicationStubFieldParameter> paramethers) {
		assertNotNull(String.format("No paramether defined for stub %s", stub.getClass().getSimpleName()), paramethers);
		final PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(stub);
		for (final PropertyDescriptor descriptor : descriptors) {
			if ("class".equals(descriptor.getName())) {
				continue;
			}
			Field field = ReflectionUtils.findField(stub.getClass(), descriptor.getName());
			boolean useReplacement = false;
			if (field == null) {
				ReplicationStubFieldParameter paramether = getReplicationParametherForReplacement(paramethers, descriptor.getName());
				if (paramether != null) {
					useReplacement = true;
					field = ReflectionUtils.findField(stub.getClass(), paramether.getName());
					assertNotNull(String.format("No field could be loaded for descriptor with name %s for stub %s with alias %s", descriptor.getName(),
							stub.getClass().getName(), paramether.getName()), field);
				}
			}
			assertNotNull(String.format("No field could be loaded for descriptor with name %s for stub %s possible alias used", descriptor.getName(),
					stub.getClass().getName()),field);
			final XmlElement elementAnotation = field.getAnnotation(XmlElement.class);
			final boolean requiredField = elementAnotation != null && elementAnotation.required();
			boolean founded = false;
			for (final ReplicationStubFieldParameter paramether : paramethers) {
				if (descriptor.getName().equals(useReplacement? paramether.getReplacementName() : paramether.getName())) {
					founded = true;
					assertFalse(String.format("Not used alias defined for paramether with name %s and alias %s", paramether.getName(),
							paramether.getReplacementName()), !useReplacement && !paramether.isEmptyReplacement());
					paramether.setFound(founded);
					//we should check this descriptor
					Method readMethod = descriptor.getReadMethod();
					if (readMethod == null) {
						if (paramether.getType().equals(Boolean.class)) {
							String readMethodName = "is" + WordUtils.capitalize(descriptor.getName());
							readMethod = ReflectionUtils.findMethod(stub.getClass(), readMethodName);
						}
					}
					assertNotNull(String.format("There are no getter for %s field found on %s stub", descriptor.getName(), stub.getClass().getName()), readMethod);
					assertEquals(String.format("test property - %s return type incompatible with defined type %s", descriptor.getName(),
							paramether.getType()), paramether.getType(), readMethod.getReturnType());
					assertEquals(String.format("test property - %s should have one paramether for setter", descriptor.getName()), 1,
							descriptor.getWriteMethod().getParameterTypes().length);
					assertEquals(String.format("test property - %s paramether type for setter incompatible with defined type %s",
							descriptor.getName(), paramether.getType()), paramether.getType(), descriptor.getWriteMethod().getParameterTypes()[0]);
					assertEquals(String.format("Both method modifiers should be equal for property %s", descriptor.getName()),
							readMethod.getModifiers(), descriptor.getWriteMethod().getModifiers());
					assertTrue(String.format("Method modifiers should be public for property %s", descriptor.getName()),
							readMethod.getModifiers() == Modifier.PUBLIC);
					assertTrue(String.format("Required anotation should be equal to defined for property %s", descriptor.getName()),
							paramether.isRequerdedField() == requiredField);
				}
			}
			if (requiredField) {
				assertTrue(String.format("Required field descriptor not defined in property map %s", descriptor.getName()), founded);
			} else {
				assertTrue(String.format("Advise field descriptor not defined in property map %s", descriptor.getName()), founded);
			}
		}
		for (final ReplicationStubFieldParameter paramether : paramethers) {
			assertTrue(String.format("Stub %s have not defined field %s", stub.getClass().getName(), paramether.getName()), paramether.isFound());
		}
	}

	/**
	 * Stub fields paramether object.
	 * Used to define field property.
	 * @author vdavidovich
	 */
	private class ReplicationStubFieldParameter {
		/**
		 * Stub field name.
		 */
		private String name;
		/**
		 * Stub alias name.
		 */
		private String replacementName;
		/**
		 * Stub field type.
		 */
		@SuppressWarnings("rawtypes")
		private Class type;
		/**
		 * Is required field flag.
		 */
		private boolean requerdedField;
		/**
		 * Internal property used to catch defined but not founded fields.
		 */
		private boolean found;
		/**
		 * Class field name (used to catch some transient methods associated with some field)
		 */
		private String dependentFieldName;
		/**
		 * Internal property used to hold class field property loaded by previous parameter.
		 */
		private Field dependentToField;
		/**
		 * Parameterized class type used when type = List.
		 */
		@SuppressWarnings("rawtypes")
		private Class parameterizedType;
		/**
		 * Property define the list of class types available to store in this field. 
		 */
		@SuppressWarnings("rawtypes")
		private List<Class> availableClasses;

		private ReplicationStubFieldParameter(String name, boolean listUsed, @SuppressWarnings("rawtypes") Class parameterizedType) {
			this(name, null, List.class, true, null, parameterizedType);
			if (!listUsed) {
				throw new IllegalArgumentException();
			}
		}

		private ReplicationStubFieldParameter(String name, @SuppressWarnings("rawtypes") Class type) {
			this(name, null, type);
		}

		private ReplicationStubFieldParameter(String name, boolean listUsed, boolean requerdedField,
											  @SuppressWarnings("rawtypes") Class parameterizedType) {
			this(name, null, List.class, requerdedField, null, parameterizedType);
			if (!listUsed) {
				throw new IllegalArgumentException();
			}
		}

		private ReplicationStubFieldParameter(String name, @SuppressWarnings("rawtypes") Class type, boolean requerdedField) {
			this(name, null, type, requerdedField, null);
		}

		private ReplicationStubFieldParameter(String name, String replacementName, @SuppressWarnings("rawtypes") Class type) {
			this(name, replacementName, type, true, null);
		}

		private ReplicationStubFieldParameter(String name, @SuppressWarnings("rawtypes") Class type, String dependentFieldName) {
			this(name, null, type, true, dependentFieldName);
		}

		private ReplicationStubFieldParameter(String name, String replacementName, @SuppressWarnings("rawtypes") Class type, boolean requerdedField,
											  String dependentFieldName) {
			this(name, replacementName, type, requerdedField, dependentFieldName, null);
		}

		/**
		 * Full constructor.
		 */
		public ReplicationStubFieldParameter(String name, String replacementName, @SuppressWarnings("rawtypes") Class type, boolean requerdedField,
											 String dependentFieldName, @SuppressWarnings("rawtypes") Class parameterizedType) {
			super();
			this.name = name;
			this.replacementName = replacementName;
			this.type = type;
			this.requerdedField = requerdedField;
			this.dependentFieldName = dependentFieldName;
			this.parameterizedType = parameterizedType;
		}

		public String getName() {
			return name;
		}

		@SuppressWarnings("rawtypes")
		public Class getType() {
			return type;
		}

		public boolean isList() {
			return List.class.equals(type);
		}

		public boolean isListWithDefinedParamethers() {
			return isList() && getParameterizedType() != null;
		}

		public boolean isRequerdedField() {
			return requerdedField;
		}

		public boolean isFound() {
			return found;
		}

		public void setFound(boolean found) {
			this.found = found;
		}

		public String getReplacementName() {
			return replacementName;
		}

		public boolean isEmptyReplacement() {
			return replacementName == null;
		}

		public Field getDependentToField() {
			return dependentToField;
		}

		public void setDependentToField(Field dependentToField) {
			this.dependentToField = dependentToField;
		}

		public String getDependentFieldName() {
			return dependentFieldName;
		}

		@SuppressWarnings("rawtypes")
		public Class getParameterizedType() {
			return parameterizedType;
		}

		@SuppressWarnings("rawtypes")
		public List<Class> getAvailableClasses() {
			if (availableClasses == null) {
				availableClasses = new ArrayList<>();
			}
			return availableClasses;
		}
	}

}

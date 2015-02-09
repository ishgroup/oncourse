package ish.oncourse.webservices.soap.v9.stubs;

import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.util.GenericDeletedStub;
import ish.oncourse.webservices.util.GenericInstructionStub;
import ish.oncourse.webservices.util.GenericParameterEntry;
import ish.oncourse.webservices.util.GenericParametersMap;
import ish.oncourse.webservices.util.GenericReplicatedRecord;
import ish.oncourse.webservices.util.GenericReplicationRecords;
import ish.oncourse.webservices.util.GenericReplicationResult;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.util.GenericTransactionGroup;
import ish.oncourse.webservices.v9.stubs.replication.*;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Stub compatibility check for v9 stubs.
 * This test should care of stub health till v9 stubs will be supported.
 * Test stubsPropertyMap definition should not be updated at all, because only if new fields will be added into stub test execution result will be OK.
 * @author vdavidovich
 */
public class StubsCompatibilityCheckTest extends ServiceTest {
	private static final String REPLICATION_STUB_FIELD_NAME = "replicationStub";
	private static final String GENERIC_ATTENDANCE_OR_BINARY_DATA_OR_BINARY_INFO_METHOD_NAME = "genericAttendanceOrBinaryDataOrBinaryInfo";
	private static final String ATTENDANCE_OR_BINARY_DATA_OR_BINARY_INFO_METHOD_NAME = "attendanceOrBinaryDataOrBinaryInfo";
	/**
	 * Map with defined stub's fields parameters.
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
		final List<ReplicationStubFieldParameter> attendanceParameters = fillDefaultReplicationStubFields();
		attendanceParameters.add(new ReplicationStubFieldParameter("attendanceType", Integer.class));
		attendanceParameters.add(new ReplicationStubFieldParameter("markerId", Long.class));
		attendanceParameters.add(new ReplicationStubFieldParameter("sessionId", Long.class));
		attendanceParameters.add(new ReplicationStubFieldParameter("studentId", Long.class));
        attendanceParameters.add(new ReplicationStubFieldParameter("durationMinutes", Integer.class,false));
        attendanceParameters.add(new ReplicationStubFieldParameter("note", String.class, false));
		stubsPropertyMap.put(getStubName(AttendanceStub.class), attendanceParameters);
		final List<ReplicationStubFieldParameter> binaryDataParameters = fillDefaultReplicationStubFields();
		binaryDataParameters.add(new ReplicationStubFieldParameter("binaryInfoId", Long.class));
		binaryDataParameters.add(new ReplicationStubFieldParameter("content", byte[].class));
		stubsPropertyMap.put(getStubName(BinaryDataStub.class), binaryDataParameters);
		final List<ReplicationStubFieldParameter> binaryInfoParameters = fillDefaultReplicationStubFields();
		binaryInfoParameters.add(new ReplicationStubFieldParameter("byteSize", Long.class));
		binaryInfoParameters.add(new ReplicationStubFieldParameter("webVisible", Integer.class));
		binaryInfoParameters.add(new ReplicationStubFieldParameter("mimeType", String.class));
		binaryInfoParameters.add(new ReplicationStubFieldParameter("name", String.class));
		binaryInfoParameters.add(new ReplicationStubFieldParameter("pixelHeight", Integer.class));
		binaryInfoParameters.add(new ReplicationStubFieldParameter("pixelWidth", Integer.class));
		binaryInfoParameters.add(new ReplicationStubFieldParameter("referenceNumber", Integer.class));
		binaryInfoParameters.add(new ReplicationStubFieldParameter("fileUUID", String.class));
		binaryInfoParameters.add(new ReplicationStubFieldParameter("thumbnail", byte[].class));
		stubsPropertyMap.put(getStubName(BinaryInfoStub.class), binaryInfoParameters);
		final List<ReplicationStubFieldParameter> concessionTypeParameters = fillDefaultReplicationStubFields();
		concessionTypeParameters.add(new ReplicationStubFieldParameter("credentialExpiryDays", Integer.class));
		concessionTypeParameters.add(new ReplicationStubFieldParameter("hasConcessionNumber", Boolean.class));
		concessionTypeParameters.add(new ReplicationStubFieldParameter("hasExpiryDate", Boolean.class));
		concessionTypeParameters.add(new ReplicationStubFieldParameter("isConcession", Boolean.class));
		concessionTypeParameters.add(new ReplicationStubFieldParameter("isEnabled", Boolean.class));
		concessionTypeParameters.add(new ReplicationStubFieldParameter("name", String.class));
		concessionTypeParameters.add(new ReplicationStubFieldParameter("requiresCredentialCheck", Boolean.class));
		stubsPropertyMap.put(getStubName(ConcessionTypeStub.class), concessionTypeParameters);
		final List<ReplicationStubFieldParameter> contactParameters = fillDefaultReplicationStubFields();
		contactParameters.add(new ReplicationStubFieldParameter("businessPhoneNumber", String.class));
		contactParameters.add(new ReplicationStubFieldParameter("cookieHash", String.class));
		contactParameters.add(new ReplicationStubFieldParameter("countryId", Long.class));
		contactParameters.add(new ReplicationStubFieldParameter("dateOfBirth", Date.class));
		contactParameters.add(new ReplicationStubFieldParameter("emailAddress", String.class));
		contactParameters.add(new ReplicationStubFieldParameter("familyName", String.class));
		contactParameters.add(new ReplicationStubFieldParameter("faxNumber", String.class));
		contactParameters.add(new ReplicationStubFieldParameter("givenName", String.class));
		contactParameters.add(new ReplicationStubFieldParameter("homePhoneNumber", String.class));
		contactParameters.add(new ReplicationStubFieldParameter("company", Boolean.class));
		contactParameters.add(new ReplicationStubFieldParameter("male", Boolean.class));
		contactParameters.add(new ReplicationStubFieldParameter("marketingViaEmailAllowed", Boolean.class));
		contactParameters.add(new ReplicationStubFieldParameter("marketingViaPostAllowed", Boolean.class));
		contactParameters.add(new ReplicationStubFieldParameter("marketingViaSMSAllowed", Boolean.class));
		contactParameters.add(new ReplicationStubFieldParameter("mobilePhoneNumber", String.class));
		contactParameters.add(new ReplicationStubFieldParameter("password", String.class));
		contactParameters.add(new ReplicationStubFieldParameter("passwordHash", String.class));
		contactParameters.add(new ReplicationStubFieldParameter("postcode", String.class));
		contactParameters.add(new ReplicationStubFieldParameter("state", String.class));
		contactParameters.add(new ReplicationStubFieldParameter("street", String.class));
		contactParameters.add(new ReplicationStubFieldParameter("suburb", String.class));
		contactParameters.add(new ReplicationStubFieldParameter("taxFileNumber", String.class));
		contactParameters.add(new ReplicationStubFieldParameter("uniqueCode", String.class));
		contactParameters.add(new ReplicationStubFieldParameter("studentId", Long.class));
		contactParameters.add(new ReplicationStubFieldParameter("tutorId", Long.class));
		contactParameters.add(new ReplicationStubFieldParameter("middleName", String.class));
		contactParameters.add(new ReplicationStubFieldParameter("abn", String.class));
		stubsPropertyMap.put(getStubName(ContactStub.class), contactParameters);

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
		courseClassParameters.add(new ReplicationStubFieldParameter("minStudentAge", Integer.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("maxStudentAge", Integer.class));
		courseClassParameters.add(new ReplicationStubFieldParameter("active", Boolean.class));
		stubsPropertyMap.put(getStubName(CourseClassStub.class), courseClassParameters);

		final List<ReplicationStubFieldParameter> courseParameters = fillDefaultReplicationStubFields();
		courseParameters.add(new ReplicationStubFieldParameter("allowWaitingList", Boolean.class));
		courseParameters.add(new ReplicationStubFieldParameter("code", String.class));
		courseParameters.add(new ReplicationStubFieldParameter("detail", String.class));
		courseParameters.add(new ReplicationStubFieldParameter("detailTextile", String.class));
		courseParameters.add(new ReplicationStubFieldParameter("fieldOfEducation", String.class));
		courseParameters.add(new ReplicationStubFieldParameter("sufficientForQualification", Boolean.class));
		courseParameters.add(new ReplicationStubFieldParameter("vetCourse", "VETCourse",Boolean.class));
		courseParameters.add(new ReplicationStubFieldParameter("webVisible", Boolean.class));
		courseParameters.add(new ReplicationStubFieldParameter("name", String.class));
		courseParameters.add(new ReplicationStubFieldParameter("nominalHours", BigDecimal.class));
		courseParameters.add(new ReplicationStubFieldParameter("searchText", String.class));
		courseParameters.add(new ReplicationStubFieldParameter("qualificationId", Long.class));
		courseParameters.add(new ReplicationStubFieldParameter("enrolmentType", Integer.class));
		stubsPropertyMap.put(getStubName(CourseStub.class), courseParameters);
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
		enrolmentParameters.add(new ReplicationStubFieldParameter("confirmationStatus", Integer.class));
		enrolmentParameters.add(new ReplicationStubFieldParameter("feeHelpAmount", BigDecimal.class));
		stubsPropertyMap.put(getStubName(EnrolmentStub.class), enrolmentParameters);

		final List<ReplicationStubFieldParameter> invoiceLineDiscountParameters = fillDefaultReplicationStubFields();
		invoiceLineDiscountParameters.add(new ReplicationStubFieldParameter("discountId", Long.class));
		invoiceLineDiscountParameters.add(new ReplicationStubFieldParameter("invoiceLineId", Long.class));
		stubsPropertyMap.put(getStubName(InvoiceLineDiscountStub.class), invoiceLineDiscountParameters);
		
		final List<ReplicationStubFieldParameter> invoiceLineParameters = fillDefaultReplicationStubFields();
		invoiceLineParameters.add(new ReplicationStubFieldParameter("description", String.class));
		invoiceLineParameters.add(new ReplicationStubFieldParameter("discountEachExTax", BigDecimal.class));
		invoiceLineParameters.add(new ReplicationStubFieldParameter("priceEachExTax", BigDecimal.class));
		invoiceLineParameters.add(new ReplicationStubFieldParameter("quantity", BigDecimal.class));
		invoiceLineParameters.add(new ReplicationStubFieldParameter("taxEach", BigDecimal.class));
		invoiceLineParameters.add(new ReplicationStubFieldParameter("title", String.class));
		invoiceLineParameters.add(new ReplicationStubFieldParameter("unit", String.class));
		invoiceLineParameters.add(new ReplicationStubFieldParameter("enrolmentId", Long.class));
		invoiceLineParameters.add(new ReplicationStubFieldParameter("invoiceId", Long.class));
		invoiceLineParameters.add(new ReplicationStubFieldParameter("sortOrder", Integer.class, false));
		invoiceLineParameters.add(new ReplicationStubFieldParameter("courseClassId", Long.class));
		stubsPropertyMap.put(getStubName(InvoiceLineStub.class), invoiceLineParameters);
		
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
		invoiceParameters.add(new ReplicationStubFieldParameter("confirmationStatus", Integer.class));
		stubsPropertyMap.put(getStubName(InvoiceStub.class), invoiceParameters);
		final List<ReplicationStubFieldParameter> messagePersonParameters = fillDefaultReplicationStubFields();
		messagePersonParameters.add(new ReplicationStubFieldParameter("destinationAddress", String.class));
		messagePersonParameters.add(new ReplicationStubFieldParameter("numberOfAttempts", Integer.class));
		messagePersonParameters.add(new ReplicationStubFieldParameter("response", String.class));
		messagePersonParameters.add(new ReplicationStubFieldParameter("status", Integer.class));
		messagePersonParameters.add(new ReplicationStubFieldParameter("timeOfDelivery", Date.class));
		messagePersonParameters.add(new ReplicationStubFieldParameter("type", Integer.class));
		messagePersonParameters.add(new ReplicationStubFieldParameter("contactId", Long.class));
		messagePersonParameters.add(new ReplicationStubFieldParameter("messageId", Long.class));
		messagePersonParameters.add(new ReplicationStubFieldParameter("studentId", Long.class));
		messagePersonParameters.add(new ReplicationStubFieldParameter("tutorId", Long.class));
		stubsPropertyMap.put(getStubName(MessagePersonStub.class), messagePersonParameters);
		final List<ReplicationStubFieldParameter> outcomeParameters = fillDefaultReplicationStubFields();
		outcomeParameters.add(new ReplicationStubFieldParameter("deliveryMode", Integer.class));
		outcomeParameters.add(new ReplicationStubFieldParameter("fundingSource", Integer.class));
		outcomeParameters.add(new ReplicationStubFieldParameter("reportableHours", BigDecimal.class));
		outcomeParameters.add(new ReplicationStubFieldParameter("hoursAttended", Integer.class));
		outcomeParameters.add(new ReplicationStubFieldParameter("enrolmentId", Long.class));
		outcomeParameters.add(new ReplicationStubFieldParameter("moduleId", Long.class));
		outcomeParameters.add(new ReplicationStubFieldParameter("startDate", Date.class));
		outcomeParameters.add(new ReplicationStubFieldParameter("endDate", Date.class));
		outcomeParameters.add(new ReplicationStubFieldParameter("status", Integer.class));
		stubsPropertyMap.put(getStubName(OutcomeStub.class), outcomeParameters);
		final List<ReplicationStubFieldParameter> paymentInLineParameters = fillDefaultReplicationStubFields();
		paymentInLineParameters.add(new ReplicationStubFieldParameter("amount", BigDecimal.class));
		paymentInLineParameters.add(new ReplicationStubFieldParameter("invoiceId", Long.class));
		paymentInLineParameters.add(new ReplicationStubFieldParameter("paymentInId", Long.class));
		stubsPropertyMap.put(getStubName(PaymentInLineStub.class), paymentInLineParameters);
		final List<ReplicationStubFieldParameter> paymentInParameters = fillDefaultReplicationStubFields();
		paymentInParameters.add(new ReplicationStubFieldParameter("amount", BigDecimal.class));
		paymentInParameters.add(new ReplicationStubFieldParameter("contactId", Long.class));
		paymentInParameters.add(new ReplicationStubFieldParameter("source", String.class));
		paymentInParameters.add(new ReplicationStubFieldParameter("status", Integer.class));
		paymentInParameters.add(new ReplicationStubFieldParameter("type", Integer.class));
		paymentInParameters.add(new ReplicationStubFieldParameter("privateNotes", String.class));
		paymentInParameters.add(new ReplicationStubFieldParameter("gatewayReference", String.class));
		paymentInParameters.add(new ReplicationStubFieldParameter("gatewayResponse", String.class));
		paymentInParameters.add(new ReplicationStubFieldParameter("sessionId", String.class));
		paymentInParameters.add(new ReplicationStubFieldParameter("creditCardExpiry", String.class));
		paymentInParameters.add(new ReplicationStubFieldParameter("creditCardName", String.class));
		paymentInParameters.add(new ReplicationStubFieldParameter("creditCardNumber", String.class));
		paymentInParameters.add(new ReplicationStubFieldParameter("creditCardType", String.class));
		paymentInParameters.add(new ReplicationStubFieldParameter("dateBanked", Date.class));
		paymentInParameters.add(new ReplicationStubFieldParameter("confirmationStatus", Integer.class));
		stubsPropertyMap.put(getStubName(PaymentInStub.class), paymentInParameters);
		final List<ReplicationStubFieldParameter> paymentOutParameters = fillDefaultReplicationStubFields();
		paymentOutParameters.add(new ReplicationStubFieldParameter("amount", BigDecimal.class));
		paymentOutParameters.add(new ReplicationStubFieldParameter("contactId", Long.class));
		paymentOutParameters.add(new ReplicationStubFieldParameter("paymentInTxnReference", String.class));
		paymentOutParameters.add(new ReplicationStubFieldParameter("type", Integer.class));
		paymentOutParameters.add(new ReplicationStubFieldParameter("source", String.class));
		paymentOutParameters.add(new ReplicationStubFieldParameter("status", Integer.class));
		paymentOutParameters.add(new ReplicationStubFieldParameter("dateBanked", Date.class));
		paymentOutParameters.add(new ReplicationStubFieldParameter("datePaid", Date.class));
		paymentOutParameters.add(new ReplicationStubFieldParameter("confirmationStatus", Integer.class));
		stubsPropertyMap.put(getStubName(PaymentOutStub.class), paymentOutParameters);
		final List<ReplicationStubFieldParameter> preferenceParameters = fillDefaultReplicationStubFields();
		preferenceParameters.add(new ReplicationStubFieldParameter("name", String.class));
		preferenceParameters.add(new ReplicationStubFieldParameter("value", byte[].class));
		preferenceParameters.add(new ReplicationStubFieldParameter("valueString", String.class));
		stubsPropertyMap.put(getStubName(PreferenceStub.class), preferenceParameters);
		final List<ReplicationStubFieldParameter> roomParameters = fillDefaultReplicationStubFields();
		roomParameters.add(new ReplicationStubFieldParameter("capacity", Integer.class));
		roomParameters.add(new ReplicationStubFieldParameter("directions", String.class));
		roomParameters.add(new ReplicationStubFieldParameter("directionsTextile", String.class));
		roomParameters.add(new ReplicationStubFieldParameter("facilities", String.class));
		roomParameters.add(new ReplicationStubFieldParameter("facilitiesTextile", String.class));
		roomParameters.add(new ReplicationStubFieldParameter("name", String.class));
		roomParameters.add(new ReplicationStubFieldParameter("siteId", Long.class));
		stubsPropertyMap.put(getStubName(RoomStub.class), roomParameters);
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
		final List<ReplicationStubFieldParameter> studentConcessionParameters = fillDefaultReplicationStubFields();
		studentConcessionParameters.add(new ReplicationStubFieldParameter("authorisationExpiresOn", Date.class));
		studentConcessionParameters.add(new ReplicationStubFieldParameter("authorisedOn", Date.class));
		studentConcessionParameters.add(new ReplicationStubFieldParameter("concessionNumber", String.class));
		studentConcessionParameters.add(new ReplicationStubFieldParameter("expiresOn", Date.class));
		studentConcessionParameters.add(new ReplicationStubFieldParameter("timeZone", String.class));
		studentConcessionParameters.add(new ReplicationStubFieldParameter("concessionTypeId", Long.class));
		studentConcessionParameters.add(new ReplicationStubFieldParameter("studentId", Long.class));
		stubsPropertyMap.put(getStubName(StudentConcessionStub.class), studentConcessionParameters);

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
		studentParameters.add(new ReplicationStubFieldParameter("specialNeedsAssistance", Boolean.class));
		studentParameters.add(new ReplicationStubFieldParameter("citizenship", Integer.class));
		studentParameters.add(new ReplicationStubFieldParameter("specialNeeds", String.class));
		studentParameters.add(new ReplicationStubFieldParameter("townOfBirth", String.class ));
		studentParameters.add(new ReplicationStubFieldParameter("usi", String.class ));
		studentParameters.add(new ReplicationStubFieldParameter("usiStatus", Integer.class ));
		stubsPropertyMap.put(getStubName(StudentStub.class), studentParameters);

		final List<ReplicationStubFieldParameter> systemUserParameters = fillDefaultReplicationStubFields();
		systemUserParameters.add(new ReplicationStubFieldParameter("editCMS", Boolean.class));
		systemUserParameters.add(new ReplicationStubFieldParameter("editTara", Boolean.class));
		systemUserParameters.add(new ReplicationStubFieldParameter("email", String.class));
		systemUserParameters.add(new ReplicationStubFieldParameter("firstName", String.class));
		systemUserParameters.add(new ReplicationStubFieldParameter("surname", String.class));
		systemUserParameters.add(new ReplicationStubFieldParameter("password", String.class));
		systemUserParameters.add(new ReplicationStubFieldParameter("login", String.class));
		systemUserParameters.add(new ReplicationStubFieldParameter("lastLoginIP", String.class));
		systemUserParameters.add(new ReplicationStubFieldParameter("lastLoginOn", Date.class));
		systemUserParameters.add(new ReplicationStubFieldParameter("isActive", Boolean.class));
		systemUserParameters.add(new ReplicationStubFieldParameter("isAdmin", Boolean.class));
		systemUserParameters.add(new ReplicationStubFieldParameter("defaultAdministrationCentreId", Long.class));
		stubsPropertyMap.put(getStubName(SystemUserStub.class), systemUserParameters);
		final List<ReplicationStubFieldParameter> tagRelationParameters = fillDefaultReplicationStubFields();
		tagRelationParameters.add(new ReplicationStubFieldParameter("entityAngelId", Long.class));
		tagRelationParameters.add(new ReplicationStubFieldParameter("entityName", String.class));
		tagRelationParameters.add(new ReplicationStubFieldParameter("entityWillowId", Long.class));
		tagRelationParameters.add(new ReplicationStubFieldParameter("tagId", Long.class));
		stubsPropertyMap.put(getStubName(TagRelationStub.class), tagRelationParameters);
		final List<ReplicationStubFieldParameter> courseClassTutorParameters = fillDefaultReplicationStubFields();
		courseClassTutorParameters.add(new ReplicationStubFieldParameter("courseClassId", Long.class));
		courseClassTutorParameters.add(new ReplicationStubFieldParameter("tutorId", Long.class));
		courseClassTutorParameters.add(new ReplicationStubFieldParameter("confirmedOn", Date.class));
		courseClassTutorParameters.add(new ReplicationStubFieldParameter("inPublicity", Boolean.class));
		stubsPropertyMap.put(getStubName(CourseClassTutorStub.class), courseClassTutorParameters);
		final List<ReplicationStubFieldParameter> tutorParameters = fillDefaultReplicationStubFields();
		tutorParameters.add(new ReplicationStubFieldParameter("finishDate", Date.class));
		tutorParameters.add(new ReplicationStubFieldParameter("resume", String.class));
		tutorParameters.add(new ReplicationStubFieldParameter("resumeTextile", String.class));
		tutorParameters.add(new ReplicationStubFieldParameter("startDate", Date.class));
		tutorParameters.add(new ReplicationStubFieldParameter("contactId", Long.class));
		stubsPropertyMap.put(getStubName(TutorStub.class), tutorParameters);
		final List<ReplicationStubFieldParameter> waitingListParameters = fillDefaultReplicationStubFields();
		waitingListParameters.add(new ReplicationStubFieldParameter("detail", String.class));
		waitingListParameters.add(new ReplicationStubFieldParameter("studentCount", Integer.class));
		waitingListParameters.add(new ReplicationStubFieldParameter("courseId", Long.class));
		waitingListParameters.add(new ReplicationStubFieldParameter("studentId", Long.class));
		stubsPropertyMap.put(getStubName(WaitingListStub.class), waitingListParameters);
		final List<ReplicationStubFieldParameter> certificateParameters = fillDefaultReplicationStubFields();
		certificateParameters.add(new ReplicationStubFieldParameter("certificateNumber", Long.class));
		certificateParameters.add(new ReplicationStubFieldParameter("endDate", Date.class));
		certificateParameters.add(new ReplicationStubFieldParameter("fundingSource", Integer.class));
		certificateParameters.add(new ReplicationStubFieldParameter("qualification", Boolean.class));
		certificateParameters.add(new ReplicationStubFieldParameter("printedWhen", Date.class));
		certificateParameters.add(new ReplicationStubFieldParameter("privateNotes", String.class));
		certificateParameters.add(new ReplicationStubFieldParameter("publicNotes", String.class));
		certificateParameters.add(new ReplicationStubFieldParameter("qualificationId", Long.class));
		certificateParameters.add(new ReplicationStubFieldParameter("revokedWhen", Date.class));
		certificateParameters.add(new ReplicationStubFieldParameter("studentFirstName", String.class));
		certificateParameters.add(new ReplicationStubFieldParameter("studentLastName", String.class));
		certificateParameters.add(new ReplicationStubFieldParameter("studentId", Long.class));
		stubsPropertyMap.put(getStubName(CertificateStub.class), certificateParameters);
		final List<ReplicationStubFieldParameter> certificateOutcomeParameters = fillDefaultReplicationStubFields();
		certificateOutcomeParameters.add(new ReplicationStubFieldParameter("certificateId", Long.class));
		certificateOutcomeParameters.add(new ReplicationStubFieldParameter("outcomeId", Long.class));
		stubsPropertyMap.put(getStubName(CertificateOutcomeStub.class), certificateOutcomeParameters);
		final List<ReplicationStubFieldParameter> deletedStubParameters = fillDefaultReplicationStubFields();
		stubsPropertyMap.put(getStubName(DeletedStub.class), deletedStubParameters);
		final List<ReplicationStubFieldParameter> hollowStubParameters = fillDefaultReplicationStubFields();
		stubsPropertyMap.put(getStubName(HollowStub.class), hollowStubParameters);
		final List<ReplicationStubFieldParameter> queuedStatisticParameters = fillDefaultReplicationStubFields();
		queuedStatisticParameters.add(new ReplicationStubFieldParameter("stackedTransactionsCount", Long.class));
		queuedStatisticParameters.add(new ReplicationStubFieldParameter("stackedCount", Long.class));
		queuedStatisticParameters.add(new ReplicationStubFieldParameter("stackedEntityIdentifier", String.class));
		queuedStatisticParameters.add(new ReplicationStubFieldParameter("receivedTimestamp", Date.class));
		queuedStatisticParameters.add(new ReplicationStubFieldParameter("cleanupStub", Boolean.class));
		stubsPropertyMap.put(getStubName(QueuedStatisticStub.class), queuedStatisticParameters);
		final List<ReplicationStubFieldParameter> voucherProductCourseParameters = fillDefaultReplicationStubFields();
		voucherProductCourseParameters.add(new ReplicationStubFieldParameter("courseId", Long.class));
		voucherProductCourseParameters.add(new ReplicationStubFieldParameter("voucherProductId", Long.class));
		stubsPropertyMap.put(getStubName(VoucherProductCourseStub.class), voucherProductCourseParameters);
		final List<ReplicationStubFieldParameter> productItemParameters = fillProductItemStubFields(fillDefaultReplicationStubFields());
		productItemParameters.add(new ReplicationStubFieldParameter("confirmationStatus", Integer.class));
		stubsPropertyMap.put(getStubName(ProductItemStub.class), productItemParameters);
		final List<ReplicationStubFieldParameter> productParameters = fillProductStubFields(fillDefaultReplicationStubFields());
		stubsPropertyMap.put(getStubName(ProductStub.class), productParameters);
		
		final List<ReplicationStubFieldParameter> voucherParameters = fillProductItemStubFields(fillDefaultReplicationStubFields());
		voucherParameters.add(new ReplicationStubFieldParameter("expiryDate", Date.class));
		voucherParameters.add(new ReplicationStubFieldParameter("key", String.class));
		voucherParameters.add(new ReplicationStubFieldParameter("contactId", Long.class));
		voucherParameters.add(new ReplicationStubFieldParameter("code", String.class));
		voucherParameters.add(new ReplicationStubFieldParameter("redeemedCoursesCount", Integer.class));
		voucherParameters.add(new ReplicationStubFieldParameter("redemptionValue", BigDecimal.class));
		voucherParameters.add(new ReplicationStubFieldParameter("status", Integer.class));
		voucherParameters.add(new ReplicationStubFieldParameter("source", String.class));
		voucherParameters.add(new ReplicationStubFieldParameter("valueOnPurchase", BigDecimal.class));
		voucherParameters.add(new ReplicationStubFieldParameter("confirmationStatus", Integer.class));
		stubsPropertyMap.put(getStubName(VoucherStub.class), voucherParameters);
		
		final List<ReplicationStubFieldParameter> voucherProductParameters = fillProductStubFields(fillDefaultReplicationStubFields());
		voucherProductParameters.add(new ReplicationStubFieldParameter("expiryDays", Integer.class));
		voucherProductParameters.add(new ReplicationStubFieldParameter("expiryType", Integer.class));
		voucherProductParameters.add(new ReplicationStubFieldParameter("value", BigDecimal.class));
		voucherProductParameters.add(new ReplicationStubFieldParameter("maxCoursesRedemption", Integer.class));
		stubsPropertyMap.put(getStubName(VoucherProductStub.class), voucherProductParameters);
		final List<ReplicationStubFieldParameter> membershipParameters = fillProductItemStubFields(fillDefaultReplicationStubFields());
		membershipParameters.add(new ReplicationStubFieldParameter("expiryDate", Date.class));
		membershipParameters.add(new ReplicationStubFieldParameter("contactId", Long.class));
		membershipParameters.add(new ReplicationStubFieldParameter("confirmationStatus", Integer.class));
		stubsPropertyMap.put(getStubName(MembershipStub.class), membershipParameters);
		final List<ReplicationStubFieldParameter> voucherPaymentInParameters = fillDefaultReplicationStubFields();
		voucherPaymentInParameters.add(new ReplicationStubFieldParameter("paymentInId", Long.class));
		voucherPaymentInParameters.add(new ReplicationStubFieldParameter("voucherId", Long.class));
		voucherPaymentInParameters.add(new ReplicationStubFieldParameter("enrolmentsCount", Integer.class));
		voucherPaymentInParameters.add(new ReplicationStubFieldParameter("status", Integer.class));
		voucherPaymentInParameters.add(new ReplicationStubFieldParameter("invoiceLineId", Long.class));
		stubsPropertyMap.put(getStubName(VoucherPaymentInStub.class), voucherPaymentInParameters);
		final List<ReplicationStubFieldParameter> sessionModuleParameters = fillDefaultReplicationStubFields();
		sessionModuleParameters.add(new ReplicationStubFieldParameter("moduleId", Long.class));
		sessionModuleParameters.add(new ReplicationStubFieldParameter("sessionId", Long.class));
		stubsPropertyMap.put(getStubName(SessionModuleStub.class), sessionModuleParameters);
		final List<ReplicationStubFieldParameter> membershipProductParameters = fillProductStubFields(fillDefaultReplicationStubFields());
		membershipProductParameters.add(new ReplicationStubFieldParameter("expiryDays", Integer.class));
		membershipProductParameters.add(new ReplicationStubFieldParameter("expiryType", Integer.class));
		stubsPropertyMap.put(getStubName(MembershipProductStub.class), membershipProductParameters);
		final List<ReplicationStubFieldParameter> surveyParameters = fillDefaultReplicationStubFields();
		surveyParameters.add(new ReplicationStubFieldParameter("comment", String.class));
		surveyParameters.add(new ReplicationStubFieldParameter("courseScore", Integer.class));
		surveyParameters.add(new ReplicationStubFieldParameter("tutorScore", Integer.class));
		surveyParameters.add(new ReplicationStubFieldParameter("venueScore", Integer.class));
		surveyParameters.add(new ReplicationStubFieldParameter("uniqueCode", String.class));
		surveyParameters.add(new ReplicationStubFieldParameter("enrolmentId", Long.class));
		surveyParameters.add(new ReplicationStubFieldParameter("publicComment", Boolean.class));
		stubsPropertyMap.put(getStubName(SurveyStub.class), surveyParameters);

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
		
		List<ReplicationStubFieldParameter> documentParameters = fillDefaultReplicationStubFields();
		documentParameters.add(new ReplicationStubFieldParameter("webVisible", Integer.class));
		documentParameters.add(new ReplicationStubFieldParameter("name", String.class));
		documentParameters.add(new ReplicationStubFieldParameter("fileUUID", String.class));
		documentParameters.add(new ReplicationStubFieldParameter("description", String.class));
		documentParameters.add(new ReplicationStubFieldParameter("removed", Boolean.class));
		documentParameters.add(new ReplicationStubFieldParameter("shared", Boolean.class));
		stubsPropertyMap.put(getStubName(DocumentStub.class), documentParameters);
		
		List<ReplicationStubFieldParameter> documentVersionParameters = fillDefaultReplicationStubFields();
		documentVersionParameters.add(new ReplicationStubFieldParameter("byteSize", Long.class));
		documentVersionParameters.add(new ReplicationStubFieldParameter("mimeType", String.class));
		documentVersionParameters.add(new ReplicationStubFieldParameter("fileName", String.class));
		documentVersionParameters.add(new ReplicationStubFieldParameter("pixelHeight", Integer.class));
		documentVersionParameters.add(new ReplicationStubFieldParameter("pixelWidth", Integer.class));
		documentVersionParameters.add(new ReplicationStubFieldParameter("thumbnail", byte[].class));
		documentVersionParameters.add(new ReplicationStubFieldParameter("versionId", String.class));
		documentVersionParameters.add(new ReplicationStubFieldParameter("timestamp", Date.class));
		documentVersionParameters.add(new ReplicationStubFieldParameter("documentId", Long.class));
		documentVersionParameters.add(new ReplicationStubFieldParameter("description", String.class));
		documentVersionParameters.add(new ReplicationStubFieldParameter("createdByUserId", Long.class));
		stubsPropertyMap.put(getStubName(DocumentVersionStub.class), documentVersionParameters);
		
		List<ReplicationStubFieldParameter> customFieldTypeParameters = fillDefaultReplicationStubFields();
		customFieldTypeParameters.add(new ReplicationStubFieldParameter("name", String.class));
		customFieldTypeParameters.add(new ReplicationStubFieldParameter("defaultValue", String.class));
		customFieldTypeParameters.add(new ReplicationStubFieldParameter("mandatory", Boolean.class));
		stubsPropertyMap.put(getStubName(CustomFieldTypeStub.class), customFieldTypeParameters);
		
		List<ReplicationStubFieldParameter> customFieldParameters = fillDefaultReplicationStubFields();
		customFieldParameters.add(new ReplicationStubFieldParameter("customFieldTypeId", Long.class));
		customFieldParameters.add(new ReplicationStubFieldParameter("foreignId", Long.class));
		customFieldParameters.add(new ReplicationStubFieldParameter("value", String.class));
		stubsPropertyMap.put(getStubName(CustomFieldStub.class), customFieldParameters);

		List<ReplicationStubFieldParameter> applicationParameters = fillDefaultReplicationStubFields();
		applicationParameters.add(new ReplicationStubFieldParameter("studentId", Long.class));
		applicationParameters.add(new ReplicationStubFieldParameter("courseId", Long.class));
		applicationParameters.add(new ReplicationStubFieldParameter("source", String.class));
		applicationParameters.add(new ReplicationStubFieldParameter("status", Integer.class));
		applicationParameters.add(new ReplicationStubFieldParameter("enrolBy", Date.class));
		applicationParameters.add(new ReplicationStubFieldParameter("feeOverride", BigDecimal.class));
		applicationParameters.add(new ReplicationStubFieldParameter("reason", String.class));
		applicationParameters.add(new ReplicationStubFieldParameter("confirmationStatus", Integer.class));
		stubsPropertyMap.put(getStubName(ApplicationStub.class), applicationParameters);

		//TODO: add new stubs here
		final List<ReplicationStubFieldParameter> replicationStubParameters = fillDefaultReplicationStubFields();
		stubsPropertyMap.put(getStubName(ReplicationStub.class), replicationStubParameters);
		final List<ReplicationStubFieldParameter> replicationResultParameters = new ArrayList<>();
		replicationResultParameters.add(new ReplicationStubFieldParameter("replicatedRecord", true, ReplicatedRecord.class));
		replicationResultParameters.add(new ReplicationStubFieldParameter("genericReplicatedRecord", "replicatedRecord", List.class, true, null,
				ReplicatedRecord.class));
		stubsPropertyMap.put(getName(ReplicationResult.class), replicationResultParameters);
		final List<ReplicationStubFieldParameter> replicationRecordsParameters = new ArrayList<>();
		replicationRecordsParameters.add(new ReplicationStubFieldParameter("groups", true, TransactionGroup.class));
		replicationRecordsParameters.add(new ReplicationStubFieldParameter("genericGroups", "groups", List.class, true, null,
				TransactionGroup.class));
		stubsPropertyMap.put(getName(ReplicationRecords.class), replicationRecordsParameters);
		final List<ReplicationStubFieldParameter> replicatedRecordParameters = new ArrayList<>();
		replicatedRecordParameters.add(new ReplicationStubFieldParameter("status", Status.class));
		replicatedRecordParameters.add(new ReplicationStubFieldParameter("message", String.class));
		replicatedRecordParameters.add(new ReplicationStubFieldParameter("stub", HollowStub.class));
		replicatedRecordParameters.add(new ReplicationStubFieldParameter("failedStatus", boolean.class, "status"));
		replicatedRecordParameters.add(new ReplicationStubFieldParameter("successStatus", boolean.class, "status"));
		stubsPropertyMap.put(getName(ReplicatedRecord.class), replicatedRecordParameters);
		final List<ReplicationStubFieldParameter> parameterEntryParameters = new ArrayList<>();
		parameterEntryParameters.add(new ReplicationStubFieldParameter("name", String.class));
		parameterEntryParameters.add(new ReplicationStubFieldParameter("value", String.class));
		stubsPropertyMap.put(getName(ParameterEntry.class), parameterEntryParameters);
		final List<ReplicationStubFieldParameter> parametersMapParameters = new ArrayList<>();
		parametersMapParameters.add(new ReplicationStubFieldParameter("entry", true, false, ParameterEntry.class));
		parametersMapParameters.add(new ReplicationStubFieldParameter("genericEntry", "entry", List.class, false, null, ParameterEntry.class));
		stubsPropertyMap.put(getName(ParametersMap.class), parametersMapParameters);
		final List<ReplicationStubFieldParameter> instructionStubParameters = new ArrayList<>();
		instructionStubParameters.add(new ReplicationStubFieldParameter("id", Long.class));
		instructionStubParameters.add(new ReplicationStubFieldParameter("message", String.class));
		instructionStubParameters.add(new ReplicationStubFieldParameter("parameters", ParametersMap.class, false));
		stubsPropertyMap.put(getName(InstructionStub.class), instructionStubParameters);
		final List<ReplicationStubFieldParameter> instructionsResponseParameters = new ArrayList<>();
		instructionsResponseParameters.add(new ReplicationStubFieldParameter("_return", "return", List.class, false, null, InstructionStub.class));
		stubsPropertyMap.put(getName(GetInstructionsResponse.class), instructionsResponseParameters);
		final List<ReplicationStubFieldParameter> faultReasonParameters = new ArrayList<>();
		faultReasonParameters.add(new ReplicationStubFieldParameter("detailMessage", String.class, false));
		faultReasonParameters.add(new ReplicationStubFieldParameter("faultCode", Integer.class, false));
		stubsPropertyMap.put(getName(FaultReason.class), faultReasonParameters);
		final List<ReplicationStubFieldParameter> transactionGroupParameters = new ArrayList<>();
		transactionGroupParameters.add(new ReplicationStubFieldParameter("transactionKeys", List.class));
		final ReplicationStubFieldParameter replicationStub = new ReplicationStubFieldParameter(
				REPLICATION_STUB_FIELD_NAME, List.class, false);
		@SuppressWarnings("rawtypes")
		final List<Class> replicationStubAvailableClasses = new ArrayList<>();
		replicationStubAvailableClasses.add(ReplicationStub.class);

		replicationStub.getAvailableClasses().addAll(replicationStubAvailableClasses);
		transactionGroupParameters.add(replicationStub);
		final ReplicationStubFieldParameter genericAttendanceOrBinaryDataOrBinaryInfo = new ReplicationStubFieldParameter(
				GENERIC_ATTENDANCE_OR_BINARY_DATA_OR_BINARY_INFO_METHOD_NAME, REPLICATION_STUB_FIELD_NAME, List.class, false, null);
		genericAttendanceOrBinaryDataOrBinaryInfo.getAvailableClasses().addAll(replicationStubAvailableClasses);
		transactionGroupParameters.add(genericAttendanceOrBinaryDataOrBinaryInfo);
		stubsPropertyMap.put(getName(TransactionGroup.class), transactionGroupParameters);
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
	public void testDocumentStub() {
		GenericReplicationStub stub = new DocumentStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testDocumentVersionStub() {
		GenericReplicationStub stub = new DocumentVersionStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testCustomFieldTypeStub() {
		GenericReplicationStub stub = new CustomFieldTypeStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testCustomFieldStub() {
		GenericReplicationStub stub = new CustomFieldStub();
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}

	@Test
	public void testApplicationStub() {
		GenericReplicationStub stub = new ApplicationStub();
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
		final List<ReplicationStubFieldParameter> parameters = new ArrayList<>();
		parameters.add(new ReplicationStubFieldParameter("angelId", Long.class));
		parameters.add(new ReplicationStubFieldParameter("willowId", Long.class));
		parameters.add(new ReplicationStubFieldParameter("entityIdentifier", String.class));
		parameters.add(new ReplicationStubFieldParameter("created", Date.class));
		parameters.add(new ReplicationStubFieldParameter("modified", Date.class));
		return parameters;
	}

	private List<ReplicationStubFieldParameter> fillProductItemStubFields(final List<ReplicationStubFieldParameter> preparedParameters) {
		preparedParameters.add(new ReplicationStubFieldParameter("type", Integer.class));
		preparedParameters.add(new ReplicationStubFieldParameter("invoiceLineId", Long.class));
		preparedParameters.add(new ReplicationStubFieldParameter("productId", Long.class));
		preparedParameters.add(new ReplicationStubFieldParameter("status", Integer.class));
		return preparedParameters;
	}

	private List<ReplicationStubFieldParameter> fillProductStubFields(final List<ReplicationStubFieldParameter> preparedParameters) {
		preparedParameters.add(new ReplicationStubFieldParameter("sku", String.class));
		preparedParameters.add(new ReplicationStubFieldParameter("description", String.class));
		preparedParameters.add(new ReplicationStubFieldParameter("incomeAccountId", Long.class));
		preparedParameters.add(new ReplicationStubFieldParameter("isOnSale", Boolean.class));
		preparedParameters.add(new ReplicationStubFieldParameter("isWebVisible", Boolean.class));
		preparedParameters.add(new ReplicationStubFieldParameter("name", String.class));
		preparedParameters.add(new ReplicationStubFieldParameter("notes", String.class));
		preparedParameters.add(new ReplicationStubFieldParameter("priceExTax", BigDecimal.class));
		preparedParameters.add(new ReplicationStubFieldParameter("taxAdjustment", BigDecimal.class));
		preparedParameters.add(new ReplicationStubFieldParameter("taxAmount", BigDecimal.class));
		preparedParameters.add(new ReplicationStubFieldParameter("taxId", Long.class));
		preparedParameters.add(new ReplicationStubFieldParameter("type", Integer.class));
		return preparedParameters;
	}

	private ReplicationStubFieldParameter getReplicationParameterForReplacement(final List<ReplicationStubFieldParameter> parameters,
																				 final String replacementName) {
		for (final ReplicationStubFieldParameter parameter : parameters) {
			if (!parameter.isEmptyReplacement() && parameter.getReplacementName().equalsIgnoreCase(replacementName)) {
				parameter.setFound(true);
				return parameter;
			}
		}
		return null;
	}

	private ReplicationStubFieldParameter getReplicationParameterForDependendFields(final List<ReplicationStubFieldParameter> parameters,
																					 final String descriptorName) {
		for (final ReplicationStubFieldParameter parameter : parameters) {
			if (StringUtils.trimToNull(parameter.getDependentFieldName()) != null && parameter.getName().equals(descriptorName)) {
				assertNotNull(String.format("Dependent field with name %s not exist for paramather %s ", parameter.getDependentFieldName(),
						parameter.getName()), parameter.getDependentToField());
				return parameter;
			}
		}
		return null;
	}

	private void fillFieldDependency(final List<ReplicationStubFieldParameter> parameters, final Object stub) {
		for (final PropertyDescriptor descriptor : PropertyUtils.getPropertyDescriptors(stub)) {
			if ("class".equals(descriptor.getName())) {
				continue;
			}
			Field field = ReflectionUtils.findField(stub.getClass(), descriptor.getName());
			if (field == null) {
				ReplicationStubFieldParameter parameter = getReplicationParameterForReplacement(parameters, descriptor.getName());
				if (parameter != null) {
					field = ReflectionUtils.findField(stub.getClass(), parameter.getName());
					assertNotNull(String.format("No field could be loaded for descriptor with name %s for stub %s with alias %s", descriptor.getName(),
							stub.getClass().getName(), parameter.getName()), field);
				}
			}
			if (field != null) {
				for (final ReplicationStubFieldParameter parameter : parameters) {
					//fill the dependent fields if required
					if (StringUtils.trimToNull(parameter.getDependentFieldName()) != null && parameter.getDependentToField() == null
							&& field.getName().equals(parameter.getDependentFieldName())) {
						parameter.setDependentToField(field);
					}
				}
			}
		}
	}

	/**
	 * Method which check current container definition for backward compatibility.
	 * @param stub - container for test.
	 * @param parameters defined for this stub.
	 */
	private void testCollectorDefinition(final Object stub, final List<ReplicationStubFieldParameter> parameters) {
		assertNotNull(String.format("No parameter defined for stub %s", stub.getClass().getSimpleName()), parameters);
		final PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(stub);
		fillFieldDependency(parameters, stub);
		for (final PropertyDescriptor descriptor : descriptors) {
			//we should skipp class
			// and attendanceOrBinaryDataOrBinaryInfo because in v9 this property replaced with replicationStub
			// but method should exists to have backward compatibility with  V4 and V5
			if ("class".equals(descriptor.getName()) || ATTENDANCE_OR_BINARY_DATA_OR_BINARY_INFO_METHOD_NAME.equals(descriptor.getName())) {
				continue;
			}
			Field field = ReflectionUtils.findField(stub.getClass(), descriptor.getName());
			boolean useReplacement = false;
			if (field == null) {
				ReplicationStubFieldParameter parameter = getReplicationParameterForReplacement(parameters, descriptor.getName());
				if (parameter != null) {
					useReplacement = true;
					field = ReflectionUtils.findField(stub.getClass(), parameter.getName());
					assertNotNull(String.format("No field could be loaded for descriptor with name %s for stub %s with alias %s", descriptor.getName(),
							stub.getClass().getName(), parameter.getName()), field);
				} else if (descriptor.getName().startsWith("generic")) {
					parameter = getReplicationParameterForReplacement(parameters, descriptor.getName()
						.replaceFirst(GENERIC_ATTENDANCE_OR_BINARY_DATA_OR_BINARY_INFO_METHOD_NAME, REPLICATION_STUB_FIELD_NAME).replaceFirst("generic", StringUtils.EMPTY));
					if (parameter != null) {
						field = ReflectionUtils.findField(stub.getClass(), parameter.getReplacementName());
						assertNotNull(String.format("No field could be loaded for generic descriptor with name %s for stub %s with alias %s",
								descriptor.getName(), stub.getClass().getName(), parameter.getName()), field);
					}
				} else {
					parameter = getReplicationParameterForDependendFields(parameters, descriptor.getName());
					if (parameter != null) {
						field = parameter.getDependentToField();
					}
				}
			}
			assertNotNull(String.format("No field could be loaded for descriptor with name %s for stub %s possible alias used", descriptor.getName(),
					stub.getClass().getName()), field);
			final XmlElement elementAnotation = field.getAnnotation(XmlElement.class);
			final boolean requiredField = elementAnotation != null && elementAnotation.required();
			boolean founded = false;
			for (final ReplicationStubFieldParameter parameter : parameters) {
				if (descriptor.getName().equals(useReplacement? parameter.getReplacementName() : parameter.getName())) {
					founded = true;
					if (descriptor.getName().startsWith("generic")) {
						useReplacement = true;
					}
					assertFalse(String.format("Not used alias defined for parameter with name %s and alias %s", parameter.getName(),
							parameter.getReplacementName()), !useReplacement && !parameter.isEmptyReplacement());
					parameter.setFound(founded);
					//we should check this descriptor
					Method readMethod = descriptor.getReadMethod();
					if (readMethod == null) {
						if (parameter.getType().equals(Boolean.class)) {
							String readMethodName = "is" + WordUtils.capitalize(descriptor.getName());
							readMethod = ReflectionUtils.findMethod(stub.getClass(), readMethodName);
						}
					}
					assertNotNull(String.format("There are no getter for %s field found on %s stub", descriptor.getName(), stub.getClass().getName()), readMethod);
					assertEquals(String.format("test property - %s return type incompatible with defined type %s", descriptor.getName(),
							parameter.getType()), parameter.getType(), readMethod.getReturnType());
					if (parameter.isListWithDefinedParameters()) {
						ParameterizedType entityType = (ParameterizedType) field.getGenericType();
						@SuppressWarnings("rawtypes")
						Class clazz = (Class) entityType.getActualTypeArguments()[0];
						assertEquals(String.format("Incorrect definition of generics for %s parameter. ", parameter.getName()),
								parameter.getParameterizedType(), clazz);
					}
					if (parameter.isList()) {
						assertEquals(String.format("test property - %s should have no setter", descriptor.getName()), null,
								descriptor.getWriteMethod());
					} else {
						Method writeMethod = descriptor.getWriteMethod();
						boolean withParam = true;
						if (writeMethod == null) {
							if (parameter.getDependentToField() != null) {
								withParam = false;
								String writeMethodName = "set" + WordUtils.capitalize(descriptor.getName());
								writeMethod = ReflectionUtils.findMethod(stub.getClass(), writeMethodName);
							}
						}
						if (writeMethod != null) {
							if (withParam) {
								assertEquals(String.format("test property - %s should have one parameter for setter", descriptor.getName()), 1,
										writeMethod.getParameterTypes().length);
							} else {
								assertEquals(String.format("test property - %s should have no parameter for setter", descriptor.getName()), 0,
										writeMethod.getParameterTypes().length);
							}
						}
					}
					assertTrue(String.format("Method modifiers should be public for property %s", descriptor.getName()),
							(readMethod.getModifiers() == Modifier.PUBLIC || readMethod.getModifiers() == 17 ||
									readMethod.getModifiers() == 1025/*public final*/ || readMethod.getModifiers() == 4161/*public abstract with override*/));
					assertTrue(String.format("Required anotation should be equal to defined for property %s", descriptor.getName()),
							parameter.isRequerdedField() == requiredField);
				}
			}
			if (requiredField) {
				assertTrue(String.format("Required field descriptor not defined in property map %s", descriptor.getName()), founded);
			} else {
				assertTrue(String.format("Advise field descriptor not defined in property map %s", descriptor.getName()), founded);
			}
		}
		for (final ReplicationStubFieldParameter parameter : parameters) {
			assertTrue(String.format("Stub %s have not defined field %s", stub.getClass().getName(), parameter.getName()), parameter.isFound());
			if (StringUtils.trimToNull(parameter.getDependentFieldName()) != null) {
				assertNotNull(String.format("Dependent field with name %s should be loaded.", parameter.getDependentFieldName()),
						parameter.getDependentToField());
			}
		}
	}

	private void testReplicationStubDefinition(final GenericReplicationStub stub, final List<ReplicationStubFieldParameter> parameters) {
		testStubDefinition(stub, parameters);
	}

	/**
	 * Method which check current stub definition for backward compatibility.
	 * @param stub for test.
	 * @param parameters defined for this stub.
	 */
	private void testStubDefinition(final Object stub, final List<ReplicationStubFieldParameter> parameters) {
		assertNotNull(String.format("No parameter defined for stub %s", stub.getClass().getSimpleName()), parameters);
		final PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(stub);
		for (final PropertyDescriptor descriptor : descriptors) {
			if ("class".equals(descriptor.getName())) {
				continue;
			}
			Field field = ReflectionUtils.findField(stub.getClass(), descriptor.getName());
			boolean useReplacement = false;
			if (field == null) {
				ReplicationStubFieldParameter parameter = getReplicationParameterForReplacement(parameters, descriptor.getName());
				if (parameter != null) {
					useReplacement = true;
					field = ReflectionUtils.findField(stub.getClass(), parameter.getName());
					assertNotNull(String.format("No field could be loaded for descriptor with name %s for stub %s with alias %s", descriptor.getName(),
							stub.getClass().getName(), parameter.getName()), field);
				}
			}
			assertNotNull(String.format("No field could be loaded for descriptor with name %s for stub %s possible alias used", descriptor.getName(),
					stub.getClass().getName()),field);
			final XmlElement elementAnotation = field.getAnnotation(XmlElement.class);
			final boolean requiredField = elementAnotation != null && elementAnotation.required();
			boolean founded = false;
			for (final ReplicationStubFieldParameter parameter : parameters) {
				if (descriptor.getName().equals(useReplacement? parameter.getReplacementName() : parameter.getName())) {
					founded = true;
					assertFalse(String.format("Not used alias defined for parameter with name %s and alias %s", parameter.getName(),
							parameter.getReplacementName()), !useReplacement && !parameter.isEmptyReplacement());
					parameter.setFound(founded);
					//we should check this descriptor
					Method readMethod = descriptor.getReadMethod();
					if (readMethod == null) {
						if (parameter.getType().equals(Boolean.class)) {
							String readMethodName = "is" + WordUtils.capitalize(descriptor.getName());
							readMethod = ReflectionUtils.findMethod(stub.getClass(), readMethodName);
						}
					}
					assertNotNull(String.format("There are no getter for %s field found on %s stub", descriptor.getName(), stub.getClass().getName()), readMethod);
					assertEquals(String.format("test property - %s return type incompatible with defined type %s", descriptor.getName(),
							parameter.getType()), parameter.getType(), readMethod.getReturnType());
					assertEquals(String.format("test property - %s should have one parameter for setter", descriptor.getName()), 1,
							descriptor.getWriteMethod().getParameterTypes().length);
					assertEquals(String.format("test property - %s parameter type for setter incompatible with defined type %s",
							descriptor.getName(), parameter.getType()), parameter.getType(), descriptor.getWriteMethod().getParameterTypes()[0]);
					assertEquals(String.format("Both method modifiers should be equal for property %s", descriptor.getName()),
							readMethod.getModifiers(), descriptor.getWriteMethod().getModifiers());
					assertTrue(String.format("Method modifiers should be public for property %s", descriptor.getName()),
							readMethod.getModifiers() == Modifier.PUBLIC);
					assertTrue(String.format("Required anotation should be equal to defined for property %s", descriptor.getName()),
							parameter.isRequerdedField() == requiredField);
				}
			}
			if (requiredField) {
				assertTrue(String.format("Required field descriptor not defined in property map %s", descriptor.getName()), founded);
			} else {
				assertTrue(String.format("Advise field descriptor not defined in property map %s", descriptor.getName()), founded);
			}
		}
		for (final ReplicationStubFieldParameter parameter : parameters) {
			assertTrue(String.format("Stub %s have not defined field %s", stub.getClass().getName(), parameter.getName()), parameter.isFound());
		}
	}

	/**
	 * Stub fields parameter object.
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

		public boolean isListWithDefinedParameters() {
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

package ish.oncourse.webservices.soap.v4.stubs;

import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.util.*;
import ish.oncourse.webservices.v4.stubs.reference.*;
import ish.oncourse.webservices.v4.stubs.replication.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.*;

import static junit.framework.Assert.*;

/**
 * Stub compatibility check for V4 stubs.
 * This test should care of stub health till V4 stubs will be supported.
 * Test stubsPropertyMap definition should not be updated at all, because only if new fields will be added into stub test execution result will be OK.
 * @author vdavidovich
 */
public class StubsCompatibilityCheckTest extends ServiceTest {
	
	/**
	 * Map with defined stub's fields paramethers.
	 */
	private Map<String,List<ReplicationStubFieldParamether>> stubsPropertyMap = new HashMap<String,List<ReplicationStubFieldParamether>>();
	
	private static String getStubName(Class<? extends GenericReplicationStub> clazz) {
		return getName(clazz);
	}
	
	private static String getName(@SuppressWarnings("rawtypes") Class clazz) {
		int index = clazz.getName().lastIndexOf(".") + 1;
		return clazz.getName().substring(index);
	}
	
	private static String getReferenceStubName(Class<? extends GenericReferenceStub> clazz) {
		return getName(clazz);
	}
	
	@Override
	public void cleanup() throws Exception {}

	@Before
    public void setupDataSet() throws Exception {
		final List<ReplicationStubFieldParamether> attendanceParamethers = fillDefaultReplicationStubFields();
		attendanceParamethers.add(new ReplicationStubFieldParamether("attendanceType", Integer.class));
		attendanceParamethers.add(new ReplicationStubFieldParamether("markerId", Long.class));
		attendanceParamethers.add(new ReplicationStubFieldParamether("sessionId", Long.class));
		attendanceParamethers.add(new ReplicationStubFieldParamether("studentId", Long.class));
		stubsPropertyMap.put(getStubName(AttendanceStub.class), attendanceParamethers);
		final List<ReplicationStubFieldParamether> binaryDataParamethers = fillDefaultReplicationStubFields();
		binaryDataParamethers.add(new ReplicationStubFieldParamether("binaryInfoId", Long.class));
		binaryDataParamethers.add(new ReplicationStubFieldParamether("content", byte[].class));
		stubsPropertyMap.put(getStubName(BinaryDataStub.class), binaryDataParamethers);
		final List<ReplicationStubFieldParamether> binaryInfoParamethers = fillDefaultReplicationStubFields();
		binaryInfoParamethers.add(new ReplicationStubFieldParamether("byteSize", Long.class));
		binaryInfoParamethers.add(new ReplicationStubFieldParamether("webVisible", Boolean.class));
		binaryInfoParamethers.add(new ReplicationStubFieldParamether("mimeType", String.class));
		binaryInfoParamethers.add(new ReplicationStubFieldParamether("name", String.class));
		binaryInfoParamethers.add(new ReplicationStubFieldParamether("pixelHeight", Integer.class));
		binaryInfoParamethers.add(new ReplicationStubFieldParamether("pixelWidth", Integer.class));
		binaryInfoParamethers.add(new ReplicationStubFieldParamether("referenceNumber", Integer.class));
		stubsPropertyMap.put(getStubName(BinaryInfoStub.class), binaryInfoParamethers);
		final List<ReplicationStubFieldParamether> concessionTypeParamethers = fillDefaultReplicationStubFields();
		concessionTypeParamethers.add(new ReplicationStubFieldParamether("credentialExpiryDays", Integer.class));
		concessionTypeParamethers.add(new ReplicationStubFieldParamether("hasConcessionNumber", Boolean.class));
		concessionTypeParamethers.add(new ReplicationStubFieldParamether("hasExpiryDate", Boolean.class));
		concessionTypeParamethers.add(new ReplicationStubFieldParamether("isConcession", Boolean.class));
		concessionTypeParamethers.add(new ReplicationStubFieldParamether("isEnabled", Boolean.class));
		concessionTypeParamethers.add(new ReplicationStubFieldParamether("name", String.class));
		concessionTypeParamethers.add(new ReplicationStubFieldParamether("requiresCredentialCheck", Boolean.class));
		stubsPropertyMap.put(getStubName(ConcessionTypeStub.class), concessionTypeParamethers);
		final List<ReplicationStubFieldParamether> contactParamethers = fillDefaultReplicationStubFields();
		contactParamethers.add(new ReplicationStubFieldParamether("businessPhoneNumber", String.class));
		contactParamethers.add(new ReplicationStubFieldParamether("cookieHash", String.class));
		contactParamethers.add(new ReplicationStubFieldParamether("countryId", Long.class));
		contactParamethers.add(new ReplicationStubFieldParamether("dateOfBirth", Date.class));
		contactParamethers.add(new ReplicationStubFieldParamether("emailAddress", String.class));
		contactParamethers.add(new ReplicationStubFieldParamether("familyName", String.class));
		contactParamethers.add(new ReplicationStubFieldParamether("faxNumber", String.class));
		contactParamethers.add(new ReplicationStubFieldParamether("givenName", String.class));
		contactParamethers.add(new ReplicationStubFieldParamether("homePhoneNumber", String.class));
		contactParamethers.add(new ReplicationStubFieldParamether("company", Boolean.class));
		contactParamethers.add(new ReplicationStubFieldParamether("male", Boolean.class));
		contactParamethers.add(new ReplicationStubFieldParamether("marketingViaEmailAllowed", Boolean.class));
		contactParamethers.add(new ReplicationStubFieldParamether("marketingViaPostAllowed", Boolean.class));
		contactParamethers.add(new ReplicationStubFieldParamether("marketingViaSMSAllowed", Boolean.class));
		contactParamethers.add(new ReplicationStubFieldParamether("mobilePhoneNumber", String.class));
		contactParamethers.add(new ReplicationStubFieldParamether("password", String.class));
		contactParamethers.add(new ReplicationStubFieldParamether("passwordHash", String.class));
		contactParamethers.add(new ReplicationStubFieldParamether("postcode", String.class));
		contactParamethers.add(new ReplicationStubFieldParamether("state", String.class));
		contactParamethers.add(new ReplicationStubFieldParamether("street", String.class));
		contactParamethers.add(new ReplicationStubFieldParamether("suburb", String.class));
		contactParamethers.add(new ReplicationStubFieldParamether("taxFileNumber", String.class));
		contactParamethers.add(new ReplicationStubFieldParamether("uniqueCode", String.class));
		contactParamethers.add(new ReplicationStubFieldParamether("studentId", Long.class));
		contactParamethers.add(new ReplicationStubFieldParamether("tutorId", Long.class));
		stubsPropertyMap.put(getStubName(ContactStub.class), contactParamethers);
		final List<ReplicationStubFieldParamether> courseClassParamethers = fillDefaultReplicationStubFields();
		courseClassParamethers.add(new ReplicationStubFieldParamether("cancelled", Boolean.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("code", String.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("countOfSessions", Integer.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("deliveryMode", Integer.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("detail", String.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("detailTextile", String.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("endDate", Date.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("feeExGst", BigDecimal.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("feeGst", BigDecimal.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("webVisible", Boolean.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("materials", String.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("materialsTextile", String.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("maximumPlaces", Integer.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("minimumPlaces", Integer.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("minutesPerSession", Integer.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("sessionDetail", String.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("sessionDetailTextile", String.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("startDate", Date.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("startingMinutePerSession", Integer.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("timeZone", String.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("courseId", Long.class));
		courseClassParamethers.add(new ReplicationStubFieldParamether("roomId", Long.class));
		stubsPropertyMap.put(getStubName(CourseClassStub.class), courseClassParamethers);
		final List<ReplicationStubFieldParamether> courseParamethers = fillDefaultReplicationStubFields();
		courseParamethers.add(new ReplicationStubFieldParamether("allowWaitingList", Boolean.class));
		courseParamethers.add(new ReplicationStubFieldParamether("code", String.class));
		courseParamethers.add(new ReplicationStubFieldParamether("detail", String.class));
		courseParamethers.add(new ReplicationStubFieldParamether("detailTextile", String.class));
		courseParamethers.add(new ReplicationStubFieldParamether("fieldOfEducation", String.class));
		courseParamethers.add(new ReplicationStubFieldParamether("sufficientForQualification", Boolean.class));
		courseParamethers.add(new ReplicationStubFieldParamether("vetCourse", "VETCourse",Boolean.class));
		courseParamethers.add(new ReplicationStubFieldParamether("webVisible", Boolean.class));
		courseParamethers.add(new ReplicationStubFieldParamether("name", String.class));
		courseParamethers.add(new ReplicationStubFieldParamether("nominalHours", BigDecimal.class));		
		courseParamethers.add(new ReplicationStubFieldParamether("searchText", String.class));
		courseParamethers.add(new ReplicationStubFieldParamether("qualificationId", Long.class));
		stubsPropertyMap.put(getStubName(CourseStub.class), courseParamethers);
		final List<ReplicationStubFieldParamether> discountParamethers = fillDefaultReplicationStubFields();
		discountParamethers.add(new ReplicationStubFieldParamether("code", String.class));
		discountParamethers.add(new ReplicationStubFieldParamether("detail", String.class));
		discountParamethers.add(new ReplicationStubFieldParamether("combinationType", Boolean.class));
		discountParamethers.add(new ReplicationStubFieldParamether("discountAmount", BigDecimal.class));
		discountParamethers.add(new ReplicationStubFieldParamether("discountRate", BigDecimal.class));
		discountParamethers.add(new ReplicationStubFieldParamether("codeRequired", Boolean.class));
		discountParamethers.add(new ReplicationStubFieldParamether("maximumDiscount", BigDecimal.class));
		discountParamethers.add(new ReplicationStubFieldParamether("minimumDiscount", BigDecimal.class));
		discountParamethers.add(new ReplicationStubFieldParamether("name", String.class));
		discountParamethers.add(new ReplicationStubFieldParamether("roundingMode", Integer.class));
		discountParamethers.add(new ReplicationStubFieldParamether("studentAge", Integer.class));
		discountParamethers.add(new ReplicationStubFieldParamether("studentAgeOperator", String.class));
		discountParamethers.add(new ReplicationStubFieldParamether("studentEnrolledWithinDays", Integer.class));
		discountParamethers.add(new ReplicationStubFieldParamether("studentPostcodes", String.class));
		discountParamethers.add(new ReplicationStubFieldParamether("validFrom", Date.class));
		discountParamethers.add(new ReplicationStubFieldParamether("validTo", Date.class));
		discountParamethers.add(new ReplicationStubFieldParamether("discountType", Integer.class));
		stubsPropertyMap.put(getStubName(DiscountStub.class), discountParamethers);
		final List<ReplicationStubFieldParamether> enrolmentParamethers = fillDefaultReplicationStubFields();
		enrolmentParamethers.add(new ReplicationStubFieldParamether("reasonForStudy", Integer.class));
		enrolmentParamethers.add(new ReplicationStubFieldParamether("status", String.class));
		enrolmentParamethers.add(new ReplicationStubFieldParamether("source", String.class));
		enrolmentParamethers.add(new ReplicationStubFieldParamether("studentId", Long.class));
		enrolmentParamethers.add(new ReplicationStubFieldParamether("courseClassId", Long.class));
		enrolmentParamethers.add(new ReplicationStubFieldParamether("invoiceLineId", Long.class));
		stubsPropertyMap.put(getStubName(EnrolmentStub.class), enrolmentParamethers);
		final List<ReplicationStubFieldParamether> invoiceLineDiscountParamethers = fillDefaultReplicationStubFields();
		invoiceLineDiscountParamethers.add(new ReplicationStubFieldParamether("discountId", Long.class));
		invoiceLineDiscountParamethers.add(new ReplicationStubFieldParamether("invoiceLineId", Long.class));
		stubsPropertyMap.put(getStubName(InvoiceLineDiscountStub.class), invoiceLineDiscountParamethers);
		final List<ReplicationStubFieldParamether> invoiceLineParamethers = fillDefaultReplicationStubFields();
		invoiceLineParamethers.add(new ReplicationStubFieldParamether("description", String.class));
		invoiceLineParamethers.add(new ReplicationStubFieldParamether("discountEachExTax", BigDecimal.class));
		invoiceLineParamethers.add(new ReplicationStubFieldParamether("priceEachExTax", BigDecimal.class));
		invoiceLineParamethers.add(new ReplicationStubFieldParamether("quantity", BigDecimal.class));
		invoiceLineParamethers.add(new ReplicationStubFieldParamether("taxEach", BigDecimal.class));
		invoiceLineParamethers.add(new ReplicationStubFieldParamether("title", String.class));
		invoiceLineParamethers.add(new ReplicationStubFieldParamether("unit", String.class));
		invoiceLineParamethers.add(new ReplicationStubFieldParamether("enrolmentId", Long.class));
		invoiceLineParamethers.add(new ReplicationStubFieldParamether("invoiceId", Long.class));
		stubsPropertyMap.put(getStubName(InvoiceLineStub.class), invoiceLineParamethers);
		final List<ReplicationStubFieldParamether> invoiceParamethers = fillDefaultReplicationStubFields();
		invoiceParamethers.add(new ReplicationStubFieldParamether("amountOwing", BigDecimal.class));
		invoiceParamethers.add(new ReplicationStubFieldParamether("billToAddress", String.class));
		invoiceParamethers.add(new ReplicationStubFieldParamether("customerPO", String.class));
		invoiceParamethers.add(new ReplicationStubFieldParamether("customerReference", String.class));
		invoiceParamethers.add(new ReplicationStubFieldParamether("dateDue", Date.class));
		invoiceParamethers.add(new ReplicationStubFieldParamether("description", String.class));
		invoiceParamethers.add(new ReplicationStubFieldParamether("invoiceDate", Date.class));
		invoiceParamethers.add(new ReplicationStubFieldParamether("invoiceNumber", Long.class));
		invoiceParamethers.add(new ReplicationStubFieldParamether("publicNotes", String.class));
		invoiceParamethers.add(new ReplicationStubFieldParamether("shippingAddress", String.class));
		invoiceParamethers.add(new ReplicationStubFieldParamether("source", String.class));
		invoiceParamethers.add(new ReplicationStubFieldParamether("status", String.class));
		invoiceParamethers.add(new ReplicationStubFieldParamether("totalExGst", BigDecimal.class));
		invoiceParamethers.add(new ReplicationStubFieldParamether("totalGst", BigDecimal.class));
		invoiceParamethers.add(new ReplicationStubFieldParamether("contactId", Long.class));
		stubsPropertyMap.put(getStubName(InvoiceStub.class), invoiceParamethers);
		final List<ReplicationStubFieldParamether> messagePersonParamethers = fillDefaultReplicationStubFields();
		messagePersonParamethers.add(new ReplicationStubFieldParamether("destinationAddress", String.class));
		messagePersonParamethers.add(new ReplicationStubFieldParamether("numberOfAttempts", Integer.class));
		messagePersonParamethers.add(new ReplicationStubFieldParamether("response", String.class));
		messagePersonParamethers.add(new ReplicationStubFieldParamether("status", Integer.class));
		messagePersonParamethers.add(new ReplicationStubFieldParamether("timeOfDelivery", Date.class));
		messagePersonParamethers.add(new ReplicationStubFieldParamether("type", Integer.class));
		messagePersonParamethers.add(new ReplicationStubFieldParamether("contactId", Long.class));
		messagePersonParamethers.add(new ReplicationStubFieldParamether("messageId", Long.class));
		messagePersonParamethers.add(new ReplicationStubFieldParamether("studentId", Long.class));
		messagePersonParamethers.add(new ReplicationStubFieldParamether("tutorId", Long.class));
		stubsPropertyMap.put(getStubName(MessagePersonStub.class), messagePersonParamethers);
		final List<ReplicationStubFieldParamether> outcomeParamethers = fillDefaultReplicationStubFields();
		outcomeParamethers.add(new ReplicationStubFieldParamether("deliveryMode", Integer.class));
		outcomeParamethers.add(new ReplicationStubFieldParamether("fundingSource", Integer.class));
		outcomeParamethers.add(new ReplicationStubFieldParamether("reportableHours", BigDecimal.class));
		outcomeParamethers.add(new ReplicationStubFieldParamether("hoursAttended", Integer.class));
		outcomeParamethers.add(new ReplicationStubFieldParamether("enrolmentId", Long.class));
		outcomeParamethers.add(new ReplicationStubFieldParamether("moduleId", Long.class));
		outcomeParamethers.add(new ReplicationStubFieldParamether("startDate", Date.class));
		outcomeParamethers.add(new ReplicationStubFieldParamether("endDate", Date.class));
		outcomeParamethers.add(new ReplicationStubFieldParamether("status", Integer.class));
		stubsPropertyMap.put(getStubName(OutcomeStub.class), outcomeParamethers);
		final List<ReplicationStubFieldParamether> paymentInLineParamethers = fillDefaultReplicationStubFields();
		paymentInLineParamethers.add(new ReplicationStubFieldParamether("amount", BigDecimal.class));
		paymentInLineParamethers.add(new ReplicationStubFieldParamether("invoiceId", Long.class));
		paymentInLineParamethers.add(new ReplicationStubFieldParamether("paymentInId", Long.class));
		stubsPropertyMap.put(getStubName(PaymentInLineStub.class), paymentInLineParamethers);
		final List<ReplicationStubFieldParamether> paymentInParamethers = fillDefaultReplicationStubFields();
		paymentInParamethers.add(new ReplicationStubFieldParamether("amount", BigDecimal.class));
		paymentInParamethers.add(new ReplicationStubFieldParamether("contactId", Long.class));
		paymentInParamethers.add(new ReplicationStubFieldParamether("source", String.class));
		paymentInParamethers.add(new ReplicationStubFieldParamether("status", Integer.class));
		paymentInParamethers.add(new ReplicationStubFieldParamether("type", Integer.class));
		paymentInParamethers.add(new ReplicationStubFieldParamether("privateNotes", String.class));
		paymentInParamethers.add(new ReplicationStubFieldParamether("gatewayReference", String.class));
		paymentInParamethers.add(new ReplicationStubFieldParamether("gatewayResponse", String.class));
		paymentInParamethers.add(new ReplicationStubFieldParamether("sessionId", String.class));
		paymentInParamethers.add(new ReplicationStubFieldParamether("creditCardExpiry", String.class));
		paymentInParamethers.add(new ReplicationStubFieldParamether("creditCardName", String.class));
		paymentInParamethers.add(new ReplicationStubFieldParamether("creditCardNumber", String.class));
		paymentInParamethers.add(new ReplicationStubFieldParamether("creditCardType", String.class));
		paymentInParamethers.add(new ReplicationStubFieldParamether("dateBanked", Date.class));
		stubsPropertyMap.put(getStubName(PaymentInStub.class), paymentInParamethers);
		final List<ReplicationStubFieldParamether> paymentOutParamethers = fillDefaultReplicationStubFields();
		paymentOutParamethers.add(new ReplicationStubFieldParamether("amount", BigDecimal.class));
		paymentOutParamethers.add(new ReplicationStubFieldParamether("contactId", Long.class));
		paymentOutParamethers.add(new ReplicationStubFieldParamether("paymentInTxnReference", String.class));
		paymentOutParamethers.add(new ReplicationStubFieldParamether("type", Integer.class));
		paymentOutParamethers.add(new ReplicationStubFieldParamether("source", String.class));
		paymentOutParamethers.add(new ReplicationStubFieldParamether("status", Integer.class));
		paymentOutParamethers.add(new ReplicationStubFieldParamether("dateBanked", Date.class));
		paymentOutParamethers.add(new ReplicationStubFieldParamether("datePaid", Date.class));
		stubsPropertyMap.put(getStubName(PaymentOutStub.class), paymentOutParamethers);
		final List<ReplicationStubFieldParamether> preferenceParamethers = fillDefaultReplicationStubFields();
		preferenceParamethers.add(new ReplicationStubFieldParamether("name", String.class));
		preferenceParamethers.add(new ReplicationStubFieldParamether("value", byte[].class));
		preferenceParamethers.add(new ReplicationStubFieldParamether("valueString", String.class));
		stubsPropertyMap.put(getStubName(PreferenceStub.class), preferenceParamethers);
		final List<ReplicationStubFieldParamether> roomParamethers = fillDefaultReplicationStubFields();
		roomParamethers.add(new ReplicationStubFieldParamether("capacity", Integer.class));
		roomParamethers.add(new ReplicationStubFieldParamether("directions", String.class));
		roomParamethers.add(new ReplicationStubFieldParamether("directionsTextile", String.class));
		roomParamethers.add(new ReplicationStubFieldParamether("facilities", String.class));
		roomParamethers.add(new ReplicationStubFieldParamether("facilitiesTextile", String.class));
		roomParamethers.add(new ReplicationStubFieldParamether("name", String.class));
		roomParamethers.add(new ReplicationStubFieldParamether("siteId", Long.class));
		stubsPropertyMap.put(getStubName(RoomStub.class), roomParamethers);
		final List<ReplicationStubFieldParamether> siteParamethers = fillDefaultReplicationStubFields();
		siteParamethers.add(new ReplicationStubFieldParamether("drivingDirections", String.class));
		siteParamethers.add(new ReplicationStubFieldParamether("webVisible", Boolean.class));
		siteParamethers.add(new ReplicationStubFieldParamether("latitude", BigDecimal.class));
		siteParamethers.add(new ReplicationStubFieldParamether("longitude", BigDecimal.class));
		siteParamethers.add(new ReplicationStubFieldParamether("name", String.class));
		siteParamethers.add(new ReplicationStubFieldParamether("postcode", String.class));
		siteParamethers.add(new ReplicationStubFieldParamether("publicTransportDirections", String.class));
		siteParamethers.add(new ReplicationStubFieldParamether("specialInstructions", String.class));
		siteParamethers.add(new ReplicationStubFieldParamether("state", String.class));
		siteParamethers.add(new ReplicationStubFieldParamether("street", String.class));
		siteParamethers.add(new ReplicationStubFieldParamether("suburb", String.class));
		siteParamethers.add(new ReplicationStubFieldParamether("timeZone", String.class));
		siteParamethers.add(new ReplicationStubFieldParamether("countryId", Long.class));
		stubsPropertyMap.put(getStubName(SiteStub.class), siteParamethers);
		final List<ReplicationStubFieldParamether> studentConcessionParamethers = fillDefaultReplicationStubFields();
		studentConcessionParamethers.add(new ReplicationStubFieldParamether("authorisationExpiresOn", Date.class));
		studentConcessionParamethers.add(new ReplicationStubFieldParamether("authorisedOn", Date.class));
		studentConcessionParamethers.add(new ReplicationStubFieldParamether("concessionNumber", String.class));
		studentConcessionParamethers.add(new ReplicationStubFieldParamether("expiresOn", Date.class));
		studentConcessionParamethers.add(new ReplicationStubFieldParamether("timeZone", String.class));
		studentConcessionParamethers.add(new ReplicationStubFieldParamether("concessionTypeId", Long.class));
		studentConcessionParamethers.add(new ReplicationStubFieldParamether("studentId", Long.class));
		stubsPropertyMap.put(getStubName(StudentConcessionStub.class), studentConcessionParamethers);
		final List<ReplicationStubFieldParamether> studentParamethers = fillDefaultReplicationStubFields();
		studentParamethers.add(new ReplicationStubFieldParamether("concessionType", Integer.class));
		studentParamethers.add(new ReplicationStubFieldParamether("disabilityType", Integer.class));
		studentParamethers.add(new ReplicationStubFieldParamether("englishProficiency", Integer.class));
		studentParamethers.add(new ReplicationStubFieldParamether("highestSchoolLevel", Integer.class));
		studentParamethers.add(new ReplicationStubFieldParamether("indigenousStatus", Integer.class));
		studentParamethers.add(new ReplicationStubFieldParamether("overseasClient", Boolean.class));
		studentParamethers.add(new ReplicationStubFieldParamether("stillAtSchool", Boolean.class));
		studentParamethers.add(new ReplicationStubFieldParamether("priorEducationCode", Integer.class));
		studentParamethers.add(new ReplicationStubFieldParamether("labourForceType", Integer.class));
		studentParamethers.add(new ReplicationStubFieldParamether("yearSchoolCompleted", Integer.class));
		studentParamethers.add(new ReplicationStubFieldParamether("contactId", Long.class));
		studentParamethers.add(new ReplicationStubFieldParamether("languageId", Long.class));
		studentParamethers.add(new ReplicationStubFieldParamether("countryOfBirthId", Long.class));
		studentParamethers.add(new ReplicationStubFieldParamether("languageHomeId", Long.class));
		stubsPropertyMap.put(getStubName(StudentStub.class), studentParamethers);
		final List<ReplicationStubFieldParamether> systemUserParamethers = fillDefaultReplicationStubFields();
		systemUserParamethers.add(new ReplicationStubFieldParamether("editCMS", Boolean.class));
		systemUserParamethers.add(new ReplicationStubFieldParamether("editTara", Boolean.class));
		systemUserParamethers.add(new ReplicationStubFieldParamether("email", String.class));
		systemUserParamethers.add(new ReplicationStubFieldParamether("firstName", String.class));
		systemUserParamethers.add(new ReplicationStubFieldParamether("surname", String.class));
		systemUserParamethers.add(new ReplicationStubFieldParamether("password", String.class));
		systemUserParamethers.add(new ReplicationStubFieldParamether("login", String.class));
		systemUserParamethers.add(new ReplicationStubFieldParamether("lastLoginIP", String.class));
		systemUserParamethers.add(new ReplicationStubFieldParamether("lastLoginOn", Date.class));
		systemUserParamethers.add(new ReplicationStubFieldParamether("isActive", Boolean.class));
		systemUserParamethers.add(new ReplicationStubFieldParamether("isAdmin", Boolean.class));
		systemUserParamethers.add(new ReplicationStubFieldParamether("defaultAdministrationCentreId", Long.class));
		stubsPropertyMap.put(getStubName(SystemUserStub.class), systemUserParamethers);
		final List<ReplicationStubFieldParamether> tagRelationParamethers = fillDefaultReplicationStubFields();
		tagRelationParamethers.add(new ReplicationStubFieldParamether("entityAngelId", Long.class));
		tagRelationParamethers.add(new ReplicationStubFieldParamether("entityName", String.class));
		tagRelationParamethers.add(new ReplicationStubFieldParamether("entityWillowId", Long.class));
		tagRelationParamethers.add(new ReplicationStubFieldParamether("tagId", Long.class));
		stubsPropertyMap.put(getStubName(TagRelationStub.class), tagRelationParamethers);
		final List<ReplicationStubFieldParamether> courseClassTutorParamethers = fillDefaultReplicationStubFields();
		courseClassTutorParamethers.add(new ReplicationStubFieldParamether("courseClassId", Long.class));
		courseClassTutorParamethers.add(new ReplicationStubFieldParamether("tutorId", Long.class));
		courseClassTutorParamethers.add(new ReplicationStubFieldParamether("confirmedOn", Date.class));
		courseClassTutorParamethers.add(new ReplicationStubFieldParamether("inPublicity", Boolean.class));
		stubsPropertyMap.put(getStubName(CourseClassTutorStub.class), courseClassTutorParamethers);
		final List<ReplicationStubFieldParamether> tutorParamethers = fillDefaultReplicationStubFields();
		tutorParamethers.add(new ReplicationStubFieldParamether("finishDate", Date.class));
		tutorParamethers.add(new ReplicationStubFieldParamether("resume", String.class));
		tutorParamethers.add(new ReplicationStubFieldParamether("resumeTextile", String.class));
		tutorParamethers.add(new ReplicationStubFieldParamether("startDate", Date.class));
		tutorParamethers.add(new ReplicationStubFieldParamether("contactId", Long.class));
		stubsPropertyMap.put(getStubName(TutorStub.class), tutorParamethers);
		final List<ReplicationStubFieldParamether> waitingListParamethers = fillDefaultReplicationStubFields();
		waitingListParamethers.add(new ReplicationStubFieldParamether("detail", String.class));
		waitingListParamethers.add(new ReplicationStubFieldParamether("studentCount", Integer.class));
		waitingListParamethers.add(new ReplicationStubFieldParamether("courseId", Long.class));
		waitingListParamethers.add(new ReplicationStubFieldParamether("studentId", Long.class));
		stubsPropertyMap.put(getStubName(WaitingListStub.class), waitingListParamethers);
		final List<ReplicationStubFieldParamether> certificateParamethers = fillDefaultReplicationStubFields();
		certificateParamethers.add(new ReplicationStubFieldParamether("certificateNumber", Long.class));
		certificateParamethers.add(new ReplicationStubFieldParamether("endDate", Date.class));
		certificateParamethers.add(new ReplicationStubFieldParamether("fundingSource", Integer.class));
		certificateParamethers.add(new ReplicationStubFieldParamether("qualification", Boolean.class));
		certificateParamethers.add(new ReplicationStubFieldParamether("printedWhen", Date.class));
		certificateParamethers.add(new ReplicationStubFieldParamether("privateNotes", String.class));
		certificateParamethers.add(new ReplicationStubFieldParamether("publicNotes", String.class));
		certificateParamethers.add(new ReplicationStubFieldParamether("qualificationId", Long.class));
		certificateParamethers.add(new ReplicationStubFieldParamether("revokedWhen", Date.class));
		certificateParamethers.add(new ReplicationStubFieldParamether("studentFirstName", String.class));
		certificateParamethers.add(new ReplicationStubFieldParamether("studentLastName", String.class));
		certificateParamethers.add(new ReplicationStubFieldParamether("studentId", Long.class));
		stubsPropertyMap.put(getStubName(CertificateStub.class), certificateParamethers);
		final List<ReplicationStubFieldParamether> certificateOutcomeParamethers = fillDefaultReplicationStubFields();
		certificateOutcomeParamethers.add(new ReplicationStubFieldParamether("certificateId", Long.class));
		certificateOutcomeParamethers.add(new ReplicationStubFieldParamether("outcomeId", Long.class));
		stubsPropertyMap.put(getStubName(CertificateOutcomeStub.class), certificateOutcomeParamethers);
		final List<ReplicationStubFieldParamether> deletedStubParamethers = fillDefaultReplicationStubFields();
		stubsPropertyMap.put(getStubName(DeletedStub.class), deletedStubParamethers);
		final List<ReplicationStubFieldParamether> hollowStubParamethers = fillDefaultReplicationStubFields();
		stubsPropertyMap.put(getStubName(HollowStub.class), hollowStubParamethers);
		final List<ReplicationStubFieldParamether> queuedStatisticParamethers = fillDefaultReplicationStubFields();
		queuedStatisticParamethers.add(new ReplicationStubFieldParamether("stackedTransactionsCount", Long.class));
		queuedStatisticParamethers.add(new ReplicationStubFieldParamether("stackedCount", Long.class));
		queuedStatisticParamethers.add(new ReplicationStubFieldParamether("stackedEntityIdentifier", String.class));
		queuedStatisticParamethers.add(new ReplicationStubFieldParamether("receivedTimestamp", Date.class));
		queuedStatisticParamethers.add(new ReplicationStubFieldParamether("cleanupStub", Boolean.class));
		stubsPropertyMap.put(getStubName(QueuedStatisticStub.class), queuedStatisticParamethers);
		final List<ReplicationStubFieldParamether> replicationStubParamethers = fillDefaultReplicationStubFields();
		stubsPropertyMap.put(getStubName(ReplicationStub.class), replicationStubParamethers);
		final List<ReplicationStubFieldParamether> referenceStubParamethers = fillDefaultReferenceStubFields();
		stubsPropertyMap.put(getReferenceStubName(ReferenceStub.class), referenceStubParamethers);
		final List<ReplicationStubFieldParamether> countryParamethers = fillDefaultReferenceStubFields();
		countryParamethers.add(new ReplicationStubFieldParamether("asccssCode", String.class, false));
		countryParamethers.add(new ReplicationStubFieldParamether("created", Date.class, false));
		countryParamethers.add(new ReplicationStubFieldParamether("ishVersion", Long.class, false));
		countryParamethers.add(new ReplicationStubFieldParamether("isoCodeAlpha2", String.class, false));
		countryParamethers.add(new ReplicationStubFieldParamether("isoCodeAlpha3", String.class, false));
		countryParamethers.add(new ReplicationStubFieldParamether("isoCodeNumeric", Integer.class, false));
		countryParamethers.add(new ReplicationStubFieldParamether("modified", Date.class, false));
		countryParamethers.add(new ReplicationStubFieldParamether("name", String.class, false));
		countryParamethers.add(new ReplicationStubFieldParamether("saccCode", Integer.class, false));
		stubsPropertyMap.put(getReferenceStubName(CountryStub.class), countryParamethers);
		final List<ReplicationStubFieldParamether> languageParamethers = fillDefaultReferenceStubFields();
		languageParamethers.add(new ReplicationStubFieldParamether("absCode", String.class, false));
		languageParamethers.add(new ReplicationStubFieldParamether("created", Date.class, false));
		languageParamethers.add(new ReplicationStubFieldParamether("isActive", Boolean.class, false));
		languageParamethers.add(new ReplicationStubFieldParamether("ishVersion", Long.class, false));
		languageParamethers.add(new ReplicationStubFieldParamether("modified", Date.class, false));
		languageParamethers.add(new ReplicationStubFieldParamether("name", String.class, false));
		stubsPropertyMap.put(getReferenceStubName(LanguageStub.class), languageParamethers);
		final List<ReplicationStubFieldParamether> moduleParamethers = fillDefaultReferenceStubFields();
		moduleParamethers.add(new ReplicationStubFieldParamether("created", Date.class, false));
		moduleParamethers.add(new ReplicationStubFieldParamether("disciplineCode", String.class, false));
		moduleParamethers.add(new ReplicationStubFieldParamether("fieldOfEducation", String.class, false));
		moduleParamethers.add(new ReplicationStubFieldParamether("isModule", Boolean.class, false));
		moduleParamethers.add(new ReplicationStubFieldParamether("ishVersion", Long.class, false));
		moduleParamethers.add(new ReplicationStubFieldParamether("modified", Date.class, false));
		moduleParamethers.add(new ReplicationStubFieldParamether("nationalCode", String.class, false));
		moduleParamethers.add(new ReplicationStubFieldParamether("title", String.class, false));
		moduleParamethers.add(new ReplicationStubFieldParamether("trainingPackageId", Long.class, false));
		stubsPropertyMap.put(getReferenceStubName(ModuleStub.class), moduleParamethers);
		final List<ReplicationStubFieldParamether> qualificationParamethers = fillDefaultReferenceStubFields();
		qualificationParamethers.add(new ReplicationStubFieldParamether("created", Date.class, false));
		qualificationParamethers.add(new ReplicationStubFieldParamether("anzsco", String.class, false));
		qualificationParamethers.add(new ReplicationStubFieldParamether("anzsic", String.class, false));
		qualificationParamethers.add(new ReplicationStubFieldParamether("asco", String.class, false));
		qualificationParamethers.add(new ReplicationStubFieldParamether("fieldOfEducation", String.class, false));
		qualificationParamethers.add(new ReplicationStubFieldParamether("fieldOfStudy", String.class, false));
		qualificationParamethers.add(new ReplicationStubFieldParamether("isAccreditedCourse", Boolean.class, false));
		qualificationParamethers.add(new ReplicationStubFieldParamether("ishVersion", Long.class, false));
		qualificationParamethers.add(new ReplicationStubFieldParamether("level", String.class, false));
		qualificationParamethers.add(new ReplicationStubFieldParamether("modified", Date.class, false));
		qualificationParamethers.add(new ReplicationStubFieldParamether("nationalCode", String.class, false));
		qualificationParamethers.add(new ReplicationStubFieldParamether("newApprentices", String.class, false));
		qualificationParamethers.add(new ReplicationStubFieldParamether("nominalHours", Float.class, false));
		qualificationParamethers.add(new ReplicationStubFieldParamether("reviewDate", Date.class, false));
		qualificationParamethers.add(new ReplicationStubFieldParamether("title", String.class, false));
		qualificationParamethers.add(new ReplicationStubFieldParamether("trainingPackageId", Long.class, false));
		stubsPropertyMap.put(getReferenceStubName(QualificationStub.class), qualificationParamethers);
		final List<ReplicationStubFieldParamether> trainingPackageParamethers = fillDefaultReferenceStubFields();
		trainingPackageParamethers.add(new ReplicationStubFieldParamether("copyrightCategory", String.class, false));
		trainingPackageParamethers.add(new ReplicationStubFieldParamether("copyrightContract", String.class, false));
		trainingPackageParamethers.add(new ReplicationStubFieldParamether("created", Date.class, false));
		trainingPackageParamethers.add(new ReplicationStubFieldParamether("developer", String.class, false));
		trainingPackageParamethers.add(new ReplicationStubFieldParamether("endorsementFrom", Date.class, false));
		trainingPackageParamethers.add(new ReplicationStubFieldParamether("endorsementTo", Date.class, false));
		trainingPackageParamethers.add(new ReplicationStubFieldParamether("ishVersion", Long.class, false));
		trainingPackageParamethers.add(new ReplicationStubFieldParamether("modified", Date.class, false));
		trainingPackageParamethers.add(new ReplicationStubFieldParamether("nationalISC", String.class, false));
		trainingPackageParamethers.add(new ReplicationStubFieldParamether("purchaseFrom", String.class, false));
		trainingPackageParamethers.add(new ReplicationStubFieldParamether("title", String.class, false));
		trainingPackageParamethers.add(new ReplicationStubFieldParamether("type", String.class, false));
		stubsPropertyMap.put(getReferenceStubName(TrainingPackageStub.class), trainingPackageParamethers);
		final List<ReplicationStubFieldParamether> referenceResultParamethers = new ArrayList<ReplicationStubFieldParamether>();
		final ReplicationStubFieldParamether countryOrLanguageOrModule = new ReplicationStubFieldParamether("countryOrLanguageOrModule", true, false, 
			ReferenceStub.class);
		@SuppressWarnings("rawtypes")
		final List<Class> countryOrLanguageOrModuleAvailableClasses = new ArrayList<Class>();
		countryOrLanguageOrModuleAvailableClasses.add(QualificationStub.class);
		countryOrLanguageOrModuleAvailableClasses.add(TrainingPackageStub.class);
		countryOrLanguageOrModuleAvailableClasses.add(ModuleStub.class);
		countryOrLanguageOrModuleAvailableClasses.add(LanguageStub.class);
		countryOrLanguageOrModuleAvailableClasses.add(CountryStub.class);
		countryOrLanguageOrModule.getAvailableClasses().addAll(countryOrLanguageOrModuleAvailableClasses);
		referenceResultParamethers.add(countryOrLanguageOrModule);
		final ReplicationStubFieldParamether genericCountryOrLanguageOrModule = new ReplicationStubFieldParamether("genericCountryOrLanguageOrModule", 
			"countryOrLanguageOrModule", List.class, false, null, ReferenceStub.class);
		genericCountryOrLanguageOrModule.getAvailableClasses().addAll(countryOrLanguageOrModuleAvailableClasses);
		referenceResultParamethers.add(genericCountryOrLanguageOrModule);
		stubsPropertyMap.put(getName(ReferenceResult.class), referenceResultParamethers);
		final List<ReplicationStubFieldParamether> replicationResultParamethers = new ArrayList<ReplicationStubFieldParamether>();
		replicationResultParamethers.add(new ReplicationStubFieldParamether("replicatedRecord", true, ReplicatedRecord.class));
		replicationResultParamethers.add(new ReplicationStubFieldParamether("genericReplicatedRecord", "replicatedRecord", List.class, true, null, 
			ReplicatedRecord.class));
		stubsPropertyMap.put(getName(ReplicationResult.class), replicationResultParamethers);
		final List<ReplicationStubFieldParamether> replicationRecordsParamethers = new ArrayList<ReplicationStubFieldParamether>();
		replicationRecordsParamethers.add(new ReplicationStubFieldParamether("groups", true, TransactionGroup.class));
		replicationRecordsParamethers.add(new ReplicationStubFieldParamether("genericGroups", "groups", List.class, true, null, 
			TransactionGroup.class));
		stubsPropertyMap.put(getName(ReplicationRecords.class), replicationRecordsParamethers);
		final List<ReplicationStubFieldParamether> replicatedRecordParamethers = new ArrayList<ReplicationStubFieldParamether>();
		replicatedRecordParamethers.add(new ReplicationStubFieldParamether("status", Status.class));
		replicatedRecordParamethers.add(new ReplicationStubFieldParamether("message", String.class));
		replicatedRecordParamethers.add(new ReplicationStubFieldParamether("stub", HollowStub.class));
		//TODO: check what depend to this field definition and mvn and IDE get different result
		replicatedRecordParamethers.add(new ReplicationStubFieldParamether("failedStatus", boolean.class, "status"));
		replicatedRecordParamethers.add(new ReplicationStubFieldParamether("successStatus", boolean.class, "status"));
		stubsPropertyMap.put(getName(ReplicatedRecord.class), replicatedRecordParamethers);
		final List<ReplicationStubFieldParamether> parameterEntryParamethers = new ArrayList<ReplicationStubFieldParamether>();
		parameterEntryParamethers.add(new ReplicationStubFieldParamether("name", String.class));
		parameterEntryParamethers.add(new ReplicationStubFieldParamether("value", String.class));
		stubsPropertyMap.put(getName(ParameterEntry.class), parameterEntryParamethers);
		final List<ReplicationStubFieldParamether> parametersMapParamethers = new ArrayList<ReplicationStubFieldParamether>();
		parametersMapParamethers.add(new ReplicationStubFieldParamether("entry", true, false, ParameterEntry.class));
		parametersMapParamethers.add(new ReplicationStubFieldParamether("genericEntry", "entry", List.class, false, null, ParameterEntry.class));
		stubsPropertyMap.put(getName(ParametersMap.class), parametersMapParamethers);
		final List<ReplicationStubFieldParamether> instructionStubParamethers = new ArrayList<ReplicationStubFieldParamether>();
		instructionStubParamethers.add(new ReplicationStubFieldParamether("id", Long.class));
		instructionStubParamethers.add(new ReplicationStubFieldParamether("message", String.class));
		instructionStubParamethers.add(new ReplicationStubFieldParamether("parameters", ParametersMap.class, false));
		stubsPropertyMap.put(getName(InstructionStub.class), instructionStubParamethers);
		final List<ReplicationStubFieldParamether> instructionsResponseParamethers = new ArrayList<ReplicationStubFieldParamether>();
		instructionsResponseParamethers.add(new ReplicationStubFieldParamether("_return", "return", List.class, false, null, InstructionStub.class));
		stubsPropertyMap.put(getName(GetInstructionsResponse.class), instructionsResponseParamethers);
		final List<ReplicationStubFieldParamether> faultReasonParamethers = new ArrayList<ReplicationStubFieldParamether>();
		faultReasonParamethers.add(new ReplicationStubFieldParamether("detailMessage", String.class, false));
		faultReasonParamethers.add(new ReplicationStubFieldParamether("faultCode", Integer.class, false));		
		stubsPropertyMap.put(getName(FaultReason.class), faultReasonParamethers);
		final List<ReplicationStubFieldParamether> transactionGroupParamethers = new ArrayList<ReplicationStubFieldParamether>();
		transactionGroupParamethers.add(new ReplicationStubFieldParamether("transactionKeys", List.class));
		final ReplicationStubFieldParamether attendanceOrBinaryDataOrBinaryInfo = new ReplicationStubFieldParamether(
			"attendanceOrBinaryDataOrBinaryInfo", List.class, false);
		@SuppressWarnings("rawtypes")
		final List<Class> attendanceOrBinaryDataOrBinaryInfoAvailableClasses = new ArrayList<Class>();
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(CertificateStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(SystemUserStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(ProductStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(InvoiceLineDiscountStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(SessionStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(ContactRelationTypeStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(PaymentOutStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(BinaryDataStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(DiscountMembershipStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(ContactStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(WaitingListStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(TagRelationStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(VoucherProductStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(CourseModuleStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(SiteStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(MessagePersonStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(MessageTemplateStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(VoucherStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(MembershipProductStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(AttendanceStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(BinaryInfoStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(BinaryInfoRelationStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(PaymentInLineStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(DiscountMembershipRelationTypeStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(ConcessionTypeStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(InvoiceLineStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(MembershipStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(TagRequirementStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(TagStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(PaymentInStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(ProductItemStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(InvoiceStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(StudentStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(DiscountStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(EnrolmentStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(SessionTutorStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(MessageStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(QueuedStatisticStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(PreferenceStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(DeletedStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(WaitingListSiteStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(OutcomeStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(ContactRelationStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(TutorStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(DiscountConcessionTypeStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(StudentConcessionStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(CourseClassStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(RoomStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(CertificateOutcomeStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(CourseClassTutorStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(CourseStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(DiscountCourseClassStub.class);
		attendanceOrBinaryDataOrBinaryInfoAvailableClasses.add(TutorAttendanceStub.class);
		attendanceOrBinaryDataOrBinaryInfo.getAvailableClasses().addAll(attendanceOrBinaryDataOrBinaryInfoAvailableClasses);
		transactionGroupParamethers.add(attendanceOrBinaryDataOrBinaryInfo);
		
		final ReplicationStubFieldParamether genericAttendanceOrBinaryDataOrBinaryInfo = new ReplicationStubFieldParamether(
			"genericAttendanceOrBinaryDataOrBinaryInfo", "attendanceOrBinaryDataOrBinaryInfo", List.class, false, null);
		genericAttendanceOrBinaryDataOrBinaryInfo.getAvailableClasses().addAll(attendanceOrBinaryDataOrBinaryInfoAvailableClasses);
		transactionGroupParamethers.add(genericAttendanceOrBinaryDataOrBinaryInfo);

		ReplicationStubFieldParamether replicationsStubs = new ReplicationStubFieldParamether(
				"replicationStub", "attendanceOrBinaryDataOrBinaryInfo",  List.class, false, null);
		replicationsStubs.getAvailableClasses().addAll(attendanceOrBinaryDataOrBinaryInfoAvailableClasses);
		transactionGroupParamethers.add(replicationsStubs);
		
		stubsPropertyMap.put(getName(TransactionGroup.class), transactionGroupParamethers);
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

		Map<String, String> methodReplacementMap = new HashMap<>();
		methodReplacementMap.put("replicationStub", "attendanceOrBinaryDataOrBinaryInfo");
		methodReplacementMap.put("genericAttendanceOrBinaryDataOrBinaryInfo", "attendanceOrBinaryDataOrBinaryInfo");

		testCollectorDefinition(group, stubsPropertyMap.get(getName(group.getClass())), methodReplacementMap);
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
		testCollectorDefinition(response, stubsPropertyMap.get(getName(response.getClass())), Collections.EMPTY_MAP);
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

		Map<String, String> methodReplacementMap = new HashMap<>();
		methodReplacementMap.put("genericEntry", "entry");

		testCollectorDefinition(result, stubsPropertyMap.get(getName(result.getClass())), methodReplacementMap);
	}
	
	@Test
	public void testParameterEntry() {
		final GenericParameterEntry result = new ParameterEntry();
		testStubDefinition(result, stubsPropertyMap.get(getName(result.getClass())));
	}
	
	@Test
	public void testReplicatedRecord() {
		final GenericReplicatedRecord result = new ReplicatedRecord();
		testCollectorDefinition(result, stubsPropertyMap.get(getName(result.getClass())), Collections.EMPTY_MAP);
	}
	
	@Test
	public void testReplicationRecords() {
		final GenericReplicationRecords result = new ReplicationRecords();

		Map<String, String> methodReplacementMap = new HashMap<>();
		methodReplacementMap.put("genericGroups", "groups");

		testCollectorDefinition(result, stubsPropertyMap.get(getName(result.getClass())), methodReplacementMap);
	}
	
	@Test
	public void testReplicationResult() {
		final GenericReplicationResult result = new ReplicationResult();

		Map<String, String> methodReplacementMap = new HashMap<>();
		methodReplacementMap.put("genericReplicatedRecord", "replicatedRecord");

		testCollectorDefinition(result, stubsPropertyMap.get(getName(result.getClass())), methodReplacementMap);
	}
	
	@Test
	public void testReplicationStub() {
		final GenericReplicationStub stub = new ReplicationStub() {};
		assertTrue("ReplicationStub should have abstract modifier", Modifier.isAbstract(ReplicationStub.class.getModifiers()));
		testReplicationStubDefinition(stub, stubsPropertyMap.get(getStubName(ReplicationStub.class)));
	}
	
	@Test
	public void testCountryStub() {
		final GenericReferenceStub stub = new CountryStub() ;
		testReferenceStubDefinition(stub, stubsPropertyMap.get(getReferenceStubName(stub.getClass())));
	}
	
	@Test
	public void testLanguageStub() {
		final GenericReferenceStub stub = new LanguageStub() ;
		testReferenceStubDefinition(stub, stubsPropertyMap.get(getReferenceStubName(stub.getClass())));
	}
	
	@Test
	public void testModuleStub() {
		final GenericReferenceStub stub = new ModuleStub() ;
		testReferenceStubDefinition(stub, stubsPropertyMap.get(getReferenceStubName(stub.getClass())));
	}
	
	@Test
	public void testQualificationStub() {
		final GenericReferenceStub stub = new QualificationStub() ;
		testReferenceStubDefinition(stub, stubsPropertyMap.get(getReferenceStubName(stub.getClass())));
	}
	
	@Test
	public void testReferenceResult() {
		final GenericReferenceResult result = new ReferenceResult();
		
		Map<String, String> methodReplacementMap = new HashMap<>();
		methodReplacementMap.put("genericCountryOrLanguageOrModule", "countryOrLanguageOrModule");
		
		testCollectorDefinition(result, stubsPropertyMap.get(getName(result.getClass())), methodReplacementMap);
	}
	
	@Test
	public void testReferenceStub() {
		final GenericReferenceStub stub = new ReferenceStub() {};
		assertTrue("ReferenceStub should have abstract modifier", Modifier.isAbstract(ReferenceStub.class.getModifiers()));
		testReferenceStubDefinition(stub, stubsPropertyMap.get(getReferenceStubName(ReferenceStub.class)));
	}
	
	@Test
	public void testTrainingPackageStub() {
		final GenericReferenceStub stub = new TrainingPackageStub() ;
		testReferenceStubDefinition(stub, stubsPropertyMap.get(getReferenceStubName(stub.getClass())));
	}
	
	private List<ReplicationStubFieldParamether> fillDefaultReplicationStubFields() {
		final List<ReplicationStubFieldParamether> paramethers = new ArrayList<ReplicationStubFieldParamether>();
		paramethers.add(new ReplicationStubFieldParamether("angelId", Long.class));
		paramethers.add(new ReplicationStubFieldParamether("willowId", Long.class));
		paramethers.add(new ReplicationStubFieldParamether("entityIdentifier", String.class));
		paramethers.add(new ReplicationStubFieldParamether("created", Date.class));
		paramethers.add(new ReplicationStubFieldParamether("modified", Date.class));
		return paramethers;
	}
	
	private List<ReplicationStubFieldParamether> fillDefaultReferenceStubFields() {
		final List<ReplicationStubFieldParamether> paramethers = new ArrayList<ReplicationStubFieldParamether>();
		paramethers.add(new ReplicationStubFieldParamether("willowId", Long.class, false));
		return paramethers;
	}
	
	private ReplicationStubFieldParamether getReplicationParametherForReplacement(final List<ReplicationStubFieldParamether> paramethers, 
		final String replacementName) {
		for (final ReplicationStubFieldParamether paramether : paramethers) {
			if (!paramether.isEmptyReplacement() && paramether.getReplacementName().equalsIgnoreCase(replacementName)) {
				paramether.setFound(true);
				return paramether;
			}
		}
		return null;
	}
	
	private ReplicationStubFieldParamether getReplicationParametherForDependendFields(final List<ReplicationStubFieldParamether> paramethers, 
		final String descriptorName) {
		for (final ReplicationStubFieldParamether paramether : paramethers) {
			if (StringUtils.trimToNull(paramether.getDependentFieldName()) != null && paramether.getName().equals(descriptorName)) {
				assertNotNull(String.format("Dependent field with name %s not exist for paramather %s ", paramether.getDependentFieldName(), 
					paramether.getName()), paramether.getDependentToField());
				return paramether;
			}
		}
		return null;
	}
	
	private void fillFieldDependency(final List<ReplicationStubFieldParamether> paramethers, final Object stub) {
		for (final PropertyDescriptor descriptor : PropertyUtils.getPropertyDescriptors(stub)) {
			if ("class".equals(descriptor.getName())) {
				continue;
			}
			Field field = ReflectionUtils.findField(stub.getClass(), descriptor.getName());
			if (field == null) {
				ReplicationStubFieldParamether paramether = getReplicationParametherForReplacement(paramethers, descriptor.getName());
				if (paramether != null) {
					field = ReflectionUtils.findField(stub.getClass(), paramether.getName());
					assertNotNull(String.format("No field could be loaded for descriptor with name %s for stub %s with alias %s", descriptor.getName(), 
						stub.getClass().getName(), paramether.getName()), field);
				}
			}
			if (field != null) {
				for (final ReplicationStubFieldParamether paramether : paramethers) {
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
	private void testCollectorDefinition(final Object stub, final List<ReplicationStubFieldParamether> paramethers, Map<String, String> methodReplacements) {
		assertNotNull(String.format("No paramether defined for stub %s", stub.getClass().getSimpleName()), paramethers);
		final PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(stub);
		fillFieldDependency(paramethers, stub);
		for (final PropertyDescriptor descriptor : descriptors) {
			if ("class".equals(descriptor.getName())) {
				continue;
			}
			Field field = ReflectionUtils.findField(stub.getClass(), descriptor.getName());
			boolean useReplacement = false;
			if (field == null) {
				ReplicationStubFieldParamether paramether = getReplicationParametherForReplacement(paramethers, descriptor.getName());
				if (paramether != null) {
					useReplacement = true;
					field = ReflectionUtils.findField(stub.getClass(), paramether.getName());
					assertNotNull(String.format("No field could be loaded for descriptor with name %s for stub %s with alias %s", descriptor.getName(), 
						stub.getClass().getName(), paramether.getName()), field);
				} else if (methodReplacements.keySet().contains(descriptor.getName())) {
					paramether = getReplicationParametherForReplacement(paramethers, methodReplacements.get(descriptor.getName()));
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
			final boolean requiredField = elementAnotation != null ? elementAnotation.required() : false;
			final XmlElements availableElementsAnotation = field.getAnnotation(XmlElements.class);
			@SuppressWarnings("rawtypes")
			final List<Class> availableElements = new ArrayList<Class>();
			if (availableElementsAnotation != null) {
				for (final XmlElement element : availableElementsAnotation.value()) {
					availableElements.add(element.type());
				}
			}
			boolean founded = false;
			for (final ReplicationStubFieldParamether paramether : paramethers) {
				if (descriptor.getName().equals(useReplacement? paramether.getReplacementName() : paramether.getName())) {
					founded = true;
					if (methodReplacements.keySet().contains(descriptor.getName())) {
						useReplacement = true;
					}
					assertFalse(String.format("Not used alias defined for paramether with name %s and alias %s", paramether.getName(), 
						paramether.getReplacementName()), !useReplacement && !paramether.isEmptyReplacement());
					paramether.setFound(founded);
					for (@SuppressWarnings("rawtypes") final Class availableElement : availableElements) {
						assertTrue(String.format("%s type should be  available for %s field but not defined", availableElement, paramether.getName()), 
							paramether.getAvailableClasses().contains(availableElement));
					}
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
							(readMethod.getModifiers() == 1/*public*/ || readMethod.getModifiers() == 17 || 
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
		for (final ReplicationStubFieldParamether paramether : paramethers) {
			assertTrue(String.format("Stub %s have not defined field %s", stub.getClass().getName(), paramether.getName()), paramether.isFound());
			if (StringUtils.trimToNull(paramether.getDependentFieldName()) != null) {
				assertNotNull(String.format("Dependent field with name %s should be loaded.", paramether.getDependentFieldName()), 
					paramether.getDependentToField());
			}
		}
	}
	
	private void testReferenceStubDefinition(final GenericReferenceStub stub, final List<ReplicationStubFieldParamether> paramethers) {
		testStubDefinition(stub, paramethers);
	}
	
	private void testReplicationStubDefinition(final GenericReplicationStub stub, final List<ReplicationStubFieldParamether> paramethers) {
		testStubDefinition(stub, paramethers);
	}
	
	/**
	 * Method which check current stub definition for backward compatibility.
	 * @param stub for test.
	 * @param paramethers defined for this stub.
	 */
	private void testStubDefinition(final Object stub, final List<ReplicationStubFieldParamether> paramethers) {
		assertNotNull(String.format("No paramether defined for stub %s", stub.getClass().getSimpleName()), paramethers);
		final PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(stub);
		for (final PropertyDescriptor descriptor : descriptors) {
			if ("class".equals(descriptor.getName())) {
				continue;
			}
			Field field = ReflectionUtils.findField(stub.getClass(), descriptor.getName());
			boolean useReplacement = false;
			if (field == null) {
				ReplicationStubFieldParamether paramether = getReplicationParametherForReplacement(paramethers, descriptor.getName());
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
			final boolean requiredField = elementAnotation != null ? elementAnotation.required() : false;;
			boolean founded = false;
			for (final ReplicationStubFieldParamether paramether : paramethers) {
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
							readMethod.getModifiers() == 1);
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
		for (final ReplicationStubFieldParamether paramether : paramethers) {
			assertTrue(String.format("Stub %s have not defined field %s", stub.getClass().getName(), paramether.getName()), paramether.isFound());
		}
	}
	
	/**
	 * Stub fields paramether object.
	 * Used to define field property.
	 * @author vdavidovich
	 */
	private class ReplicationStubFieldParamether {
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
		
		private ReplicationStubFieldParamether(String name, boolean listUsed, @SuppressWarnings("rawtypes") Class parameterizedType) {
			this(name, null, List.class, true, null, parameterizedType);
			if (!listUsed) {
				throw new IllegalArgumentException();
			}
		}
		
		private ReplicationStubFieldParamether(String name, @SuppressWarnings("rawtypes") Class type) {
			this(name, null, type);
		}
		
		private ReplicationStubFieldParamether(String name, boolean listUsed, boolean requerdedField, 
			@SuppressWarnings("rawtypes") Class parameterizedType) {
			this(name, null, List.class, requerdedField, null, parameterizedType);
			if (!listUsed) {
				throw new IllegalArgumentException();
			}
		}
		
		private ReplicationStubFieldParamether(String name, @SuppressWarnings("rawtypes") Class type, boolean requerdedField) {
			this(name, null, type, requerdedField, null);
		}
		
		private ReplicationStubFieldParamether(String name, String replacementName, @SuppressWarnings("rawtypes") Class type) {
			this(name, replacementName, type, true, null);
		}
		
		private ReplicationStubFieldParamether(String name, @SuppressWarnings("rawtypes") Class type, String dependentFieldName) {
			this(name, null, type, true, dependentFieldName);
		}
		
		private ReplicationStubFieldParamether(String name, String replacementName, @SuppressWarnings("rawtypes") Class type, boolean requerdedField, 
				String dependentFieldName) {
			this(name, replacementName, type, requerdedField, dependentFieldName, null);
		}

		/**
		 * Full constructor.
		 */
		public ReplicationStubFieldParamether(String name, String replacementName, @SuppressWarnings("rawtypes") Class type, boolean requerdedField, 
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
				availableClasses = new ArrayList<Class>();
			}
			return availableClasses;
		}
	}

}

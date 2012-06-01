package ish.oncourse.webservices.soap.v4.stubs;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.WordUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;
import ish.oncourse.test.ServiceTest;
import ish.oncourse.webservices.util.GenericReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.*;

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
		int index = clazz.getName().lastIndexOf(".") + 1;
		return clazz.getName().substring(index);
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
	}
	
	@Test
    public void testAttendanceStub() {
		final GenericReplicationStub stub = new AttendanceStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testBinaryDataStub() {
		final GenericReplicationStub stub = new BinaryDataStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testBinaryInfoStub() {
		final GenericReplicationStub stub = new BinaryInfoStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testConcessionTypeStub() {
		final GenericReplicationStub stub = new ConcessionTypeStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testContactStub() {
		final GenericReplicationStub stub = new ContactStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testCourseClassStub() {
		final GenericReplicationStub stub = new CourseClassStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testCourseStub() {
		final GenericReplicationStub stub = new CourseStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testDiscountStub() {
		final GenericReplicationStub stub = new DiscountStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testEnrolmentStub() {
		final GenericReplicationStub stub = new EnrolmentStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testInvoiceLineDiscountStub() {
		final GenericReplicationStub stub = new InvoiceLineDiscountStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testInvoiceLineStub() {
		final GenericReplicationStub stub = new InvoiceLineStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testInvoiceStub() {
		final GenericReplicationStub stub = new InvoiceStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testMessagePersonStub() {
		final GenericReplicationStub stub = new MessagePersonStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testOutcomeStub() {
		final GenericReplicationStub stub = new OutcomeStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testPaymentInLineStub() {
		final GenericReplicationStub stub = new PaymentInLineStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testPaymentInStub() {
		final GenericReplicationStub stub = new PaymentInStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testPaymentOutStub() {
		final GenericReplicationStub stub = new PaymentOutStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testPreferenceStub() {
		final GenericReplicationStub stub = new PreferenceStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testRoomStub() {
		final GenericReplicationStub stub = new RoomStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testSiteStub() {
		final GenericReplicationStub stub = new SiteStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testStudentConcessionStub() {
		final GenericReplicationStub stub = new StudentConcessionStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testStudentStub() {
		final GenericReplicationStub stub = new StudentStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testSystemUserStub() {
		final GenericReplicationStub stub = new SystemUserStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testTagRelationStub() {
		final GenericReplicationStub stub = new TagRelationStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testCourseClassTutorStub() {
		final GenericReplicationStub stub = new CourseClassTutorStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testTutorStub() {
		final GenericReplicationStub stub = new TutorStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testWaitingListStub() {
		final GenericReplicationStub stub = new WaitingListStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testCertificateStub() {
		final GenericReplicationStub stub = new CertificateStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
	}
	
	@Test
	public void testCertificateOutcomeStub() {
		final GenericReplicationStub stub = new CertificateOutcomeStub();
		testStubDefinition(stub, stubsPropertyMap.get(getStubName(stub.getClass())));
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
	
	private ReplicationStubFieldParamether getReplicationParametherForReplacement(final List<ReplicationStubFieldParamether> paramethers, 
		final String replacementName) {
		for (final ReplicationStubFieldParamether paramether : paramethers) {
			if (!paramether.isEmptyReplacement() && paramether.getReplacementName().equals(replacementName)) {
				paramether.setFound(true);
				return paramether;
			}
		}
		return null;
	}
	
	private void testStubDefinition(final GenericReplicationStub stub, final List<ReplicationStubFieldParamether> paramethers) {
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
			final boolean requiredField = elementAnotation.required();
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
		private String name;
		private String replacementName;
		@SuppressWarnings("rawtypes")
		private Class type;
		private boolean requerdedField;
		private boolean found;
		
		private ReplicationStubFieldParamether(String name, @SuppressWarnings("rawtypes") Class type) {
			this(name, null, type);
		}
		
		private ReplicationStubFieldParamether(String name, @SuppressWarnings("rawtypes") Class type, boolean requerdedField) {
			this(name, null, type, requerdedField);
		}
		
		private ReplicationStubFieldParamether(String name, String replacementName, @SuppressWarnings("rawtypes") Class type) {
			this(name, replacementName, type, true);
		}
		
		private ReplicationStubFieldParamether(String name, String replacementName, @SuppressWarnings("rawtypes") Class type, boolean requerdedField) {
			super();
			this.name = name;
			this.replacementName = replacementName;
			this.type = type;
			this.requerdedField = requerdedField;
		}

		public String getName() {
			return name;
		}
		
		@SuppressWarnings("rawtypes")
		public Class getType() {
			return type;
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
	}

}

package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.BinaryData;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.BinaryInfoRelation;
import ish.oncourse.model.Certificate;
import ish.oncourse.model.CertificateOutcome;
import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Contact;
import ish.oncourse.model.ContactRelation;
import ish.oncourse.model.ContactRelationType;
import ish.oncourse.model.Course;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.CourseModule;
import ish.oncourse.model.Discount;
import ish.oncourse.model.DiscountConcessionType;
import ish.oncourse.model.DiscountCourseClass;
import ish.oncourse.model.DiscountMembership;
import ish.oncourse.model.DiscountMembershipRelationType;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Instruction;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.InvoiceLineDiscount;
import ish.oncourse.model.KeyStatus;
import ish.oncourse.model.LicenseFee;
import ish.oncourse.model.Log;
import ish.oncourse.model.Message;
import ish.oncourse.model.MessagePerson;
import ish.oncourse.model.MessageTemplate;
import ish.oncourse.model.NotificationTemplate;
import ish.oncourse.model.Outcome;
import ish.oncourse.model.PaymentGatewayType;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentInLine;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.Preference;
import ish.oncourse.model.Product;
import ish.oncourse.model.ProductItem;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedTransaction;
import ish.oncourse.model.Room;
import ish.oncourse.model.Session;
import ish.oncourse.model.SessionTutor;
import ish.oncourse.model.Site;
import ish.oncourse.model.Student;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.model.Tag;
import ish.oncourse.model.TagGroupRequirement;
import ish.oncourse.model.Taggable;
import ish.oncourse.model.TaggableTag;
import ish.oncourse.model.Tutor;
import ish.oncourse.model.TutorRole;
import ish.oncourse.model.WaitingList;
import ish.oncourse.model.WaitingListSite;
import ish.oncourse.model.WebHostName;
import ish.oncourse.model.WebSite;
import ish.oncourse.model.WillowUser;

/**
 * Class _College was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _College extends CayenneDataObject {

    public static final String ANGEL_VERSION_PROPERTY = "angelVersion";
    public static final String BILLING_CODE_PROPERTY = "billingCode";
    public static final String COMMUNICATION_KEY_PROPERTY = "communicationKey";
    public static final String COMMUNICATION_KEY_STATUS_PROPERTY = "communicationKeyStatus";
    public static final String CREATED_PROPERTY = "created";
    public static final String FIRST_REMOTE_AUTHENTICATION_PROPERTY = "firstRemoteAuthentication";
    public static final String IP_ADDRESS_PROPERTY = "ipAddress";
    public static final String IS_TESTING_WEB_SERVICE_PAYMENTS_PROPERTY = "isTestingWebServicePayments";
    public static final String IS_TESTING_WEB_SITE_PAYMENTS_PROPERTY = "isTestingWebSitePayments";
    public static final String IS_WEB_SERVICE_PAYMENTS_ENABLED_PROPERTY = "isWebServicePaymentsEnabled";
    public static final String IS_WEB_SITE_PAYMENTS_ENABLED_PROPERTY = "isWebSitePaymentsEnabled";
    public static final String LAST_REMOTE_AUTHENTICATION_PROPERTY = "lastRemoteAuthentication";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String NATIONAL_PROVIDER_CODE_PROPERTY = "nationalProviderCode";
    public static final String PAYMENT_GATEWAY_ACCOUNT_PROPERTY = "paymentGatewayAccount";
    public static final String PAYMENT_GATEWAY_PASS_PROPERTY = "paymentGatewayPass";
    public static final String PAYMENT_GATEWAY_TYPE_PROPERTY = "paymentGatewayType";
    public static final String REQUIRES_AVETMISS_PROPERTY = "requiresAvetmiss";
    public static final String TIME_ZONE_PROPERTY = "timeZone";
    public static final String WEB_SERVICES_LOGIN_PROPERTY = "webServicesLogin";
    public static final String WEB_SERVICES_PASS_PROPERTY = "webServicesPass";
    public static final String WEB_SERVICES_SECURITY_CODE_PROPERTY = "webServicesSecurityCode";
    public static final String ATTENDANCES_PROPERTY = "attendances";
    public static final String BINARY_DATA_PROPERTY = "binaryData";
    public static final String BINARY_INFO_RELATIONS_PROPERTY = "binaryInfoRelations";
    public static final String BINARY_INFOS_PROPERTY = "binaryInfos";
    public static final String CERTIFICATE_OUTCOMES_PROPERTY = "certificateOutcomes";
    public static final String CERTIFICATES_PROPERTY = "certificates";
    public static final String COLLEGE_DOMAINS_PROPERTY = "collegeDomains";
    public static final String CONCESSION_TYPES_PROPERTY = "concessionTypes";
    public static final String CONTACT_RELATION_TYPES_PROPERTY = "contactRelationTypes";
    public static final String CONTACT_RELATIONS_PROPERTY = "contactRelations";
    public static final String CONTACTS_PROPERTY = "contacts";
    public static final String COURSE_CLASSES_PROPERTY = "courseClasses";
    public static final String COURSE_MODULES_PROPERTY = "courseModules";
    public static final String COURSES_PROPERTY = "courses";
    public static final String DISCOUNT_CONCESSION_TYPES_PROPERTY = "discountConcessionTypes";
    public static final String DISCOUNT_COURSE_CLASSES_PROPERTY = "discountCourseClasses";
    public static final String DISCOUNT_MEMBERSHIP_RELATION_TYPES_PROPERTY = "discountMembershipRelationTypes";
    public static final String DISCOUNT_MEMBERSHIPS_PROPERTY = "discountMemberships";
    public static final String DISCOUNTS_PROPERTY = "discounts";
    public static final String ENROLMENTS_PROPERTY = "enrolments";
    public static final String INSTRUCTIONS_PROPERTY = "instructions";
    public static final String INVOICE_LINE_DISCOUNTS_PROPERTY = "invoiceLineDiscounts";
    public static final String INVOICE_LINES_PROPERTY = "invoiceLines";
    public static final String INVOICES_PROPERTY = "invoices";
    public static final String LICENSE_FEES_PROPERTY = "licenseFees";
    public static final String LOGS_PROPERTY = "logs";
    public static final String MESSAGE_PEOPLE_PROPERTY = "messagePeople";
    public static final String MESSAGE_TEMPLATES_PROPERTY = "messageTemplates";
    public static final String MESSAGES_PROPERTY = "messages";
    public static final String NOTIFICATION_TEMPLATES_PROPERTY = "notificationTemplates";
    public static final String OUTCOMES_PROPERTY = "outcomes";
    public static final String PAYMENT_IN_LINES_PROPERTY = "paymentInLines";
    public static final String PAYMENTS_IN_PROPERTY = "paymentsIn";
    public static final String PAYMENTS_OUT_PROPERTY = "paymentsOut";
    public static final String PREFERENCES_PROPERTY = "preferences";
    public static final String PRODUCT_ITEMS_PROPERTY = "productItems";
    public static final String PRODUCTS_PROPERTY = "products";
    public static final String QUEUED_RECORDS_PROPERTY = "queuedRecords";
    public static final String QUEUED_TRANSACTIONS_PROPERTY = "queuedTransactions";
    public static final String ROOMS_PROPERTY = "rooms";
    public static final String SESSION_TUTORS_PROPERTY = "sessionTutors";
    public static final String SESSIONS_PROPERTY = "sessions";
    public static final String SITES_PROPERTY = "sites";
    public static final String STUDENT_CONCESSIONS_PROPERTY = "studentConcessions";
    public static final String STUDENTS_PROPERTY = "students";
    public static final String TAG_GROUP_REQUIREMENTS_PROPERTY = "tagGroupRequirements";
    public static final String TAGGABLE_TAGS_PROPERTY = "taggableTags";
    public static final String TAGGABLES_PROPERTY = "taggables";
    public static final String TAGS_PROPERTY = "tags";
    public static final String TUTOR_ROLES_PROPERTY = "tutorRoles";
    public static final String TUTORS_PROPERTY = "tutors";
    public static final String WAITING_LIST_SITES_PROPERTY = "waitingListSites";
    public static final String WAITING_LISTS_PROPERTY = "waitingLists";
    public static final String WEB_SITES_PROPERTY = "webSites";
    public static final String WILLOW_USERS_PROPERTY = "willowUsers";

    public static final String ID_PK_COLUMN = "id";

    public void setAngelVersion(String angelVersion) {
        writeProperty("angelVersion", angelVersion);
    }
    public String getAngelVersion() {
        return (String)readProperty("angelVersion");
    }

    public void setBillingCode(String billingCode) {
        writeProperty("billingCode", billingCode);
    }
    public String getBillingCode() {
        return (String)readProperty("billingCode");
    }

    public void setCommunicationKey(Long communicationKey) {
        writeProperty("communicationKey", communicationKey);
    }
    public Long getCommunicationKey() {
        return (Long)readProperty("communicationKey");
    }

    public void setCommunicationKeyStatus(KeyStatus communicationKeyStatus) {
        writeProperty("communicationKeyStatus", communicationKeyStatus);
    }
    public KeyStatus getCommunicationKeyStatus() {
        return (KeyStatus)readProperty("communicationKeyStatus");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setFirstRemoteAuthentication(Date firstRemoteAuthentication) {
        writeProperty("firstRemoteAuthentication", firstRemoteAuthentication);
    }
    public Date getFirstRemoteAuthentication() {
        return (Date)readProperty("firstRemoteAuthentication");
    }

    public void setIpAddress(String ipAddress) {
        writeProperty("ipAddress", ipAddress);
    }
    public String getIpAddress() {
        return (String)readProperty("ipAddress");
    }

    public void setIsTestingWebServicePayments(Boolean isTestingWebServicePayments) {
        writeProperty("isTestingWebServicePayments", isTestingWebServicePayments);
    }
    public Boolean getIsTestingWebServicePayments() {
        return (Boolean)readProperty("isTestingWebServicePayments");
    }

    public void setIsTestingWebSitePayments(Boolean isTestingWebSitePayments) {
        writeProperty("isTestingWebSitePayments", isTestingWebSitePayments);
    }
    public Boolean getIsTestingWebSitePayments() {
        return (Boolean)readProperty("isTestingWebSitePayments");
    }

    public void setIsWebServicePaymentsEnabled(Boolean isWebServicePaymentsEnabled) {
        writeProperty("isWebServicePaymentsEnabled", isWebServicePaymentsEnabled);
    }
    public Boolean getIsWebServicePaymentsEnabled() {
        return (Boolean)readProperty("isWebServicePaymentsEnabled");
    }

    public void setIsWebSitePaymentsEnabled(Boolean isWebSitePaymentsEnabled) {
        writeProperty("isWebSitePaymentsEnabled", isWebSitePaymentsEnabled);
    }
    public Boolean getIsWebSitePaymentsEnabled() {
        return (Boolean)readProperty("isWebSitePaymentsEnabled");
    }

    public void setLastRemoteAuthentication(Date lastRemoteAuthentication) {
        writeProperty("lastRemoteAuthentication", lastRemoteAuthentication);
    }
    public Date getLastRemoteAuthentication() {
        return (Date)readProperty("lastRemoteAuthentication");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }

    public void setNationalProviderCode(String nationalProviderCode) {
        writeProperty("nationalProviderCode", nationalProviderCode);
    }
    public String getNationalProviderCode() {
        return (String)readProperty("nationalProviderCode");
    }

    public void setPaymentGatewayAccount(String paymentGatewayAccount) {
        writeProperty("paymentGatewayAccount", paymentGatewayAccount);
    }
    public String getPaymentGatewayAccount() {
        return (String)readProperty("paymentGatewayAccount");
    }

    public void setPaymentGatewayPass(String paymentGatewayPass) {
        writeProperty("paymentGatewayPass", paymentGatewayPass);
    }
    public String getPaymentGatewayPass() {
        return (String)readProperty("paymentGatewayPass");
    }

    public void setPaymentGatewayType(PaymentGatewayType paymentGatewayType) {
        writeProperty("paymentGatewayType", paymentGatewayType);
    }
    public PaymentGatewayType getPaymentGatewayType() {
        return (PaymentGatewayType)readProperty("paymentGatewayType");
    }

    public void setRequiresAvetmiss(Boolean requiresAvetmiss) {
        writeProperty("requiresAvetmiss", requiresAvetmiss);
    }
    public Boolean getRequiresAvetmiss() {
        return (Boolean)readProperty("requiresAvetmiss");
    }

    public void setTimeZone(String timeZone) {
        writeProperty("timeZone", timeZone);
    }
    public String getTimeZone() {
        return (String)readProperty("timeZone");
    }

    public void setWebServicesLogin(String webServicesLogin) {
        writeProperty("webServicesLogin", webServicesLogin);
    }
    public String getWebServicesLogin() {
        return (String)readProperty("webServicesLogin");
    }

    public void setWebServicesPass(String webServicesPass) {
        writeProperty("webServicesPass", webServicesPass);
    }
    public String getWebServicesPass() {
        return (String)readProperty("webServicesPass");
    }

    public void setWebServicesSecurityCode(String webServicesSecurityCode) {
        writeProperty("webServicesSecurityCode", webServicesSecurityCode);
    }
    public String getWebServicesSecurityCode() {
        return (String)readProperty("webServicesSecurityCode");
    }

    public void addToAttendances(Attendance obj) {
        addToManyTarget("attendances", obj, true);
    }
    public void removeFromAttendances(Attendance obj) {
        removeToManyTarget("attendances", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Attendance> getAttendances() {
        return (List<Attendance>)readProperty("attendances");
    }


    public void addToBinaryData(BinaryData obj) {
        addToManyTarget("binaryData", obj, true);
    }
    public void removeFromBinaryData(BinaryData obj) {
        removeToManyTarget("binaryData", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<BinaryData> getBinaryData() {
        return (List<BinaryData>)readProperty("binaryData");
    }


    public void addToBinaryInfoRelations(BinaryInfoRelation obj) {
        addToManyTarget("binaryInfoRelations", obj, true);
    }
    public void removeFromBinaryInfoRelations(BinaryInfoRelation obj) {
        removeToManyTarget("binaryInfoRelations", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<BinaryInfoRelation> getBinaryInfoRelations() {
        return (List<BinaryInfoRelation>)readProperty("binaryInfoRelations");
    }


    public void addToBinaryInfos(BinaryInfo obj) {
        addToManyTarget("binaryInfos", obj, true);
    }
    public void removeFromBinaryInfos(BinaryInfo obj) {
        removeToManyTarget("binaryInfos", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<BinaryInfo> getBinaryInfos() {
        return (List<BinaryInfo>)readProperty("binaryInfos");
    }


    public void addToCertificateOutcomes(CertificateOutcome obj) {
        addToManyTarget("certificateOutcomes", obj, true);
    }
    public void removeFromCertificateOutcomes(CertificateOutcome obj) {
        removeToManyTarget("certificateOutcomes", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<CertificateOutcome> getCertificateOutcomes() {
        return (List<CertificateOutcome>)readProperty("certificateOutcomes");
    }


    public void addToCertificates(Certificate obj) {
        addToManyTarget("certificates", obj, true);
    }
    public void removeFromCertificates(Certificate obj) {
        removeToManyTarget("certificates", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Certificate> getCertificates() {
        return (List<Certificate>)readProperty("certificates");
    }


    public void addToCollegeDomains(WebHostName obj) {
        addToManyTarget("collegeDomains", obj, true);
    }
    public void removeFromCollegeDomains(WebHostName obj) {
        removeToManyTarget("collegeDomains", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WebHostName> getCollegeDomains() {
        return (List<WebHostName>)readProperty("collegeDomains");
    }


    public void addToConcessionTypes(ConcessionType obj) {
        addToManyTarget("concessionTypes", obj, true);
    }
    public void removeFromConcessionTypes(ConcessionType obj) {
        removeToManyTarget("concessionTypes", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<ConcessionType> getConcessionTypes() {
        return (List<ConcessionType>)readProperty("concessionTypes");
    }


    public void addToContactRelationTypes(ContactRelationType obj) {
        addToManyTarget("contactRelationTypes", obj, true);
    }
    public void removeFromContactRelationTypes(ContactRelationType obj) {
        removeToManyTarget("contactRelationTypes", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<ContactRelationType> getContactRelationTypes() {
        return (List<ContactRelationType>)readProperty("contactRelationTypes");
    }


    public void addToContactRelations(ContactRelation obj) {
        addToManyTarget("contactRelations", obj, true);
    }
    public void removeFromContactRelations(ContactRelation obj) {
        removeToManyTarget("contactRelations", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<ContactRelation> getContactRelations() {
        return (List<ContactRelation>)readProperty("contactRelations");
    }


    public void addToContacts(Contact obj) {
        addToManyTarget("contacts", obj, true);
    }
    public void removeFromContacts(Contact obj) {
        removeToManyTarget("contacts", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Contact> getContacts() {
        return (List<Contact>)readProperty("contacts");
    }


    public void addToCourseClasses(CourseClass obj) {
        addToManyTarget("courseClasses", obj, true);
    }
    public void removeFromCourseClasses(CourseClass obj) {
        removeToManyTarget("courseClasses", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<CourseClass> getCourseClasses() {
        return (List<CourseClass>)readProperty("courseClasses");
    }


    public void addToCourseModules(CourseModule obj) {
        addToManyTarget("courseModules", obj, true);
    }
    public void removeFromCourseModules(CourseModule obj) {
        removeToManyTarget("courseModules", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<CourseModule> getCourseModules() {
        return (List<CourseModule>)readProperty("courseModules");
    }


    public void addToCourses(Course obj) {
        addToManyTarget("courses", obj, true);
    }
    public void removeFromCourses(Course obj) {
        removeToManyTarget("courses", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Course> getCourses() {
        return (List<Course>)readProperty("courses");
    }


    public void addToDiscountConcessionTypes(DiscountConcessionType obj) {
        addToManyTarget("discountConcessionTypes", obj, true);
    }
    public void removeFromDiscountConcessionTypes(DiscountConcessionType obj) {
        removeToManyTarget("discountConcessionTypes", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DiscountConcessionType> getDiscountConcessionTypes() {
        return (List<DiscountConcessionType>)readProperty("discountConcessionTypes");
    }


    public void addToDiscountCourseClasses(DiscountCourseClass obj) {
        addToManyTarget("discountCourseClasses", obj, true);
    }
    public void removeFromDiscountCourseClasses(DiscountCourseClass obj) {
        removeToManyTarget("discountCourseClasses", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DiscountCourseClass> getDiscountCourseClasses() {
        return (List<DiscountCourseClass>)readProperty("discountCourseClasses");
    }


    public void addToDiscountMembershipRelationTypes(DiscountMembershipRelationType obj) {
        addToManyTarget("discountMembershipRelationTypes", obj, true);
    }
    public void removeFromDiscountMembershipRelationTypes(DiscountMembershipRelationType obj) {
        removeToManyTarget("discountMembershipRelationTypes", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DiscountMembershipRelationType> getDiscountMembershipRelationTypes() {
        return (List<DiscountMembershipRelationType>)readProperty("discountMembershipRelationTypes");
    }


    public void addToDiscountMemberships(DiscountMembership obj) {
        addToManyTarget("discountMemberships", obj, true);
    }
    public void removeFromDiscountMemberships(DiscountMembership obj) {
        removeToManyTarget("discountMemberships", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DiscountMembership> getDiscountMemberships() {
        return (List<DiscountMembership>)readProperty("discountMemberships");
    }


    public void addToDiscounts(Discount obj) {
        addToManyTarget("discounts", obj, true);
    }
    public void removeFromDiscounts(Discount obj) {
        removeToManyTarget("discounts", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Discount> getDiscounts() {
        return (List<Discount>)readProperty("discounts");
    }


    public void addToEnrolments(Enrolment obj) {
        addToManyTarget("enrolments", obj, true);
    }
    public void removeFromEnrolments(Enrolment obj) {
        removeToManyTarget("enrolments", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Enrolment> getEnrolments() {
        return (List<Enrolment>)readProperty("enrolments");
    }


    public void addToInstructions(Instruction obj) {
        addToManyTarget("instructions", obj, true);
    }
    public void removeFromInstructions(Instruction obj) {
        removeToManyTarget("instructions", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Instruction> getInstructions() {
        return (List<Instruction>)readProperty("instructions");
    }


    public void addToInvoiceLineDiscounts(InvoiceLineDiscount obj) {
        addToManyTarget("invoiceLineDiscounts", obj, true);
    }
    public void removeFromInvoiceLineDiscounts(InvoiceLineDiscount obj) {
        removeToManyTarget("invoiceLineDiscounts", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<InvoiceLineDiscount> getInvoiceLineDiscounts() {
        return (List<InvoiceLineDiscount>)readProperty("invoiceLineDiscounts");
    }


    public void addToInvoiceLines(InvoiceLine obj) {
        addToManyTarget("invoiceLines", obj, true);
    }
    public void removeFromInvoiceLines(InvoiceLine obj) {
        removeToManyTarget("invoiceLines", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<InvoiceLine> getInvoiceLines() {
        return (List<InvoiceLine>)readProperty("invoiceLines");
    }


    public void addToInvoices(Invoice obj) {
        addToManyTarget("invoices", obj, true);
    }
    public void removeFromInvoices(Invoice obj) {
        removeToManyTarget("invoices", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Invoice> getInvoices() {
        return (List<Invoice>)readProperty("invoices");
    }


    public void addToLicenseFees(LicenseFee obj) {
        addToManyTarget("licenseFees", obj, true);
    }
    public void removeFromLicenseFees(LicenseFee obj) {
        removeToManyTarget("licenseFees", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<LicenseFee> getLicenseFees() {
        return (List<LicenseFee>)readProperty("licenseFees");
    }


    public void addToLogs(Log obj) {
        addToManyTarget("logs", obj, true);
    }
    public void removeFromLogs(Log obj) {
        removeToManyTarget("logs", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Log> getLogs() {
        return (List<Log>)readProperty("logs");
    }


    public void addToMessagePeople(MessagePerson obj) {
        addToManyTarget("messagePeople", obj, true);
    }
    public void removeFromMessagePeople(MessagePerson obj) {
        removeToManyTarget("messagePeople", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<MessagePerson> getMessagePeople() {
        return (List<MessagePerson>)readProperty("messagePeople");
    }


    public void addToMessageTemplates(MessageTemplate obj) {
        addToManyTarget("messageTemplates", obj, true);
    }
    public void removeFromMessageTemplates(MessageTemplate obj) {
        removeToManyTarget("messageTemplates", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<MessageTemplate> getMessageTemplates() {
        return (List<MessageTemplate>)readProperty("messageTemplates");
    }


    public void addToMessages(Message obj) {
        addToManyTarget("messages", obj, true);
    }
    public void removeFromMessages(Message obj) {
        removeToManyTarget("messages", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Message> getMessages() {
        return (List<Message>)readProperty("messages");
    }


    public void addToNotificationTemplates(NotificationTemplate obj) {
        addToManyTarget("notificationTemplates", obj, true);
    }
    public void removeFromNotificationTemplates(NotificationTemplate obj) {
        removeToManyTarget("notificationTemplates", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<NotificationTemplate> getNotificationTemplates() {
        return (List<NotificationTemplate>)readProperty("notificationTemplates");
    }


    public void addToOutcomes(Outcome obj) {
        addToManyTarget("outcomes", obj, true);
    }
    public void removeFromOutcomes(Outcome obj) {
        removeToManyTarget("outcomes", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Outcome> getOutcomes() {
        return (List<Outcome>)readProperty("outcomes");
    }


    public void addToPaymentInLines(PaymentInLine obj) {
        addToManyTarget("paymentInLines", obj, true);
    }
    public void removeFromPaymentInLines(PaymentInLine obj) {
        removeToManyTarget("paymentInLines", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<PaymentInLine> getPaymentInLines() {
        return (List<PaymentInLine>)readProperty("paymentInLines");
    }


    public void addToPaymentsIn(PaymentIn obj) {
        addToManyTarget("paymentsIn", obj, true);
    }
    public void removeFromPaymentsIn(PaymentIn obj) {
        removeToManyTarget("paymentsIn", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<PaymentIn> getPaymentsIn() {
        return (List<PaymentIn>)readProperty("paymentsIn");
    }


    public void addToPaymentsOut(PaymentOut obj) {
        addToManyTarget("paymentsOut", obj, true);
    }
    public void removeFromPaymentsOut(PaymentOut obj) {
        removeToManyTarget("paymentsOut", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<PaymentOut> getPaymentsOut() {
        return (List<PaymentOut>)readProperty("paymentsOut");
    }


    public void addToPreferences(Preference obj) {
        addToManyTarget("preferences", obj, true);
    }
    public void removeFromPreferences(Preference obj) {
        removeToManyTarget("preferences", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Preference> getPreferences() {
        return (List<Preference>)readProperty("preferences");
    }


    public void addToProductItems(ProductItem obj) {
        addToManyTarget("productItems", obj, true);
    }
    public void removeFromProductItems(ProductItem obj) {
        removeToManyTarget("productItems", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<ProductItem> getProductItems() {
        return (List<ProductItem>)readProperty("productItems");
    }


    public void addToProducts(Product obj) {
        addToManyTarget("products", obj, true);
    }
    public void removeFromProducts(Product obj) {
        removeToManyTarget("products", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Product> getProducts() {
        return (List<Product>)readProperty("products");
    }


    public void addToQueuedRecords(QueuedRecord obj) {
        addToManyTarget("queuedRecords", obj, true);
    }
    public void removeFromQueuedRecords(QueuedRecord obj) {
        removeToManyTarget("queuedRecords", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<QueuedRecord> getQueuedRecords() {
        return (List<QueuedRecord>)readProperty("queuedRecords");
    }


    public void addToQueuedTransactions(QueuedTransaction obj) {
        addToManyTarget("queuedTransactions", obj, true);
    }
    public void removeFromQueuedTransactions(QueuedTransaction obj) {
        removeToManyTarget("queuedTransactions", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<QueuedTransaction> getQueuedTransactions() {
        return (List<QueuedTransaction>)readProperty("queuedTransactions");
    }


    public void addToRooms(Room obj) {
        addToManyTarget("rooms", obj, true);
    }
    public void removeFromRooms(Room obj) {
        removeToManyTarget("rooms", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Room> getRooms() {
        return (List<Room>)readProperty("rooms");
    }


    public void addToSessionTutors(SessionTutor obj) {
        addToManyTarget("sessionTutors", obj, true);
    }
    public void removeFromSessionTutors(SessionTutor obj) {
        removeToManyTarget("sessionTutors", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<SessionTutor> getSessionTutors() {
        return (List<SessionTutor>)readProperty("sessionTutors");
    }


    public void addToSessions(Session obj) {
        addToManyTarget("sessions", obj, true);
    }
    public void removeFromSessions(Session obj) {
        removeToManyTarget("sessions", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Session> getSessions() {
        return (List<Session>)readProperty("sessions");
    }


    public void addToSites(Site obj) {
        addToManyTarget("sites", obj, true);
    }
    public void removeFromSites(Site obj) {
        removeToManyTarget("sites", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Site> getSites() {
        return (List<Site>)readProperty("sites");
    }


    public void addToStudentConcessions(StudentConcession obj) {
        addToManyTarget("studentConcessions", obj, true);
    }
    public void removeFromStudentConcessions(StudentConcession obj) {
        removeToManyTarget("studentConcessions", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<StudentConcession> getStudentConcessions() {
        return (List<StudentConcession>)readProperty("studentConcessions");
    }


    public void addToStudents(Student obj) {
        addToManyTarget("students", obj, true);
    }
    public void removeFromStudents(Student obj) {
        removeToManyTarget("students", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Student> getStudents() {
        return (List<Student>)readProperty("students");
    }


    public void addToTagGroupRequirements(TagGroupRequirement obj) {
        addToManyTarget("tagGroupRequirements", obj, true);
    }
    public void removeFromTagGroupRequirements(TagGroupRequirement obj) {
        removeToManyTarget("tagGroupRequirements", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<TagGroupRequirement> getTagGroupRequirements() {
        return (List<TagGroupRequirement>)readProperty("tagGroupRequirements");
    }


    public void addToTaggableTags(TaggableTag obj) {
        addToManyTarget("taggableTags", obj, true);
    }
    public void removeFromTaggableTags(TaggableTag obj) {
        removeToManyTarget("taggableTags", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<TaggableTag> getTaggableTags() {
        return (List<TaggableTag>)readProperty("taggableTags");
    }


    public void addToTaggables(Taggable obj) {
        addToManyTarget("taggables", obj, true);
    }
    public void removeFromTaggables(Taggable obj) {
        removeToManyTarget("taggables", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Taggable> getTaggables() {
        return (List<Taggable>)readProperty("taggables");
    }


    public void addToTags(Tag obj) {
        addToManyTarget("tags", obj, true);
    }
    public void removeFromTags(Tag obj) {
        removeToManyTarget("tags", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Tag> getTags() {
        return (List<Tag>)readProperty("tags");
    }


    public void addToTutorRoles(TutorRole obj) {
        addToManyTarget("tutorRoles", obj, true);
    }
    public void removeFromTutorRoles(TutorRole obj) {
        removeToManyTarget("tutorRoles", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<TutorRole> getTutorRoles() {
        return (List<TutorRole>)readProperty("tutorRoles");
    }


    public void addToTutors(Tutor obj) {
        addToManyTarget("tutors", obj, true);
    }
    public void removeFromTutors(Tutor obj) {
        removeToManyTarget("tutors", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Tutor> getTutors() {
        return (List<Tutor>)readProperty("tutors");
    }


    public void addToWaitingListSites(WaitingListSite obj) {
        addToManyTarget("waitingListSites", obj, true);
    }
    public void removeFromWaitingListSites(WaitingListSite obj) {
        removeToManyTarget("waitingListSites", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WaitingListSite> getWaitingListSites() {
        return (List<WaitingListSite>)readProperty("waitingListSites");
    }


    public void addToWaitingLists(WaitingList obj) {
        addToManyTarget("waitingLists", obj, true);
    }
    public void removeFromWaitingLists(WaitingList obj) {
        removeToManyTarget("waitingLists", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WaitingList> getWaitingLists() {
        return (List<WaitingList>)readProperty("waitingLists");
    }


    public void addToWebSites(WebSite obj) {
        addToManyTarget("webSites", obj, true);
    }
    public void removeFromWebSites(WebSite obj) {
        removeToManyTarget("webSites", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WebSite> getWebSites() {
        return (List<WebSite>)readProperty("webSites");
    }


    public void addToWillowUsers(WillowUser obj) {
        addToManyTarget("willowUsers", obj, true);
    }
    public void removeFromWillowUsers(WillowUser obj) {
        removeToManyTarget("willowUsers", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<WillowUser> getWillowUsers() {
        return (List<WillowUser>)readProperty("willowUsers");
    }


}

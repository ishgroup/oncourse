package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.ContactRelation;
import ish.oncourse.model.CorporatePass;
import ish.oncourse.model.Country;
import ish.oncourse.model.CustomField;
import ish.oncourse.model.DiscussionCommentContact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.MessagePerson;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.ProductItem;
import ish.oncourse.model.Student;
import ish.oncourse.model.SupportPassword;
import ish.oncourse.model.Tutor;

/**
 * Class _Contact was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Contact extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    @Deprecated
    public static final String ABN_PROPERTY = "abn";
    @Deprecated
    public static final String ANGEL_ID_PROPERTY = "angelId";
    @Deprecated
    public static final String BUSINESS_PHONE_NUMBER_PROPERTY = "businessPhoneNumber";
    @Deprecated
    public static final String COOKIE_HASH_PROPERTY = "cookieHash";
    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String DATE_OF_BIRTH_PROPERTY = "dateOfBirth";
    @Deprecated
    public static final String EMAIL_ADDRESS_PROPERTY = "emailAddress";
    @Deprecated
    public static final String FAMILY_NAME_PROPERTY = "familyName";
    @Deprecated
    public static final String FAX_NUMBER_PROPERTY = "faxNumber";
    @Deprecated
    public static final String GIVEN_NAME_PROPERTY = "givenName";
    @Deprecated
    public static final String HOME_PHONE_NUMBER_PROPERTY = "homePhoneNumber";
    @Deprecated
    public static final String IS_COMPANY_PROPERTY = "isCompany";
    @Deprecated
    public static final String IS_MALE_PROPERTY = "isMale";
    @Deprecated
    public static final String IS_MARKETING_VIA_EMAIL_ALLOWED_PROPERTY = "isMarketingViaEmailAllowed";
    @Deprecated
    public static final String IS_MARKETING_VIA_POST_ALLOWED_PROPERTY = "isMarketingViaPostAllowed";
    @Deprecated
    public static final String IS_MARKETING_VIA_SMSALLOWED_PROPERTY = "isMarketingViaSMSAllowed";
    @Deprecated
    public static final String LAST_LOGIN_TIME_PROPERTY = "lastLoginTime";
    @Deprecated
    public static final String MIDDLE_NAME_PROPERTY = "middleName";
    @Deprecated
    public static final String MOBILE_PHONE_NUMBER_PROPERTY = "mobilePhoneNumber";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String PASSWORD_PROPERTY = "password";
    @Deprecated
    public static final String PASSWORD_HASH_PROPERTY = "passwordHash";
    @Deprecated
    public static final String PASSWORD_RECOVER_EXPIRE_PROPERTY = "passwordRecoverExpire";
    @Deprecated
    public static final String PASSWORD_RECOVERY_KEY_PROPERTY = "passwordRecoveryKey";
    @Deprecated
    public static final String POSTCODE_PROPERTY = "postcode";
    @Deprecated
    public static final String STATE_PROPERTY = "state";
    @Deprecated
    public static final String STREET_PROPERTY = "street";
    @Deprecated
    public static final String SUBURB_PROPERTY = "suburb";
    @Deprecated
    public static final String TAX_FILE_NUMBER_PROPERTY = "taxFileNumber";
    @Deprecated
    public static final String UNIQUE_CODE_PROPERTY = "uniqueCode";
    @Deprecated
    public static final String COLLEGE_PROPERTY = "college";
    @Deprecated
    public static final String CONTACT_COMMENTS_PROPERTY = "contactComments";
    @Deprecated
    public static final String CORPORATE_PASSES_PROPERTY = "corporatePasses";
    @Deprecated
    public static final String COUNTRY_PROPERTY = "country";
    @Deprecated
    public static final String CUSTOM_FIELDS_PROPERTY = "customFields";
    @Deprecated
    public static final String FROM_CONTACTS_PROPERTY = "fromContacts";
    @Deprecated
    public static final String INVOICES_PROPERTY = "invoices";
    @Deprecated
    public static final String MESSAGE_PEOPLE_PROPERTY = "messagePeople";
    @Deprecated
    public static final String PAYMENTS_IN_PROPERTY = "paymentsIn";
    @Deprecated
    public static final String PAYMENTS_OUT_PROPERTY = "paymentsOut";
    @Deprecated
    public static final String PRODUCTS_PROPERTY = "products";
    @Deprecated
    public static final String STUDENT_PROPERTY = "student";
    @Deprecated
    public static final String SUPPORT_PASSWORD_PROPERTY = "supportPassword";
    @Deprecated
    public static final String TO_CONTACTS_PROPERTY = "toContacts";
    @Deprecated
    public static final String TUTOR_PROPERTY = "tutor";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<String> ABN = new Property<String>("abn");
    public static final Property<Long> ANGEL_ID = new Property<Long>("angelId");
    public static final Property<String> BUSINESS_PHONE_NUMBER = new Property<String>("businessPhoneNumber");
    public static final Property<String> COOKIE_HASH = new Property<String>("cookieHash");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<Date> DATE_OF_BIRTH = new Property<Date>("dateOfBirth");
    public static final Property<String> EMAIL_ADDRESS = new Property<String>("emailAddress");
    public static final Property<String> FAMILY_NAME = new Property<String>("familyName");
    public static final Property<String> FAX_NUMBER = new Property<String>("faxNumber");
    public static final Property<String> GIVEN_NAME = new Property<String>("givenName");
    public static final Property<String> HOME_PHONE_NUMBER = new Property<String>("homePhoneNumber");
    public static final Property<Boolean> IS_COMPANY = new Property<Boolean>("isCompany");
    public static final Property<Boolean> IS_MALE = new Property<Boolean>("isMale");
    public static final Property<Boolean> IS_MARKETING_VIA_EMAIL_ALLOWED = new Property<Boolean>("isMarketingViaEmailAllowed");
    public static final Property<Boolean> IS_MARKETING_VIA_POST_ALLOWED = new Property<Boolean>("isMarketingViaPostAllowed");
    public static final Property<Boolean> IS_MARKETING_VIA_SMSALLOWED = new Property<Boolean>("isMarketingViaSMSAllowed");
    public static final Property<Date> LAST_LOGIN_TIME = new Property<Date>("lastLoginTime");
    public static final Property<String> MIDDLE_NAME = new Property<String>("middleName");
    public static final Property<String> MOBILE_PHONE_NUMBER = new Property<String>("mobilePhoneNumber");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<String> PASSWORD = new Property<String>("password");
    public static final Property<String> PASSWORD_HASH = new Property<String>("passwordHash");
    public static final Property<Date> PASSWORD_RECOVER_EXPIRE = new Property<Date>("passwordRecoverExpire");
    public static final Property<String> PASSWORD_RECOVERY_KEY = new Property<String>("passwordRecoveryKey");
    public static final Property<String> POSTCODE = new Property<String>("postcode");
    public static final Property<String> STATE = new Property<String>("state");
    public static final Property<String> STREET = new Property<String>("street");
    public static final Property<String> SUBURB = new Property<String>("suburb");
    public static final Property<String> TAX_FILE_NUMBER = new Property<String>("taxFileNumber");
    public static final Property<String> UNIQUE_CODE = new Property<String>("uniqueCode");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<List<DiscussionCommentContact>> CONTACT_COMMENTS = new Property<List<DiscussionCommentContact>>("contactComments");
    public static final Property<CorporatePass> CORPORATE_PASSES = new Property<CorporatePass>("corporatePasses");
    public static final Property<Country> COUNTRY = new Property<Country>("country");
    public static final Property<List<CustomField>> CUSTOM_FIELDS = new Property<List<CustomField>>("customFields");
    public static final Property<List<ContactRelation>> FROM_CONTACTS = new Property<List<ContactRelation>>("fromContacts");
    public static final Property<List<Invoice>> INVOICES = new Property<List<Invoice>>("invoices");
    public static final Property<List<MessagePerson>> MESSAGE_PEOPLE = new Property<List<MessagePerson>>("messagePeople");
    public static final Property<List<PaymentIn>> PAYMENTS_IN = new Property<List<PaymentIn>>("paymentsIn");
    public static final Property<List<PaymentOut>> PAYMENTS_OUT = new Property<List<PaymentOut>>("paymentsOut");
    public static final Property<List<ProductItem>> PRODUCTS = new Property<List<ProductItem>>("products");
    public static final Property<Student> STUDENT = new Property<Student>("student");
    public static final Property<SupportPassword> SUPPORT_PASSWORD = new Property<SupportPassword>("supportPassword");
    public static final Property<List<ContactRelation>> TO_CONTACTS = new Property<List<ContactRelation>>("toContacts");
    public static final Property<Tutor> TUTOR = new Property<Tutor>("tutor");

    public void setAbn(String abn) {
        writeProperty("abn", abn);
    }
    public String getAbn() {
        return (String)readProperty("abn");
    }

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setBusinessPhoneNumber(String businessPhoneNumber) {
        writeProperty("businessPhoneNumber", businessPhoneNumber);
    }
    public String getBusinessPhoneNumber() {
        return (String)readProperty("businessPhoneNumber");
    }

    public void setCookieHash(String cookieHash) {
        writeProperty("cookieHash", cookieHash);
    }
    public String getCookieHash() {
        return (String)readProperty("cookieHash");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setDateOfBirth(Date dateOfBirth) {
        writeProperty("dateOfBirth", dateOfBirth);
    }
    public Date getDateOfBirth() {
        return (Date)readProperty("dateOfBirth");
    }

    public void setEmailAddress(String emailAddress) {
        writeProperty("emailAddress", emailAddress);
    }
    public String getEmailAddress() {
        return (String)readProperty("emailAddress");
    }

    public void setFamilyName(String familyName) {
        writeProperty("familyName", familyName);
    }
    public String getFamilyName() {
        return (String)readProperty("familyName");
    }

    public void setFaxNumber(String faxNumber) {
        writeProperty("faxNumber", faxNumber);
    }
    public String getFaxNumber() {
        return (String)readProperty("faxNumber");
    }

    public void setGivenName(String givenName) {
        writeProperty("givenName", givenName);
    }
    public String getGivenName() {
        return (String)readProperty("givenName");
    }

    public void setHomePhoneNumber(String homePhoneNumber) {
        writeProperty("homePhoneNumber", homePhoneNumber);
    }
    public String getHomePhoneNumber() {
        return (String)readProperty("homePhoneNumber");
    }

    public void setIsCompany(Boolean isCompany) {
        writeProperty("isCompany", isCompany);
    }
    public Boolean getIsCompany() {
        return (Boolean)readProperty("isCompany");
    }

    public void setIsMale(Boolean isMale) {
        writeProperty("isMale", isMale);
    }
    public Boolean getIsMale() {
        return (Boolean)readProperty("isMale");
    }

    public void setIsMarketingViaEmailAllowed(Boolean isMarketingViaEmailAllowed) {
        writeProperty("isMarketingViaEmailAllowed", isMarketingViaEmailAllowed);
    }
    public Boolean getIsMarketingViaEmailAllowed() {
        return (Boolean)readProperty("isMarketingViaEmailAllowed");
    }

    public void setIsMarketingViaPostAllowed(Boolean isMarketingViaPostAllowed) {
        writeProperty("isMarketingViaPostAllowed", isMarketingViaPostAllowed);
    }
    public Boolean getIsMarketingViaPostAllowed() {
        return (Boolean)readProperty("isMarketingViaPostAllowed");
    }

    public void setIsMarketingViaSMSAllowed(Boolean isMarketingViaSMSAllowed) {
        writeProperty("isMarketingViaSMSAllowed", isMarketingViaSMSAllowed);
    }
    public Boolean getIsMarketingViaSMSAllowed() {
        return (Boolean)readProperty("isMarketingViaSMSAllowed");
    }

    public void setLastLoginTime(Date lastLoginTime) {
        writeProperty("lastLoginTime", lastLoginTime);
    }
    public Date getLastLoginTime() {
        return (Date)readProperty("lastLoginTime");
    }

    public void setMiddleName(String middleName) {
        writeProperty("middleName", middleName);
    }
    public String getMiddleName() {
        return (String)readProperty("middleName");
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        writeProperty("mobilePhoneNumber", mobilePhoneNumber);
    }
    public String getMobilePhoneNumber() {
        return (String)readProperty("mobilePhoneNumber");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setPassword(String password) {
        writeProperty("password", password);
    }
    public String getPassword() {
        return (String)readProperty("password");
    }

    public void setPasswordHash(String passwordHash) {
        writeProperty("passwordHash", passwordHash);
    }
    public String getPasswordHash() {
        return (String)readProperty("passwordHash");
    }

    public void setPasswordRecoverExpire(Date passwordRecoverExpire) {
        writeProperty("passwordRecoverExpire", passwordRecoverExpire);
    }
    public Date getPasswordRecoverExpire() {
        return (Date)readProperty("passwordRecoverExpire");
    }

    public void setPasswordRecoveryKey(String passwordRecoveryKey) {
        writeProperty("passwordRecoveryKey", passwordRecoveryKey);
    }
    public String getPasswordRecoveryKey() {
        return (String)readProperty("passwordRecoveryKey");
    }

    public void setPostcode(String postcode) {
        writeProperty("postcode", postcode);
    }
    public String getPostcode() {
        return (String)readProperty("postcode");
    }

    public void setState(String state) {
        writeProperty("state", state);
    }
    public String getState() {
        return (String)readProperty("state");
    }

    public void setStreet(String street) {
        writeProperty("street", street);
    }
    public String getStreet() {
        return (String)readProperty("street");
    }

    public void setSuburb(String suburb) {
        writeProperty("suburb", suburb);
    }
    public String getSuburb() {
        return (String)readProperty("suburb");
    }

    public void setTaxFileNumber(String taxFileNumber) {
        writeProperty("taxFileNumber", taxFileNumber);
    }
    public String getTaxFileNumber() {
        return (String)readProperty("taxFileNumber");
    }

    public void setUniqueCode(String uniqueCode) {
        writeProperty("uniqueCode", uniqueCode);
    }
    public String getUniqueCode() {
        return (String)readProperty("uniqueCode");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void addToContactComments(DiscussionCommentContact obj) {
        addToManyTarget("contactComments", obj, true);
    }
    public void removeFromContactComments(DiscussionCommentContact obj) {
        removeToManyTarget("contactComments", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DiscussionCommentContact> getContactComments() {
        return (List<DiscussionCommentContact>)readProperty("contactComments");
    }


    public void setCorporatePasses(CorporatePass corporatePasses) {
        setToOneTarget("corporatePasses", corporatePasses, true);
    }

    public CorporatePass getCorporatePasses() {
        return (CorporatePass)readProperty("corporatePasses");
    }


    public void setCountry(Country country) {
        setToOneTarget("country", country, true);
    }

    public Country getCountry() {
        return (Country)readProperty("country");
    }


    public void addToCustomFields(CustomField obj) {
        addToManyTarget("customFields", obj, true);
    }
    public void removeFromCustomFields(CustomField obj) {
        removeToManyTarget("customFields", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<CustomField> getCustomFields() {
        return (List<CustomField>)readProperty("customFields");
    }


    public void addToFromContacts(ContactRelation obj) {
        addToManyTarget("fromContacts", obj, true);
    }
    public void removeFromFromContacts(ContactRelation obj) {
        removeToManyTarget("fromContacts", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<ContactRelation> getFromContacts() {
        return (List<ContactRelation>)readProperty("fromContacts");
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


    public void addToProducts(ProductItem obj) {
        addToManyTarget("products", obj, true);
    }
    public void removeFromProducts(ProductItem obj) {
        removeToManyTarget("products", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<ProductItem> getProducts() {
        return (List<ProductItem>)readProperty("products");
    }


    public void setStudent(Student student) {
        setToOneTarget("student", student, true);
    }

    public Student getStudent() {
        return (Student)readProperty("student");
    }


    public void setSupportPassword(SupportPassword supportPassword) {
        setToOneTarget("supportPassword", supportPassword, true);
    }

    public SupportPassword getSupportPassword() {
        return (SupportPassword)readProperty("supportPassword");
    }


    public void addToToContacts(ContactRelation obj) {
        addToManyTarget("toContacts", obj, true);
    }
    public void removeFromToContacts(ContactRelation obj) {
        removeToManyTarget("toContacts", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<ContactRelation> getToContacts() {
        return (List<ContactRelation>)readProperty("toContacts");
    }


    public void setTutor(Tutor tutor) {
        setToOneTarget("tutor", tutor, true);
    }

    public Tutor getTutor() {
        return (Tutor)readProperty("tutor");
    }


    protected abstract void onPostAdd();

    protected abstract void onPrePersist();

}

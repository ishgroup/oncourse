package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.College;
import ish.oncourse.model.ContactRelation;
import ish.oncourse.model.Country;
import ish.oncourse.model.DiscussionCommentContact;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.MessagePerson;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.ProductItem;
import ish.oncourse.model.Student;
import ish.oncourse.model.Tutor;

/**
 * Class _Contact was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Contact extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String BUSINESS_PHONE_NUMBER_PROPERTY = "businessPhoneNumber";
    public static final String COOKIE_HASH_PROPERTY = "cookieHash";
    public static final String CREATED_PROPERTY = "created";
    public static final String DATE_OF_BIRTH_PROPERTY = "dateOfBirth";
    public static final String EMAIL_ADDRESS_PROPERTY = "emailAddress";
    public static final String FAMILY_NAME_PROPERTY = "familyName";
    public static final String FAX_NUMBER_PROPERTY = "faxNumber";
    public static final String GIVEN_NAME_PROPERTY = "givenName";
    public static final String HOME_PHONE_NUMBER_PROPERTY = "homePhoneNumber";
    public static final String IS_COMPANY_PROPERTY = "isCompany";
    public static final String IS_MALE_PROPERTY = "isMale";
    public static final String IS_MARKETING_VIA_EMAIL_ALLOWED_PROPERTY = "isMarketingViaEmailAllowed";
    public static final String IS_MARKETING_VIA_POST_ALLOWED_PROPERTY = "isMarketingViaPostAllowed";
    public static final String IS_MARKETING_VIA_SMSALLOWED_PROPERTY = "isMarketingViaSMSAllowed";
    public static final String LAST_LOGIN_TIME_PROPERTY = "lastLoginTime";
    public static final String MOBILE_PHONE_NUMBER_PROPERTY = "mobilePhoneNumber";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String PASSWORD_PROPERTY = "password";
    public static final String PASSWORD_HASH_PROPERTY = "passwordHash";
    public static final String PASSWORD_RECOVER_EXPIRE_PROPERTY = "passwordRecoverExpire";
    public static final String PASSWORD_RECOVERY_KEY_PROPERTY = "passwordRecoveryKey";
    public static final String POSTCODE_PROPERTY = "postcode";
    public static final String STATE_PROPERTY = "state";
    public static final String STREET_PROPERTY = "street";
    public static final String SUBURB_PROPERTY = "suburb";
    public static final String TAX_FILE_NUMBER_PROPERTY = "taxFileNumber";
    public static final String UNIQUE_CODE_PROPERTY = "uniqueCode";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String CONTACT_COMMENTS_PROPERTY = "contactComments";
    public static final String COUNTRY_PROPERTY = "country";
    public static final String FROM_CONTACTS_PROPERTY = "fromContacts";
    public static final String INVOICES_PROPERTY = "invoices";
    public static final String MESSAGE_PEOPLE_PROPERTY = "messagePeople";
    public static final String PAYMENTS_IN_PROPERTY = "paymentsIn";
    public static final String PAYMENTS_OUT_PROPERTY = "paymentsOut";
    public static final String PRODUCTS_PROPERTY = "products";
    public static final String STUDENT_PROPERTY = "student";
    public static final String TO_CONTACTS_PROPERTY = "toContacts";
    public static final String TUTOR_PROPERTY = "tutor";

    public static final String ID_PK_COLUMN = "id";

    public void setAngelId(Long angelId) {
        writeProperty(ANGEL_ID_PROPERTY, angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty(ANGEL_ID_PROPERTY);
    }

    public void setBusinessPhoneNumber(String businessPhoneNumber) {
        writeProperty(BUSINESS_PHONE_NUMBER_PROPERTY, businessPhoneNumber);
    }
    public String getBusinessPhoneNumber() {
        return (String)readProperty(BUSINESS_PHONE_NUMBER_PROPERTY);
    }

    public void setCookieHash(String cookieHash) {
        writeProperty(COOKIE_HASH_PROPERTY, cookieHash);
    }
    public String getCookieHash() {
        return (String)readProperty(COOKIE_HASH_PROPERTY);
    }

    public void setCreated(Date created) {
        writeProperty(CREATED_PROPERTY, created);
    }
    public Date getCreated() {
        return (Date)readProperty(CREATED_PROPERTY);
    }

    public void setDateOfBirth(Date dateOfBirth) {
        writeProperty(DATE_OF_BIRTH_PROPERTY, dateOfBirth);
    }
    public Date getDateOfBirth() {
        return (Date)readProperty(DATE_OF_BIRTH_PROPERTY);
    }

    public void setEmailAddress(String emailAddress) {
        writeProperty(EMAIL_ADDRESS_PROPERTY, emailAddress);
    }
    public String getEmailAddress() {
        return (String)readProperty(EMAIL_ADDRESS_PROPERTY);
    }

    public void setFamilyName(String familyName) {
        writeProperty(FAMILY_NAME_PROPERTY, familyName);
    }
    public String getFamilyName() {
        return (String)readProperty(FAMILY_NAME_PROPERTY);
    }

    public void setFaxNumber(String faxNumber) {
        writeProperty(FAX_NUMBER_PROPERTY, faxNumber);
    }
    public String getFaxNumber() {
        return (String)readProperty(FAX_NUMBER_PROPERTY);
    }

    public void setGivenName(String givenName) {
        writeProperty(GIVEN_NAME_PROPERTY, givenName);
    }
    public String getGivenName() {
        return (String)readProperty(GIVEN_NAME_PROPERTY);
    }

    public void setHomePhoneNumber(String homePhoneNumber) {
        writeProperty(HOME_PHONE_NUMBER_PROPERTY, homePhoneNumber);
    }
    public String getHomePhoneNumber() {
        return (String)readProperty(HOME_PHONE_NUMBER_PROPERTY);
    }

    public void setIsCompany(Boolean isCompany) {
        writeProperty(IS_COMPANY_PROPERTY, isCompany);
    }
    public Boolean getIsCompany() {
        return (Boolean)readProperty(IS_COMPANY_PROPERTY);
    }

    public void setIsMale(Boolean isMale) {
        writeProperty(IS_MALE_PROPERTY, isMale);
    }
    public Boolean getIsMale() {
        return (Boolean)readProperty(IS_MALE_PROPERTY);
    }

    public void setIsMarketingViaEmailAllowed(Boolean isMarketingViaEmailAllowed) {
        writeProperty(IS_MARKETING_VIA_EMAIL_ALLOWED_PROPERTY, isMarketingViaEmailAllowed);
    }
    public Boolean getIsMarketingViaEmailAllowed() {
        return (Boolean)readProperty(IS_MARKETING_VIA_EMAIL_ALLOWED_PROPERTY);
    }

    public void setIsMarketingViaPostAllowed(Boolean isMarketingViaPostAllowed) {
        writeProperty(IS_MARKETING_VIA_POST_ALLOWED_PROPERTY, isMarketingViaPostAllowed);
    }
    public Boolean getIsMarketingViaPostAllowed() {
        return (Boolean)readProperty(IS_MARKETING_VIA_POST_ALLOWED_PROPERTY);
    }

    public void setIsMarketingViaSMSAllowed(Boolean isMarketingViaSMSAllowed) {
        writeProperty(IS_MARKETING_VIA_SMSALLOWED_PROPERTY, isMarketingViaSMSAllowed);
    }
    public Boolean getIsMarketingViaSMSAllowed() {
        return (Boolean)readProperty(IS_MARKETING_VIA_SMSALLOWED_PROPERTY);
    }

    public void setLastLoginTime(Date lastLoginTime) {
        writeProperty(LAST_LOGIN_TIME_PROPERTY, lastLoginTime);
    }
    public Date getLastLoginTime() {
        return (Date)readProperty(LAST_LOGIN_TIME_PROPERTY);
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        writeProperty(MOBILE_PHONE_NUMBER_PROPERTY, mobilePhoneNumber);
    }
    public String getMobilePhoneNumber() {
        return (String)readProperty(MOBILE_PHONE_NUMBER_PROPERTY);
    }

    public void setModified(Date modified) {
        writeProperty(MODIFIED_PROPERTY, modified);
    }
    public Date getModified() {
        return (Date)readProperty(MODIFIED_PROPERTY);
    }

    public void setPassword(String password) {
        writeProperty(PASSWORD_PROPERTY, password);
    }
    public String getPassword() {
        return (String)readProperty(PASSWORD_PROPERTY);
    }

    public void setPasswordHash(String passwordHash) {
        writeProperty(PASSWORD_HASH_PROPERTY, passwordHash);
    }
    public String getPasswordHash() {
        return (String)readProperty(PASSWORD_HASH_PROPERTY);
    }

    public void setPasswordRecoverExpire(Date passwordRecoverExpire) {
        writeProperty(PASSWORD_RECOVER_EXPIRE_PROPERTY, passwordRecoverExpire);
    }
    public Date getPasswordRecoverExpire() {
        return (Date)readProperty(PASSWORD_RECOVER_EXPIRE_PROPERTY);
    }

    public void setPasswordRecoveryKey(String passwordRecoveryKey) {
        writeProperty(PASSWORD_RECOVERY_KEY_PROPERTY, passwordRecoveryKey);
    }
    public String getPasswordRecoveryKey() {
        return (String)readProperty(PASSWORD_RECOVERY_KEY_PROPERTY);
    }

    public void setPostcode(String postcode) {
        writeProperty(POSTCODE_PROPERTY, postcode);
    }
    public String getPostcode() {
        return (String)readProperty(POSTCODE_PROPERTY);
    }

    public void setState(String state) {
        writeProperty(STATE_PROPERTY, state);
    }
    public String getState() {
        return (String)readProperty(STATE_PROPERTY);
    }

    public void setStreet(String street) {
        writeProperty(STREET_PROPERTY, street);
    }
    public String getStreet() {
        return (String)readProperty(STREET_PROPERTY);
    }

    public void setSuburb(String suburb) {
        writeProperty(SUBURB_PROPERTY, suburb);
    }
    public String getSuburb() {
        return (String)readProperty(SUBURB_PROPERTY);
    }

    public void setTaxFileNumber(String taxFileNumber) {
        writeProperty(TAX_FILE_NUMBER_PROPERTY, taxFileNumber);
    }
    public String getTaxFileNumber() {
        return (String)readProperty(TAX_FILE_NUMBER_PROPERTY);
    }

    public void setUniqueCode(String uniqueCode) {
        writeProperty(UNIQUE_CODE_PROPERTY, uniqueCode);
    }
    public String getUniqueCode() {
        return (String)readProperty(UNIQUE_CODE_PROPERTY);
    }

    public void setCollege(College college) {
        setToOneTarget(COLLEGE_PROPERTY, college, true);
    }

    public College getCollege() {
        return (College)readProperty(COLLEGE_PROPERTY);
    }


    public void addToContactComments(DiscussionCommentContact obj) {
        addToManyTarget(CONTACT_COMMENTS_PROPERTY, obj, true);
    }
    public void removeFromContactComments(DiscussionCommentContact obj) {
        removeToManyTarget(CONTACT_COMMENTS_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<DiscussionCommentContact> getContactComments() {
        return (List<DiscussionCommentContact>)readProperty(CONTACT_COMMENTS_PROPERTY);
    }


    public void setCountry(Country country) {
        setToOneTarget(COUNTRY_PROPERTY, country, true);
    }

    public Country getCountry() {
        return (Country)readProperty(COUNTRY_PROPERTY);
    }


    public void addToFromContacts(ContactRelation obj) {
        addToManyTarget(FROM_CONTACTS_PROPERTY, obj, true);
    }
    public void removeFromFromContacts(ContactRelation obj) {
        removeToManyTarget(FROM_CONTACTS_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<ContactRelation> getFromContacts() {
        return (List<ContactRelation>)readProperty(FROM_CONTACTS_PROPERTY);
    }


    public void addToInvoices(Invoice obj) {
        addToManyTarget(INVOICES_PROPERTY, obj, true);
    }
    public void removeFromInvoices(Invoice obj) {
        removeToManyTarget(INVOICES_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Invoice> getInvoices() {
        return (List<Invoice>)readProperty(INVOICES_PROPERTY);
    }


    public void addToMessagePeople(MessagePerson obj) {
        addToManyTarget(MESSAGE_PEOPLE_PROPERTY, obj, true);
    }
    public void removeFromMessagePeople(MessagePerson obj) {
        removeToManyTarget(MESSAGE_PEOPLE_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<MessagePerson> getMessagePeople() {
        return (List<MessagePerson>)readProperty(MESSAGE_PEOPLE_PROPERTY);
    }


    public void addToPaymentsIn(PaymentIn obj) {
        addToManyTarget(PAYMENTS_IN_PROPERTY, obj, true);
    }
    public void removeFromPaymentsIn(PaymentIn obj) {
        removeToManyTarget(PAYMENTS_IN_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<PaymentIn> getPaymentsIn() {
        return (List<PaymentIn>)readProperty(PAYMENTS_IN_PROPERTY);
    }


    public void addToPaymentsOut(PaymentOut obj) {
        addToManyTarget(PAYMENTS_OUT_PROPERTY, obj, true);
    }
    public void removeFromPaymentsOut(PaymentOut obj) {
        removeToManyTarget(PAYMENTS_OUT_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<PaymentOut> getPaymentsOut() {
        return (List<PaymentOut>)readProperty(PAYMENTS_OUT_PROPERTY);
    }


    public void addToProducts(ProductItem obj) {
        addToManyTarget(PRODUCTS_PROPERTY, obj, true);
    }
    public void removeFromProducts(ProductItem obj) {
        removeToManyTarget(PRODUCTS_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<ProductItem> getProducts() {
        return (List<ProductItem>)readProperty(PRODUCTS_PROPERTY);
    }


    public void setStudent(Student student) {
        setToOneTarget(STUDENT_PROPERTY, student, true);
    }

    public Student getStudent() {
        return (Student)readProperty(STUDENT_PROPERTY);
    }


    public void addToToContacts(ContactRelation obj) {
        addToManyTarget(TO_CONTACTS_PROPERTY, obj, true);
    }
    public void removeFromToContacts(ContactRelation obj) {
        removeToManyTarget(TO_CONTACTS_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<ContactRelation> getToContacts() {
        return (List<ContactRelation>)readProperty(TO_CONTACTS_PROPERTY);
    }


    public void setTutor(Tutor tutor) {
        setToOneTarget(TUTOR_PROPERTY, tutor, true);
    }

    public Tutor getTutor() {
        return (Tutor)readProperty(TUTOR_PROPERTY);
    }


    protected abstract void onPostAdd();

    protected abstract void onPrePersist();

}

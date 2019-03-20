package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.exp.Property;

import ish.oncourse.model.College;
import ish.oncourse.model.DiscountConcessionType;
import ish.oncourse.model.StudentConcession;
import ish.oncourse.model.SystemUser;

/**
 * Class _ConcessionType was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _ConcessionType extends WillowCayenneObject {

    private static final long serialVersionUID = 1L; 

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String CREDENTIAL_EXPIRY_DAYS_PROPERTY = "credentialExpiryDays";
    public static final String HAS_CONCESSION_NUMBER_PROPERTY = "hasConcessionNumber";
    public static final String HAS_EXPIRY_DATE_PROPERTY = "hasExpiryDate";
    public static final String IS_CONCESSION_PROPERTY = "isConcession";
    public static final String IS_ENABLED_PROPERTY = "isEnabled";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String REQUIRES_CREDENTIAL_CHECK_PROPERTY = "requiresCredentialCheck";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String CREATED_BY_PROPERTY = "createdBy";
    public static final String DISCOUNT_CONCESSION_TYPES_PROPERTY = "discountConcessionTypes";
    public static final String STUDENT_CONCESSIONS_PROPERTY = "studentConcessions";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = Property.create("angelId", Long.class);
    public static final Property<Date> CREATED = Property.create("created", Date.class);
    public static final Property<Integer> CREDENTIAL_EXPIRY_DAYS = Property.create("credentialExpiryDays", Integer.class);
    public static final Property<Boolean> HAS_CONCESSION_NUMBER = Property.create("hasConcessionNumber", Boolean.class);
    public static final Property<Boolean> HAS_EXPIRY_DATE = Property.create("hasExpiryDate", Boolean.class);
    public static final Property<Boolean> IS_CONCESSION = Property.create("isConcession", Boolean.class);
    public static final Property<Boolean> IS_ENABLED = Property.create("isEnabled", Boolean.class);
    public static final Property<Date> MODIFIED = Property.create("modified", Date.class);
    public static final Property<String> NAME = Property.create("name", String.class);
    public static final Property<Boolean> REQUIRES_CREDENTIAL_CHECK = Property.create("requiresCredentialCheck", Boolean.class);
    public static final Property<College> COLLEGE = Property.create("college", College.class);
    public static final Property<SystemUser> CREATED_BY = Property.create("createdBy", SystemUser.class);
    public static final Property<List<DiscountConcessionType>> DISCOUNT_CONCESSION_TYPES = Property.create("discountConcessionTypes", List.class);
    public static final Property<List<StudentConcession>> STUDENT_CONCESSIONS = Property.create("studentConcessions", List.class);

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setCredentialExpiryDays(Integer credentialExpiryDays) {
        writeProperty("credentialExpiryDays", credentialExpiryDays);
    }
    public Integer getCredentialExpiryDays() {
        return (Integer)readProperty("credentialExpiryDays");
    }

    public void setHasConcessionNumber(Boolean hasConcessionNumber) {
        writeProperty("hasConcessionNumber", hasConcessionNumber);
    }
    public Boolean getHasConcessionNumber() {
        return (Boolean)readProperty("hasConcessionNumber");
    }

    public void setHasExpiryDate(Boolean hasExpiryDate) {
        writeProperty("hasExpiryDate", hasExpiryDate);
    }
    public Boolean getHasExpiryDate() {
        return (Boolean)readProperty("hasExpiryDate");
    }

    public void setIsConcession(Boolean isConcession) {
        writeProperty("isConcession", isConcession);
    }
    public Boolean getIsConcession() {
        return (Boolean)readProperty("isConcession");
    }

    public void setIsEnabled(Boolean isEnabled) {
        writeProperty("isEnabled", isEnabled);
    }
    public Boolean getIsEnabled() {
        return (Boolean)readProperty("isEnabled");
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

    public void setRequiresCredentialCheck(Boolean requiresCredentialCheck) {
        writeProperty("requiresCredentialCheck", requiresCredentialCheck);
    }
    public Boolean getRequiresCredentialCheck() {
        return (Boolean)readProperty("requiresCredentialCheck");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setCreatedBy(SystemUser createdBy) {
        setToOneTarget("createdBy", createdBy, true);
    }

    public SystemUser getCreatedBy() {
        return (SystemUser)readProperty("createdBy");
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


}

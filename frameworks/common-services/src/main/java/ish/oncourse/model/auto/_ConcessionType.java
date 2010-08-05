package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.DiscountConcessionType;
import ish.oncourse.model.StudentConcession;

/**
 * Class _ConcessionType was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _ConcessionType extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String COLLEGE_ID_PROPERTY = "collegeId";
    public static final String CREATED_PROPERTY = "created";
    public static final String CREDENTIAL_EXPIRY_DAYS_PROPERTY = "credentialExpiryDays";
    public static final String HAS_CONCESSION_NUMBER_PROPERTY = "hasConcessionNumber";
    public static final String HAS_EXPIRY_DATE_PROPERTY = "hasExpiryDate";
    public static final String IS_CONCESSION_PROPERTY = "isConcession";
    public static final String IS_DELETED_PROPERTY = "isDeleted";
    public static final String IS_ENABLED_PROPERTY = "isEnabled";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String REQUIRES_CREDENTIAL_CHECK_PROPERTY = "requiresCredentialCheck";
    public static final String DISCOUNT_CONCESSION_TYPES_PROPERTY = "discountConcessionTypes";
    public static final String STUDENT_CONCESSIONS_PROPERTY = "studentConcessions";

    public static final String ID_PK_COLUMN = "id";

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setCollegeId(Long collegeId) {
        writeProperty("collegeId", collegeId);
    }
    public Long getCollegeId() {
        return (Long)readProperty("collegeId");
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

    public void setHasExpiryDate(Integer hasExpiryDate) {
        writeProperty("hasExpiryDate", hasExpiryDate);
    }
    public Integer getHasExpiryDate() {
        return (Integer)readProperty("hasExpiryDate");
    }

    public void setIsConcession(Boolean isConcession) {
        writeProperty("isConcession", isConcession);
    }
    public Boolean getIsConcession() {
        return (Boolean)readProperty("isConcession");
    }

    public void setIsDeleted(Boolean isDeleted) {
        writeProperty("isDeleted", isDeleted);
    }
    public Boolean getIsDeleted() {
        return (Boolean)readProperty("isDeleted");
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

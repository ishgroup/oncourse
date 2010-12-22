package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.College;
import ish.oncourse.model.ConcessionType;
import ish.oncourse.model.Student;

/**
 * Class _StudentConcession was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _StudentConcession extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String AUTHORISATION_EXPIRES_ON_PROPERTY = "authorisationExpiresOn";
    public static final String AUTHORISED_ON_PROPERTY = "authorisedOn";
    public static final String CONCESSION_NUMBER_PROPERTY = "concessionNumber";
    public static final String CREATED_PROPERTY = "created";
    public static final String EXPIRES_ON_PROPERTY = "expiresOn";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String TIME_ZONE_PROPERTY = "timeZone";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String CONCESSION_TYPE_PROPERTY = "concessionType";
    public static final String STUDENT_PROPERTY = "student";

    public static final String ID_PK_COLUMN = "id";

    public void setAngelId(Long angelId) {
        writeProperty("angelId", angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty("angelId");
    }

    public void setAuthorisationExpiresOn(Date authorisationExpiresOn) {
        writeProperty("authorisationExpiresOn", authorisationExpiresOn);
    }
    public Date getAuthorisationExpiresOn() {
        return (Date)readProperty("authorisationExpiresOn");
    }

    public void setAuthorisedOn(Date authorisedOn) {
        writeProperty("authorisedOn", authorisedOn);
    }
    public Date getAuthorisedOn() {
        return (Date)readProperty("authorisedOn");
    }

    public void setConcessionNumber(String concessionNumber) {
        writeProperty("concessionNumber", concessionNumber);
    }
    public String getConcessionNumber() {
        return (String)readProperty("concessionNumber");
    }

    public void setCreated(Date created) {
        writeProperty("created", created);
    }
    public Date getCreated() {
        return (Date)readProperty("created");
    }

    public void setExpiresOn(Date expiresOn) {
        writeProperty("expiresOn", expiresOn);
    }
    public Date getExpiresOn() {
        return (Date)readProperty("expiresOn");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setTimeZone(String timeZone) {
        writeProperty("timeZone", timeZone);
    }
    public String getTimeZone() {
        return (String)readProperty("timeZone");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setConcessionType(ConcessionType concessionType) {
        setToOneTarget("concessionType", concessionType, true);
    }

    public ConcessionType getConcessionType() {
        return (ConcessionType)readProperty("concessionType");
    }


    public void setStudent(Student student) {
        setToOneTarget("student", student, true);
    }

    public Student getStudent() {
        return (Student)readProperty("student");
    }


}

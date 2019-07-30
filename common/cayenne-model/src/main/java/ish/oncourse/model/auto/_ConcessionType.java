package ish.oncourse.model.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    protected Long angelId;
    protected Date created;
    protected Integer credentialExpiryDays;
    protected Boolean hasConcessionNumber;
    protected Boolean hasExpiryDate;
    protected Boolean isConcession;
    protected Boolean isEnabled;
    protected Date modified;
    protected String name;
    protected Boolean requiresCredentialCheck;

    protected Object college;
    protected Object createdBy;
    protected Object discountConcessionTypes;
    protected Object studentConcessions;

    public void setAngelId(Long angelId) {
        beforePropertyWrite("angelId", this.angelId, angelId);
        this.angelId = angelId;
    }

    public Long getAngelId() {
        beforePropertyRead("angelId");
        return this.angelId;
    }

    public void setCreated(Date created) {
        beforePropertyWrite("created", this.created, created);
        this.created = created;
    }

    public Date getCreated() {
        beforePropertyRead("created");
        return this.created;
    }

    public void setCredentialExpiryDays(Integer credentialExpiryDays) {
        beforePropertyWrite("credentialExpiryDays", this.credentialExpiryDays, credentialExpiryDays);
        this.credentialExpiryDays = credentialExpiryDays;
    }

    public Integer getCredentialExpiryDays() {
        beforePropertyRead("credentialExpiryDays");
        return this.credentialExpiryDays;
    }

    public void setHasConcessionNumber(Boolean hasConcessionNumber) {
        beforePropertyWrite("hasConcessionNumber", this.hasConcessionNumber, hasConcessionNumber);
        this.hasConcessionNumber = hasConcessionNumber;
    }

    public Boolean getHasConcessionNumber() {
        beforePropertyRead("hasConcessionNumber");
        return this.hasConcessionNumber;
    }

    public void setHasExpiryDate(Boolean hasExpiryDate) {
        beforePropertyWrite("hasExpiryDate", this.hasExpiryDate, hasExpiryDate);
        this.hasExpiryDate = hasExpiryDate;
    }

    public Boolean getHasExpiryDate() {
        beforePropertyRead("hasExpiryDate");
        return this.hasExpiryDate;
    }

    public void setIsConcession(Boolean isConcession) {
        beforePropertyWrite("isConcession", this.isConcession, isConcession);
        this.isConcession = isConcession;
    }

    public Boolean getIsConcession() {
        beforePropertyRead("isConcession");
        return this.isConcession;
    }

    public void setIsEnabled(Boolean isEnabled) {
        beforePropertyWrite("isEnabled", this.isEnabled, isEnabled);
        this.isEnabled = isEnabled;
    }

    public Boolean getIsEnabled() {
        beforePropertyRead("isEnabled");
        return this.isEnabled;
    }

    public void setModified(Date modified) {
        beforePropertyWrite("modified", this.modified, modified);
        this.modified = modified;
    }

    public Date getModified() {
        beforePropertyRead("modified");
        return this.modified;
    }

    public void setName(String name) {
        beforePropertyWrite("name", this.name, name);
        this.name = name;
    }

    public String getName() {
        beforePropertyRead("name");
        return this.name;
    }

    public void setRequiresCredentialCheck(Boolean requiresCredentialCheck) {
        beforePropertyWrite("requiresCredentialCheck", this.requiresCredentialCheck, requiresCredentialCheck);
        this.requiresCredentialCheck = requiresCredentialCheck;
    }

    public Boolean getRequiresCredentialCheck() {
        beforePropertyRead("requiresCredentialCheck");
        return this.requiresCredentialCheck;
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

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "angelId":
                return this.angelId;
            case "created":
                return this.created;
            case "credentialExpiryDays":
                return this.credentialExpiryDays;
            case "hasConcessionNumber":
                return this.hasConcessionNumber;
            case "hasExpiryDate":
                return this.hasExpiryDate;
            case "isConcession":
                return this.isConcession;
            case "isEnabled":
                return this.isEnabled;
            case "modified":
                return this.modified;
            case "name":
                return this.name;
            case "requiresCredentialCheck":
                return this.requiresCredentialCheck;
            case "college":
                return this.college;
            case "createdBy":
                return this.createdBy;
            case "discountConcessionTypes":
                return this.discountConcessionTypes;
            case "studentConcessions":
                return this.studentConcessions;
            default:
                return super.readPropertyDirectly(propName);
        }
    }

    @Override
    public void writePropertyDirectly(String propName, Object val) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch (propName) {
            case "angelId":
                this.angelId = (Long)val;
                break;
            case "created":
                this.created = (Date)val;
                break;
            case "credentialExpiryDays":
                this.credentialExpiryDays = (Integer)val;
                break;
            case "hasConcessionNumber":
                this.hasConcessionNumber = (Boolean)val;
                break;
            case "hasExpiryDate":
                this.hasExpiryDate = (Boolean)val;
                break;
            case "isConcession":
                this.isConcession = (Boolean)val;
                break;
            case "isEnabled":
                this.isEnabled = (Boolean)val;
                break;
            case "modified":
                this.modified = (Date)val;
                break;
            case "name":
                this.name = (String)val;
                break;
            case "requiresCredentialCheck":
                this.requiresCredentialCheck = (Boolean)val;
                break;
            case "college":
                this.college = val;
                break;
            case "createdBy":
                this.createdBy = val;
                break;
            case "discountConcessionTypes":
                this.discountConcessionTypes = val;
                break;
            case "studentConcessions":
                this.studentConcessions = val;
                break;
            default:
                super.writePropertyDirectly(propName, val);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        writeSerialized(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        readSerialized(in);
    }

    @Override
    protected void writeState(ObjectOutputStream out) throws IOException {
        super.writeState(out);
        out.writeObject(this.angelId);
        out.writeObject(this.created);
        out.writeObject(this.credentialExpiryDays);
        out.writeObject(this.hasConcessionNumber);
        out.writeObject(this.hasExpiryDate);
        out.writeObject(this.isConcession);
        out.writeObject(this.isEnabled);
        out.writeObject(this.modified);
        out.writeObject(this.name);
        out.writeObject(this.requiresCredentialCheck);
        out.writeObject(this.college);
        out.writeObject(this.createdBy);
        out.writeObject(this.discountConcessionTypes);
        out.writeObject(this.studentConcessions);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.angelId = (Long)in.readObject();
        this.created = (Date)in.readObject();
        this.credentialExpiryDays = (Integer)in.readObject();
        this.hasConcessionNumber = (Boolean)in.readObject();
        this.hasExpiryDate = (Boolean)in.readObject();
        this.isConcession = (Boolean)in.readObject();
        this.isEnabled = (Boolean)in.readObject();
        this.modified = (Date)in.readObject();
        this.name = (String)in.readObject();
        this.requiresCredentialCheck = (Boolean)in.readObject();
        this.college = in.readObject();
        this.createdBy = in.readObject();
        this.discountConcessionTypes = in.readObject();
        this.studentConcessions = in.readObject();
    }

}

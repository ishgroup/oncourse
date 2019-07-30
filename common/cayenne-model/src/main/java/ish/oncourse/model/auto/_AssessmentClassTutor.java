package ish.oncourse.model.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import org.apache.cayenne.exp.Property;

import ish.oncourse.model.AssessmentClass;
import ish.oncourse.model.College;
import ish.oncourse.model.Tutor;

/**
 * Class _AssessmentClassTutor was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _AssessmentClassTutor extends WillowCayenneObject {

    private static final long serialVersionUID = 1L; 

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String COLLEGE_ID_PROPERTY = "collegeId";
    public static final String CREATED_PROPERTY = "created";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String ASSESSMENT_CLASS_PROPERTY = "assessmentClass";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String TUTOR_PROPERTY = "tutor";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = Property.create("angelId", Long.class);
    public static final Property<Long> COLLEGE_ID = Property.create("collegeId", Long.class);
    public static final Property<Date> CREATED = Property.create("created", Date.class);
    public static final Property<Date> MODIFIED = Property.create("modified", Date.class);
    public static final Property<AssessmentClass> ASSESSMENT_CLASS = Property.create("assessmentClass", AssessmentClass.class);
    public static final Property<College> COLLEGE = Property.create("college", College.class);
    public static final Property<Tutor> TUTOR = Property.create("tutor", Tutor.class);

    protected Long angelId;
    protected Long collegeId;
    protected Date created;
    protected Date modified;

    protected Object assessmentClass;
    protected Object college;
    protected Object tutor;

    public void setAngelId(Long angelId) {
        beforePropertyWrite("angelId", this.angelId, angelId);
        this.angelId = angelId;
    }

    public Long getAngelId() {
        beforePropertyRead("angelId");
        return this.angelId;
    }

    public void setCollegeId(Long collegeId) {
        beforePropertyWrite("collegeId", this.collegeId, collegeId);
        this.collegeId = collegeId;
    }

    public Long getCollegeId() {
        beforePropertyRead("collegeId");
        return this.collegeId;
    }

    public void setCreated(Date created) {
        beforePropertyWrite("created", this.created, created);
        this.created = created;
    }

    public Date getCreated() {
        beforePropertyRead("created");
        return this.created;
    }

    public void setModified(Date modified) {
        beforePropertyWrite("modified", this.modified, modified);
        this.modified = modified;
    }

    public Date getModified() {
        beforePropertyRead("modified");
        return this.modified;
    }

    public void setAssessmentClass(AssessmentClass assessmentClass) {
        setToOneTarget("assessmentClass", assessmentClass, true);
    }

    public AssessmentClass getAssessmentClass() {
        return (AssessmentClass)readProperty("assessmentClass");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }

    public void setTutor(Tutor tutor) {
        setToOneTarget("tutor", tutor, true);
    }

    public Tutor getTutor() {
        return (Tutor)readProperty("tutor");
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "angelId":
                return this.angelId;
            case "collegeId":
                return this.collegeId;
            case "created":
                return this.created;
            case "modified":
                return this.modified;
            case "assessmentClass":
                return this.assessmentClass;
            case "college":
                return this.college;
            case "tutor":
                return this.tutor;
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
            case "collegeId":
                this.collegeId = (Long)val;
                break;
            case "created":
                this.created = (Date)val;
                break;
            case "modified":
                this.modified = (Date)val;
                break;
            case "assessmentClass":
                this.assessmentClass = val;
                break;
            case "college":
                this.college = val;
                break;
            case "tutor":
                this.tutor = val;
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
        out.writeObject(this.collegeId);
        out.writeObject(this.created);
        out.writeObject(this.modified);
        out.writeObject(this.assessmentClass);
        out.writeObject(this.college);
        out.writeObject(this.tutor);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.angelId = (Long)in.readObject();
        this.collegeId = (Long)in.readObject();
        this.created = (Date)in.readObject();
        this.modified = (Date)in.readObject();
        this.assessmentClass = in.readObject();
        this.college = in.readObject();
        this.tutor = in.readObject();
    }

}

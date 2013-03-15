package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentSource;
import ish.oncourse.model.College;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.Outcome;
import ish.oncourse.model.Student;
import ish.oncourse.model.Survey;

/**
 * Class _Enrolment was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Enrolment extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String REASON_FOR_STUDY_PROPERTY = "reasonForStudy";
    public static final String SOURCE_PROPERTY = "source";
    public static final String STATUS_PROPERTY = "status";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String COURSE_CLASS_PROPERTY = "courseClass";
    public static final String INVOICE_LINE_PROPERTY = "invoiceLine";
    public static final String OUTCOMES_PROPERTY = "outcomes";
    public static final String STUDENT_PROPERTY = "student";
    public static final String SURVEYS_PROPERTY = "surveys";

    public static final String ID_PK_COLUMN = "id";

    public void setAngelId(Long angelId) {
        writeProperty(ANGEL_ID_PROPERTY, angelId);
    }
    public Long getAngelId() {
        return (Long)readProperty(ANGEL_ID_PROPERTY);
    }

    public void setCreated(Date created) {
        writeProperty(CREATED_PROPERTY, created);
    }
    public Date getCreated() {
        return (Date)readProperty(CREATED_PROPERTY);
    }

    public void setModified(Date modified) {
        writeProperty(MODIFIED_PROPERTY, modified);
    }
    public Date getModified() {
        return (Date)readProperty(MODIFIED_PROPERTY);
    }

    public void setReasonForStudy(Integer reasonForStudy) {
        writeProperty(REASON_FOR_STUDY_PROPERTY, reasonForStudy);
    }
    public Integer getReasonForStudy() {
        return (Integer)readProperty(REASON_FOR_STUDY_PROPERTY);
    }

    public void setSource(PaymentSource source) {
        writeProperty(SOURCE_PROPERTY, source);
    }
    public PaymentSource getSource() {
        return (PaymentSource)readProperty(SOURCE_PROPERTY);
    }

    public void setStatus(EnrolmentStatus status) {
        writeProperty(STATUS_PROPERTY, status);
    }
    public EnrolmentStatus getStatus() {
        return (EnrolmentStatus)readProperty(STATUS_PROPERTY);
    }

    public void setCollege(College college) {
        setToOneTarget(COLLEGE_PROPERTY, college, true);
    }

    public College getCollege() {
        return (College)readProperty(COLLEGE_PROPERTY);
    }


    public void setCourseClass(CourseClass courseClass) {
        setToOneTarget(COURSE_CLASS_PROPERTY, courseClass, true);
    }

    public CourseClass getCourseClass() {
        return (CourseClass)readProperty(COURSE_CLASS_PROPERTY);
    }


    public void setInvoiceLine(InvoiceLine invoiceLine) {
        setToOneTarget(INVOICE_LINE_PROPERTY, invoiceLine, true);
    }

    public InvoiceLine getInvoiceLine() {
        return (InvoiceLine)readProperty(INVOICE_LINE_PROPERTY);
    }


    public void addToOutcomes(Outcome obj) {
        addToManyTarget(OUTCOMES_PROPERTY, obj, true);
    }
    public void removeFromOutcomes(Outcome obj) {
        removeToManyTarget(OUTCOMES_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Outcome> getOutcomes() {
        return (List<Outcome>)readProperty(OUTCOMES_PROPERTY);
    }


    public void setStudent(Student student) {
        setToOneTarget(STUDENT_PROPERTY, student, true);
    }

    public Student getStudent() {
        return (Student)readProperty(STUDENT_PROPERTY);
    }


    public void addToSurveys(Survey obj) {
        addToManyTarget(SURVEYS_PROPERTY, obj, true);
    }
    public void removeFromSurveys(Survey obj) {
        removeToManyTarget(SURVEYS_PROPERTY, obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Survey> getSurveys() {
        return (List<Survey>)readProperty(SURVEYS_PROPERTY);
    }


    protected abstract void onPostAdd();

    protected abstract void onPrePersist();

}

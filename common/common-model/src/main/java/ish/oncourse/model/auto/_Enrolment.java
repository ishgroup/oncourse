package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;

import ish.common.types.CreditLevel;
import ish.common.types.CreditProviderType;
import ish.common.types.CreditType;
import ish.common.types.EnrolmentStatus;
import ish.common.types.EnrolmentVETFeeHelpStatus;
import ish.common.types.PaymentSource;
import ish.common.types.RecognitionOfPriorLearningIndicator;
import ish.common.types.StudentStatusForUnitOfStudy;
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
    public static final String CREDIT_FOEID_PROPERTY = "creditFOEId";
    public static final String CREDIT_LEVEL_PROPERTY = "creditLevel";
    public static final String CREDIT_OFFERED_VALUE_PROPERTY = "creditOfferedValue";
    public static final String CREDIT_PROVIDER_PROPERTY = "creditProvider";
    public static final String CREDIT_PROVIDER_TYPE_PROPERTY = "creditProviderType";
    public static final String CREDIT_TOTAL_PROPERTY = "creditTotal";
    public static final String CREDIT_TYPE_PROPERTY = "creditType";
    public static final String CREDIT_USED_VALUE_PROPERTY = "creditUsedValue";
    public static final String FEE_HELP_STATUS_PROPERTY = "feeHelpStatus";
    public static final String FEE_STATUS_PROPERTY = "feeStatus";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String REASON_FOR_STUDY_PROPERTY = "reasonForStudy";
    public static final String SOURCE_PROPERTY = "source";
    public static final String STATUS_PROPERTY = "status";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String COURSE_CLASS_PROPERTY = "courseClass";
    public static final String INVOICE_LINES_PROPERTY = "invoiceLines";
    public static final String OUTCOMES_PROPERTY = "outcomes";
    public static final String STUDENT_PROPERTY = "student";
    public static final String SURVEYS_PROPERTY = "surveys";

    public static final String ID_PK_COLUMN = "id";

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

    public void setCreditFOEId(String creditFOEId) {
        writeProperty("creditFOEId", creditFOEId);
    }
    public String getCreditFOEId() {
        return (String)readProperty("creditFOEId");
    }

    public void setCreditLevel(CreditLevel creditLevel) {
        writeProperty("creditLevel", creditLevel);
    }
    public CreditLevel getCreditLevel() {
        return (CreditLevel)readProperty("creditLevel");
    }

    public void setCreditOfferedValue(String creditOfferedValue) {
        writeProperty("creditOfferedValue", creditOfferedValue);
    }
    public String getCreditOfferedValue() {
        return (String)readProperty("creditOfferedValue");
    }

    public void setCreditProvider(String creditProvider) {
        writeProperty("creditProvider", creditProvider);
    }
    public String getCreditProvider() {
        return (String)readProperty("creditProvider");
    }

    public void setCreditProviderType(CreditProviderType creditProviderType) {
        writeProperty("creditProviderType", creditProviderType);
    }
    public CreditProviderType getCreditProviderType() {
        return (CreditProviderType)readProperty("creditProviderType");
    }

    public void setCreditTotal(RecognitionOfPriorLearningIndicator creditTotal) {
        writeProperty("creditTotal", creditTotal);
    }
    public RecognitionOfPriorLearningIndicator getCreditTotal() {
        return (RecognitionOfPriorLearningIndicator)readProperty("creditTotal");
    }

    public void setCreditType(CreditType creditType) {
        writeProperty("creditType", creditType);
    }
    public CreditType getCreditType() {
        return (CreditType)readProperty("creditType");
    }

    public void setCreditUsedValue(String creditUsedValue) {
        writeProperty("creditUsedValue", creditUsedValue);
    }
    public String getCreditUsedValue() {
        return (String)readProperty("creditUsedValue");
    }

    public void setFeeHelpStatus(EnrolmentVETFeeHelpStatus feeHelpStatus) {
        writeProperty("feeHelpStatus", feeHelpStatus);
    }
    public EnrolmentVETFeeHelpStatus getFeeHelpStatus() {
        return (EnrolmentVETFeeHelpStatus)readProperty("feeHelpStatus");
    }

    public void setFeeStatus(StudentStatusForUnitOfStudy feeStatus) {
        writeProperty("feeStatus", feeStatus);
    }
    public StudentStatusForUnitOfStudy getFeeStatus() {
        return (StudentStatusForUnitOfStudy)readProperty("feeStatus");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setReasonForStudy(Integer reasonForStudy) {
        writeProperty("reasonForStudy", reasonForStudy);
    }
    public Integer getReasonForStudy() {
        return (Integer)readProperty("reasonForStudy");
    }

    public void setSource(PaymentSource source) {
        writeProperty("source", source);
    }
    public PaymentSource getSource() {
        return (PaymentSource)readProperty("source");
    }

    public void setStatus(EnrolmentStatus status) {
        writeProperty("status", status);
    }
    public EnrolmentStatus getStatus() {
        return (EnrolmentStatus)readProperty("status");
    }

    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setCourseClass(CourseClass courseClass) {
        setToOneTarget("courseClass", courseClass, true);
    }

    public CourseClass getCourseClass() {
        return (CourseClass)readProperty("courseClass");
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


    public void setStudent(Student student) {
        setToOneTarget("student", student, true);
    }

    public Student getStudent() {
        return (Student)readProperty("student");
    }


    public void addToSurveys(Survey obj) {
        addToManyTarget("surveys", obj, true);
    }
    public void removeFromSurveys(Survey obj) {
        removeToManyTarget("surveys", obj, true);
    }
    @SuppressWarnings("unchecked")
    public List<Survey> getSurveys() {
        return (List<Survey>)readProperty("surveys");
    }


    protected abstract void onPostAdd();

    protected abstract void onPrePersist();

}

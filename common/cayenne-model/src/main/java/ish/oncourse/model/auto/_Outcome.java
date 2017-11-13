package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

import ish.common.types.ClassFundingSource;
import ish.common.types.OutcomeStatus;
import ish.oncourse.model.CertificateOutcome;
import ish.oncourse.model.College;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Module;
import ish.oncourse.model.PriorLearning;
import ish.oncourse.model.Tutor;

/**
 * Class _Outcome was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Outcome extends CayenneDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String DELIVERY_MODE_PROPERTY = "deliveryMode";
    public static final String END_DATE_PROPERTY = "endDate";
    public static final String FUNDING_SOURCE_PROPERTY = "fundingSource";
    public static final String MARKED_BY_TUTOR_DATE_PROPERTY = "markedByTutorDate";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String REPORTABLE_HOURS_PROPERTY = "reportableHours";
    public static final String START_DATE_PROPERTY = "startDate";
    public static final String STATUS_PROPERTY = "status";
    public static final String CERTIFICATE_OUTCOMES_PROPERTY = "certificateOutcomes";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String ENROLMENT_PROPERTY = "enrolment";
    public static final String MARKED_BY_TUTOR_PROPERTY = "markedByTutor";
    public static final String MODULE_PROPERTY = "module";
    public static final String PRIOR_LEARNING_PROPERTY = "priorLearning";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = Property.create("angelId", Long.class);
    public static final Property<Date> CREATED = Property.create("created", Date.class);
    public static final Property<Integer> DELIVERY_MODE = Property.create("deliveryMode", Integer.class);
    public static final Property<Date> END_DATE = Property.create("endDate", Date.class);
    public static final Property<ClassFundingSource> FUNDING_SOURCE = Property.create("fundingSource", ClassFundingSource.class);
    public static final Property<Date> MARKED_BY_TUTOR_DATE = Property.create("markedByTutorDate", Date.class);
    public static final Property<Date> MODIFIED = Property.create("modified", Date.class);
    public static final Property<Double> REPORTABLE_HOURS = Property.create("reportableHours", Double.class);
    public static final Property<Date> START_DATE = Property.create("startDate", Date.class);
    public static final Property<OutcomeStatus> STATUS = Property.create("status", OutcomeStatus.class);
    public static final Property<List<CertificateOutcome>> CERTIFICATE_OUTCOMES = Property.create("certificateOutcomes", List.class);
    public static final Property<College> COLLEGE = Property.create("college", College.class);
    public static final Property<Enrolment> ENROLMENT = Property.create("enrolment", Enrolment.class);
    public static final Property<Tutor> MARKED_BY_TUTOR = Property.create("markedByTutor", Tutor.class);
    public static final Property<Module> MODULE = Property.create("module", Module.class);
    public static final Property<PriorLearning> PRIOR_LEARNING = Property.create("priorLearning", PriorLearning.class);

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

    public void setDeliveryMode(Integer deliveryMode) {
        writeProperty("deliveryMode", deliveryMode);
    }
    public Integer getDeliveryMode() {
        return (Integer)readProperty("deliveryMode");
    }

    public void setEndDate(Date endDate) {
        writeProperty("endDate", endDate);
    }
    public Date getEndDate() {
        return (Date)readProperty("endDate");
    }

    public void setFundingSource(ClassFundingSource fundingSource) {
        writeProperty("fundingSource", fundingSource);
    }
    public ClassFundingSource getFundingSource() {
        return (ClassFundingSource)readProperty("fundingSource");
    }

    public void setMarkedByTutorDate(Date markedByTutorDate) {
        writeProperty("markedByTutorDate", markedByTutorDate);
    }
    public Date getMarkedByTutorDate() {
        return (Date)readProperty("markedByTutorDate");
    }

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setReportableHours(Double reportableHours) {
        writeProperty("reportableHours", reportableHours);
    }
    public Double getReportableHours() {
        return (Double)readProperty("reportableHours");
    }

    public void setStartDate(Date startDate) {
        writeProperty("startDate", startDate);
    }
    public Date getStartDate() {
        return (Date)readProperty("startDate");
    }

    public void setStatus(OutcomeStatus status) {
        writeProperty("status", status);
    }
    public OutcomeStatus getStatus() {
        return (OutcomeStatus)readProperty("status");
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


    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setEnrolment(Enrolment enrolment) {
        setToOneTarget("enrolment", enrolment, true);
    }

    public Enrolment getEnrolment() {
        return (Enrolment)readProperty("enrolment");
    }


    public void setMarkedByTutor(Tutor markedByTutor) {
        setToOneTarget("markedByTutor", markedByTutor, true);
    }

    public Tutor getMarkedByTutor() {
        return (Tutor)readProperty("markedByTutor");
    }


    public void setModule(Module module) {
        setToOneTarget("module", module, true);
    }

    public Module getModule() {
        return (Module)readProperty("module");
    }


    public void setPriorLearning(PriorLearning priorLearning) {
        setToOneTarget("priorLearning", priorLearning, true);
    }

    public PriorLearning getPriorLearning() {
        return (PriorLearning)readProperty("priorLearning");
    }


}

package ish.oncourse.model.auto;

import java.util.Date;
import java.util.List;

import org.apache.cayenne.CayenneDataObject;
import org.apache.cayenne.exp.Property;

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

    @Deprecated
    public static final String ANGEL_ID_PROPERTY = "angelId";
    @Deprecated
    public static final String CREATED_PROPERTY = "created";
    @Deprecated
    public static final String DELIVERY_MODE_PROPERTY = "deliveryMode";
    @Deprecated
    public static final String END_DATE_PROPERTY = "endDate";
    @Deprecated
    public static final String FUNDING_SOURCE_PROPERTY = "fundingSource";
    @Deprecated
    public static final String MARKED_BY_TUTOR_DATE_PROPERTY = "markedByTutorDate";
    @Deprecated
    public static final String MODIFIED_PROPERTY = "modified";
    @Deprecated
    public static final String REPORTABLE_HOURS_PROPERTY = "reportableHours";
    @Deprecated
    public static final String START_DATE_PROPERTY = "startDate";
    @Deprecated
    public static final String STATUS_PROPERTY = "status";
    @Deprecated
    public static final String CERTIFICATE_OUTCOMES_PROPERTY = "certificateOutcomes";
    @Deprecated
    public static final String COLLEGE_PROPERTY = "college";
    @Deprecated
    public static final String ENROLMENT_PROPERTY = "enrolment";
    @Deprecated
    public static final String MARKED_BY_TUTOR_PROPERTY = "markedByTutor";
    @Deprecated
    public static final String MODULE_PROPERTY = "module";
    @Deprecated
    public static final String PRIOR_LEARNING_PROPERTY = "priorLearning";

    public static final String ID_PK_COLUMN = "id";

    public static final Property<Long> ANGEL_ID = new Property<Long>("angelId");
    public static final Property<Date> CREATED = new Property<Date>("created");
    public static final Property<Integer> DELIVERY_MODE = new Property<Integer>("deliveryMode");
    public static final Property<Date> END_DATE = new Property<Date>("endDate");
    public static final Property<Integer> FUNDING_SOURCE = new Property<Integer>("fundingSource");
    public static final Property<Date> MARKED_BY_TUTOR_DATE = new Property<Date>("markedByTutorDate");
    public static final Property<Date> MODIFIED = new Property<Date>("modified");
    public static final Property<Double> REPORTABLE_HOURS = new Property<Double>("reportableHours");
    public static final Property<Date> START_DATE = new Property<Date>("startDate");
    public static final Property<OutcomeStatus> STATUS = new Property<OutcomeStatus>("status");
    public static final Property<List<CertificateOutcome>> CERTIFICATE_OUTCOMES = new Property<List<CertificateOutcome>>("certificateOutcomes");
    public static final Property<College> COLLEGE = new Property<College>("college");
    public static final Property<Enrolment> ENROLMENT = new Property<Enrolment>("enrolment");
    public static final Property<Tutor> MARKED_BY_TUTOR = new Property<Tutor>("markedByTutor");
    public static final Property<Module> MODULE = new Property<Module>("module");
    public static final Property<PriorLearning> PRIOR_LEARNING = new Property<PriorLearning>("priorLearning");

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

    public void setFundingSource(Integer fundingSource) {
        writeProperty("fundingSource", fundingSource);
    }
    public Integer getFundingSource() {
        return (Integer)readProperty("fundingSource");
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

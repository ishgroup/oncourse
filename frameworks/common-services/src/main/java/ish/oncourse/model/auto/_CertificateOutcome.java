package ish.oncourse.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;

import ish.oncourse.model.Certificate;
import ish.oncourse.model.College;
import ish.oncourse.model.Outcome;

/**
 * Class _CertificateOutcome was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _CertificateOutcome extends CayenneDataObject {

    public static final String ANGEL_ID_PROPERTY = "angelId";
    public static final String CREATED_PROPERTY = "created";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String CERTIFICATE_PROPERTY = "certificate";
    public static final String COLLEGE_PROPERTY = "college";
    public static final String OUTCOME_PROPERTY = "outcome";

    public static final String CERTIFICATE_ID_PK_COLUMN = "certificateId";
    public static final String OUTCOME_ID_PK_COLUMN = "outcomeId";

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

    public void setModified(Date modified) {
        writeProperty("modified", modified);
    }
    public Date getModified() {
        return (Date)readProperty("modified");
    }

    public void setCertificate(Certificate certificate) {
        setToOneTarget("certificate", certificate, true);
    }

    public Certificate getCertificate() {
        return (Certificate)readProperty("certificate");
    }


    public void setCollege(College college) {
        setToOneTarget("college", college, true);
    }

    public College getCollege() {
        return (College)readProperty("college");
    }


    public void setOutcome(Outcome outcome) {
        setToOneTarget("outcome", outcome, true);
    }

    public Outcome getOutcome() {
        return (Outcome)readProperty("outcome");
    }


}

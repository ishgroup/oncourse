package ish.oncourse.server.lifecycle;

import ish.common.types.PayslipStatus;
import ish.common.types.SystemEventType;
import ish.oncourse.common.SystemEvent;
import ish.oncourse.server.cayenne.Payslip;
import ish.oncourse.server.integration.EventService;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.commitlog.model.AttributeChange;

public class PayslipPaidListener extends AbstractPropertyChangeListener<Payslip, PayslipStatus> {

    private final EventService service;

    public PayslipPaidListener(EventService service) {
        this.service = service;
        this.entityClass = Payslip.class;
        this.property = Payslip.STATUS;
    }

    protected boolean changed(AttributeChange attributeChange) {
        return attributeChange.getNewValue() != null && attributeChange.getNewValue().equals(PayslipStatus.FINALISED);
    }

    @Override
    protected void run(ObjectId id) {
        service.postEvent(SystemEvent.valueOf(SystemEventType.PAYSLIP_APPROVED, id));
    }
}

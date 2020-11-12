/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.lifecycle;

import ish.common.types.PayslipStatus;
import ish.common.types.SystemEventType;
import ish.oncourse.common.SystemEvent;
import ish.oncourse.server.cayenne.Payslip;
import ish.oncourse.server.integration.EventService;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.commitlog.model.AttributeChange;

public class PayslipApprovedListener extends AbstractPropertyChangeListener<Payslip, PayslipStatus> {
    
    private final EventService service;

    public PayslipApprovedListener(EventService service) {
        this.service = service;
        this.entityClass = Payslip.class;
        this.property = Payslip.STATUS;
    }

    protected boolean changed(AttributeChange attributeChange) {
        return attributeChange.getNewValue() != null && attributeChange.getNewValue().equals(PayslipStatus.APPROVED);
    }

    @Override
    protected void run(ObjectId id) {
        service.postEvent(SystemEvent.valueOf(SystemEventType.PAYSLIP_APPROVED, id));
    }

}

/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.lifecycle;

import ish.common.types.ConfirmationStatus;
import ish.common.types.PaymentSource;
import ish.math.Money;
import ish.oncourse.server.cayenne.AbstractInvoice;
import ish.oncourse.server.cayenne.Account;
import ish.util.AccountUtil;
import org.apache.cayenne.annotation.PostAdd;
import org.apache.cayenne.annotation.PrePersist;

import java.time.LocalDate;

public class AbstractInvoiceLifecycleListener {

    @PrePersist(value = AbstractInvoice.class)
    public void prePersist(AbstractInvoice entity) {
        entity.updateAmountOwing();
        entity.updateDateDue();
        entity.updateOverdue();
    }

    @PostAdd(value = AbstractInvoice.class)
    public void postAdd(AbstractInvoice entity) {
        var aContext = entity.getContext();

        if (entity.getDebtorsAccount() == null) {
            entity.setDebtorsAccount(AccountUtil.getDefaultDebtorsAccount(aContext, Account.class));
        }
        if (entity.getInvoiceDate() == null) {
            entity.setInvoiceDate(LocalDate.now());
        }
        if (entity.getSource() == null) {
            entity.setSource(PaymentSource.SOURCE_ONCOURSE);
        }
        if (entity.getDateDue() == null) {
            entity.setDateDue(LocalDate.now());
        }
        if (entity.getConfirmationStatus() == null) {
            entity.setConfirmationStatus(ConfirmationStatus.NOT_SENT);
        }
        if (entity.getOverdue() == null) {
            entity.setOverdue(Money.ZERO);
        }
    }
}

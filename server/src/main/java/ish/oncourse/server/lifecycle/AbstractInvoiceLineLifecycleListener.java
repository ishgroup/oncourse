/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.lifecycle;

import ish.oncourse.server.cayenne.AbstractInvoiceLine;
import ish.oncourse.server.cayenne.Account;
import ish.util.AccountUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.annotation.PostAdd;
import org.apache.cayenne.annotation.PrePersist;

public class AbstractInvoiceLineLifecycleListener {

    @PrePersist(value = AbstractInvoiceLine.class)
    public void prePersist(AbstractInvoiceLine invoiceLine) {
        if (invoiceLine.getInvoiceLineDiscounts() != null && invoiceLine.getInvoiceLineDiscounts().size() > 0) {
            if (invoiceLine.getInvoiceLineDiscounts().get(0).getDiscount().getCosAccount() != null) {
                invoiceLine.setCosAccount(invoiceLine.getInvoiceLineDiscounts().get(0).getDiscount().getCosAccount());
            }
        }
    }

    @PostAdd(value = AbstractInvoiceLine.class)
    public void postAdd(AbstractInvoiceLine invLine) {
        ObjectContext aContext = invLine.getContext();

        if (invLine.getTax() == null) {
            //get Default Tax from Default Income account. We get this account as default
            //see InvoiceLineInitHelper.assignDefaults()
            var defaultIncomeAccount = AccountUtil.getDefaultStudentEnrolmentsAccount(aContext, Account.class);
            invLine.setTax(defaultIncomeAccount.getTax());
        }
    }
}

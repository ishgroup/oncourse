/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.lifecycle;

import ish.oncourse.server.accounting.AccountTransactionService;
import ish.oncourse.server.accounting.builder.InvoiceLineTransactionsBuilder;
import ish.oncourse.server.cayenne.InvoiceLine;
import ish.oncourse.server.cayenne.PaymentOut;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.graph.GraphChangeHandler;

public class InvoiceLinePostCreateHandler implements GraphChangeHandler {
    private final ObjectContext currentContext;
    private final AccountTransactionService accountTransactionService;


    public InvoiceLinePostCreateHandler(ObjectContext currentContext, AccountTransactionService accountTransactionService){
        this.currentContext = currentContext;
        this.accountTransactionService = accountTransactionService;
    }

    @Override
    public void nodeIdChanged(Object nodeId, Object newId) {

    }

    @Override
    public void nodeCreated(Object nodeId) {
        if(nodeId instanceof ObjectId){
            Object o = Cayenne.objectForPK(currentContext, (ObjectId) nodeId);
            if(o instanceof InvoiceLine){
                accountTransactionService.createTransactions(InvoiceLineTransactionsBuilder.valueOf((InvoiceLine) o));
            }
        }
    }

    @Override
    public void nodeRemoved(Object nodeId) {

    }

    @Override
    public void nodePropertyChanged(Object nodeId, String property, Object oldValue, Object newValue) {
        if(nodeId instanceof ObjectId){
            Object o = Cayenne.objectForPK(currentContext, (ObjectId) nodeId);
            if(o instanceof PaymentOut){
                accountTransactionService.createTransactions(InvoiceLineTransactionsBuilder.valueOf((InvoiceLine) o));
            }
        }
    }

    @Override
    public void arcCreated(Object nodeId, Object targetNodeId, Object arcId) {

    }

    @Override
    public void arcDeleted(Object nodeId, Object targetNodeId, Object arcId) {

    }
}

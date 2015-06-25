package ish.oncourse.webservices.jobs;

import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SQLSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;

/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
public class UpdateAmountOwingJob implements Job{
    private static final Logger logger = LogManager.getLogger();

    private static final String SQL  = "select i.id as ID, i.angelId as ANGELID, i.collegeId as COLLEGEID\n" +
            "from Invoice i \n" +
            "inner join PaymentInLine pil on pil.invoiceId = i.id\n" +
            "inner join PaymentIn p on p.id = pil.paymentInId\n" +
            "left join InvoiceLine il on i.id = il.invoiceId\n" +
            "left join Enrolment e on e.id = il.enrolmentId\n" +
            "where i.amountOwing > 0 and p.amount = i.amountOwing and pil.amount = i.amountOwing and p.status = 3 \n" +
            "and i.amountOwing = (select sum(il.priceEachExTax - il.discountEachExTax + il.taxEach) from InvoiceLine il where il.invoiceId = i.id)\n" +
            "and 0 = (select count(p1.id) from PaymentInLine pl1 inner join PaymentIn p1 on pl1.paymentInId = p1.id where pl1.invoiceId = i.id and pl1.amount < 0 and p1.type in (7,10,5))\n" +
            "and (e.id is null or e.status = 3)\n" +
            "order by i.collegeId, i.created";

    private ICayenneService cayenneService;



    public UpdateAmountOwingJob(ICayenneService cayenneService) {
        this.cayenneService = cayenneService;
    }

    @Override
    public void execute() {
        ObjectContext objectContext = this.cayenneService.newNonReplicatingContext();

        List<DataRow> rows =  SQLSelect.dataRowQuery(SQL).select(objectContext);
        for (DataRow dataRow : rows) {
            try {
                Long angelId = (Long) dataRow.get("ANGELID");
                Long collegeId = (Long) dataRow.get("COLLEGEID");
                String instruction = String.format("queue:instructWithRelationships:Invoice:%d", angelId);
                SQLSelect.dataRowQuery("INSERT INTO Instruction (collegeId, created, message) VALUES(#bind($collegeId), #bind($date), #bind($instruction))")
                        .paramsArray(collegeId, new Date(), instruction).select(objectContext);
                //objectContext.commitChanges();
                logger.warn("Update amount owing instruction was added for invoice: {}, college: {}", angelId, collegeId);
            } catch (Exception e) {
                logger.error("Unexpected exception. DataRow: {}", dataRow, e);
            }
        }
    }
}

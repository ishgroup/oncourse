/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

def run(args) {
    def context = args.context

    contactsToDelete = ObjectSelect.query(Contact.class)
            .where(Contact.IS_COMPANY.eq(false))
            .and(Contact.IS_TUTOR.eq(false))
            .and(Contact.IS_STUDENT.eq(false))
            .and(Contact.INVOICES.outer().dot(Invoice.CONTACT).isNull())
            .and(Contact.INVOICES.outer().dot(Invoice.CONTACT).isNull())
            .and(Contact.PAYMENTS_IN.outer().dot(PaymentIn.PAYER).isNull())
            .and(Contact.PAYMENTS_OUT.outer().dot(PaymentOut.PAYEE).isNull())
            .and(Contact.PAYSLIPS.outer().dot(Payslip.CONTACT).isNull())
            .and(Contact.CORPORATE_PASSES.outer().dot(CorporatePass.CONTACT).isNull())
            .and(Contact.STUDENT.outer().dot(Student.CONTACT).isNull())
            .select(context)

    context.deleteObjects(contactsToDelete)
    context.commitChanges()
}

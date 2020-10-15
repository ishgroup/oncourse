/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

package ish.oncourse.server.replication.handler

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.common.types.PaymentStatus
import ish.oncourse.cayenne.QueuedRecordAction
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.ISHDataContext
import ish.oncourse.server.cayenne.Article
import ish.oncourse.server.cayenne.ArticleProduct
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.Membership
import ish.oncourse.server.cayenne.MembershipProduct
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.server.cayenne.QueuedRecord
import ish.oncourse.server.cayenne.QueuedTransaction
import ish.oncourse.server.cayenne.Voucher
import ish.oncourse.server.cayenne.VoucherPaymentIn
import ish.oncourse.server.modules.ISoapPortLocator
import ish.oncourse.webservices.util.GenericInstructionStub
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.EJBQLQuery
import org.apache.cayenne.query.SelectQuery
import org.apache.commons.lang3.StringUtils

import java.util.stream.Collectors

@CompileStatic
class InstructionHandler implements ReplicationHandler {

    protected static final String RESET_RETRIES_ARGUMENT = "resetRetries"
    protected static final String INSTRUCT_WITH_RELATIONSHIPS_ARGUMENT = "instructWithRelationships"

    protected static final String ENROLMENT_ENTITY_NAME = "Enrolment"
    protected static final String INVOICE_ENTITY_NAME = "Invoice"
    protected static final String PAYMENT_IN_ENTITY_NAME = "PaymentIn"

    /**
     * Transaction size limit for regular objects.
     */
    private static final int TRANSACTION_SIZE_LIMIT = 50

    /**
     * Transaction size limit for binary objects, since they're quite large. Preventing OutOfMemory.
     */
    private static final int BINARY_TRANSACTION_LIMIT = 4
    static final String SUCCEED_RESULT = "Succeed"
    protected static final String ATTENDANCE_FOR_CLASS_ARGUMENT = "AttendanceForClass"
    protected static final String ATTENDANCE_FOR_ENROLMENT_ARGUMENT = "AttendanceForEnrolment"
    protected static final String QUEUE_ARGUMENT = "queue"
    protected static final String PAYMENT_IN_LINE_ENTITY_NAME = "PaymentInLine"
    protected static final String ATTENDANCE_ENTITY_NAME = "Attendance"

    /**
     * Soap port locator.
     */
    private final ISoapPortLocator soapPortLocator

    /**
     * Cayenne service.
     */
    private final ICayenneService cayenneService

    @Inject
    InstructionHandler(ISoapPortLocator soapPortLocator, ICayenneService cayenneService) {
        super()
        this.soapPortLocator = soapPortLocator
        this.cayenneService = cayenneService
    }

    @Override
    void replicate() {

        def port = soapPortLocator.replicationPort()

        List<? extends GenericInstructionStub> instructions = port.getInstructions()

        if (instructions != null) {
            for (GenericInstructionStub inst : instructions) {
                def response = execute(inst)
                port.confirmExecution(inst.getId(), response)
            }
        }
    }

    /**
     * Check if identifier exists within cayenne entities.
     *
     * @return
     */
    private boolean entityIdentifierExists(String entityIdentifier) {
        try {
            Class.forName("ish.oncourse.server.cayenne." + entityIdentifier)
            return true
        } catch (ClassNotFoundException e) {
            return false
        }
    }

    private String execute(GenericInstructionStub instruction) {
        def message = instruction.getMessage()
        def splittedMessage = message.split(":")

        if (splittedMessage.length < 2) {
            return "Incorrect format of the Instruction message"
        }

        if (QUEUE_ARGUMENT.equals(splittedMessage[0])) {
            def entityIdentifier = splittedMessage[1]
            if (RESET_RETRIES_ARGUMENT.equalsIgnoreCase(entityIdentifier)) {
                return resetRetries(splittedMessage)
            } else if ("*".equals(entityIdentifier)) {
                return "The ability to replicate ALL the entities is not supported yet"
            } else if (ATTENDANCE_FOR_CLASS_ARGUMENT.equalsIgnoreCase(entityIdentifier) && splittedMessage.length == 3) {
                def entityId = splittedMessage[2]
                if (StringUtils.trimToNull(entityId) != null && !"*".equals(entityId)) {
                    return requestAttendanciesForClass(entityId)
                }
                return "Queue Attendances for Class supported only with defined class angelid."
            } else if (ATTENDANCE_FOR_ENROLMENT_ARGUMENT.equalsIgnoreCase(entityIdentifier) && splittedMessage.length == 3) {
                def entityId = splittedMessage[2]
                if (StringUtils.trimToNull(entityId) != null && !"*".equals(entityId)) {
                    return requestAttendanciesForEnrolment(entityId)
                }
                return "Queue Attendances for Enrolment supported only with defined enrolment angelid."
            } else if (INSTRUCT_WITH_RELATIONSHIPS_ARGUMENT.equalsIgnoreCase(entityIdentifier) && splittedMessage.length == 4) {
                def entityId = splittedMessage[3]
                def entityObject = splittedMessage[2]
                if ((PAYMENT_IN_ENTITY_NAME.equalsIgnoreCase(entityObject) || INVOICE_ENTITY_NAME.equalsIgnoreCase(entityObject) || ENROLMENT_ENTITY_NAME
                        .equalsIgnoreCase(entityObject)) && StringUtils.trimToNull(entityId) != null && !"*".equals(entityId)) {
                    return requestWithRelationships(entityObject, entityId)
                }
                return "Instruct with relationships supported only for PaymentIn,Invoice and Enrolment entities with defined angelids."
            } else if (!entityIdentifierExists(entityIdentifier)) {
                return "There are no entity with name \"" + entityIdentifier + "\""
            } else if (PAYMENT_IN_ENTITY_NAME.equalsIgnoreCase(entityIdentifier) || PAYMENT_IN_LINE_ENTITY_NAME.equalsIgnoreCase(entityIdentifier)) {
                return putPaymentInLineIntoTheQueue(entityIdentifier, (splittedMessage.length == 3) ? splittedMessage[2] : null)
            } else {
                return putEntitiesIntoTheQueue(entityIdentifier, (splittedMessage.length == 3 && !"*".equals(splittedMessage[2])) ? splittedMessage[2] : null)
            }
        }
        return "The instruction \"" + splittedMessage[0] + "\"is unsupported"
    }

    private boolean isTransactionContainsEntity(final QueuedTransaction transaction, final String entity, final Long id, final Long willowId) {
        for (final def queuedRecord : transaction.getQueuedRecords()) {
            if (entity.equals(queuedRecord.getTableName()) && id.equals(queuedRecord.getForeignRecordId()) &&
                    (willowId != null && willowId.equals(queuedRecord.getWillowId()))) {
                return true
            }
        }
        return false
    }

    private List<PaymentInLine> addPaymentInRelationships(final QueuedTransaction transaction, final ISHDataContext context, final PaymentIn paymentIn) {
        QueuedRecord queuedRecord
        if (!isTransactionContainsEntity(transaction, PAYMENT_IN_ENTITY_NAME, paymentIn.getId(), paymentIn.getWillowId())) {
            queuedRecord = createNewQueuedRecord(context, PAYMENT_IN_ENTITY_NAME, paymentIn.getId(), paymentIn.getWillowId())
            queuedRecord.setQueuedTransaction(transaction)
        }
        final def payer = paymentIn.getPayer()
        if (!isTransactionContainsEntity(transaction, "Contact", payer.getId(), payer.getWillowId())) {
            queuedRecord = createNewQueuedRecord(context, "Contact", payer.getId(), payer.getWillowId())
            queuedRecord.setQueuedTransaction(transaction)
        }
        for (final def paymentInLine : paymentIn.getPaymentInLines()) {
            if (!isTransactionContainsEntity(transaction, PAYMENT_IN_LINE_ENTITY_NAME, paymentInLine.getId(), paymentInLine.getWillowId())) {
                queuedRecord = createNewQueuedRecord(context, PAYMENT_IN_LINE_ENTITY_NAME, paymentInLine.getId(), paymentInLine.getWillowId())
                queuedRecord.setQueuedTransaction(transaction)
            }
        }
        //add voucher payment in relations
        for (def voucherPaymentIn : paymentIn.getVoucherPayments()) {
            if (!isTransactionContainsEntity(transaction, "VoucherPaymentIn", voucherPaymentIn.getId(), voucherPaymentIn.getWillowId())) {
                queuedRecord = createNewQueuedRecord(context, "VoucherPaymentIn", voucherPaymentIn.getId(), voucherPaymentIn.getWillowId())
                queuedRecord.setQueuedTransaction(transaction)
            }
            addVoucherRelations(transaction, context, voucherPaymentIn.getVoucher())
        }
        return paymentIn.getPaymentInLines()
    }

    private void addVoucherRelations(final QueuedTransaction transaction, final ISHDataContext context, final Voucher voucher) {
        QueuedRecord queuedRecord
        if (!isTransactionContainsEntity(transaction, "Voucher", voucher.getId(), voucher.getWillowId())) {
            queuedRecord = createNewQueuedRecord(context, "Voucher", voucher.getId(), voucher.getWillowId())
            queuedRecord.setQueuedTransaction(transaction)
        }
        def voucherProduct = voucher.getVoucherProduct()
        if (!isTransactionContainsEntity(transaction, "VoucherProduct", voucherProduct.getId(), voucherProduct.getWillowId())) {
            queuedRecord = createNewQueuedRecord(context, "VoucherProduct", voucherProduct.getId(), voucherProduct.getWillowId())
            queuedRecord.setQueuedTransaction(transaction)
        }
        for (def voucherProductCourse : voucherProduct.getVoucherProductCourses()) {
            if (!isTransactionContainsEntity(transaction, "VoucherProductCourse", voucherProductCourse.getId(), voucherProductCourse.getWillowId())) {
                queuedRecord = createNewQueuedRecord(context, "VoucherProductCourse", voucherProductCourse.getId(), voucherProductCourse.getWillowId())
                queuedRecord.setQueuedTransaction(transaction)
            }
            def course = voucherProductCourse.getCourse()
            if (!isTransactionContainsEntity(transaction, "Course", course.getId(), course.getWillowId())) {
                queuedRecord = createNewQueuedRecord(context, "Course", course.getId(), course.getWillowId())
                queuedRecord.setQueuedTransaction(transaction)
            }
            for (def courseClass : course.getCourseClasses()) {
                if (courseClass != null && !isTransactionContainsEntity(transaction, "CourseClass", courseClass.getId(), courseClass.getWillowId())) {
                    queuedRecord = createNewQueuedRecord(context, "CourseClass", courseClass.getId(), courseClass.getWillowId())
                    queuedRecord.setQueuedTransaction(transaction)
                    final def room = courseClass.getRoom()
                    if (room != null && !isTransactionContainsEntity(transaction, "Room", room.getId(), room.getWillowId())) {
                        queuedRecord = createNewQueuedRecord(context, "Room", room.getId(), room.getWillowId())
                        queuedRecord.setQueuedTransaction(transaction)
                    }
                    for (final def session : courseClass.getSessions()) {
                        if (!isTransactionContainsEntity(transaction, "Session", session.getId(), session.getWillowId())) {
                            queuedRecord = createNewQueuedRecord(context, "Session", session.getId(), session.getWillowId())
                            queuedRecord.setQueuedTransaction(transaction)
                        }
                    }
                }
            }
        }
    }

    private void addVoucherPaymentInRelations(final QueuedTransaction transaction, final ISHDataContext context, final VoucherPaymentIn voucherPaymentIn) {
        //add related to voucherpayment payment structure
        for (final def paymentInLine : addPaymentInRelationships(transaction, context, voucherPaymentIn.getPaymentIn())) {
            final def relatedInvoice = paymentInLine.getInvoice()
            for (final def relatedInvoiceLine : addInvoiceRelationships(transaction, context, relatedInvoice)) {
                if (relatedInvoiceLine.getEnrolment() != null) {
                    addEnrolmentRelationships(transaction, context, relatedInvoiceLine.getEnrolment())
                }
            }
        }
    }

    private List<InvoiceLine> addInvoiceRelationships(final QueuedTransaction transaction, final ISHDataContext context, final Invoice invoice) {
        QueuedRecord queuedRecord
        if (!isTransactionContainsEntity(transaction, INVOICE_ENTITY_NAME, invoice.getId(), invoice.getWillowId())) {
            queuedRecord = createNewQueuedRecord(context, INVOICE_ENTITY_NAME, invoice.getId(), invoice.getWillowId())
            queuedRecord.setQueuedTransaction(transaction)
        }
        final def invoiceContact = invoice.getContact()
        if (!isTransactionContainsEntity(transaction, "Contact", invoiceContact.getId(), invoiceContact.getWillowId())) {
            queuedRecord = createNewQueuedRecord(context, "Contact", invoiceContact.getId(), invoiceContact.getWillowId())
            queuedRecord.setQueuedTransaction(transaction)
        }
        for (final def invoiceLine : invoice.getInvoiceLines()) {
            if (!isTransactionContainsEntity(transaction, "InvoiceLine", invoiceLine.getId(), invoiceLine.getWillowId())) {
                queuedRecord = createNewQueuedRecord(context, "InvoiceLine", invoiceLine.getId(), invoiceLine.getWillowId())
                queuedRecord.setQueuedTransaction(transaction)
            }
            for (final def discount : invoiceLine.getInvoiceLineDiscounts()) {
                if (!isTransactionContainsEntity(transaction, "InvoiceLineDiscount", discount.getId(), discount.getWillowId())) {
                    queuedRecord = createNewQueuedRecord(context, "InvoiceLineDiscount", discount.getId(), discount.getWillowId())
                    queuedRecord.setQueuedTransaction(transaction)
                }
            }
            for (def voucherPaymentIn : invoiceLine.getVoucherPaymentIn()) {
                addVoucherPaymentInRelations(transaction, context, voucherPaymentIn)
            }
            //add purchased product items
            for (def productItem : invoiceLine.getProductItems()) {
                if (productItem instanceof Membership) {
                    def membership = (Membership) productItem
                    if (!isTransactionContainsEntity(transaction, "Membership", membership.getId(), membership.getWillowId())) {
                        queuedRecord = createNewQueuedRecord(context, "Membership", membership.getId(), membership.getWillowId())
                        queuedRecord.setQueuedTransaction(transaction)
                    }
                    def membershipProduct = (MembershipProduct) membership.getProduct()
                    if (!isTransactionContainsEntity(transaction, "MembershipProduct", membershipProduct.getId(), membershipProduct.getWillowId())) {
                        queuedRecord = createNewQueuedRecord(context, "MembershipProduct", membershipProduct.getId(), membershipProduct.getWillowId())
                        queuedRecord.setQueuedTransaction(transaction)
                    }
                    for (def discountMembership : membershipProduct.getDiscountMemberships()) {
                        if (!isTransactionContainsEntity(transaction, "DiscountMembership", discountMembership.getId(), discountMembership.getWillowId())) {
                            queuedRecord = createNewQueuedRecord(context, "DiscountMembership", discountMembership.getId(), discountMembership.getWillowId())
                            queuedRecord.setQueuedTransaction(transaction)
                        }
                        def discount = discountMembership.getDiscount()
                        if (!isTransactionContainsEntity(transaction, "Discount", discount.getId(), discount.getWillowId())) {
                            queuedRecord = createNewQueuedRecord(context, "Discount", discount.getId(), discount.getWillowId())
                            queuedRecord.setQueuedTransaction(transaction)
                        }
                    }
                } else if (productItem instanceof Voucher) {
                    def voucher = (Voucher) productItem
                    addVoucherRelations(transaction, context, voucher)
                } else if (productItem instanceof Article) {
                    def article = (Article) productItem
                    if (!isTransactionContainsEntity(transaction, "Article", article.getId(), article.getWillowId())) {
                        queuedRecord = createNewQueuedRecord(context, "Article", article.getId(), article.getWillowId())
                        queuedRecord.setQueuedTransaction(transaction)
                    }
                    def articleProduct = (ArticleProduct) article.getProduct()
                    if (!isTransactionContainsEntity(transaction, "ArticleProduct", articleProduct.getId(), articleProduct.getWillowId())) {
                        queuedRecord = createNewQueuedRecord(context, "ArticleProduct", articleProduct.getId(), articleProduct.getWillowId())
                        queuedRecord.setQueuedTransaction(transaction)
                    }
                }
            }
        }
        for (final def paymentOutLine : invoice.getPaymentOutLines()) {
            final def paymentOut = paymentOutLine.getPaymentOut()
            if (!isTransactionContainsEntity(transaction, "PaymentOut", paymentOut.getId(), paymentOut.getWillowId())) {
                queuedRecord = createNewQueuedRecord(context, "PaymentOut", paymentOut.getId(), paymentOut.getWillowId())
                queuedRecord.setQueuedTransaction(transaction)
            }
        }
        return invoice.getInvoiceLines()
    }

    private void addEnrolmentRelationships(final QueuedTransaction transaction, final ISHDataContext context, final Enrolment enrolment) {
        QueuedRecord queuedRecord
        if (!isTransactionContainsEntity(transaction, ENROLMENT_ENTITY_NAME, enrolment.getId(), enrolment.getWillowId())) {
            queuedRecord = createNewQueuedRecord(context, ENROLMENT_ENTITY_NAME, enrolment.getId(), enrolment.getWillowId())
            queuedRecord.setQueuedTransaction(transaction)
        }
        final def courseClass = enrolment.getCourseClass()
        if (courseClass != null && !isTransactionContainsEntity(transaction, "CourseClass", courseClass.getId(), courseClass.getWillowId())) {
            queuedRecord = createNewQueuedRecord(context, "CourseClass", courseClass.getId(), courseClass.getWillowId())
            queuedRecord.setQueuedTransaction(transaction)
            final def course = courseClass.getCourse()
            if (course != null && !isTransactionContainsEntity(transaction, "Course", course.getId(), course.getWillowId())) {
                queuedRecord = createNewQueuedRecord(context, "Course", course.getId(), course.getWillowId())
                queuedRecord.setQueuedTransaction(transaction)
            }
            final def room = courseClass.getRoom()
            if (room != null && !isTransactionContainsEntity(transaction, "Room", room.getId(), room.getWillowId())) {
                queuedRecord = createNewQueuedRecord(context, "Room", room.getId(), room.getWillowId())
                queuedRecord.setQueuedTransaction(transaction)
            }
            for (final def session : courseClass.getSessions()) {
                if (!isTransactionContainsEntity(transaction, "Session", session.getId(), session.getWillowId())) {
                    queuedRecord = createNewQueuedRecord(context, "Session", session.getId(), session.getWillowId())
                    queuedRecord.setQueuedTransaction(transaction)
                }
            }
        }
        for (final def outcome : enrolment.getOutcomes()) {
            if (!isTransactionContainsEntity(transaction, "Outcome", outcome.getId(), outcome.getWillowId())) {
                queuedRecord = createNewQueuedRecord(context, "Outcome", outcome.getId(), outcome.getWillowId())
                queuedRecord.setQueuedTransaction(transaction)
            }
        }
        final def student = enrolment.getStudent()
        if (!isTransactionContainsEntity(transaction, "Student", student.getId(), student.getWillowId())) {
            queuedRecord = createNewQueuedRecord(context, "Student", student.getId(), student.getWillowId())
            queuedRecord.setQueuedTransaction(transaction)
        }
        final def studentContact = student.getContact()
        if (!isTransactionContainsEntity(transaction, "Contact", studentContact.getId(), studentContact.getWillowId())) {
            queuedRecord = createNewQueuedRecord(context, "Contact", studentContact.getId(), studentContact.getWillowId())
            queuedRecord.setQueuedTransaction(transaction)
        }
    }

    private String requestAttendanciesForClass(String angelIdParam) {
        final def context = (ISHDataContext) cayenneService.getNewNonReplicatingContext()
        def ejbQL = String.format("select r.id, r.willowId from %s r", "CourseClass")
        if (angelIdParam != null) {
            ejbQL += String.format(" where r.id=%s", angelIdParam)
        }
        final def ejQuery = new EJBQLQuery(ejbQL)
        final List<Object[]> result = context.performQuery(ejQuery) as List<Object[]>
        if (result.isEmpty()) {
            return String.format("There is no such record with entityIdentifier = %s with angelid = %s", "CourseClass", angelIdParam)
        }
        Long angelId = null
        for (Object[] v : result) {
            if (angelId == null) {
                angelId = (Long) v[0]
            }
        }
        final def expr = ExpressionFactory.matchExp(CourseClass.ID_PK_COLUMN, angelId)
        final def query = SelectQuery.query(CourseClass.class, expr)
        query.setFetchLimit(1)
        final def courseClass = context.select(query).get(0)
        final def transaction = createNewTransaction(context)

        for (final def session : courseClass.getSessions()) {
            for (final def attendance : session.getAttendance()) {
                if (attendance != null && !isTransactionContainsEntity(transaction, ATTENDANCE_ENTITY_NAME, attendance.getId(), attendance.getWillowId())) {
                    def queuedRecord = createNewQueuedRecord(context, ATTENDANCE_ENTITY_NAME, attendance.getId(), attendance.getWillowId())
                    queuedRecord.setQueuedTransaction(transaction)
                }
            }
        }
        context.commitChanges()
        return SUCCEED_RESULT
    }

    private String requestAttendanciesForEnrolment(String angelIdParam) {
        final def context = (ISHDataContext) cayenneService.getNewNonReplicatingContext()
        def ejbQL = String.format("select r.id, r.willowId from %s r", ENROLMENT_ENTITY_NAME)
        if (angelIdParam != null) {
            ejbQL += String.format(" where r.id=%s", angelIdParam)
        }
        final def ejQuery = new EJBQLQuery(ejbQL)
        final List<Object[]> result = context.performQuery(ejQuery) as List<Object[]>
        if (result.isEmpty()) {
            return String.format("There is no such record with entityIdentifier = %s with angelid = %s", ENROLMENT_ENTITY_NAME, angelIdParam)
        }
        Long angelId = null
        for (def v : result) {
            if (angelId == null) {
                angelId = (Long) v[0]
            }
        }
        final def expr = ExpressionFactory.matchExp(Enrolment.ID_PK_COLUMN, angelId)
        final def enrolmentQuery = SelectQuery.query(Enrolment.class, expr)
        enrolmentQuery.setFetchLimit(1)
        final def enrolment = context.select(enrolmentQuery).get(0)
        final def transaction = createNewTransaction(context)
        def student = enrolment.getStudent()
        for (final def session : enrolment.getCourseClass().getSessions()) {
            def attendance = enrolment.getAttendanceForSessionAndStudent(session, student)
            if (attendance != null && !isTransactionContainsEntity(transaction, ATTENDANCE_ENTITY_NAME, attendance.getId(), attendance.getWillowId())) {
                def queuedRecord = createNewQueuedRecord(context, ATTENDANCE_ENTITY_NAME, attendance.getId(), attendance.getWillowId())
                queuedRecord.setQueuedTransaction(transaction)
            }
        }
        context.commitChanges()
        return SUCCEED_RESULT
    }

    private String requestWithRelationships(String entityIdentifier, String angelIdParam) {
        final def context = (ISHDataContext) cayenneService.getNewNonReplicatingContext()
        def ejbQL = String.format("select r.id, r.willowId from %s r", entityIdentifier)
        if (angelIdParam != null) {
            ejbQL += String.format(" where r.id=%s", angelIdParam)
        }
        final def ejQuery = new EJBQLQuery(ejbQL)
        final List<Object[]> result = context.performQuery(ejQuery) as List<Object[]>
        if (result.isEmpty()) {
            return String.format("There is no such record with entityIdentifier = %s with angelid = %s", entityIdentifier, angelIdParam)
        }
        Long angelId = null
        for (def v : result) {
            if (angelId == null) {
                angelId = (Long) v[0]
            }
        }
        if (PAYMENT_IN_ENTITY_NAME.equalsIgnoreCase(entityIdentifier)) {
            final def expr = ExpressionFactory.matchExp(PaymentIn.ID_PK_COLUMN, angelId)
            final def paymentInQuery = SelectQuery.query(PaymentIn.class, expr)
            paymentInQuery.setFetchLimit(1)
            final def paymentIn = context.select(paymentInQuery).get(0)
            final def transaction = createNewTransaction(context)
            for (final def paymentInLine : addPaymentInRelationships(transaction, context, paymentIn)) {
                final def invoice = paymentInLine.getInvoice()
                for (final def invoiceLine : addInvoiceRelationships(transaction, context, invoice)) {
                    if (invoiceLine.getEnrolment() != null) {
                        addEnrolmentRelationships(transaction, context, invoiceLine.getEnrolment())
                    }
                }
            }
            context.commitChanges()
            return SUCCEED_RESULT
        } else if (INVOICE_ENTITY_NAME.equalsIgnoreCase(entityIdentifier)) {
            final def expr = ExpressionFactory.matchExp(Invoice.ID_PK_COLUMN, angelId)
            final def invoiceQuery = SelectQuery.query(Invoice.class, expr)
            invoiceQuery.setFetchLimit(1)
            final def invoice = context.select(invoiceQuery).get(0)
            final def transaction = createNewTransaction(context)
            for (final def paymentInLine : invoice.getPaymentInLines()) {
                addPaymentInRelationships(transaction, context, paymentInLine.getPaymentIn())
            }
            for (final def invoiceLine : addInvoiceRelationships(transaction, context, invoice)) {
                if (invoiceLine.getEnrolment() != null) {
                    addEnrolmentRelationships(transaction, context, invoiceLine.getEnrolment())
                }
            }
            context.commitChanges()
            return SUCCEED_RESULT
        } else if (ENROLMENT_ENTITY_NAME.equalsIgnoreCase(entityIdentifier)) {
            final def expr = ExpressionFactory.matchExp(Enrolment.ID_PK_COLUMN, angelId)
            final def enrolmentQuery = SelectQuery.query(Enrolment.class, expr)
            enrolmentQuery.setFetchLimit(1)
            final def enrolment = context.select(enrolmentQuery).get(0)
            final def transaction = createNewTransaction(context)
            addEnrolmentRelationships(transaction, context, enrolment)
            final def invoices = enrolment.getInvoiceLines().collect { il -> il.invoice }.collect()
            invoices.each { invoice ->
                addInvoiceRelationships(transaction, context, invoice)
                for (final def paymentInLine : invoice.getPaymentInLines()) {
                    addPaymentInRelationships(transaction, context, paymentInLine.getPaymentIn())
                }
            }

            context.commitChanges()
            return SUCCEED_RESULT
        }
        return "The instruction of " + entityIdentifier + " with relationships not supported."
    }

    /**
     * Reset retries in the queue.
     *
     * @return
     */
    private String resetRetries(String[] splittedMessage) {

        if (splittedMessage.length >= 3 && !"*".equalsIgnoreCase(splittedMessage[2])) {

            def name = splittedMessage[2]

            if (!entityIdentifierExists(name)) {
                return "There are no entity with name \"" + name + "\""
            }

            def context = (ISHDataContext) cayenneService.getNewNonReplicatingContext()
            def q = SelectQuery.query(QueuedRecord.class, QueuedRecord.TABLE_NAME.eq(name))

            Set<Long> processed = new HashSet<>()

            for (def r : context.select(q)) {
                def transaction = r.getQueuedTransaction()
                if (!processed.contains(transaction.getId())) {
                    for (def tr : transaction.getQueuedRecords()) {
                        tr.setNumberOfAttempts(0)
                    }
                    processed.add(transaction.getId())
                }
            }

            context.commitChanges()

            return SUCCEED_RESULT
        }

        def context = (ISHDataContext) cayenneService.getNewNonReplicatingContext()
        def q = new EJBQLQuery("update QueuedRecord q set q.numberOfAttempts = 0")
        context.performQuery(q)
        context.commitChanges()

        return SUCCEED_RESULT
    }

    /**
     * Adds PaymentIn and relates lines into the queue
     *
     * @return
     */
    private String putPaymentInLineIntoTheQueue(String entityIdentifier, String angelIdParam) {
        try {
            def context = (ISHDataContext) cayenneService.getNewNonReplicatingContext()
            def expression = PaymentIn.STATUS.isNotNull().andExp(PaymentIn.STATUS.nin(PaymentStatus.IN_TRANSACTION))

            if (PAYMENT_IN_ENTITY_NAME.equalsIgnoreCase(entityIdentifier)) {
                if (StringUtils.trimToNull(angelIdParam) != null) {
                    Long id
                    try {
                        id = Long.parseLong(angelIdParam)
                    } catch (Throwable t) {
                        return String.format("Illegal angelId %s passed for request %s entity", angelIdParam, PAYMENT_IN_ENTITY_NAME)
                    }
                    expression = expression.andExp(ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, id))
                }
            } else if (PAYMENT_IN_LINE_ENTITY_NAME.equalsIgnoreCase(entityIdentifier)) {
                if (StringUtils.trimToNull(angelIdParam) != null) {
                    Long id
                    try {
                        id = Long.parseLong(angelIdParam)
                    } catch (Throwable t) {
                        return String.format("Illegal angelId %s passed for request %s entity", angelIdParam, PAYMENT_IN_LINE_ENTITY_NAME)
                    }
                    expression = expression.andExp(ExpressionFactory.matchDbExp(PaymentIn.PAYMENT_IN_LINES.dot(PaymentInLine.ID_PK_COLUMN).getName(), id))
                }
            } else {
                return String.format("Illegal entityIdentifier %s received for queue Payment's data", entityIdentifier)
            }

            def query = SelectQuery.query(PaymentIn.class, expression)
            query.setPageSize(1)

            long count = context.select(query).size()
            if (count == 0) {
                return SUCCEED_RESULT
            } else {
                def index = 0
                def fetchSize = 30
                def hasMoreRecords = true

                while (index < count && hasMoreRecords) {
                    query.setPageSize(fetchSize)
                    query.setFetchOffset(index)
                    query.setFetchLimit(fetchSize)

                    def list = context.select(query)
                    for (def p : list) {
                        def transaction = createNewTransaction(context)
                        transaction.addToQueuedRecords(createNewQueuedRecord(context, PAYMENT_IN_ENTITY_NAME, p.getId(), p.getWillowId()))
                        for (def line : p.getPaymentInLines()) {
                            transaction.addToQueuedRecords(createNewQueuedRecord(context, PAYMENT_IN_LINE_ENTITY_NAME, line.getId(), line.getWillowId()))
                            def invoice = line.getInvoice()
                            transaction.addToQueuedRecords(createNewQueuedRecord(context, INVOICE_ENTITY_NAME, invoice.getId(), invoice.getWillowId()))
                            for (def invl : invoice.getInvoiceLines()) {
                                transaction.addToQueuedRecords(createNewQueuedRecord(context, "InvoiceLine", invl.getId(), invl.getWillowId()))
                                for (def invDiscount : invl.getInvoiceLineDiscounts()) {
                                    transaction.addToQueuedRecords(createNewQueuedRecord(context, "InvoiceLineDiscount", invDiscount.getId(),
                                            invDiscount.getWillowId()))
                                }
                            }
                        }
                        context.commitChanges()
                    }

                    index += fetchSize
                    hasMoreRecords = (list.size() >= fetchSize)
                }
                return SUCCEED_RESULT
            }
        } catch (Throwable e) {
            return "Failed:" + e.getMessage()
        }
    }

    /**
     * Put entities into the queue, shouldn't be used for paymentIn
     *
     * @param entityIdentifier
     * @param angelIdParam
     * @return
     */
    private String putEntitiesIntoTheQueue(String entityIdentifier, String angelIdParam) {

        if (PAYMENT_IN_ENTITY_NAME.equalsIgnoreCase(entityIdentifier)) {
            throw new IllegalArgumentException("Method should not be used for PaymentIn.")
        }

        try {
            def context = (ISHDataContext) cayenneService.getNewNonReplicatingContext()

            def ejbQL = String.format("select r.id, r.willowId from %s r", entityIdentifier)

            if (angelIdParam != null) {
                ejbQL += String.format(" where r.id=%s", angelIdParam)
            }

            def ejQuery = new EJBQLQuery(ejbQL)
            List<Object[]> result = context.performQuery(ejQuery) as List<Object[]>

            if (result.isEmpty()) {
                return SUCCEED_RESULT
            }

            def threshold = "AttachmentData".equalsIgnoreCase(entityIdentifier) ? BINARY_TRANSACTION_LIMIT : TRANSACTION_SIZE_LIMIT
            def t = createNewTransaction(context)

            def i = 0

            for (def v : result) {

                def angelId = (Long) v[0]
                def willowId = (Long) v[1]

                def queuedRec = createNewQueuedRecord(context, entityIdentifier, angelId, willowId)
                queuedRec.setQueuedTransaction(t)

                if (i == (threshold - 1) && (i != result.size() - 1)) {
                    context.commitChanges()
                    t = createNewTransaction(context)
                    i = 0
                } else {
                    i++
                }
            }

            context.commitChanges()

            return SUCCEED_RESULT

        } catch (Throwable e) {
            return "Failed:" + e.getMessage()
        }
    }

    /**
     * Creates a new QueuedRecord it isn't attached to any transaction
     *
     * @param context
     * @param entityIdentifier
     * @param angelId
     * @param willowId
     * @return
     */
    private static QueuedRecord createNewQueuedRecord(ObjectContext context, String entityIdentifier, Long angelId, Long willowId) {
        def queuedRec = context.newObject(QueuedRecord.class)
        queuedRec.setLastAttemptOn(new Date())
        queuedRec.setNumberOfAttempts(0)
        queuedRec.setTableName(entityIdentifier)
        queuedRec.setForeignRecordId(angelId)
        queuedRec.setWillowId(willowId)
        queuedRec.setAction(QueuedRecordAction.UPDATE)
        return queuedRec
    }

    /**
     * Creates a new queued transaction.
     *
     * @param context object context
     * @return queued transaction
     */
    private static QueuedTransaction createNewTransaction(ObjectContext context) {

        def t = context.newObject(QueuedTransaction.class)

        def today = new Date()
        t.setCreatedOn(today)
        t.setModifiedOn(today)

        def transactionKey = String.format("%s%s", Thread.currentThread().hashCode(), System.nanoTime())
        t.setTransactionKey(transactionKey)

        return t
    }
}

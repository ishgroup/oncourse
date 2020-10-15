import org.apache.cayenne.query.SQLSelect

import java.time.format.DateTimeFormatter
import org.apache.commons.lang3.StringUtils;
import ish.util.StringUtil;
import ish.common.types.AccountTransactionType
import static ish.common.types.AccountTransactionType.INVOICE_LINE
import static ish.common.types.AccountTransactionType.PAYMENT_IN_LINE
import static ish.common.types.PaymentStatus.SUCCESS
import ish.math.Money
import org.apache.cayenne.PersistentObject
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.exp.ExpressionFactory

String asOnDateString = asOnDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

String FIRST_NAME = 'FIRST_NAME'
String LAST_NAME = 'LAST_NAME'
String MIDDLE_NAME = 'MIDDLE_NAME'
String IS_COMPANY = 'IS_COMPANY'

String NUMBER = 'NUMBER'
String DATE = 'DATE'
String ACCOUT = 'ACCOUT'
String TOTAL = 'TOTAL'
String INCOME = 'INCOME'
String REMAINER = 'REMAINER'
String START = 'START'
String START_1 = 'START_1'
String END = 'END'
String END_1 = 'END_1'

String sql = "select il.id, " +
        "c.firstName as $FIRST_NAME," +
        "c.lastName as $LAST_NAME," +
        "c.middleName as $MIDDLE_NAME," +
        "c.isCompany as $IS_COMPANY," +
        "i.invoiceNumber as $NUMBER, " +
        "i.invoiceDate as $DATE,\n" +
        "a.description as $ACCOUT,  " +
        "((il.priceEachexTax - il.discountEachexTax) * il.quantity) as $TOTAL,\n" +
        "sum(tr.amount) as $REMAINER,\n" +
        "((il.priceEachexTax - il.discountEachexTax) * il.quantity) - sum(tr.amount)  as $INCOME,\n" +
        "cc.startDateTime as $START," +
        "cc.endDateTime as $END, " +
        "cc1.startDateTime as $START_1," +
        "cc1.endDateTime as $END_1\n" +
        "from InvoiceLine il \n" +
        "join Invoice i on i.id = il.invoiceId\n" +
        "join Contact c on c.id = i.contactId\n" +
        "join Account a on a.id = il.accountId\n" +
        "left join AccountTransaction tr on tr.tableName='I' \n" +
        "                                and tr.foreignRecordId = il.id \n" +
        "                                and  tr.accountId = il.prepaidFeesAccountId \n" +
        "                                and tr.transactionDate <= '$asOnDateString'\n" +
        "left join Enrolment e on e.id = il.enrolmentId\n" +
        "left join CourseClass cc on cc.id = e.courseClass_Id\n" +
        "left join CourseClass cc1 on cc1.id = il.courseClassId\n" +
        "where i.invoiceDate <= '$asOnDateString' \n" +
        "      and (il.enrolmentId is not null or il.courseClassId is not null)\n" +
        "group by il.id \n" +
        "having $REMAINER != 0\n" +
        "order by c.lastName, c.firstName"

String getName(boolean isCompany, String firstName, String middleName, String lastName){
    if (isCompany || StringUtils.equals(firstName, lastName)) {
        return StringUtils.trimToEmpty(lastName);
    }

    StringBuilder builder = new StringBuilder();


    if (StringUtils.isNotBlank(lastName)) { builder.append(lastName); }
    if (StringUtils.isNotBlank(firstName)) { builder.append(StringUtil.COMMA_CHARACTER).append(StringUtils.SPACE).append(firstName); }
    if (StringUtils.isNotBlank(middleName)) { builder.append(StringUtils.SPACE).append(middleName); }


    return builder.toString();
}

SQLSelect.dataRowQuery(sql).select(context).each { row ->

    csv << [
            "invoice contact name" : getName(row[IS_COMPANY] == 1, row[FIRST_NAME], row[MIDDLE_NAME],row[LAST_NAME]),
            "invoice number" : row[NUMBER],
            "invoice date" :  row[DATE].format('yyyy-MM-dd'),
            "Invoice line GL account" : row[ACCOUT],
            "invoice line total amount ex tax" : row[TOTAL],
            "invoice line amount already recognised as income" : row[INCOME],
            "invoice line amount not yet recognised as income" : row[REMAINER],
            "class start date" : row[START]?row[START].format('yyyy-MM-dd'):row[START_1]?.format('yyyy-MM-dd'),
            "class end date" : row[END]?row[END].format('yyyy-MM-dd'):row[END_1]?.format('yyyy-MM-dd'),
    ]
}

Long PRE_PAID_ACCOUNT_ID = 40l


Closure<Money> getTotalLiability = { List<PersistentObject> obj ->
    List<Long> ids = obj.collect { it.objectId.idSnapshot['id'] as Long }
    AccountTransactionType type = obj[0] instanceof InvoiceLine ? INVOICE_LINE : PAYMENT_IN_LINE

    List<AccountTransaction> transactions =  ObjectSelect.query(AccountTransaction)
            .where(AccountTransaction.TABLE_NAME.eq(type))
            .and(AccountTransaction.FOREIGN_RECORD_ID.in(ids))
            .and(AccountTransaction.ACCOUNT.dot(Account.ID).eq(PRE_PAID_ACCOUNT_ID))
            .and(AccountTransaction.TRANSACTION_DATE.lte(asOnDate))
            .select(context)

    if (transactions.empty) {
        return Money.ZERO
    } else {
        transactions.collect { it.amount }.inject {a,b -> a.add(b) }
    }

}

ObjectSelect.query(Voucher)
        .where(ExpressionFactory.matchDbExp(Voucher.PRODUCT.dot(VoucherProduct.LIABILITY_ACCOUNT).dot(Account.ID).name, PRE_PAID_ACCOUNT_ID))
        .and(Voucher.INVOICE_LINE.dot(InvoiceLine.INVOICE).dot(Invoice.INVOICE_DATE).lte(asOnDate))
        .select(context).each { voucher ->

    Money invoiceTotal =  getTotalLiability([voucher.invoiceLine])
    Money paymentTotal = getTotalLiability(voucher.voucherPaymentsIn*.paymentIn.findAll{it.status== SUCCESS}*.paymentInLines.flatten())

    Money balance = invoiceTotal.add(paymentTotal)
    if (balance != Money.ZERO) {
        csv << [
                "invoice contact name" : voucher.invoiceLine.invoice.contact.name,
                "invoice number" : voucher.invoiceLine.invoice.invoiceNumber,
                "invoice date" : voucher.invoiceLine.invoice.invoiceDate.format('yyyy-MM-dd'),
                "Invoice line GL account": null,
                "invoice line total amount ex tax" : voucher.valueOnPurchase.toBigDecimal(),
                "invoice line amount already recognised as income" : null,
                "invoice line amount not yet recognised as income" : balance.toBigDecimal(),
                "class start date" : voucher.createdOn.format('yyyy-MM-dd'),
                "class end date": voucher.expiryDate.format('yyyy-MM-dd'),
        ]
    }
}
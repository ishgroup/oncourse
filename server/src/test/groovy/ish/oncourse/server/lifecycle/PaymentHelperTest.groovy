package ish.oncourse.server.lifecycle

import ish.CayenneIshTestCase
import ish.common.types.AccountType
import ish.common.types.PaymentSource
import ish.common.types.PaymentStatus
import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.cayenne.*
import ish.util.PaymentMethodUtil
import org.apache.cayenne.access.DataContext
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate

import static org.junit.Assert.*

class PaymentHelperTest extends CayenneIshTestCase {

    @BeforeEach
    void setup() throws Exception {
        wipeTables()
        super.setup()
    }

    @Test
    void testAssignAutoBankingForAutomaticallyPayments() {
        DataContext context = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        assertEquals(0, ObjectSelect.query(PaymentIn.class).select(context).size())
        assertEquals(0, ObjectSelect.query(Banking.class).select(context).size())

        Contact contact = context.newObject(Contact.class)
        contact.setFirstName("firstName1")
        contact.setLastName("lastName1")

        Account account = context.newObject(Account.class)
        account.setAccountCode("accountCode")
        account.setDescription("description")
        account.setType(AccountType.ASSET)
        account.setIsEnabled(true)

        PaymentIn pIn = context.newObject(PaymentIn.class)
        pIn.setPaymentDate(LocalDate.now())
        pIn.setAmount(Money.ONE)
        pIn.setPayer(contact)
        pIn.setAccountIn(account)
        pIn.setStatus(PaymentStatus.IN_TRANSACTION)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(context, PaymentMethod.class, PaymentType.EFT), pIn).set()

        pIn.setSource(PaymentSource.SOURCE_ONCOURSE)

        Invoice invoice = context.newObject(Invoice.class)

        invoice.setContact(contact)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)
        invoice.setAmountOwing(Money.ZERO)

        invoice.setDebtorsAccount(account)

        PaymentInLine piLine = context.newObject(PaymentInLine.class)
        piLine.setInvoice(invoice)
        piLine.setAmount(Money.ONE)
        piLine.setAccountOut(account)

        pIn.addToPaymentInLines(piLine)
        context.commitChanges()

        assertEquals(1, ObjectSelect.query(PaymentIn.class).select(context).size())
        assertEquals(0, ObjectSelect.query(Banking.class).select(context).size())

        pIn.setStatus(PaymentStatus.SUCCESS)
        context.commitChanges()

        List<PaymentIn> paymentIns = ObjectSelect.query(PaymentIn.class).select(context)
        assertEquals(1, paymentIns.size())
        assertNotNull(paymentIns.get(0).getBanking())
        assertEquals(1, ObjectSelect.query(Banking.class).select(context).size())

    }


    @Test
    void testAssignAutoBankingForNonAutomaticallyPayments() {
        DataContext context = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        assertEquals(0, ObjectSelect.query(PaymentIn.class).select(context).size())
        assertEquals(0, ObjectSelect.query(Banking.class).select(context).size())

        Contact contact = context.newObject(Contact.class)
        contact.setFirstName("firstName1")
        contact.setLastName("lastName1")

        Account account = context.newObject(Account.class)
        account.setAccountCode("accountCode")
        account.setDescription("description")
        account.setType(AccountType.ASSET)
        account.setIsEnabled(true)

        PaymentIn pIn = context.newObject(PaymentIn.class)
        pIn.setPaymentDate(LocalDate.now())
        pIn.setAmount(Money.ONE)
        pIn.setPayer(contact)
        pIn.setAccountIn(account)
        pIn.setStatus(PaymentStatus.IN_TRANSACTION)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(context, PaymentMethod.class, PaymentType.CASH), pIn).set()

        pIn.setSource(PaymentSource.SOURCE_ONCOURSE)

        Invoice invoice = context.newObject(Invoice.class)

        invoice.setContact(contact)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)
        invoice.setAmountOwing(Money.ZERO)

        invoice.setDebtorsAccount(account)

        PaymentInLine piLine = context.newObject(PaymentInLine.class)
        piLine.setInvoice(invoice)
        piLine.setAmount(Money.ONE)
        piLine.setAccountOut(account)

        pIn.addToPaymentInLines(piLine)
        context.commitChanges()

        assertEquals(1, ObjectSelect.query(PaymentIn.class).select(context).size())
        assertEquals(0, ObjectSelect.query(Banking.class).select(context).size())

        pIn.setStatus(PaymentStatus.SUCCESS)
        context.commitChanges()

        assertEquals(1, ObjectSelect.query(PaymentIn.class).select(context).size())
        assertEquals(0, ObjectSelect.query(Banking.class).select(context).size())
    }


    @Test
    void testAssignAutoBankingForSystemTypePayments() {
        DataContext context = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        assertEquals(0, ObjectSelect.query(PaymentOut.class).select(context).size())
        assertEquals(0, ObjectSelect.query(Banking.class).select(context).size())

        Contact contact = context.newObject(Contact.class)
        contact.setFirstName("firstName1")
        contact.setLastName("lastName1")

        Account account = context.newObject(Account.class)
        account.setAccountCode("accountCode")
        account.setDescription("description")
        account.setType(AccountType.ASSET)
        account.setIsEnabled(true)

        PaymentIn pIn = context.newObject(PaymentIn.class)
        pIn.setPaymentDate(LocalDate.now())
        pIn.setAmount(Money.ZERO)
        pIn.setPayer(contact)
        pIn.setAccountIn(account)
        pIn.setStatus(PaymentStatus.IN_TRANSACTION)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(context, PaymentMethod.class, PaymentType.INTERNAL), pIn).set()

        pIn.setSource(PaymentSource.SOURCE_ONCOURSE)

        Invoice invoice = context.newObject(Invoice.class)

        invoice.setContact(contact)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)
        invoice.setAmountOwing(Money.ZERO)

        invoice.setDebtorsAccount(account)

        PaymentInLine piLine = context.newObject(PaymentInLine.class)
        piLine.setInvoice(invoice)
        piLine.setAmount(Money.ZERO)
        piLine.setAccountOut(account)

        pIn.addToPaymentInLines(piLine)
        context.commitChanges()

        assertEquals(1, ObjectSelect.query(PaymentIn.class).select(context).size())
        assertEquals(0, ObjectSelect.query(Banking.class).select(context).size())

        pIn.setStatus(PaymentStatus.SUCCESS)
        context.commitChanges()

        assertEquals(1, ObjectSelect.query(PaymentIn.class).select(context).size())
        assertEquals(0, ObjectSelect.query(Banking.class).select(context).size())
    }

    @Test
    void testAssignAutoBankingForAutomaticallyPaymentOuts() {
        DataContext context = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        assertEquals(0, ObjectSelect.query(PaymentIn.class).select(context).size())
        assertEquals(0, ObjectSelect.query(Banking.class).select(context).size())

        Contact contact = context.newObject(Contact.class)
        contact.setFirstName("firstName1")
        contact.setLastName("lastName1")

        Account account = context.newObject(Account.class)
        account.setAccountCode("accountCode")
        account.setDescription("description")
        account.setType(AccountType.ASSET)
        account.setIsEnabled(true)

        PaymentOut pOut = context.newObject(PaymentOut.class)
        pOut.setPaymentDate(LocalDate.now())
        pOut.setAmount(Money.ONE)
        pOut.setPayee(contact)
        pOut.setAccountOut(account)
        pOut.setStatus(PaymentStatus.IN_TRANSACTION)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(context, PaymentMethod.class, PaymentType.EFT), pOut).set()

        Invoice invoice = context.newObject(Invoice.class)

        invoice.setContact(contact)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)
        invoice.setAmountOwing(Money.ZERO)

        invoice.setDebtorsAccount(account)

        PaymentOutLine pLine = context.newObject(PaymentOutLine.class)
        pLine.setInvoice(invoice)
        pLine.setAmount(Money.ONE)
        pLine.setAccountIn(account)

        pOut.addToPaymentOutLines(pLine)
        context.commitChanges()

        assertEquals(1, ObjectSelect.query(PaymentOut.class).select(context).size())
        assertEquals(0, ObjectSelect.query(Banking.class).select(context).size())

        pOut.setStatus(PaymentStatus.SUCCESS)
        context.commitChanges()

        List<PaymentOut> paymentIns = ObjectSelect.query(PaymentOut.class).select(context)
        assertEquals(1, paymentIns.size())
        assertNotNull(paymentIns.get(0).getBanking())
        assertEquals(1, ObjectSelect.query(Banking.class).select(context).size())
    }

    @Test
    void testAssignAutoBankingForReversedPayments() {
        // create automatic payment
        DataContext context = injector.getInstance(ICayenneService.class).getNewNonReplicatingContext()

        assertEquals(0, ObjectSelect.query(PaymentIn.class).select(context).size())
        assertEquals(0, ObjectSelect.query(Banking.class).select(context).size())

        Contact contact = context.newObject(Contact.class)
        contact.setFirstName("firstName1")
        contact.setLastName("lastName1")

        Account account = context.newObject(Account.class)
        account.setAccountCode("accountCode")
        account.setDescription("description")
        account.setType(AccountType.ASSET)
        account.setIsEnabled(true)

        PaymentIn pIn = context.newObject(PaymentIn.class)
        pIn.setPaymentDate(LocalDate.now())
        pIn.setAmount(Money.ONE)
        pIn.setPayer(contact)
        pIn.setAccountIn(account)
        pIn.setStatus(PaymentStatus.IN_TRANSACTION)

        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(context, PaymentMethod.class, PaymentType.EFT), pIn).set()


        pIn.setSource(PaymentSource.SOURCE_ONCOURSE)

        Invoice invoice = context.newObject(Invoice.class)

        invoice.setContact(contact)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)
        invoice.setAmountOwing(Money.ZERO)

        invoice.setDebtorsAccount(account)

        PaymentInLine piLine = context.newObject(PaymentInLine.class)
        piLine.setInvoice(invoice)
        piLine.setAmount(Money.ONE)
        piLine.setAccountOut(account)

        pIn.addToPaymentInLines(piLine)
        context.commitChanges()

        assertEquals(1, ObjectSelect.query(PaymentIn.class).select(context).size())
        assertEquals(0, ObjectSelect.query(Banking.class).select(context).size())

        pIn.setStatus(PaymentStatus.SUCCESS)
        context.commitChanges()

        List<PaymentIn> paymentIns = ObjectSelect.query(PaymentIn.class).select(context)
        assertEquals(1, paymentIns.size())
        assertNotNull(paymentIns.get(0).getBanking())
        List<Banking> bankings = ObjectSelect.query(Banking.class).select(context)
        assertEquals(1, bankings.size())

        //remove banking
        context.deleteObjects(bankings.get(0))
        context.commitChanges()

        List<PaymentIn> paymentIns2 = ObjectSelect.query(PaymentIn.class).select(context)
        assertEquals(1, paymentIns2.size())
        assertNull(paymentIns2.get(0).getBanking())

        assertEquals(0, ObjectSelect.query(Banking.class).select(context).size())

        //make reverse payment

        PaymentIn reversePIn = context.newObject(PaymentIn.class)
        reversePIn.setPaymentDate(LocalDate.now())
        reversePIn.setAmount(Money.ONE.negate())
        reversePIn.setPayer(contact)
        reversePIn.setAccountIn(account)
        reversePIn.setStatus(PaymentStatus.SUCCESS)

        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(context, PaymentMethod.class, PaymentType.EFT), reversePIn).set()
        reversePIn.setSource(PaymentSource.SOURCE_ONCOURSE)

        PaymentInLine rPiLine = context.newObject(PaymentInLine.class)
        rPiLine.setInvoice(invoice)
        rPiLine.setAmount(Money.ONE.negate())
        rPiLine.setAccountOut(account)
        reversePIn.addToPaymentInLines(rPiLine)

        reversePIn.setReversalOf(pIn)

        context.commitChanges()

        List<PaymentIn> allPaymentIns = ObjectSelect.query(PaymentIn.class).select(context)
        assertEquals(2, allPaymentIns.size())
        assertNull(allPaymentIns.get(0).getBanking())
        assertNull(allPaymentIns.get(1).getBanking())

        assertEquals(0, ObjectSelect.query(Banking.class).select(context).size())

    }
}
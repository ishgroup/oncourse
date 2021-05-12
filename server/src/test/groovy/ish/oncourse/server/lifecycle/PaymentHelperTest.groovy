package ish.oncourse.server.lifecycle

import groovy.transform.CompileStatic
import ish.CayenneIshTestCase
import ish.common.types.AccountType
import ish.common.types.PaymentSource
import ish.common.types.PaymentStatus
import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.entity.services.SetPaymentMethod
import ish.oncourse.server.cayenne.*
import ish.util.PaymentMethodUtil
import org.apache.cayenne.query.ObjectSelect
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate

@CompileStatic
class PaymentHelperTest extends CayenneIshTestCase {

    @BeforeEach
    void setup() throws Exception {
        wipeTables()
        super.setup()
    }

    
    @Test
    void testAssignAutoBankingForAutomaticallyPayments() {
        Assertions.assertEquals(0, ObjectSelect.query(PaymentIn.class).select(cayenneContext).size())
        Assertions.assertEquals(0, ObjectSelect.query(Banking.class).select(cayenneContext).size())

        Contact contact = cayenneContext.newObject(Contact.class)
        contact.setFirstName("firstName1")
        contact.setLastName("lastName1")

        Account account = cayenneContext.newObject(Account.class)
        account.setAccountCode("accountCode")
        account.setDescription("description")
        account.setType(AccountType.ASSET)
        account.setIsEnabled(true)

        PaymentIn pIn = cayenneContext.newObject(PaymentIn.class)
        pIn.setPaymentDate(LocalDate.now())
        pIn.setAmount(Money.ONE)
        pIn.setPayer(contact)
        pIn.setAccountIn(account)
        pIn.setStatus(PaymentStatus.IN_TRANSACTION)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(cayenneContext, PaymentMethod.class, PaymentType.EFT), pIn).set()

        pIn.setSource(PaymentSource.SOURCE_ONCOURSE)

        Invoice invoice = cayenneContext.newObject(Invoice.class)

        invoice.setContact(contact)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)
        invoice.setAmountOwing(Money.ZERO)

        invoice.setDebtorsAccount(account)

        PaymentInLine piLine = cayenneContext.newObject(PaymentInLine.class)
        piLine.setInvoice(invoice)
        piLine.setAmount(Money.ONE)
        piLine.setAccountOut(account)

        pIn.addToPaymentInLines(piLine)
        cayenneContext.commitChanges()

        Assertions.assertEquals(1, ObjectSelect.query(PaymentIn.class).select(cayenneContext).size())
        Assertions.assertEquals(0, ObjectSelect.query(Banking.class).select(cayenneContext).size())

        pIn.setStatus(PaymentStatus.SUCCESS)
        cayenneContext.commitChanges()

        List<PaymentIn> paymentIns = ObjectSelect.query(PaymentIn.class).select(cayenneContext)
        Assertions.assertEquals(1, paymentIns.size())
        Assertions.assertNotNull(paymentIns.get(0).getBanking())
        Assertions.assertEquals(1, ObjectSelect.query(Banking.class).select(cayenneContext).size())
    }

    @Test
    void testAssignAutoBankingForNonAutomaticallyPayments() {
        Assertions.assertEquals(0, ObjectSelect.query(PaymentIn.class).select(cayenneContext).size())
        Assertions.assertEquals(0, ObjectSelect.query(Banking.class).select(cayenneContext).size())

        Contact contact = cayenneContext.newObject(Contact.class)
        contact.setFirstName("firstName1")
        contact.setLastName("lastName1")

        Account account = cayenneContext.newObject(Account.class)
        account.setAccountCode("accountCode")
        account.setDescription("description")
        account.setType(AccountType.ASSET)
        account.setIsEnabled(true)

        PaymentIn pIn = cayenneContext.newObject(PaymentIn.class)
        pIn.setPaymentDate(LocalDate.now())
        pIn.setAmount(Money.ONE)
        pIn.setPayer(contact)
        pIn.setAccountIn(account)
        pIn.setStatus(PaymentStatus.IN_TRANSACTION)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(cayenneContext, PaymentMethod.class, PaymentType.CASH), pIn).set()

        pIn.setSource(PaymentSource.SOURCE_ONCOURSE)

        Invoice invoice = cayenneContext.newObject(Invoice.class)

        invoice.setContact(contact)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)
        invoice.setAmountOwing(Money.ZERO)

        invoice.setDebtorsAccount(account)

        PaymentInLine piLine = cayenneContext.newObject(PaymentInLine.class)
        piLine.setInvoice(invoice)
        piLine.setAmount(Money.ONE)
        piLine.setAccountOut(account)

        pIn.addToPaymentInLines(piLine)
        cayenneContext.commitChanges()

        Assertions.assertEquals(1, ObjectSelect.query(PaymentIn.class).select(cayenneContext).size())
        Assertions.assertEquals(0, ObjectSelect.query(Banking.class).select(cayenneContext).size())

        pIn.setStatus(PaymentStatus.SUCCESS)
        cayenneContext.commitChanges()

        Assertions.assertEquals(1, ObjectSelect.query(PaymentIn.class).select(cayenneContext).size())
        Assertions.assertEquals(0, ObjectSelect.query(Banking.class).select(cayenneContext).size())
    }
    
    @Test
    void testAssignAutoBankingForSystemTypePayments() {
        Assertions.assertEquals(0, ObjectSelect.query(PaymentOut.class).select(cayenneContext).size())
        Assertions.assertEquals(0, ObjectSelect.query(Banking.class).select(cayenneContext).size())

        Contact contact = cayenneContext.newObject(Contact.class)
        contact.setFirstName("firstName1")
        contact.setLastName("lastName1")

        Account account = cayenneContext.newObject(Account.class)
        account.setAccountCode("accountCode")
        account.setDescription("description")
        account.setType(AccountType.ASSET)
        account.setIsEnabled(true)

        PaymentIn pIn = cayenneContext.newObject(PaymentIn.class)
        pIn.setPaymentDate(LocalDate.now())
        pIn.setAmount(Money.ZERO)
        pIn.setPayer(contact)
        pIn.setAccountIn(account)
        pIn.setStatus(PaymentStatus.IN_TRANSACTION)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(cayenneContext, PaymentMethod.class, PaymentType.INTERNAL), pIn).set()

        pIn.setSource(PaymentSource.SOURCE_ONCOURSE)

        Invoice invoice = cayenneContext.newObject(Invoice.class)

        invoice.setContact(contact)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)
        invoice.setAmountOwing(Money.ZERO)

        invoice.setDebtorsAccount(account)

        PaymentInLine piLine = cayenneContext.newObject(PaymentInLine.class)
        piLine.setInvoice(invoice)
        piLine.setAmount(Money.ZERO)
        piLine.setAccountOut(account)

        pIn.addToPaymentInLines(piLine)
        cayenneContext.commitChanges()

        Assertions.assertEquals(1, ObjectSelect.query(PaymentIn.class).select(cayenneContext).size())
        Assertions.assertEquals(0, ObjectSelect.query(Banking.class).select(cayenneContext).size())

        pIn.setStatus(PaymentStatus.SUCCESS)
        cayenneContext.commitChanges()

        Assertions.assertEquals(1, ObjectSelect.query(PaymentIn.class).select(cayenneContext).size())
        Assertions.assertEquals(0, ObjectSelect.query(Banking.class).select(cayenneContext).size())
    }

    @Test
    void testAssignAutoBankingForAutomaticallyPaymentOuts() {
        Assertions.assertEquals(0, ObjectSelect.query(PaymentIn.class).select(cayenneContext).size())
        Assertions.assertEquals(0, ObjectSelect.query(Banking.class).select(cayenneContext).size())

        Contact contact = cayenneContext.newObject(Contact.class)
        contact.setFirstName("firstName1")
        contact.setLastName("lastName1")

        Account account = cayenneContext.newObject(Account.class)
        account.setAccountCode("accountCode")
        account.setDescription("description")
        account.setType(AccountType.ASSET)
        account.setIsEnabled(true)

        PaymentOut pOut = cayenneContext.newObject(PaymentOut.class)
        pOut.setPaymentDate(LocalDate.now())
        pOut.setAmount(Money.ONE)
        pOut.setPayee(contact)
        pOut.setAccountOut(account)
        pOut.setStatus(PaymentStatus.IN_TRANSACTION)
        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(cayenneContext, PaymentMethod.class, PaymentType.EFT), pOut).set()

        Invoice invoice = cayenneContext.newObject(Invoice.class)

        invoice.setContact(contact)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)
        invoice.setAmountOwing(Money.ZERO)

        invoice.setDebtorsAccount(account)

        PaymentOutLine pLine = cayenneContext.newObject(PaymentOutLine.class)
        pLine.setInvoice(invoice)
        pLine.setAmount(Money.ONE)
        pLine.setAccountIn(account)

        pOut.addToPaymentOutLines(pLine)
        cayenneContext.commitChanges()

        Assertions.assertEquals(1, ObjectSelect.query(PaymentOut.class).select(cayenneContext).size())
        Assertions.assertEquals(0, ObjectSelect.query(Banking.class).select(cayenneContext).size())

        pOut.setStatus(PaymentStatus.SUCCESS)
        cayenneContext.commitChanges()

        List<PaymentOut> paymentIns = ObjectSelect.query(PaymentOut.class).select(cayenneContext)
        Assertions.assertEquals(1, paymentIns.size())
        Assertions.assertNotNull(paymentIns.get(0).getBanking())
        Assertions.assertEquals(1, ObjectSelect.query(Banking.class).select(cayenneContext).size())
    }

    
    @Test
    void testAssignAutoBankingForReversedPayments() {
        // create automatic payment
        Assertions.assertEquals(0, ObjectSelect.query(PaymentIn.class).select(cayenneContext).size())
        Assertions.assertEquals(0, ObjectSelect.query(Banking.class).select(cayenneContext).size())

        Contact contact = cayenneContext.newObject(Contact.class)
        contact.setFirstName("firstName1")
        contact.setLastName("lastName1")

        Account account = cayenneContext.newObject(Account.class)
        account.setAccountCode("accountCode")
        account.setDescription("description")
        account.setType(AccountType.ASSET)
        account.setIsEnabled(true)

        PaymentIn pIn = cayenneContext.newObject(PaymentIn.class)
        pIn.setPaymentDate(LocalDate.now())
        pIn.setAmount(Money.ONE)
        pIn.setPayer(contact)
        pIn.setAccountIn(account)
        pIn.setStatus(PaymentStatus.IN_TRANSACTION)

        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(cayenneContext, PaymentMethod.class, PaymentType.EFT), pIn).set()


        pIn.setSource(PaymentSource.SOURCE_ONCOURSE)

        Invoice invoice = cayenneContext.newObject(Invoice.class)

        invoice.setContact(contact)
        invoice.setSource(PaymentSource.SOURCE_ONCOURSE)
        invoice.setAmountOwing(Money.ZERO)

        invoice.setDebtorsAccount(account)

        PaymentInLine piLine = cayenneContext.newObject(PaymentInLine.class)
        piLine.setInvoice(invoice)
        piLine.setAmount(Money.ONE)
        piLine.setAccountOut(account)

        pIn.addToPaymentInLines(piLine)
        cayenneContext.commitChanges()

        Assertions.assertEquals(1, ObjectSelect.query(PaymentIn.class).select(cayenneContext).size())
        Assertions.assertEquals(0, ObjectSelect.query(Banking.class).select(cayenneContext).size())

        pIn.setStatus(PaymentStatus.SUCCESS)
        cayenneContext.commitChanges()

        List<PaymentIn> paymentIns = ObjectSelect.query(PaymentIn.class).select(cayenneContext)
        Assertions.assertEquals(1, paymentIns.size())
        Assertions.assertNotNull(paymentIns.get(0).getBanking())
        List<Banking> bankings = ObjectSelect.query(Banking.class).select(cayenneContext)
        Assertions.assertEquals(1, bankings.size())

        //remove banking
        cayenneContext.deleteObjects(bankings.get(0))
        cayenneContext.commitChanges()

        List<PaymentIn> paymentIns2 = ObjectSelect.query(PaymentIn.class).select(cayenneContext)
        Assertions.assertEquals(1, paymentIns2.size())
        Assertions.assertNull(paymentIns2.get(0).getBanking())

        Assertions.assertEquals(0, ObjectSelect.query(Banking.class).select(cayenneContext).size())

        //make reverse payment

        PaymentIn reversePIn = cayenneContext.newObject(PaymentIn.class)
        reversePIn.setPaymentDate(LocalDate.now())
        reversePIn.setAmount(Money.ONE.negate())
        reversePIn.setPayer(contact)
        reversePIn.setAccountIn(account)
        reversePIn.setStatus(PaymentStatus.SUCCESS)

        SetPaymentMethod.valueOf(PaymentMethodUtil.getCustomPaymentMethod(cayenneContext, PaymentMethod.class, PaymentType.EFT), reversePIn).set()
        reversePIn.setSource(PaymentSource.SOURCE_ONCOURSE)

        PaymentInLine rPiLine = cayenneContext.newObject(PaymentInLine.class)
        rPiLine.setInvoice(invoice)
        rPiLine.setAmount(Money.ONE.negate())
        rPiLine.setAccountOut(account)
        reversePIn.addToPaymentInLines(rPiLine)

        reversePIn.setReversalOf(pIn)

        cayenneContext.commitChanges()

        List<PaymentIn> allPaymentIns = ObjectSelect.query(PaymentIn.class).select(cayenneContext)
        Assertions.assertEquals(2, allPaymentIns.size())
        Assertions.assertNull(allPaymentIns.get(0).getBanking())
        Assertions.assertNull(allPaymentIns.get(1).getBanking())

        Assertions.assertEquals(0, ObjectSelect.query(Banking.class).select(cayenneContext).size())

    }
}
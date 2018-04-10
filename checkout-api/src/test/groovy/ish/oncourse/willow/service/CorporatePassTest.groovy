package ish.oncourse.willow.service

import ish.common.types.ConfirmationStatus
import ish.common.types.EnrolmentStatus
import ish.common.types.ProductStatus
import ish.math.Money
import ish.oncourse.model.*
import ish.oncourse.willow.checkout.corporatepass.CorporatePassApiImpl
import ish.oncourse.willow.checkout.corporatepass.CreateCorpPassModel
import ish.oncourse.willow.checkout.corporatepass.ProcessCorporatePassRequest
import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.model.checkout.Amount
import ish.oncourse.willow.model.checkout.CheckoutModelRequest
import ish.oncourse.willow.model.checkout.ContactNode
import ish.oncourse.willow.model.checkout.Voucher
import ish.oncourse.willow.model.checkout.corporatepass.MakeCorporatePassRequest
import ish.oncourse.willow.service.impl.CollegeService
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.junit.Test


import static org.junit.Assert.*

class CorporatePassTest extends ApiTest {
    
    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/CorporatePassTest.xml'
    }
    
    
    @Test
    void test() {

        ObjectContext context = cayenneService.newContext()
        College college = SelectById.query(College, 1l).selectOne(context)

        WebSite webSite = SelectById.query(WebSite, 1l).selectOne(context)
        
        CheckoutModelRequest modelRequest = new CheckoutModelRequest().with { r ->
            r.contactNodes << new ContactNode().with { n ->
                n.contactId = '1001'
                n.enrolments += [ new ish.oncourse.willow.model.checkout.Enrolment().with { e -> 
                    e.contactId = '1001'
                    e.classId = '1001'
                    e.selected = true
                    e
                }, new  ish.oncourse.willow.model.checkout.Enrolment().with { e ->
                    e.contactId = '1001'
                    e.classId = '1002'
                    e.selected = true
                    e
                }, new ish.oncourse.willow.model.checkout.Enrolment().with { e ->
                    e.contactId = '1001'
                    e.classId = '1003'
                    e.selected = true
                    e
                },]
                
                n.vouchers << new Voucher().with { v ->
                    v.contactId = '1001'
                    v.productId = '1001'
                    v.value = 100.00
                    v.price = 100.00
                    v.isEditablePrice = true
                    v.selected = true
                    v
                }
                
                
                n
            }
            r.payerId = '1001'
            r
        }
        
        MakeCorporatePassRequest corporatePassRequest = new MakeCorporatePassRequest().with { r ->
            r.agreementFlag = true
            r.reference = 'corp pass ref'
            r.corporatePassId = '2001'
            r.checkoutModelRequest = modelRequest
            r
            
        }
        
        ProcessCorporatePassRequest processor = new ProcessCorporatePassRequest(context, college, corporatePassRequest).process()
        
        Amount amount = processor.model.amount
        assertNotNull(amount)
        assertEquals(430.00, amount.total, 0)
        assertEquals( -66.00, amount.discount, 0)

        new CreateCorpPassModel(context, college, webSite, corporatePassRequest.reference, processor.pass, processor.model).create()
        context.commitChanges()

        CorporatePass corporatePass = SelectById.query(CorporatePass, 2001l).selectOne(cayenneService.newContext())
        Contact company = corporatePass.contact
        List<Invoice> invoices = corporatePass.getInvoice()

        assertEquals(3, invoices.size())
        
        Invoice mainInvoice = invoices.find { it.invoiceLines*.enrolment*.courseClass*.id.flatten().contains(1003L) }
        assertEquals(ConfirmationStatus.NOT_SENT, mainInvoice.confirmationStatus)
        assertEquals(new Money('232.00'), mainInvoice.amountOwing)
        assertEquals(company, mainInvoice.contact)
        assertEquals(0, mainInvoice.paymentInLines.size())
        assertEquals(2, mainInvoice.invoiceLines.size())

        InvoiceLine invoiceLine1 = mainInvoice.invoiceLines.find { it.enrolment?.courseClass?.id == 1003L }
        assertNotNull(invoiceLine1)
        assertEquals(EnrolmentStatus.SUCCESS, invoiceLine1.enrolment.status)
        assertEquals(ConfirmationStatus.NOT_SENT, invoiceLine1.enrolment.confirmationStatus)
        assertEquals(1001, invoiceLine1.enrolment.student.id)
        assertEquals(new Money('12.00'), invoiceLine1.totalTax)
        assertEquals(new Money('-20.00'), invoiceLine1.discountTotalExTax)
        assertEquals(new Money('132.00'), invoiceLine1.finalPriceToPayIncTax)
        
        InvoiceLine invoiceLine2 = mainInvoice.invoiceLines.find { it.productItems*.product*.id.flatten().contains(1001L) }
        assertNotNull(invoiceLine2)
        assertEquals(1, invoiceLine2.productItems.size())
        assertEquals(ProductStatus.ACTIVE, invoiceLine2.productItems[0].status)
        assertEquals(null, invoiceLine2.productItems[0].contact)
        assertEquals(ConfirmationStatus.NOT_SENT, invoiceLine2.productItems[0].confirmationStatus)
        assertEquals(new Money('0.00'), invoiceLine2.totalTax)
        assertEquals(new Money('0.00'), invoiceLine2.discountTotalExTax)
        assertEquals(new Money('100.00'), invoiceLine2.finalPriceToPayIncTax)

        Invoice ppInvoice1 = invoices.find { it.invoiceLines*.enrolment*.courseClass*.id.flatten().contains(1001L) }
        assertEquals(ConfirmationStatus.NOT_SENT, ppInvoice1.confirmationStatus)
        assertEquals(new Money('132.00'), ppInvoice1.amountOwing)
        assertEquals(company, ppInvoice1.contact)
        assertEquals(0, ppInvoice1.paymentInLines.size())
        assertEquals(1, ppInvoice1.invoiceLines.size())

        InvoiceLine invoiceLine3 = ppInvoice1.invoiceLines[0]
        assertNotNull(invoiceLine3)
        assertEquals(EnrolmentStatus.SUCCESS, invoiceLine3.enrolment.status)
        assertEquals(1001, invoiceLine3.enrolment.student.id)
        assertEquals(new Money('12.00'), invoiceLine3.totalTax)
        assertEquals(new Money('-20.00'), invoiceLine3.discountTotalExTax)
        assertEquals(new Money('132.00'), invoiceLine3.finalPriceToPayIncTax)


        Invoice ppInvoice2 = invoices.find { it.invoiceLines*.enrolment*.courseClass*.id.flatten().contains(1002L) }
        assertEquals(ConfirmationStatus.NOT_SENT, ppInvoice2.confirmationStatus)
        assertEquals(new Money('132.00'), ppInvoice1.amountOwing)
        assertEquals(company, ppInvoice2.contact)
        assertEquals(0, ppInvoice2.paymentInLines.size())
        assertEquals(1, ppInvoice2.invoiceLines.size())

        InvoiceLine invoiceLine4 = ppInvoice2.invoiceLines[0]
        assertNotNull(invoiceLine4)
        assertEquals(EnrolmentStatus.SUCCESS, invoiceLine4.enrolment.status)
        assertEquals(ConfirmationStatus.NOT_SENT, invoiceLine4.enrolment.confirmationStatus)

        assertEquals(1001, invoiceLine4.enrolment.student.id)
        assertEquals(new Money('12.00'), invoiceLine4.totalTax)
        assertEquals(new Money('-20.00'), invoiceLine4.discountTotalExTax)
        assertEquals(new Money('132.00'), invoiceLine4.finalPriceToPayIncTax)

        
        QueuedTransaction queuedTransaction = ObjectSelect.query(QueuedTransaction).selectOne(cayenneService.newContext())

        assertNotNull(queuedTransaction)
        assertEquals(3, queuedTransaction.queuedRecords.findAll {it.entityIdentifier == Invoice.simpleName}.size())
        assertEquals(4, queuedTransaction.queuedRecords.findAll {it.entityIdentifier == InvoiceLine.simpleName}.size())
        assertEquals(3, queuedTransaction.queuedRecords.findAll {it.entityIdentifier == Enrolment.simpleName}.size())
        assertEquals(1, queuedTransaction.queuedRecords.findAll {it.entityIdentifier == ish.oncourse.model.Voucher.simpleName}.size())
        assertEquals(1, queuedTransaction.queuedRecords.findAll {it.entityIdentifier == Student.simpleName}.size())
        assertEquals(2, queuedTransaction.queuedRecords.findAll {it.entityIdentifier == Contact.simpleName}.size())
        assertEquals(1, queuedTransaction.queuedRecords.findAll {it.entityIdentifier == CorporatePass.simpleName}.size())
        assertEquals(0, queuedTransaction.queuedRecords.findAll {it.entityIdentifier == PaymentIn.simpleName}.size())

    }

    @Test
    void isAvailable() {
        RequestFilter.ThreadLocalXOrigin.set('mammoth.oncourse.cc')
        CorporatePassApiImpl api = new CorporatePassApiImpl(cayenneService, new CollegeService(cayenneService))
        CheckoutModelRequest modelRequest = new CheckoutModelRequest().with { r ->
            r.contactNodes << new ContactNode().with { n ->
                n.enrolments += [ new ish.oncourse.willow.model.checkout.Enrolment().with { e ->
                    e.classId = '1001'
                    e.selected = true
                    e
                }, new  ish.oncourse.willow.model.checkout.Enrolment().with { e ->
                    e.classId = '1002'
                    e.selected = true
                    e
                }, new ish.oncourse.willow.model.checkout.Enrolment().with { e ->
                    e.classId = '1003'
                    e.selected = true
                    e
                },]
                n
            }
            r
        }
        
        assertTrue(api.isCorporatePassEnabledFor(modelRequest))

        modelRequest.contactNodes[0].memberships<< new ish.oncourse.willow.model.checkout.Membership().with { m ->
            m.productId = '1002'
            m.selected = true
            m
        }
        modelRequest.contactNodes[0].vouchers << new Voucher().with { v ->
            v.productId = '1001'
            v.selected = true
            v
        }
        
        assertTrue(api.isCorporatePassEnabledFor(modelRequest))
        
        modelRequest.contactNodes[0].enrolments << new ish.oncourse.willow.model.checkout.Enrolment().with { e ->
            e.classId = '1004'
            e.selected = true
            e
        }
        
        assertFalse(api.isCorporatePassEnabledFor(modelRequest))


    }
    
    
}

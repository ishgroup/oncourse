package ish.oncourse.willow.checkout.payment

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.math.DoubleMethods
import ish.math.Money
import ish.oncourse.model.Discount
import ish.oncourse.model.DiscountCourseClass
import ish.oncourse.model.Enrolment
import ish.oncourse.model.InvoiceLine
import ish.oncourse.model.InvoiceLineDiscount
import ish.oncourse.willow.checkout.functions.CalculatePrice
import ish.oncourse.willow.model.web.CourseClassPrice
import ish.util.DiscountUtils
import ish.util.InvoiceUtil
import org.apache.cayenne.ObjectContext
import org.apache.commons.lang3.StringUtils

import java.text.SimpleDateFormat

@CompileStatic
class EnrolmentInvoiceLine {
    
    private static final String DATE_FORMAT = 'dd-MM-yyyy h:mm a z'

    Enrolment e
    CourseClassPrice price

    EnrolmentInvoiceLine(Enrolment e, CourseClassPrice price) {
        this.e = e
        this.price = price
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    InvoiceLine create() {
        InvoiceLine invoiceLine = e.objectContext.newObject(InvoiceLine)
        invoiceLine.title = "$e.student.contact.fullName $e.courseClass.uniqueIdentifier"
        if (! e.courseClass.startDateTime) {
            invoiceLine.description = e.courseClass.course.name
        } else {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT)
            format.timeZone = e.courseClass.classTimeZone
            invoiceLine.description = "$e.courseClass.course.name starting on ${format.format(e.courseClass.startDateTime)}"
        }

        invoiceLine.quantity = BigDecimal.ONE
        invoiceLine.college = e.college
        
        if (price.feeOverriden != null) {
            //Calculate enrolment fee (for enrolments whose courses has ENROLMENT_BY_APPLICATION type) as application.feeOverride if !=null.
            //Application.feeOverride doesn't need to combine with discounts.
            Money feeOverriden = price.feeOverriden.toMoney()
            InvoiceUtil.fillInvoiceLine(invoiceLine, feeOverriden, Money.ZERO, e.courseClass.taxRate, Money.ZERO)
        } else {
            Money taxAdjustment = CalculatePrice.calculateTaxAdjustment(e.courseClass)
            InvoiceUtil.fillInvoiceLine(invoiceLine, e.courseClass.feeExGst, Money.ZERO,
                    e.courseClass.taxRate, taxAdjustment)

            if (price.appliedDiscount) {
                DiscountCourseClass chosenDiscount = e.courseClass.discountCourseClasses
                        .find {(it.discount as Discount).id.toString() == price.appliedDiscount.id}
                DiscountUtils.applyDiscounts(chosenDiscount, invoiceLine, e.courseClass.taxRate, taxAdjustment)
                createInvoiceLineDiscounts(invoiceLine, chosenDiscount.discount as Discount, e.objectContext)
            }
        }

        invoiceLine
    }

    private createInvoiceLineDiscounts(InvoiceLine invoiceLine, Discount discount, ObjectContext objectContext) {
        InvoiceLineDiscount invoiceLineDiscount = objectContext.newObject(InvoiceLineDiscount)
        invoiceLineDiscount.invoiceLine = invoiceLine
        invoiceLineDiscount.discount = discount
        invoiceLineDiscount.college = invoiceLine.college
    }
}

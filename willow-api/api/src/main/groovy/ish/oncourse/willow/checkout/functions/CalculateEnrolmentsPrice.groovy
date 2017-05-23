package ish.oncourse.willow.checkout.functions

import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Discount
import ish.oncourse.model.DiscountCourseClass
import ish.oncourse.model.GetDiscountForEnrolment
import ish.oncourse.services.discount.WebDiscountUtils
import ish.oncourse.willow.model.checkout.CheckoutModel
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class CalculateEnrolmentsPrice {

    ObjectContext context
    College college
    Money total 
    int enrolmentsCount
    CheckoutModel model
    Map<Contact, List<CourseClass>> enrolmentsToProceed
    
    Money totalDiscount = Money.ZERO
    Money totalPayNow = Money.ZERO

    List<Discount> promotions = []

    CalculateEnrolmentsPrice(ObjectContext context, College college, Money total, int enrolmentsCount, CheckoutModel model, Map<Contact, List<CourseClass>> enrolmentsToProceed) {
        this.context = context
        this.college = college
        this.total = total
        this.enrolmentsCount = enrolmentsCount
        this.model = model
        this.enrolmentsToProceed = enrolmentsToProceed

        this.model.promotionIds.each { id ->
            this.promotions << new GetDiscount(this.context, this.college, id).get()
        }
        
    }

    CalculateEnrolmentsPrice calculate() {
        enrolmentsToProceed.each { contact, classes ->
            classes.each { courseClass ->
                CalculatePrice price = new CalculatePrice(courseClass.feeExGst, Money.ZERO, courseClass.taxRate, CalculatePrice.calculateTaxAdjustment(courseClass)).calculate()

                price = applyDiscount(contact, courseClass, price)

                if (courseClass.paymentPlanLines.empty) {
                    totalPayNow = totalPayNow.add(price.finalPriceToPayIncTax)
                } else {
                    totalPayNow = totalPayNow.add(getPayNow(courseClass, price))
                }
            }
        }
        
        this
    }
    
    private setDiscountedPrice(Contact contact, CourseClass courseClass, DiscountCourseClass chosenDiscount, CalculatePrice price) {
        model.purchaseItemsList
                .find {it.contactId == contact.id.toString()}
                .enrolments.find {it.classId == courseClass.id.toString()}
                .price
                .appliedDiscount = new ish.oncourse.willow.model.web.Discount().with { d ->
                    d.title = chosenDiscount.discount.name
                    d.discountValue = price.discountTotalIncTax.toPlainString()
                    d.discountedFee = price.finalPriceToPayIncTax.toPlainString()
                    d.expiryDate = WebDiscountUtils.expiryDate(chosenDiscount.discount, courseClass.startDateTime)?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
                    d
                }
    }   
    
    private Money getPayNow(CourseClass courseClass, CalculatePrice price) {
        Money payNow = Money.ZERO
        Money amountLeftToPay = price.finalPriceToPayIncTax
        Instant now = new Date().toInstant()

        courseClass.paymentPlanLines.sort {it.dayOffset}.each { planLine ->
            Money amount = amountLeftToPay.isGreaterThan(planLine.amount) ? planLine.amount : amountLeftToPay
            if (!planLine.dayOffset) {
                payNow = payNow.add(amount)
            } else if ((ChronoUnit.DAYS.between(courseClass.startDate?.toInstant()?:now, now) as Integer) >= planLine.dayOffset) {
                payNow = payNow.add(amount)
            }
            amountLeftToPay = amountLeftToPay.subtract(amount)
        }
        payNow
    }
    
    private CalculatePrice applyDiscount(Contact contact, CourseClass courseClass, CalculatePrice price) {
        List<DiscountCourseClass> classDiscounts = ((ObjectSelect.query(DiscountCourseClass.class).
                where(DiscountCourseClass.DISCOUNT.dot(Discount.IS_AVAILABLE_ON_WEB).isTrue())
                & DiscountCourseClass.COURSE_CLASS.eq(courseClass))
                & Discount.getCurrentDateFilterForDiscountCourseClass(courseClass.startDate)).
                select(context)


        GetDiscountForEnrolment discounts = GetDiscountForEnrolment.valueOf(classDiscounts, promotions, null, enrolmentsCount, total, contact.student,  courseClass).get()
        DiscountCourseClass chosenDiscount = discounts.chosenDiscount

        if (chosenDiscount != null) {
            price.applyDiscount(chosenDiscount)
            totalDiscount = totalDiscount.add(price.discountTotalIncTax)
            setDiscountedPrice(contact, courseClass, chosenDiscount, price)
        }
        price
    }
}

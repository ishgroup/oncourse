package ish.oncourse.willow.checkout.functions

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.math.Money
import ish.oncourse.cayenne.DiscountInterface
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CorporatePass
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

@CompileStatic
class CalculateEnrolmentsPrice {

    ObjectContext context
    College college
    Money total 
    int enrolmentsCount
    CheckoutModel model
    Map<Contact, List<CourseClass>> enrolmentsToProceed
    CorporatePass corporatePass
    
    Money totalDiscount = Money.ZERO
    Money totalPayNow = Money.ZERO
    List<EnrolmentNode> enrolmentNodes = []

    List<Discount> promotions = []

    CalculateEnrolmentsPrice(ObjectContext context, College college, Money total, int enrolmentsCount, CheckoutModel model, Map<Contact, List<CourseClass>> enrolmentsToProceed, List<String> promotionIds, CorporatePass corporatePass) {
        this.context = context
        this.college = college
        this.total = total
        this.enrolmentsCount = enrolmentsCount
        this.model = model
        this.enrolmentsToProceed = enrolmentsToProceed
        this.corporatePass = corporatePass
        
        promotionIds.each { id ->
            this.promotions << new GetDiscount(this.context, this.college, id).get()
        }
        
    }

    CalculateEnrolmentsPrice calculate() {
        enrolmentsToProceed.each { contact, classes ->
            classes.each { courseClass ->

                Money classPrice = getOverridenFee(contact, courseClass)
                
                if (!classPrice) {
                    CalculatePrice price = new CalculatePrice(courseClass.feeExGst, Money.ZERO, courseClass.taxRate, CalculatePrice.calculateTaxAdjustment(courseClass)).calculate()
                    price = applyDiscount(contact, courseClass, price)
                    classPrice = price.finalPriceToPayIncTax
                }
                Money payNow 
                if (courseClass.paymentPlanLines.empty) {
                    payNow = classPrice
                } else {
                    payNow = getPayNow(courseClass, classPrice)
                }
                totalPayNow = totalPayNow.add(payNow)
                enrolmentNodes << new EnrolmentNode(finalPrice: classPrice, contact: contact, course: courseClass.course, payNow: payNow)
            }
        }
        
        this
    }
    
    private setDiscountedPrice(Contact contact, CourseClass courseClass, DiscountCourseClass chosenDiscount, Money discountValue, Money discountedFee) {
        DiscountInterface discount = chosenDiscount.getDiscount()
        model.contactNodes
                .find {it.contactId == contact.id.toString()}
                .enrolments.find {it.classId == courseClass.id.toString()}
                .price
                .appliedDiscount = new ish.oncourse.willow.model.web.Discount().with { d ->
                    d.id = (discount as Discount).id.toString()
                    d.title = (discount as Discount).name
                    d.discountValue = discountValue.doubleValue()
                    d.discountedFee = discountedFee.doubleValue()
                    d.expiryDate = WebDiscountUtils.expiryDate(discount as Discount, courseClass.startDateTime)?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
                    d
                }
    }
    
    @CompileStatic(TypeCheckingMode.SKIP)
    private Money getOverridenFee(Contact contact, CourseClass courseClass) {
       Double feeOverriden =  model.contactNodes
                .find {it.contactId == contact.id.toString()}
                .enrolments.find {it.classId == courseClass.id.toString()}
                .price.feeOverriden
        
        return feeOverriden?.toMoney()
    }
    
    private Money getPayNow(CourseClass courseClass, Money amountLeftToPay) {
        Money payNow = Money.ZERO
        Instant now = new Date().toInstant()

        courseClass.paymentPlanLines.sort {it.dayOffset}.each { planLine ->
            Money amount = amountLeftToPay.min(planLine.amount)
            if (planLine.dayOffset == null) {
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


        GetDiscountForEnrolment discounts = GetDiscountForEnrolment.valueOf(classDiscounts, promotions, corporatePass, enrolmentsCount, total, contact.student,  courseClass).get()
        DiscountCourseClass chosenDiscount = discounts.chosenDiscount

        if (chosenDiscount != null) {
            Money fullPrice = price.finalPriceToPayIncTax
            price.applyDiscount(chosenDiscount)
            Money discount = fullPrice.subtract(price.finalPriceToPayIncTax)
            totalDiscount = totalDiscount.add(discount)
            setDiscountedPrice(contact, courseClass, chosenDiscount, discount, price.finalPriceToPayIncTax)
        }
        price
    }
}

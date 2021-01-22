package ish.oncourse.willow.checkout.functions

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.math.Money
import ish.oncourse.cayenne.DiscountInterface
import ish.oncourse.model.*
import ish.oncourse.services.discount.GetDiscountForEnrolment
import ish.oncourse.services.discount.WebDiscountUtils
import ish.oncourse.willow.model.checkout.CheckoutModel
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

import static ish.common.types.EntityRelationCartAction.ADD_ALLOW_REMOVAL
import static ish.common.types.EntityRelationCartAction.ADD_NO_REMOVAL

@CompileStatic
class CalculateEnrolmentsPrice {

    ObjectContext context
    College college
    Money total
    CheckoutModel model
    Map<Contact, List<CourseClass>> enrolmentsToProceed
    Map<Contact, List<Product>> productsToProceed
    CorporatePass corporatePass
    
    Money totalDiscount = Money.ZERO
    Money minPayNow = Money.ZERO
    List<EnrolmentNode> enrolmentNodes = []

    List<Discount> promotions = []

    private Tax taxOverridden
    
    CalculateEnrolmentsPrice(ObjectContext context, College college, Money total, CheckoutModel model, Map<Contact, List<CourseClass>> enrolmentsToProceed, Map<Contact, List<Product>> productsToProceed, List<String> promotionIds, CorporatePass corporatePass, Tax taxOverridden) {
        this.context = context
        this.college = college
        this.total = total
        this.model = model
        this.enrolmentsToProceed = enrolmentsToProceed
        this.productsToProceed = productsToProceed
        this.corporatePass = corporatePass
        this.taxOverridden = taxOverridden
        promotionIds.each { id ->
            this.promotions << new GetDiscount(this.context, this.college, id).get()
        }
        
    }

    CalculateEnrolmentsPrice calculate() {
        enrolmentsToProceed.each { contact, classes ->
            classes.each { courseClass ->

                Money classPrice = getOverridenFee(contact, courseClass)
                
                if (!classPrice) {
                    CalculatePrice price = new CalculatePrice(courseClass.feeExGst, Money.ZERO, taxOverridden, courseClass, BigDecimal.ONE).calculate()
                    price = applyDiscount(contact, courseClass, price)
                    classPrice = price.finalPriceToPayIncTax
                }
                Money payNow 
                if (courseClass.paymentPlanLines.empty) {
                    payNow = classPrice
                } else {
                    payNow = getPayNow(courseClass, classPrice)
                }
                minPayNow = minPayNow.add(payNow)
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
        
        DiscountCourseClass chosenDiscount
        
        GetDiscountForEnrolment discounts = GetDiscountForEnrolment.valueOf(classDiscounts, promotions, corporatePass, enrolmentsToProceed, total, contact.student,  courseClass, taxOverridden?.rate).get()
        Discount programDiscount = getProgramDiscount(contact, courseClass)
        
        if (programDiscount) {
            chosenDiscount = discounts.applicableDiscounts.find {(it.discount as Discount).id == programDiscount.id } ?: discounts.chosenDiscount
        } else {
            chosenDiscount = discounts.chosenDiscount
        }
        

        if (chosenDiscount != null) {
            Money fullPrice = price.finalPriceToPayIncTax
            price.applyDiscount(chosenDiscount)
            Money discount = fullPrice.subtract(price.finalPriceToPayIncTax)
            totalDiscount = totalDiscount.add(discount)
            setDiscountedPrice(contact, courseClass, chosenDiscount, discount, price.finalPriceToPayIncTax)
        }
        price
    }
    
    Discount getProgramDiscount(Contact contact, CourseClass courseClass) {
        
        List<Long> courseIds = enrolmentsToProceed[contact]*.course*.id.unique()
        courseIds.remove(courseClass.course.id)
        List<Long> productIds = productsToProceed[contact]*.id
        
        EntityRelation relation = ObjectSelect.query(EntityRelation)
            .where(
                    EntityRelation.FROM_ENTITY_IDENTIFIER.eq(Course.simpleName).andExp(EntityRelation.FROM_ENTITY_WILLOW_ID.in(courseIds))
                            .orExp(EntityRelation.FROM_ENTITY_IDENTIFIER.eq(Product.simpleName).andExp(EntityRelation.FROM_ENTITY_WILLOW_ID.in(productIds)))
            )
            .and(EntityRelation.TO_ENTITY_IDENTIFIER.eq(Course.simpleName))
            .and(EntityRelation.TO_ENTITY_WILLOW_ID.eq(courseClass.course.id))
            .and(EntityRelation.RELATION_TYPE.dot(EntityRelationType.DISCOUNT).isNotNull())
            .and(EntityRelation.RELATION_TYPE.dot(EntityRelationType.SHOPPING_CART).in(ADD_ALLOW_REMOVAL, ADD_NO_REMOVAL))
            .selectFirst(context)
        
        return relation?.relationType?.discount
    }
}

package ish.oncourse.willow.checkout.functions

import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.CourseClass
import ish.oncourse.model.Discount
import ish.oncourse.model.DiscountCourseClass
import ish.oncourse.model.GetDiscountForEnrolment
import ish.oncourse.model.InvoiceLine
import ish.oncourse.willow.model.checkout.CheckoutModel
import ish.util.DiscountUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

class ApplayDiscounts {

    ObjectContext context
    College college
    Money total 
    int enrolmentsCount
    CheckoutModel model
    
    List<Discount> promotions = []

    ApplayDiscounts(ObjectContext context, College college, Money total, int enrolmentsCount, CheckoutModel model) {
        this.context = context
        this.college = college
        this.total = total
        this.enrolmentsCount = enrolmentsCount
        this.model = model

        this.model.promotionIds.each { id ->
            this.promotions << new GetDiscount(this.context, this.college, id).get()
        }
        
    }

    CheckoutModel applyFor( Map<Contact, List<CourseClass>> enrolmentsToProceed) {

        enrolmentsToProceed.each { contact, classes ->
            classes.each { courseClass ->
                
                List<DiscountCourseClass> classDiscounts = ((ObjectSelect.query(DiscountCourseClass.class).
                        where(DiscountCourseClass.DISCOUNT.dot(Discount.IS_AVAILABLE_ON_WEB).isTrue()) 
                        & DiscountCourseClass.COURSE_CLASS.eq(courseClass)) 
                        & Discount.getCurrentDateFilterForDiscountCourseClass(courseClass.startDate)).
                        select(context)

                GetDiscountForEnrolment discounts = GetDiscountForEnrolment.valueOf(classDiscounts, promotions, null, enrolmentsCount, total, contact.student,  courseClass).get()
                DiscountCourseClass chosenDiscount = discounts.chosenDiscount
                
            }
            
        }


        model

        
        
    }
    
    
}

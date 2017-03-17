package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import ish.math.Money
import ish.math.MoneyType
import ish.oncourse.model.Application
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.DiscountCourseClass
import ish.oncourse.model.PaymentGatewayType
import ish.oncourse.model.Preference
import ish.oncourse.services.application.FindOfferedApplication
import ish.oncourse.services.courseclass.CheckClassAge
import ish.oncourse.services.courseclass.ClassAge
import ish.oncourse.services.discount.GetAppliedDiscounts
import ish.oncourse.services.discount.GetPossibleDiscounts
import ish.oncourse.services.discount.WebDiscountUtils
import ish.oncourse.services.preference.GetPreference
import ish.oncourse.services.preference.IsPaymentGatewayEnabled
import ish.oncourse.services.preference.PreferenceConstant
import ish.oncourse.util.FormatUtils
import ish.oncourse.willow.model.CourseClassPrice
import ish.oncourse.willow.model.Discount
import ish.oncourse.willow.service.*
import ish.oncourse.willow.model.CourseClass
import ish.oncourse.willow.model.CourseClassesParams
import ish.util.DiscountUtils
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.access.DataNode
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SelectById
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.ZoneOffset

import static ish.common.types.CourseEnrolmentType.ENROLMENT_BY_APPLICATION
import static ish.oncourse.services.preference.PreferenceConstant.PAYMENT_GATEWAY_TYPE

class CourseClassesApiServiceImpl implements CourseClassesApi {
    
    ServerRuntime cayenneRuntime

    @Inject
    CourseClassService(ServerRuntime cayenneRuntime) {
        this.cayenneRuntime = cayenneRuntime
        for(DataNode dataNode: cayenneRuntime.getDataDomain().getDataNodes()){
            dataNode.getAdapter().getExtendedTypes().registerType(new MoneyType())
        }
    }
    
    List<CourseClass> getCourseClasses(CourseClassesParams courseClassesParams) {

        ObjectContext context = cayenneRuntime.newContext()
        List<CourseClass> result = []
        List<ish.oncourse.model.Discount> promotions = []
        Contact contact

        if (courseClassesParams.contact?.id) {
            contact = SelectById.query(Contact, courseClassesParams.contact.id)
                    .prefetch(Contact.STUDENT.joint())
                    .selectOne(context)
        }

        if (courseClassesParams.promotions) {
            promotions = ObjectSelect.query(ish.oncourse.model.Discount).where(ExpressionFactory.inDbExp(ish.oncourse.model.Discount.ID_PK_COLUMN, courseClassesParams.promotions*.id)).select(context)
        }

        ObjectSelect.query(ish.oncourse.model.CourseClass)
                .where(ExpressionFactory.inDbExp(ish.oncourse.model.CourseClass.ID_PK_COLUMN, courseClassesParams.courseClassesIds))
                .prefetch(ish.oncourse.model.CourseClass.COLLEGE.joint())
                .prefetch(ish.oncourse.model.CourseClass.COURSE.joint())
                .select(cayenneRuntime.newContext())
                .each { c ->

                    boolean allowByApplication = false
                    Money overridenFee = null
            
                    if (ENROLMENT_BY_APPLICATION == c.course.enrolmentType) {
                        if (contact?.student) {
                            Application application = new FindOfferedApplication(c.course, contact.student, context)
                            if (application != null) {
                                overridenFee = application.feeOverride
                                allowByApplication = false
                            }
                        } else {
                            overridenFee = null
                            allowByApplication = true
                        }
                    }
            
                    result << new CourseClass().with {
                        id = c.id.toString()
                        start = c.startDateTime?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
                        end = c.endDateTime?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDateTime()
                        hasAvailablePlaces = hasAvailablePlaces(c)
                        isFinished = !c.cancelled && c.hasEnded()
                        isCancelled = c.cancelled
                        isAllowByApplication = allowByApplication
                        isPaymentGatewayEnabled = new IsPaymentGatewayEnabled(college: c.college).get()
                        price = new CourseClassPrice().with {
                            fee = c.feeIncGst.toBigDecimal().toString()
                            hasTax = !c.gstExempt
                            feeOverriden = overridenFee ? (c.gstExempt ? overridenFee.toBigDecimal().toString() : (overridenFee * BigDecimal.ONE.add(c.taxRate)).toBigDecimal().toString()) : null
        
                            DiscountCourseClass bestDiscount = (DiscountCourseClass) DiscountUtils.chooseDiscountForApply(GetAppliedDiscounts.valueOf(c, promotions).get(), c.feeExGst, c.taxRate)
                            
                            if (bestDiscount) {
                                appliedDiscount = new Discount().with {
                                    Money value = c.getDiscountedFeeIncTax(bestDiscount)
                                    discountedFee = value.toBigDecimal().toString()
                                    discountValue = c.feeIncGst.subtract(value).toBigDecimal().toString()
                                    title = bestDiscount.discount.name
                                    Date discountExpiryDate = WebDiscountUtils.expiryDate(bestDiscount.discount, c.startDate)
                                    if (discountExpiryDate) {
                                        title = title + " expires ${FormatUtils.getShortDateFormat(c.college.timeZone).format(discountExpiryDate)}"
                                    }
                                    it
                                }  
                            }
        
                            WebDiscountUtils.sortByDiscountValue(GetPossibleDiscounts.valueOf(c).get(), c.feeExGst, c.taxRate).each { d ->
                                possibleDiscounts << new Discount(discountedFee: d.feeIncTax.toBigDecimal().toString(), title: d.title)
                            }
                            it
                        }
                        it
                    }
        }
        result
    }
    
    private static boolean hasAvailablePlaces(ish.oncourse.model.CourseClass courseClass) {
        String  age = new GetPreference(college: courseClass.college, key: PreferenceConstant.STOP_WEB_ENROLMENTS_AGE).get()
        String type = new GetPreference(college: courseClass.college, key: PreferenceConstant.STOP_WEB_ENROLMENTS_AGE_TYPE).get()
        return courseClass.isHasAvailableEnrolmentPlaces() && new CheckClassAge().courseClass(courseClass).classAge(ClassAge.valueOf(age, type)).check()
    }
}


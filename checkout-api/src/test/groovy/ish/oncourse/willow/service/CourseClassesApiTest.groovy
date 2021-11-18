package ish.oncourse.willow.service

import ish.oncourse.util.FormatUtils
import ish.oncourse.willow.filters.RequestFilter
import ish.oncourse.willow.model.web.ContactParams
import ish.oncourse.willow.model.web.CourseClass
import ish.oncourse.willow.model.web.CourseClassesParams
import ish.oncourse.willow.model.web.PromotionParams
import ish.oncourse.willow.service.impl.CollegeService
import ish.oncourse.willow.service.impl.CourseClassesApiServiceImpl
import org.junit.Test

import java.text.SimpleDateFormat
import java.time.ZoneOffset

import static org.junit.Assert.assertEquals
/**
 * API tests for CourseClassesApi
 */
class CourseClassesApiTest extends ApiTest {

    private CourseClassesApi api

    private SimpleDateFormat formater =  new SimpleDateFormat("yyy-MM-dd HH:mm:ss")

    /**
     * Get list of CourseClasses
     * Get list of course classes based on current shopping cart state
     */
    @Test
    void getCourseClassesTest() {
        RequestFilter.ThreadLocalSiteKey.set('mammoth')
        api = new CourseClassesApiServiceImpl(cayenneService,  new CollegeService(cayenneService))
        List<CourseClass> classes = api.getCourseClasses(new CourseClassesParams().with {
            courseClassesIds=["1001","1002","1003"]
            promotions = [new PromotionParams(id: "1001"), new PromotionParams(id: "1002")]
            contact = new ContactParams(id:"1003")
            it
        })

        assertEquals(classes.size(), 3)

        def startDateTime = formater.parse("2011-10-12 00:55:59").toInstant()
                .atZone(ZoneOffset.UTC).toLocalDateTime()
        def endDateTime = formater.parse("2111-10-16 01:55:59").toInstant().
                atZone(ZoneOffset.UTC).toLocalDateTime()
        def expireDiscountDate = formater.parse("2111-09-13 12:00:00").toInstant()
                .atZone(ZoneOffset.UTC).toDate()

        String appliedDiscountTitle = "name_2" +
                " expires ${FormatUtils.getShortDateFormat(collegeService.getCollege().timeZone).format(expireDiscountDate)}"

        assertEquals(classes.get(0).hasAvailablePlaces, true)
        assertEquals(classes.get(0).isAllowByApplication, false)
        assertEquals(classes.get(0).isCancelled, false)
        assertEquals(classes.get(0).isFinished, false)
        assertEquals(classes.get(0).isPaymentGatewayEnabled, true)
        assertEquals(classes.get(0).id, "1001")
        assertEquals( classes.get(0).availableEnrolmentPlaces,4)
        assertEquals(classes.get(0).course.id, "1001")
        assertEquals(classes.get(0).course.name, "Managerial Accounting")
        assertEquals(classes.get(0).start, startDateTime)
        assertEquals(classes.get(0).end, endDateTime)
        assertEquals(classes.get(0).price.fee, 110.00, 0)
        assertEquals(classes.get(0).price.feeOverriden, null)
        assertEquals(classes.get(0).price.hasTax, true)
        assertEquals(classes.get(0).price.appliedDiscount.discountedFee, 88.00, 0)
        assertEquals(classes.get(0).price.appliedDiscount.discountValue, 22.00, 0)
        assertEquals(classes.get(0).price.appliedDiscount.id, null)
        assertEquals(classes.get(0).price.appliedDiscount.title, appliedDiscountTitle)
        assertEquals(classes.get(0).price.possibleDiscounts.size(), 1)
        assertEquals(classes.get(0).price.possibleDiscounts.get(0).discountedFee, 66.00, 0)
        assertEquals(classes.get(0).price.possibleDiscounts.get(0).title, "negative discount")
        
        assertEquals(classes.get(1).id, "1002")
        assertEquals(classes.get(1).price.feeOverriden, 220.00, 0)
        assertEquals(classes.get(1).price.hasTax, true)
        assertEquals(classes.get(1).price.appliedDiscount, null)
        assertEquals(classes.get(1).price.possibleDiscounts.size(), 0)

        assertEquals(classes.get(2).id, "1003")

    }

    @Override
    protected String getDataSetResource() {
        return 'ish/oncourse/willow/service/CourseClassesApiTest.xml'
    }
}

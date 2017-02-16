package ish.oncourse.services.discount

import ish.oncourse.model.Discount
import ish.oncourse.model.DiscountCourseClass
import org.apache.commons.lang3.time.DateUtils
import org.junit.Assert
import org.junit.Test

import static ish.oncourse.services.discount.WebDiscountUtils.filterValidDateRange
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class WebDiscountUtilsTest {

    @Test
    void test_filterValidDateRange_ValidFromIsNotNull_ValidToIsNull_ValidFromOffsetIsNull_validToOffsetIsNull() {

        DiscountCourseClass discountCourseClass = mock(DiscountCourseClass)
        when(discountCourseClass.discount).thenReturn(new Discount().with {
            it.validFrom = DateUtils.addDays(new Date(), -1)
            it.validTo = null
            it.validToOffset = null
            it.validFromOffset = null
            it
        })

        List<DiscountCourseClass> discounts = [discountCourseClass]

        Date classStartDate = DateUtils.addDays(new Date(), 1)
        List<DiscountCourseClass> result = filterValidDateRange(discounts, classStartDate)
        Assert.assertTrue(result.size() == 1)
    }

}

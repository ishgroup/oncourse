package ish.oncourse.willow.service

import ish.oncourse.willow.model.checkout.CodeResponse
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.model.web.Promotion
import ish.oncourse.willow.service.impl.CollegeService
import ish.oncourse.willow.service.impl.PromotionApiServiceImpl
import ish.oncourse.willow.filters.RequestFilter
import org.junit.Test

import javax.ws.rs.BadRequestException

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

class PromotionApiTest extends ApiTest {
    @Override
    protected String getDataSetResource() {
        'ish/oncourse/willow/service/CourseClassesApiTest.xml'
    }

    @Test
    void getPromotionTest() {

        RequestFilter.ThreadLocalXOrigin.set('mammoth.oncourse.cc')

        PromotionApi api = new PromotionApiServiceImpl(cayenneService, new CollegeService(cayenneService))

        Promotion promotion = api.getPromotion('CODE')

        assertEquals(promotion.id, "1001")
        assertEquals(promotion.code, "code")
        assertEquals(promotion.name, "name_1")

        promotion = api.getPromotion('code1')

        assertEquals(promotion.id, "1002")
        assertEquals(promotion.code, "code1")
        assertEquals(promotion.name, "name_2")
    }

    @Test
    void submitCodeTest() {

        RequestFilter.ThreadLocalXOrigin.set('mammoth.oncourse.cc')

        PromotionApi api = new PromotionApiServiceImpl(cayenneService, new CollegeService(cayenneService))
        CodeResponse response
        try {
            api.submitCode('xxxx')
            assertFalse(true)
        } catch (BadRequestException e) {
            assertTrue(e.response.entity instanceof CommonError)

            assertEquals('The code you have entered was incorrect or not available.', (e.response.entity as CommonError).message)
        }


        response = api.submitCode('voucher')

        assertNotNull(response.voucher)
        assertEquals('1002', response.voucher.id)


        response = api.submitCode('CODE')

        assertNotNull(response.promotion)
        assertEquals('1001', response.promotion.id)

    }
}

package ish.oncourse.willow.service

import ish.oncourse.willow.model.web.Promotion
import ish.oncourse.willow.service.impl.CollegeService
import ish.oncourse.willow.service.impl.PromotionApiServiceImpl
import ish.oncourse.willow.filters.RequestFilter
import org.junit.Test

import static org.junit.Assert.assertEquals

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
}

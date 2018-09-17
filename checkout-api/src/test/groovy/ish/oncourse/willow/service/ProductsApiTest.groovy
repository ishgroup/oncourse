package ish.oncourse.willow.service

import ish.oncourse.willow.model.web.Product
import ish.oncourse.willow.model.web.ProductsParams
import ish.oncourse.willow.service.impl.ProductsApiServiceImpl
import org.junit.Test
import ish.oncourse.willow.service.impl.CollegeService
import ish.oncourse.willow.filters.RequestFilter

import static org.junit.Assert.assertEquals

class ProductsApiTest extends ApiTest {
    @Test
    void getProductsTest() {
        RequestFilter.ThreadLocalSiteKey.set('mammoth')

        ProductsApi api = new ProductsApiServiceImpl(cayenneService, new CollegeService(cayenneService))

        List<Product> products = api.getProducts(new ProductsParams(productsIds: ["7", "8", "9", "10", "11", "12", "13"]))
        assertEquals(products.size(), 7)
        assertEquals(products.get(0).id, '7')
        assertEquals(products.get(0).code, '23456')
        assertEquals(products.get(0).name, 'my test membership product part 5')
        assertEquals(products.get(0).isPaymentGatewayEnabled, true)
        assertEquals(products.get(0).canBuy, true)
        assertEquals(products.get(0).description, '11$ product double edition')
        
        assertEquals(products.get(1).id, '8')
        assertEquals(products.get(1).canBuy, false)


        assertEquals(products.get(2).id, '9')
        assertEquals(products.get(2).canBuy, false)


    }

    @Override
    protected String getDataSetResource() {
        'ish/oncourse/willow/service/CourseClassesApiTest.xml'
    }
}

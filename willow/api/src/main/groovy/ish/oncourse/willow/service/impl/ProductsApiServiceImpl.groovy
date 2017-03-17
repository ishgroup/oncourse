package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import ish.oncourse.services.preference.IsPaymentGatewayEnabled
import ish.oncourse.willow.service.*
import ish.oncourse.willow.model.Product
import ish.oncourse.willow.model.ProductsParams
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect


class ProductsApiServiceImpl implements ProductsApi {
    
    ServerRuntime cayenneRuntime

    @Inject
    ProductsApiServiceImpl(ServerRuntime cayenneRuntime) {
        this.cayenneRuntime = cayenneRuntime
    }
    
    List<Product> getProducts(ProductsParams productsParams) {

        ObjectContext context = cayenneRuntime.newContext()
        List<Product> result = []

        ObjectSelect.query(ish.oncourse.model.Product)
            .where(ExpressionFactory.inDbExp(ish.oncourse.model.Product.ID_PK_COLUMN, productsParams.productsIds))
            .select(context)
            .each { p ->
                result << new Product().with {
                    id = p.id
                    name = p.name
                    code = p.sku
                    canBuy = p.isOnSale & p.isWebVisible
                    isPaymentGatewayEnabled = new IsPaymentGatewayEnabled(college: p.college).get()
                    it
                }
            }
        
        return result
    }
}


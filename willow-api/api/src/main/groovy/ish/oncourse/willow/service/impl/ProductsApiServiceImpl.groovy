package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.model.Contact
import ish.oncourse.services.preference.IsPaymentGatewayEnabled
import ish.oncourse.willow.service.*
import ish.oncourse.willow.model.Product
import ish.oncourse.willow.model.ProductsParams
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy

@CompileStatic
class ProductsApiServiceImpl implements ProductsApi {
    
    private ServerRuntime cayenneRuntime

    @Inject
    ProductsApiServiceImpl(ServerRuntime cayenneRuntime) {
        this.cayenneRuntime = cayenneRuntime
    }
    
    List<Product> getProducts(ProductsParams productsParams) {

        ObjectContext context = cayenneRuntime.newContext()
        List<Product> result = []

        ObjectSelect.query(ish.oncourse.model.Product)
            .where(ExpressionFactory.inDbExp(ish.oncourse.model.Product.ID_PK_COLUMN, productsParams.productsIds))
                .cacheStrategy(QueryCacheStrategy.SHARED_CACHE)
                .cacheGroups(ish.oncourse.model.Product.class.simpleName)
                .select(context)
            .each { p ->
                result << new Product().with { pr ->
                    pr.id = p.id.toString()
                    pr.name = p.name
                    pr.code = p.sku
                    pr.canBuy = p.isOnSale & p.isWebVisible
                    pr.description = p.description
                    pr.isPaymentGatewayEnabled = new IsPaymentGatewayEnabled(p.college, p.objectContext).get()
                    pr
                }
            }
        
        return result
    }
}


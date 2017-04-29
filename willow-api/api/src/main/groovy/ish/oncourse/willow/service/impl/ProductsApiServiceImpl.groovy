package ish.oncourse.willow.service.impl

import com.google.inject.Inject
import groovy.transform.CompileStatic
import ish.oncourse.services.preference.IsPaymentGatewayEnabled
import ish.oncourse.willow.model.web.Product
import ish.oncourse.willow.model.web.ProductsParams
import ish.oncourse.willow.service.ProductsApi
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.configuration.server.ServerRuntime
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.QueryCacheStrategy

@CompileStatic
class ProductsApiServiceImpl implements ProductsApi {
    
    private ServerRuntime cayenneRuntime
    private CollegeService collegeService

    @Inject
    ProductsApiServiceImpl(ServerRuntime cayenneRuntime, CollegeService collegeService) {
        this.cayenneRuntime = cayenneRuntime
        this.collegeService = collegeService

    }
    
    List<Product> getProducts(ProductsParams productsParams) {

        ObjectContext context = cayenneRuntime.newContext()
        List<Product> result = []

        (ObjectSelect.query(ish.oncourse.model.Product)
                .where(ExpressionFactory.inDbExp(ish.oncourse.model.Product.ID_PK_COLUMN, productsParams.productsIds)) & ish.oncourse.model.Product.COLLEGE.eq(collegeService.college))
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


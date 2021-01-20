package ish.oncourse.willow.checkout.functions

import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.Product
import ish.oncourse.willow.model.checkout.Article
import ish.oncourse.willow.model.checkout.Membership
import ish.oncourse.willow.model.checkout.Voucher
import ish.oncourse.willow.model.checkout.request.ProductContainer
import org.apache.cayenne.ObjectContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory 

class ProcessProducts {

    final static  Logger logger = LoggerFactory.getLogger(ProcessProducts.class)

    ObjectContext context
    Contact contact
    College college
    List<ProductContainer> products

    List<Article> articles = []
    List<Membership> memberships = []
    List<Voucher> vouchers = []

    List<Product> productsToPurchase = []


    ProcessProducts(ObjectContext context, Contact contact, College college, List<ProductContainer> products) {
        this.context = context
        this.contact = contact
        this.college = college
        this.products = products
    }

    ProcessProducts process() {
        products.each { p ->
            ProcessProduct processProduct = new ProcessProduct(context, contact, college, p.productId, p.quantity, null, null).process()
            productsToPurchase << processProduct.persistentProduct
            processProduct.article && articles << processProduct.article
            processProduct.membership && memberships << processProduct.membership
            processProduct.voucher && vouchers << processProduct.voucher
        }
        
        this
    }
    
}

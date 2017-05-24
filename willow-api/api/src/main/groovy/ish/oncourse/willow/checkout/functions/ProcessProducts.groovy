package ish.oncourse.willow.checkout.functions

import ish.common.types.ProductType
import ish.common.types.TypesUtil
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.InvoiceLine
import ish.oncourse.model.MembershipProduct
import ish.oncourse.model.VoucherProduct
import ish.oncourse.willow.model.checkout.Article
import ish.oncourse.willow.model.checkout.Membership
import ish.oncourse.willow.model.checkout.Voucher
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.model.Product
import ish.util.InvoiceUtil
import org.apache.cayenne.ObjectContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

import static ish.common.types.ProductType.ARTICLE
import static ish.common.types.ProductType.MEMBERSHIP
import static ish.common.types.ProductType.VOUCHER

class ProcessProducts {

    final static  Logger logger = LoggerFactory.getLogger(ProcessClasses.class)

    ObjectContext context
    Contact contact
    College college
    List<String> productIds
    String payerId 

    List<Article> articles = []
    List<Membership> memberships = []
    List<Voucher> vouchers = []
    
    ProcessProducts(ObjectContext context, Contact contact, College college, List<String> productIds,  String payerId) {
        this.context = context
        this.contact = contact
        this.college = college
        this.productIds = productIds
        this.payerId = payerId
    }

    ProcessProducts process() {

        if (productIds.unique().size() < productIds.size()) {
            logger.error("product list contains duplicate entries: $productIds")
            throw new BadRequestException(Response.status(400).entity(new CommonError(message: 'product list contains duplicate entries')).build())
        }
        
        productIds.each { id ->
           ProcessProduct processProduct = new ProcessProduct(context, contact, college, id, payerId)
            processProduct.article && articles << processProduct.article
            processProduct.membership && memberships << processProduct.membership
            processProduct.voucher && vouchers << processProduct.voucher
        }
        
        this
    }
    
}

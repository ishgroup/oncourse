package ish.oncourse.willow.checkout.persistent

import ish.common.types.ConfirmationStatus
import ish.common.types.ProductStatus
import ish.oncourse.model.Article
import ish.oncourse.model.ArticleProduct
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.Invoice
import ish.oncourse.model.InvoiceLine
import ish.oncourse.model.Tax
import ish.oncourse.willow.checkout.functions.GetProduct
import ish.oncourse.willow.checkout.payment.ProductItemInvoiceLine
import org.apache.cayenne.ObjectContext

class CreateArticle {

    private ObjectContext context
    private College college
    private ish.oncourse.willow.model.checkout.Article a
    private Contact contact
    private Invoice invoice
    private ProductStatus status
    private Tax taxOverride

    CreateArticle(ObjectContext context, College college, ish.oncourse.willow.model.checkout.Article a, Contact contact, Invoice invoice, ProductStatus status, Tax taxOverride) {
        this.context = context
        this.college = college
        this.a = a
        this.contact = contact
        this.invoice = invoice
        this.status = status
        this.taxOverride = taxOverride
    }
    
    void create() {
        ArticleProduct ap = new GetProduct(context, college, a.productId).get() as ArticleProduct
        Article article = context.newObject(Article)
        article.college = college
        article.contact = contact
        article.setProduct(ap)
        article.status = status
        article.confirmationStatus = ConfirmationStatus.NOT_SENT
        InvoiceLine invoiceLine = new ProductItemInvoiceLine(article, contact, article.product.priceExTax, taxOverride).create()
        invoiceLine.invoice = invoice
    }
}

package ish.oncourse.willow.checkout.persistent

import ish.common.types.ProductStatus
import ish.math.Money
import ish.oncourse.model.Article
import ish.oncourse.model.ArticleProduct
import ish.oncourse.model.College
import ish.oncourse.model.Contact
import ish.oncourse.model.Invoice
import ish.oncourse.model.InvoiceLine
import ish.oncourse.model.ProductItem
import ish.oncourse.model.Tax
import ish.oncourse.willow.checkout.functions.GetProduct
import ish.oncourse.willow.checkout.payment.ProductsItemInvoiceLine
import ish.oncourse.willow.functions.field.FieldHelper
import ish.oncourse.willow.model.field.FieldHeading
import org.apache.cayenne.ObjectContext

import static ish.oncourse.willow.functions.field.FieldHelper.separateFields

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
        List<FieldHeading> separatedFieldHeadings = separateFields(a.fieldHeadings, a.quantity)

        List<ProductItem> articles = new ArrayList<>()
        (1..a.quantity).each {
            Article article = createArticle(context, college, contact, ap, status)
            if (!separatedFieldHeadings.empty) {
                FieldHelper.valueOf([] as Set).populateFields(Arrays.asList(separatedFieldHeadings.get(it - 1)), article)
            }
            articles << article
        }
        InvoiceLine invoiceLine = new ProductsItemInvoiceLine(context, articles, contact, ap.priceExTax, taxOverride).create()
        invoiceLine.invoice = invoice
    }

    private Article createArticle(ObjectContext context, College college, Contact contact, ArticleProduct ap, ProductStatus status) {
        Article article = context.newObject(Article)
        article.college = college
        article.contact = contact
        article.product = ap
        article.status = status
        article
    }
}

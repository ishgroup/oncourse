package ish.oncourse.willow.checkout.functions

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.Product
import ish.oncourse.model.Tax
import ish.oncourse.willow.model.checkout.Article
import org.apache.cayenne.ObjectContext

class ValidateArticle extends Validate<Article> {

    ValidateArticle(ObjectContext context, College college) {
        super(context, college)
    }


    @CompileStatic(TypeCheckingMode.SKIP)
    Validate validate(Article a) {
        Tax tax = new GetContact(context, college, a.contactId).get().taxOverride
        
        Product p = new GetProduct(context, college, a.productId).get()

        Money productPrice = new CalculatePrice(p.priceExTax, Money.ZERO, tax ? tax.rate : p.taxRate, p.taxAdjustment, new BigDecimal(a.quantity)).calculate().finalPriceToPayIncTax
        if (productPrice != a.total.toMoney()) {
            errors << "Product price is wrong".toString()
        }
        this
    }
}

package ish.oncourse.util;

import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.cayenne.query.SelectQuery;

import java.util.List;

public class InvoiceUtils {

    /**
     * sums all invoice amount owing for a payer
     *
     * @param contact to be analysed. contact should be persisted.
     * @return Money sum of amount owing
     */
    public static Money amountOwingForPayer(Contact contact) {
        Expression exp = ExpressionFactory.matchExp(Invoice.CONTACT_PROPERTY, contact);
        /**
         * we use query with Strategy.LOCAL_CACHE_REFRESH to be sure that
         * these invoices will be loaded from database and amountOwing will have actual value.
         */
        SelectQuery q = new SelectQuery(Invoice.class, exp);
        q.setCacheStrategy(QueryCacheStrategy.LOCAL_CACHE_REFRESH);
        List<Invoice> invoices = contact.getObjectContext().performQuery(q);
        Money result = Money.ZERO;
        for (Invoice invoice : invoices) {
            result = result.add(invoice.getAmountOwing()) ;
        }
        return result;
    }
}

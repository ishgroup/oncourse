package ish.oncourse.admin.billing.get

import ish.oncourse.admin.billing.BillingValue
import ish.oncourse.model.LicenseFee
import ish.oncourse.model.WebSite
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.SQLSelect

import java.text.MessageFormat

import static ish.oncourse.admin.billing.LicenseCode.ccWeb
import static ish.oncourse.admin.services.billing.StockCodes.WEB_CC
import static ish.oncourse.model.auto._LicenseFee.*

/**
 *
 * akoiro - 2/24/16.
 */
class GetWebCCBillingValue extends AbstractGetter<BillingValue> {
    private static final String SQL = 'select count(p.id) as count from PaymentIn p ' +
            'join PaymentInLine pil on p.id = pil.paymentInId join ' +
            'Invoice i on pil.invoiceId = i.id ' +
            'where p.collegeId = #bind($collegeId) ' +
            'and i.webSiteId = #bind($webSiteId) ' +
            'and p.created >= #bind($from) ' +
            'and p.created <= #bind($to) ' +
            'and p.source = #bind($source) ' +
            'and p.type = 2 ' +
            'and p.status in (3, 6)';

    def WebSite webSite

    @Override
    protected BillingValue innerGet() {
        def quantity = (Long) SQLSelect.dataRowQuery(SQL).params([
                collegeId: context.college.id,
                webSiteId: webSite.id,
                from     : context.from,
                to       : context.to,
                source   : 'W'
        ]).selectFirst(context.context).values().first()

        def description = MessageFormat.format(ccWeb.descTemplate, webSite.getName());

        return new BillingValue(code: WEB_CC.productionCode,
                description: description,
                quantity: quantity,
                unitPrice: licenseFee.fee)
    }

    @Override
    protected init() {
        licenseFee = ObjectSelect.query(LicenseFee.class).where(COLLEGE.eq(context.college)
                .andExp(WEB_SITE.eq(webSite))
                .andExp(KEY_CODE.eq(ccWeb.dbValue)))
                .selectFirst(context.context)
    }
}

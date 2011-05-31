package ish.oncourse.services.payment;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.PaymentTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;

public class PaymentService implements IPaymentService {

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@Override
	public PaymentIn paymentInByAngelId(Long angelId) {
		SelectQuery q = new SelectQuery(PaymentIn.class);
		q.andQualifier(ExpressionFactory.matchExp(PaymentIn.ANGEL_ID_PROPERTY, angelId));
		q.andQualifier(ExpressionFactory.matchExp(PaymentIn.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()));
		return (PaymentIn) Cayenne.objectForQuery(cayenneService.sharedContext(), q);
	}

	@Override
	public PaymentOut paymentOutByAngelId(Long angelId) {
		SelectQuery q = new SelectQuery(PaymentOut.class);
		q.andQualifier(ExpressionFactory.matchExp(PaymentOut.ANGEL_ID_PROPERTY, angelId));
		q.andQualifier(ExpressionFactory.matchExp(PaymentOut.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()));
		return (PaymentOut) Cayenne.objectForQuery(cayenneService.sharedContext(), q);
	}

	@Override
	public PaymentTransaction paymentTransactionByReferenceId(String referenceId) {
		Expression expr = ExpressionFactory.matchExp(PaymentTransaction.SESSION_ID_PROPERTY, referenceId);
		SelectQuery q = new SelectQuery(PaymentTransaction.class, expr);
		return (PaymentTransaction) Cayenne.objectForQuery(cayenneService.sharedContext(), q);
	}
}

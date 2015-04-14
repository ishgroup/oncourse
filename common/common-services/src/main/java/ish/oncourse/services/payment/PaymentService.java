package ish.oncourse.services.payment;

import ish.common.types.PaymentStatus;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.PaymentTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class PaymentService implements IPaymentService {

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@Override
	public PaymentIn paymentInByWillowId(Long willowId) {
		SelectQuery q = new SelectQuery(PaymentIn.class);
		q.andQualifier(ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, willowId));
		q.andQualifier(ExpressionFactory.matchExp(PaymentIn.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()));
		return (PaymentIn) Cayenne.objectForQuery(cayenneService.sharedContext(), q);
	}

	/**
	 * @see IPaymentService#paymentInByAngelId(Long)
	 */
	@Override
	public PaymentIn paymentInByAngelId(Long angelId) {
		SelectQuery q = new SelectQuery(PaymentIn.class);
		q.andQualifier(ExpressionFactory.matchExp(PaymentIn.ANGEL_ID_PROPERTY, angelId));
		q.andQualifier(ExpressionFactory.matchExp(PaymentIn.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()));
		return (PaymentIn) Cayenne.objectForQuery(cayenneService.sharedContext(), q);
	}

	/**
	 * @see IPaymentService#paymentOutByAngelId(String)
	 */
	@Override
	public PaymentOut paymentOutByAngelId(Long angelId) {
		SelectQuery q = new SelectQuery(PaymentOut.class);
		q.andQualifier(ExpressionFactory.matchExp(PaymentOut.ANGEL_ID_PROPERTY, angelId));
		q.andQualifier(ExpressionFactory.matchExp(PaymentOut.COLLEGE_PROPERTY, webSiteService.getCurrentCollege()));
		return (PaymentOut) Cayenne.objectForQuery(cayenneService.sharedContext(), q);
	}

	/**
	 * @see IPaymentService#currentPaymentInBySessionId(String)
	 */
	@Override
	public PaymentIn currentPaymentInBySessionId(String sessionId) {

		Expression expr = ExpressionFactory.matchExp(PaymentIn.SESSION_ID_PROPERTY, sessionId);
		expr = expr.andExp(ExpressionFactory.inExp(PaymentIn.STATUS_PROPERTY, PaymentStatus.IN_TRANSACTION));

		SelectQuery q = new SelectQuery(PaymentIn.class, expr);
		q.addPrefetch(PaymentIn.PAYMENT_IN_LINES_PROPERTY);
		q.addPrefetch(PaymentIn.PAYMENT_TRANSACTIONS_PROPERTY);

		return (PaymentIn) Cayenne.objectForQuery(cayenneService.newContext(), q);
	}

	/**
	 * @see IPaymentService#getPaymentsBySessionId(String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PaymentIn> getPaymentsBySessionId(String sessionId) {
		SelectQuery q = new SelectQuery(PaymentIn.class, ExpressionFactory.matchExp(PaymentIn.SESSION_ID_PROPERTY, sessionId));
		q.addPrefetch(PaymentIn.PAYMENT_IN_LINES_PROPERTY);
		q.addPrefetch(PaymentIn.PAYMENT_TRANSACTIONS_PROPERTY);
		return cayenneService.newContext().performQuery(q);
	}

	/**
	 * @see IPaymentService#isProcessedByGateway(PaymentIn)
	 */
	@Override
	public boolean isProcessedByGateway(PaymentIn payment) {
		return ExpressionFactory.noMatchExp(PaymentTransaction.IS_FINALISED_PROPERTY, true)
				.filterObjects(payment.getPaymentTransactions()).isEmpty();
	}
	
	
}

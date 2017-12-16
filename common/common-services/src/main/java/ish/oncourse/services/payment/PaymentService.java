package ish.oncourse.services.payment;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.PaymentTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.services.site.IWebSiteService;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

public class PaymentService implements IPaymentService {

	@Inject
	private ICayenneService cayenneService;

	@Inject
	private IWebSiteService webSiteService;

	@Override
	public PaymentIn paymentInByWillowId(Long willowId) {
		return ObjectSelect.query(PaymentIn.class)
				.where(ExpressionFactory.matchDbExp(PaymentIn.ID_PK_COLUMN, willowId)
						.andExp(PaymentIn.COLLEGE.eq(webSiteService.getCurrentCollege()))).selectOne(cayenneService.newContext());
	}

	/**
	 * @see IPaymentService#paymentInByAngelId(Long)
	 */
	@Override
	public PaymentIn paymentInByAngelId(Long angelId) {
		return ObjectSelect.query(PaymentIn.class)
				.where(PaymentIn.ANGEL_ID.eq(angelId).andExp(PaymentIn.COLLEGE.eq(webSiteService.getCurrentCollege())))
				.selectOne(cayenneService.newContext());
	}

	/**
	 * @see IPaymentService#paymentOutByAngelId(Long)
	 */
	@Override
	public PaymentOut paymentOutByAngelId(Long angelId) {
		return ObjectSelect.query(PaymentOut.class)
				.where(PaymentIn.ANGEL_ID.eq(angelId).andExp(PaymentOut.COLLEGE.eq(webSiteService.getCurrentCollege())))
				.selectOne(cayenneService.newContext());
	}

	/**
	 * @see IPaymentService#getPaymentsBySessionId(String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PaymentIn> getPaymentsBySessionId(String sessionId) {
		return ObjectSelect.query(PaymentIn.class)
				.where(PaymentIn.SESSION_ID.eq(sessionId).andExp(PaymentOut.COLLEGE.eq(webSiteService.getCurrentCollege())))
				.prefetch(PaymentIn.PAYMENT_IN_LINES.joint())
				.prefetch(PaymentIn.PAYMENT_TRANSACTIONS.joint())
				.select(cayenneService.newContext());
	}

	/**
	 * @see IPaymentService#isProcessedByGateway(PaymentIn)
	 */
	@Override
	public boolean isProcessedByGateway(PaymentIn payment) {
		return PaymentTransaction.IS_FINALISED.ne(true)
				.filterObjects(payment.getPaymentTransactions()).isEmpty();
	}
	
	
}

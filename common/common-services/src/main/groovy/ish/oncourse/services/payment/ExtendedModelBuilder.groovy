package ish.oncourse.services.payment

import groovy.transform.CompileStatic
import ish.common.types.PaymentStatus
import ish.oncourse.model.PaymentIn
import ish.oncourse.util.payment.PaymentInModel
import ish.oncourse.util.payment.PaymentInModelFromSessionIdBuilder
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect

import static ish.common.types.PaymentStatus.STATUSES_FAILED


@CompileStatic
class ExtendedModelBuilder  {
	
	protected ObjectContext context
	protected String sessionId
	private ExtendedModel model
	
	def ExtendedModel build() {
		PaymentInModel simpleModel = PaymentInModelFromSessionIdBuilder.valueOf(sessionId, context).build().model
		model = new ExtendedModel()
		model.paymentIn = simpleModel.paymentIn
		model.invoices.addAll(simpleModel.invoices)
		model.enrolments.addAll(simpleModel.enrolments)
		model.voucherPayments.addAll(simpleModel.voucherPayments)

		model.successPayment = ObjectSelect.query(PaymentIn).where(PaymentIn.SESSION_ID.eq(sessionId)).and(PaymentIn.STATUS.eq(PaymentStatus.SUCCESS)).selectOne(context)
		model.failedPayments = ObjectSelect.query(PaymentIn).where(PaymentIn.SESSION_ID.eq(sessionId)).
				and(PaymentIn.STATUS.in(STATUSES_FAILED)).select(context)
		return model
	}

}

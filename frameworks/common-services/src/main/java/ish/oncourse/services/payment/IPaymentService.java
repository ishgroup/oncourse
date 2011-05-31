package ish.oncourse.services.payment;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.model.PaymentTransaction;

public interface IPaymentService {
	PaymentIn paymentInByAngelId(Long angelId);
	PaymentOut paymentOutByAngelId(Long angelId);
	PaymentTransaction paymentTransactionByReferenceId(String referenceId);
}

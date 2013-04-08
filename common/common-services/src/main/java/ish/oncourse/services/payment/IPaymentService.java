package ish.oncourse.services.payment;

import java.util.List;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;


public interface IPaymentService {
	
	/**
	 * Finds payment in object by willow id.
	 * @param angelId willow id.
	 * @return payment in
	 */
	PaymentIn paymentInByWillowId(Long willowId);
	
	/**
	 * Finds payment in object by angel id.
	 * @param angelId angel id.
	 * @return payment in
	 */
	PaymentIn paymentInByAngelId(Long angelId);

	/**
	 * Finds payment out object by angel id.
	 * @param angelId angel id.
	 * @return payment out
	 */
	PaymentOut paymentOutByAngelId(Long angelId);

	/**
	 * Finds current payment, which means statuses IN_TRANSACTION required.
	 * @param sessionId sesion id
	 * @return payment in
	 */
	PaymentIn currentPaymentInBySessionId(String sessionId);
	
	/**
	 * Finds payment in objects by session id.
	 * @param sessionId session id
	 * @return list of payment in
	 */
	List<PaymentIn> getPaymentsBySessionId(String sessionId);
	
	/**
	 * Determines if payment processing by gateway was finalized. Returns true for payments processed by
	 * gateway and payments which are not meant to be processed using gateway.
	 * 
	 * @param payment
	 * @return
	 */
	boolean isProcessedByGateway(PaymentIn payment);
}

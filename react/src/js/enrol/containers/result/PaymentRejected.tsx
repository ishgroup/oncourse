import React from "react";

interface Props {

}

class PaymentRejected extends React.Component<Props, any> {
	render() {
		return (
			<div>
				<h2>Enrolment <span>»</span> Payment rejected</h2>
				<p>Your transaction with ish onCourse could not be completed because your credit card payment could not be authorised. Your credit card has not been charged.</p>
				<p>Please check your credit card details or credit balance and try again. In particular, check the CVV and expiry date have been entered correctly.</p>
				<p>
					<a title="Proceed with this enrolment" href="/enrol/payment">
						Try again with another card or reenter details
					</a>{" "}Or{" "}<a href="/enrol/payment.paymentresult:abandon">Abandon</a>{" "}this enrolment.
				</p>
			</div>
		);
	}
}

export default PaymentRejected;
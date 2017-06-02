import React from "react";

export class PaymentInProgress extends React.Component<any, any> {
	render() {
		return (
			<div>
				<h2>Transaction <span>»</span> In progress</h2>
				<p><img alt="processing" height="32" width="32" src="/s/img/loading.gif" /></p>

				<h3>Please Wait!</h3>
				<p>Your transaction is being processed. This will take a few seconds.</p>
				<p><strong>Do not close this window.</strong></p>
				<p>This page will update automatically, or <a href="#/enrol/payment">click here</a> to check the payment status now.</p>
			</div>
		);
	}
}

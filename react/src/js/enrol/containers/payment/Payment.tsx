import React from "react";
import AmountComp from "../../components/AmountComp";
import PaymentForm from "./components/PaymentForm";
import {Amount} from "../../../model/checkout/Amount";
import {Contact} from "../../../model/web/Contact";

interface Props {
	contacts: Contact[];
	amount: Amount;
}

class Payment extends React.Component<Props, any> {
	onPaymentFormSubmit = (data, dispatch, props) => {}

	render() {
		const {amount, contacts} = this.props;

		return (
			<div>
				<div className="row">
					<div className="col-xs-24">
						<div className="amount-container">
							<AmountComp amount={amount}/>
						</div>
					</div>
				</div>

				<PaymentForm amount={amount} contacts={contacts} onSubmit={this.onPaymentFormSubmit} />
			</div>
		)
	}
}

export default Payment;
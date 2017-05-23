import React from "react";
import {Amount} from "../../../model/checkout/Amount";
import AmountComp from "../../components/AmountComp";
import PaymentForm from "./components/PaymentForm";
import {Props as ContactProps} from "../summary/components/ContactComp";

interface Props {
	contacts: ContactProps[];
	amount: Amount;
}

class Payment extends React.Component<Props, any> {
	onPaymentFormSubmit = (data, dispatch, props) => {
		console.log("onPaymentFormSubmit");
		console.log(data);
		console.log(props);
	}

	render() {
		const {amount, contacts} = this.props;

		console.log(amount);
		console.log(contacts);

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
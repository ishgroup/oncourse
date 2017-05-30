import React from "react";
import {Field, reduxForm, FormProps, DataShape} from "redux-form";
import classNames from "classnames";
import {Amount} from "../../../../model/checkout/Amount";
import CreditCardComp from "./CreditCardComp";
import CorporatePassComp from "./CorporatePassComp";
import {Contact} from "../../../../model/web/Contact";
import {Conditions} from "./Conditions";

/**
 * @Deprecated will be remove, now it is used only as example
 */

interface Props extends FormProps<DataShape, any, any> {
	contacts: Contact[];
	amount: Amount;
	onSubmit: (data, dispatch, props) => any;
}

const NAME = "PaymentForm";

class PaymentForm extends React.Component<Props, any> {

	constructor(props) {
		super(props);
	}

	componentWillMount() {
		this.state = {
			selectedPayer: 0,
			currentForm: "credit-card"
		}
	}

	paymentTabOnClick = (e) => {
		e.preventDefault();
		const tabPart = e.target.href.split("#");
		const currentTab = tabPart[tabPart.length - 1];

		this.setState({
			currentForm: currentTab
		});
	}

	render() {
		const {handleSubmit, contacts, amount} = this.props;
		return (
			<form onSubmit={handleSubmit} id="paymentform">
				<div id="tabable-container">
					<PaymentFormNav paymentTabOnClick={this.paymentTabOnClick} currentForm={this.state.currentForm} />
					<div className="tab-content">
						<CreditCardComp contacts={contacts} amount={amount} />
						<CorporatePassComp currentForm={this.state.currentForm} />
					</div>
				</div>
				<Conditions/>

				<div className="form-controls enrolmentsSelected">
					<input value="Confirm" className="btn btn-primary" id="paymentSubmit" name="paymentSubmit" type="submit" />
				</div>
			</form>
		)
	}
}

const PaymentFormNav = (props) => {
	const {paymentTabOnClick, currentForm} = props;

	return (
		<ul className="nav">
			<li className={classNames("first", { "active": currentForm === "credit-card" })}>
				<a href="#credit-card" onClick={paymentTabOnClick.bind(this)}>Credit card</a>
			</li>
			<li className={classNames({ "active": currentForm === "corporate-pass" })}>
				<a href="#corporate-pass" onClick={paymentTabOnClick.bind(this)}>CorporatePass</a>
			</li>
		</ul>
	)
}

const Container = reduxForm({
	form: NAME,
	//validate: validate,
	onSubmitSuccess: (result, dispatch, props: any) => {
		//dispatch({type: DispatchRequest});
	},
	onSubmitFail: (errors, dispatch, submitError, props) => {
		//dispatch(showErrors(submitError, NAME));
	}
})(PaymentForm);

export default Container;
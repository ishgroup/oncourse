import React from "react";
import classNames from "classnames";
import {Field} from "redux-form";
import SelectField from "../../../../components/form-new/SelectField";
import TextField from "../../../../components/form-new/TextField";
import RadioGroup from "../../../../components/form-new/RadioGroup";
import {Amount} from "../../../../model/checkout/Amount";
import {Contact} from "../../../../model/web/Contact";

interface Props {
	contacts: Contact[];
	amount: Amount;
	currentForm: string;
	selectedPayer: number;
	payerHandleChange: (index: number) => void;
}

class CreditCardComp extends React.Component<Props, any> {

	constructor(props) {
		super(props);
	}

	render() {
		const {currentForm, contacts, amount, selectedPayer, payerHandleChange} = this.props;
		const payerList = PayerList({contacts: contacts, payerHandleChange: payerHandleChange, selectedPayer: selectedPayer});

		return (
			<div id="credit-card" className={classNames("single-tab", { "active": currentForm === "credit-card" })}>
				<div id="paymentEditor">
					<div className="header-content">
						<div className="header">
							<h1>Secure credit card payment</h1>
							<span>This is a secure SSL encrypted payment.</span>
						</div>
					</div>

					<div className="enrolmentsSelected">
						<fieldset>
							<div className="form-details">
								<p>
									<label>Pay now</label>
									<span id="cardtotalstring">
										${ amount.payNow } <img alt="visa card and master card" src="/s/img/visa-mastercard.png" />
									</span>
								</p>

								<div className="clearfix form-group">
									<label htmlFor="contact">
										Payer<em title="This field is required">*</em>
										<small>(issue invoice to)</small>
									</label>

									<div className="select-payer">
										<Field component={RadioGroup} name="payer" required={true} items={payerList} value={payerList[0].key} />
									</div>
								</div>

								<div className="payer-selection">
									<a className="button" href="#">Choose a different payer</a>
									<ul className="new-payer-option">
										<li id="new-person">
											<a href="#">a person</a>
										</li>
										<li id="new-company">
											<a href="#">a business</a>
										</li>
									</ul>
								</div>

								<Field component={TextField} maxLength={ 80 } className="input-fixed " autoComplete="off" name="creditCardName" label="Name on Card" type="text" required={true} />

								<Field component={TextField} maxLength={ 80 } className="input-fixed " autoComplete="off" name="creditCardNumber" label="Number" type="text" required={true} />

								<Field component={TextField} maxLength={ 80 } className="input-fixed " autoComplete="off" name="creditCardCVV" label="CVV" type="text" required={true}>
									<img className="vcc-card-image" alt="CVV" src="/s/img/cvv-image.png" />
									<a className="nyromodal" href="/enrol/ui/cvv?wrap=false" id="cvvLink">What is CVV?</a>
								</Field>

								<div className="clearfix form-group">
									<label>
										Expiry<em title="This field is required">*</em>
									</label>
									<span className="valid input-select-small">
										<Field component={SelectField} name="expiryMonth" required={true} loadOptions={getExpiryMonths} />
										/
										<Field component={SelectField} name="expiryYear" required={true} loadOptions={() => getExpiryYear()} />
									</span>
								</div>
							</div>
						</fieldset>
					</div>
				</div>
			</div>
		)
	}
}

const PayerList = (props) => {
	let payerList = [];
	props.contacts.map((payer, index) => {
		return payerList.push({key: payer.contact.id, value: payer.contact.firstName + " " + payer.contact.lastName });
	});
	return payerList;
}

const getExpiryMonths = () => {
	let months = [];
	for(let i=1; i<=12; i++) {
		let month = (i < 10 ? "0" + i : i);
		months.push({ key: month, value: month });
	}
	return Promise.resolve(months);
}

const getExpiryYear = (from = 2017, to = 2031) => {
	let years = [];
	for(let i=from; i<=to; i++) {
		years.push({ key: i, value: i });
	}
	return Promise.resolve(years);
}

export default CreditCardComp;
import React from "react";
import classNames from "classnames";
import {Amount} from "../../../../model/checkout/Amount";
import {Props as ContactProps} from "../../summary/components/ContactComp";
import {Field} from "redux-form";
import SelectField from "../../../../components/form-new/SelectField";

interface Props {
	contacts: ContactProps[];
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

								<p>
									<label htmlFor="contact">
										Payer<em title="This field is required">*</em>
										<small>(issue invoice to)</small>
									</label>

									<span className="select-payer">
										{ PayerList({contacts: contacts, payerHandleChange: payerHandleChange, selectedPayer: selectedPayer}) }
									</span>
								</p>

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

								<p>
									<label htmlFor="creditCardName">
										Name on Card<em title="This field is required">*</em>
									</label>
									<span className="valid">
										<input maxLength={ 80 } className="input-fixed " autoComplete="off" id="creditCardName" name="creditCardName" type="text" />
										<span className="validate-text"></span>
									</span>
								</p>

								<p>
									<label htmlFor="creditCardNumber">
										Number<em title="This field is required">*</em>
									</label>
									<span className="valid">
										<input maxLength={ 20 } className="input-fixed " autoComplete="off" id="creditCardNumber" name="creditCardNumber" type="text" />
										<span className="validate-text" />
									</span>
								</p>

								<p>
									<label htmlFor="creditCardCVV">
										CVV<em title="This field is required">*</em>
									</label>
									<span className="valid">
										<input maxLength={ 4 } className="input-fixed " autoComplete="off" id="creditCardCVV" name="creditCardCVV" type="text" />
										<img className="vcc-card-image" alt="CVV" src="/s/img/cvv-image.png" />
										<a className="nyromodal" href="/enrol/ui/cvv?wrap=false" id="cvvLink">What is CVV?</a>
										<span className="validate-text" />
									</span>
								</p>

								<div>
									<label>
										Expiry<em title="This field is required">*</em>
									</label>
									<span className="valid">
										<Field component={SelectField} name="expiryMonth" label="expiryMonth" required={true} loadOptions={() => Promise.resolve([ { key: "01", value: "01" }, { key: "02", value: "02" }, { key: "03", value: "03" }, { key: "04", value: "04" }, { key: "05", value: "05" }, { key: "06", value: "06" }, { key: "07", value: "07" }, { key: "08", value: "08" }, { key: "09", value: "09" }, { key: "10", value: "10" }, { key: "11", value: "11" }, { key: "12", value: "12" } ]) } />
										/
										<Field component={SelectField} name="expiryYear" label="expiryYear" required={true} loadOptions={() => Promise.resolve([ { key: "2017", value: "2017" }, { key: "2018", value: "2018" }, { key: "2019", value: "2019" }, { key: "2020", value: "2020" }, { key: "2021", value: "2021" }, { key: "2022", value: "2022" }, { key: "2023", value: "2023" }, { key: "2024", value: "2024" }, { key: "2025", value: "2025" }, { key: "2026", value: "2026" }, { key: "2027", value: "2027" }, { key: "2028", value: "2028" }, { key: "2029", value: "2029" }, { key: "2030", value: "2030" }, { key: "2031", value: "2031" } ]) } />
										<span className="validate-text" />
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
	return props.contacts.map((payer, index) => {
		return (
			<label className="radio-btn" key={index}>
				<input type="radio" id="payer" name="payer" onChange={ props.payerHandleChange.bind(this, index) } checked={ index === props.selectedPayer ? true : false } value={ payer.contact.id } />
				{ payer.contact.firstName + " " + payer.contact.lastName }
			</label>
		)
	});
}

export default CreditCardComp;
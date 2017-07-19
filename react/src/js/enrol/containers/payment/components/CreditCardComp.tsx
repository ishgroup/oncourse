import React from "react";
import {Field} from "redux-form";
import SelectField from "../../../../components/form-new/SelectField";
import {TextField} from "../../../../components/form-new/TextField";
import {Amount, Contact} from "../../../../model";
import {PayerSelect} from "./PayerSelect";
import {PayerAdd} from "./PayerAdd";

import {FieldName, PaymentService} from "../services/PaymentService";
import {MaskedTextField} from "../../../../components/form-new/MaskedTextField";

interface Props {
  contacts: Contact[];
  amount: Amount;
  onSetPayer?: (id: string) => void;
  onAddPayer?: () => any;
  onAddCompany?: () => any;
  payerId?: string;
  voucherPayerEnabled?: boolean;
}

const Header = () => {
  return (<div className="header-content">
    <div className="header">
      <h1>Secure credit card payment</h1>
      <span>This is a secure SSL encrypted payment.</span>
    </div>
  </div>);
};

class CreditCardComp extends React.Component<Props, any> {

  constructor(props) {
    super(props);
  }

  render() {
    const {contacts, amount, onSetPayer, payerId, onAddPayer, onAddCompany, voucherPayerEnabled} = this.props;

    return (
      <div id="credit-card" className="single-tab active">
        <div id="paymentEditor">
          <Header/>
          <div className="enrolmentsSelected">
            <fieldset>
              <div className="form-details">
                <p>
                  <label>Pay now</label>
                  <span id="cardtotalstring">
                    ${ amount.payNow } <img alt="visa card and master card" src="/s/img/visa-mastercard.png"/>
                  </span>
                </p>

                <PayerSelect
                  contacts={contacts}
                  payer={contacts.filter(c => c.id === payerId)[0]}
                  onChange={onSetPayer}
                  disabled={voucherPayerEnabled}
                />

                <PayerAdd
                  onAddPayer={onAddPayer}
                  onAddCompany={onAddCompany}
                  disabled={voucherPayerEnabled}
                />

                <Field
                  component={TextField}
                  maxLength={ 40 }
                  className="input-fixed"
                  autoComplete="off"
                  name={FieldName.creditCardName}
                  label="Name on Card"
                  type="text"
                  required={true}
                />

                <Field
                  component={MaskedTextField}
                  maxLength={ 24 }
                  className="input-fixed "
                  autoComplete="off"
                  name={FieldName.creditCardNumber}
                  label="Number"
                  type="text"
                  required={true}
                  mask={[/\d/, /\d/, /\d/, /\d/, " ", /\d/, /\d/, /\d/, /\d/, " ", /\d/, /\d/, /\d/, /\d/, " ", /\d/, /\d/, /\d/, /\d/]}
                />

                <Field
                  component={MaskedTextField}
                  maxLength={ 4 }
                  className="input-fixed "
                  autoComplete="off"
                  name={FieldName.creditCardCvv}
                  label="CVV"
                  type="text"
                  required={true}
                  mask={[/\d/, /\d/, /\d/, /\d/]}
                >
                  <img className="vcc-card-image" alt="CVV" src="/s/img/cvv-image.png"/>
                  <a className="nyromodal" href="/enrol/ui/cvv?wrap=false" id="cvvLink">What is CVV?</a>
                </Field>

                <div className="clearfix form-group">
                  <label>
                    <span>Expiry</span>
                    <span><em title="This field is required">*</em></span>
                  </label>
                  <span className="valid input-select-small">
                    <Field
                      component={SelectField}
                      name={FieldName.expiryMonth}
                      required={true}
                      loadOptions={PaymentService.months}
                    />
                    <span className="date-separator">/</span>
                    <Field
                      component={SelectField}
                      name={FieldName.expiryYear}
                      required={true}
                      loadOptions={PaymentService.years}
                    />
                  </span>
                </div>
              </div>
            </fieldset>
          </div>
        </div>
      </div>
    );
  }
}
export default CreditCardComp;

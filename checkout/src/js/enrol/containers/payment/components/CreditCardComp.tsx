import React from "react";
import {Field} from "redux-form";
import valid from 'card-validator';
import SelectField from "../../../../components/form-new/SelectField";
import {TextField} from "../../../../components/form/TextField";
import {Amount, Contact} from "../../../../model";
import {PayerSelect} from "./PayerSelect";
import {PayerAdd} from "./PayerAdd";
import {FieldName, PaymentService} from "../services/PaymentService";
import {MaskedTextField} from "../../../../components/form-new/MaskedTextField";
import {VisaMasterCardImg, AmexImg, CvvImg} from "./CreditCardImages";

interface Props {
  contacts: Contact[];
  amount: Amount;
  onSetPayer?: (id: string) => void;
  onAddPayer?: () => any;
  onAddCompany?: () => any;
  onInit?: () => any;
  payerId?: string;
  voucherPayerEnabled?: boolean;
  amexEnabled?: boolean;
  openCvvHelp: () => void;
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

  componentDidMount() {
    this.props.onInit && this.props.onInit();
  }

  getCardNumberMask(val) {
    const number = val.replace(/_/g, '');
    const card = valid.number(number).card;

    return card && card.type === 'american-express'
    ? [/\d/, /\d/, /\d/, /\d/, " ", /\d/, /\d/, /\d/, /\d/, " ", /\d/, /\d/, /\d/, /\d/, " ", /\d/, /\d/, /\d/]
    : [/\d/, /\d/, /\d/, /\d/, " ", /\d/, /\d/, /\d/, /\d/, " ", /\d/, /\d/, /\d/, /\d/, " ", /\d/, /\d/, /\d/, /\d/];
  }

  render() {
    const {
      contacts, amount, onSetPayer, payerId, onAddPayer, onAddCompany, voucherPayerEnabled, openCvvHelp, amexEnabled,
    } = this.props;

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
                    ${Number(amount.ccPayment).toFixed(2)}
                    <VisaMasterCardImg/>
                    {amexEnabled && <AmexImg/>}
                  </span>
                </p>

                <PayerSelect
                  contacts={contacts}
                  payer={contacts.find(c => c.id === payerId)}
                  onChange={onSetPayer}
                  disabled={voucherPayerEnabled}
                />

                <PayerAdd
                  onAddPayer={onAddPayer}
                  onAddCompany={onAddCompany}
                  disabled={voucherPayerEnabled}
                />

                <div className="form-group">
                  <Field
                    component={TextField}
                    maxLength={40}
                    className="input-fixed"
                    autoComplete="off"
                    name={FieldName.creditCardName}
                    label="Name on Card"
                    type="text"
                    required={true}
                  />
                </div>

                <Field
                  component={MaskedTextField}
                  maxLength={24}
                  className="input-fixed "
                  autoComplete="off"
                  name={FieldName.creditCardNumber}
                  label="Number"
                  type="text"
                  required={true}
                  mask={ val => this.getCardNumberMask(val)}
                />

                <Field
                  component={MaskedTextField}
                  maxLength={4}
                  className="input-fixed "
                  autoComplete="off"
                  name={FieldName.creditCardCvv}
                  label="CVV"
                  type="text"
                  required={true}
                  placeholderChar={' '}
                  mask={[/\d/, /\d/, /\d/, /\d/]}
                >
                  <span className="cvv-help">
                    <CvvImg/>
                    <a href="#" onClick={openCvvHelp}> What is CVV? </a>
                  </span>

                </Field>

                <div className="clearfix form-group">
                  <label>
                    <span>Expiry</span>
                    <span><em title="This field is required">*</em></span>
                  </label>
                  <span className="input-select-small inline-form">
                    <Field
                      component={SelectField}
                      placeholder="MM"
                      name={FieldName.expiryMonth}
                      searchable={false}
                      required={true}
                      loadOptions={PaymentService.months}
                    />
                    <span className="date-separator">/</span>
                    <Field
                      component={SelectField}
                      placeholder="YYYY"
                      name={FieldName.expiryYear}
                      searchable={false}
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

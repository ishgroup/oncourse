import React from "react";
import classnames from "classnames";
import {change, DataShape, FormErrors, FormProps, reduxForm} from "redux-form";
import {Contact} from "../../../model/web/Contact";
import {IshState} from "../../../services/IshState";
import {connect} from "react-redux";
import CreditCardComp from "./components/CreditCardComp";
import {Amount} from "../../../model/checkout/Amount";
import {Conditions} from "./components/Conditions";
import {makePayment} from "./actions/Actions";
import {changePhase, setPayer} from "../../actions/Actions";
import {Phase} from "../../reducers/State";

import {FieldName, Values} from "./services/PaymentService";

interface Props extends FormProps<DataShape, any, any> {
  amount: Amount;
  contacts: Contact[];
  onSetPayer: (Contact) => any;
  onAddPayer: () => any;
  onAddCompany: () => any;
  payerId: string;

}

export const NAME = "CreditCartForm";

class CreditCartForm extends React.Component<Props, any> {
  render() {
    const {amount, contacts, handleSubmit, invalid, pristine, submitting, onSetPayer, payerId, onAddPayer, onAddCompany} = this.props;
    const disabled = (invalid || pristine || submitting);
    const className = classnames("btn", "btn-primary", {disabled});

    return (
      <div>
        <form onSubmit={handleSubmit} id="payment-form">
          {amount.payNow != "0.00" &&
            <CreditCardComp
              amount={amount}
              contacts={contacts}
              payerId={payerId}
              onSetPayer={onSetPayer}
              onAddPayer={onAddPayer}
              onAddCompany={onAddCompany}
            />
          }
          <Conditions/>
          <div className="form-controls enrolmentsSelected">
            <input
              className={className}
              id="paymentSubmit"
              name="paymentSubmit"
              type="submit"
              disabled={disabled}
            />
          </div>
        </form>
      </div>
    );
  }
}


const Form = reduxForm({
  form: NAME,
  validate: (data: Values, props: Props): FormErrors<FormData> => {
    const errors = {};
    
    if (!data.agreementFlag) {
      errors[FieldName.agreementFlag] =  'You must agree to the policies before proceeding.';
    } 
    if (props.amount.payNow != "0.00") {
      if (!data.creditCardName) {
        errors[FieldName.creditCardName] = 'Please supply your name as printed on the card (maximum 40 characters)';
      }

      if (!data.creditCardNumber) {
        errors[FieldName.creditCardNumber] = 'The credit card number cannot be blank.';
      }

      if (!data.creditCardCvv) {
        errors[FieldName.creditCardCvv] = 'The credit card CVV cannot be blank.';
      }

      if (!data.expiryMonth) {
        errors[FieldName.expiryMonth] = 'The credit card expiry month cannot be blank.';
      }

      if (!data.expiryYear) {
        errors[FieldName.expiryYear] = 'The credit card expiry year cannot be blank.';
      }
    }
    
    return errors;
  },
  
  onSubmit: (data: Values, dispatch, props): void => {
    if (props.amount.payNow != "0.00") {
      data.creditCardNumber = data.creditCardNumber.replace(/\s+/g, "");
      data.creditCardCvv = data.creditCardCvv.replace(/\_/g, "");
    }
    dispatch(makePayment(data));
  },
})(CreditCartForm);


const mapStateToProps = (state: IshState) => {
  return {
    contacts: Object.values(state.checkout.contacts.entities.contact),
    amount: state.checkout.amount,
    payerId: state.checkout.payerId,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    onSetPayer: id => dispatch(setPayer(id)),
    onAddPayer: () => dispatch(changePhase(Phase.AddContactAsPayer)),
    onAddCompany: () => dispatch(changePhase(Phase.AddContactAsCompany)),
  };
};

export const Container = connect(
  mapStateToProps,
  mapDispatchToProps,
)(Form);


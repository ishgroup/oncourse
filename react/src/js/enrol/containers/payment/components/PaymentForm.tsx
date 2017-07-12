import * as React from "react";
import {reduxForm, FormProps, FormErrors, DataShape} from "redux-form";
import classnames from "classnames";
import {Amount} from "../../../../model/checkout/Amount";
import CreditCardComp from "./CreditCardComp";
import CorporatePassComp from "./CorporatePassComp";
import {Contact} from "../../../../model/web/Contact";
import {Conditions} from "./Conditions";
import {FieldName, Values} from "../services/PaymentService";
import {getCorporatePass, makePayment} from "../actions/Actions";
import {connect} from "react-redux";
import {changePhase, setPayer} from "../../../actions/Actions";
import {Phase} from "../../../reducers/State";
import CheckoutService from "../../../services/CheckoutService";
import {IshState} from "../../../../services/IshState";

/**
 * @Deprecated will be remove, now it is used only as example
 */

interface Props extends FormProps<DataShape, any, any> {
  contacts: Contact[];
  amount: Amount;
  onSubmit: (data, dispatch, props) => any;
  payerId: string;
  onSubmitPass?: (code: string) => any;
}

export const NAME = "PaymentForm";

class PaymentForm extends React.Component<any, any> {

  constructor(props) {
    super(props);
  }

  componentWillMount() {
    this.state = {
      selectedPayer: 0,
      currentForm: "credit-card",
    };
  }

  paymentTabOnClick = e => {
    e.preventDefault();
    this.setState({
      currentForm: e.target.getAttribute('href').replace('#', ''),
    });
  }

  render() {
    const {
      handleSubmit, contacts, amount, invalid, pristine, submitting, onSubmitPass,
      onSetPayer, payerId, onAddPayer, onAddCompany, voucherPayerEnabled,
    } = this.props;

    const disabled = (invalid || pristine || submitting);


    const {currentForm} = this.state;

    return (
      <form onSubmit={handleSubmit} id="paymentform">
        {amount.payNow !== 0 &&
        <div>
          <div id="tabable-container">
            <PaymentFormNav paymentTabOnClick={this.paymentTabOnClick} currentForm={currentForm}/>
            <div className="tab-content">

              {currentForm === 'credit-card' &&
              <CreditCardComp
                amount={amount}
                contacts={contacts}
                payerId={payerId}
                onSetPayer={onSetPayer}
                onAddPayer={onAddPayer}
                onAddCompany={onAddCompany}
                voucherPayerEnabled={voucherPayerEnabled}
              />
              }

              {currentForm === 'corporate-pass' &&
              <CorporatePassComp onSubmitPass={onSubmitPass}/>
              }

            </div>
          </div>
          <Conditions/>

          <div className="form-controls enrolmentsSelected">
            <input
              disabled={disabled}
              value="Confirm"
              className={classnames("btn btn-primary", {disabled})}
              id="paymentSubmit"
              name="paymentSubmit"
              type="submit"
            />
          </div>
        </div>
        }
      </form>
    );
  }
}

const PaymentFormNav = props => {
  const {paymentTabOnClick, currentForm} = props;

  return (
    <ul className="nav">
      <li className={classnames("first", {active: currentForm === "credit-card"})}>
        <a href="#credit-card" onClick={paymentTabOnClick.bind(this)}>Credit card</a>
      </li>
      <li className={classnames({active: currentForm === "corporate-pass"})}>
        <a href="#corporate-pass" onClick={paymentTabOnClick.bind(this)}>CorporatePass</a>
      </li>
    </ul>
  );
};

const Form = reduxForm({
  form: NAME,
  validate: (data: Values, props: Props): FormErrors<FormData> => {
    const errors = {};

    if (!data.agreementFlag) {
      errors[FieldName.agreementFlag] = 'You must agree to the policies before proceeding.';
    }
    if (props.amount.payNow !== 0) {
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
  onSubmitSuccess: (result, dispatch, props: any) => {
    // dispatch({type: DispatchRequest});
  },
  onSubmitFail: (errors, dispatch, submitError, props) => {
    // dispatch(showErrors(submitError, NAME));
  },
  onSubmit: (data: Values, dispatch, props): void => {
    if (props.amount.payNow !== 0) {
      data.creditCardNumber = data.creditCardNumber.replace(/\s+/g, "");
      data.creditCardCvv = data.creditCardCvv.replace(/\_/g, "");
    }
    dispatch(makePayment(data));
  },
})(PaymentForm);

const mapStateToProps = (state: IshState) => {
  return {
    contacts: Object.values(state.checkout.contacts.entities.contact),
    amount: state.checkout.amount,
    payerId: state.checkout.payerId,
    voucherPayerEnabled: CheckoutService.hasActiveVoucherPayer(state.checkout),
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onSetPayer: id => dispatch(setPayer(id)),
    onAddPayer: () => dispatch(changePhase(Phase.AddContactAsPayer)),
    onAddCompany: () => dispatch(changePhase(Phase.AddContactAsCompany)),
    onSubmitPass: code => dispatch(getCorporatePass(code)),
  };
};

export const Container = connect(
  mapStateToProps,
  mapDispatchToProps,
)(Form);

export default Container;

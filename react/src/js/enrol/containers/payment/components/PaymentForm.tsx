import * as React from "react";
import {reduxForm, FormProps, FormErrors, DataShape} from "redux-form";
import classnames from "classnames";
import {Amount} from "../../../../model/checkout/Amount";
import CreditCardComp from "./CreditCardComp";
import CorporatePassComp from "./CorporatePassComp";
import {Contact} from "../../../../model/web/Contact";
import {Conditions} from "./Conditions";
import {FieldName, CreditCardFormValues, CorporatePassFormValues} from "../services/PaymentService";
import {
  changeTab, getCorporatePass, submitPaymentCreditCard, resetCorporatePass,
  submitPaymentCorporatePass, updatePaymentStatus,
} from "../actions/Actions";
import {connect} from "react-redux";
import {changePhase, getAmount, setPayer} from "../../../actions/Actions";
import {Phase} from "../../../reducers/State";
import CheckoutService from "../../../services/CheckoutService";
import {IshState} from "../../../../services/IshState";
import {CorporatePass} from "../../../../model/checkout/corporatepass/CorporatePass";
import {Tabs} from "../reducers/State";
import {PaymentStatus} from "../../../../model/checkout/payment/PaymentStatus";

/**
 * @Deprecated will be remove, now it is used only as example
 */

interface Props extends FormProps<DataShape, any, any> {
  contacts: Contact[];
  amount: Amount;
  onSubmit: (data, dispatch, props) => any;
  payerId: string;
  voucherPayerEnabled: boolean;
  onSubmitPass?: (code: string) => any;
  corporatePass?: CorporatePass;
  currentTab: Tabs;
  resetPass?: () => void;
  onSetPayer?: () => void;
  onAddPayer?: () => void;
  onAddCompany?: () => void;
  onChangeTab?: (tab) => void;
  onUnmountPassComponent?: () => void;
  corporatePassAvailable?: boolean;
}

export const NAME = "PaymentForm";

class PaymentForm extends React.Component<Props, any> {

  paymentTabOnClick = e => {
    e.preventDefault();
    const {currentTab, onChangeTab} = this.props;
    const nextTab = Number(e.target.getAttribute('href').replace('#', ''));

    if (currentTab === nextTab) return;

    this.props.reset();
    onChangeTab(nextTab);
  }

  render() {
    const {
      handleSubmit, contacts, amount, invalid, pristine, submitting, onSubmitPass, corporatePass,
      onSetPayer, payerId, onAddPayer, onAddCompany, voucherPayerEnabled, currentTab, corporatePassAvailable,
      onUnmountPassComponent,
    } = this.props;

    const disabled = (invalid || pristine || submitting);

    return (
      <form onSubmit={handleSubmit} id="payment-form">
        {amount.payNow !== 0 &&
        <div>
          <div id="tabable-container">
            <PaymentFormNav
              paymentTabOnClick={this.paymentTabOnClick}
              currentTab={currentTab}
              corporatePassAvailable={corporatePassAvailable}
            />

            <div className="tab-content">

              {currentTab === Tabs.creditCard &&
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

              {currentTab === Tabs.corporatePass &&
              <CorporatePassComp
                onSubmitPass={onSubmitPass}
                corporatePass={corporatePass}
                onUnmount={onUnmountPassComponent}
              />
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
  const {paymentTabOnClick, currentTab, corporatePassAvailable} = props;

  return (
    <ul className="nav">
      <li className={classnames("first", {active: currentTab === Tabs.creditCard})}>
        <a href={`#${Tabs.creditCard}`} onClick={paymentTabOnClick.bind(this)}>Credit card</a>
      </li>
      {corporatePassAvailable &&
      <li className={classnames({active: currentTab === Tabs.corporatePass})}>
        <a href={`#${Tabs.corporatePass}`} onClick={paymentTabOnClick.bind(this)}>CorporatePass</a>
      </li>
      }
    </ul>
  );
};

const validateCreditCard = (data, props) => {
  const errors = {};

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
}

const validateCorporatePass = (data, props) => {
  const errors = {};

  if (!props.corporatePass.id || !data.corporatePass) {
    errors['corporatePass'] = "You must apply a correct code";
  }

  return errors;
}

const Form = reduxForm({
  form: NAME,
  validate: (data: CreditCardFormValues & CorporatePassFormValues, props: Props): FormErrors<FormData> => {
    const errors = {};

    if (!data.agreementFlag) {
      errors[FieldName.agreementFlag] = 'You must agree to the policies before proceeding.';
    }

    if (props.currentTab === Tabs.creditCard) {
      return {...errors, ...validateCreditCard(data, props)};
    }

    if (props.currentTab === Tabs.corporatePass) {
      return {...errors, ...validateCorporatePass(data, props)};
    }

    return errors;
  },
  onSubmit: (data: CreditCardFormValues & CorporatePassFormValues, dispatch, props): void => {
    if (props.amount.payNow !== 0 && props.currentTab === Tabs.creditCard) {
      data.creditCardNumber = data.creditCardNumber.replace(/\s+/g, "");
      data.creditCardCvv = data.creditCardCvv.replace(/\_/g, "");
    }

    if (props.currentTab === Tabs.creditCard) {
      dispatch(submitPaymentCreditCard(data));
    }

    if (props.currentTab === Tabs.corporatePass) {
      dispatch(updatePaymentStatus({status: PaymentStatus.IN_PROGRESS}));
      dispatch(submitPaymentCorporatePass(data));
    }

  },
})(PaymentForm);

const mapStateToProps = (state: IshState) => {
  return {
    contacts: Object.values(state.checkout.contacts.entities.contact),
    amount: state.checkout.amount,
    payerId: state.checkout.payerId,
    voucherPayerEnabled: CheckoutService.hasActiveVoucherPayer(state.checkout),
    corporatePass: state.checkout.payment.corporatePass,
    currentTab: state.checkout.payment.currentTab,
    corporatePassAvailable: state.checkout.payment.corporatePassAvailable,
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onSetPayer: id => dispatch(setPayer(id)),
    onAddPayer: () => dispatch(changePhase(Phase.AddContactAsPayer)),
    onAddCompany: () => dispatch(changePhase(Phase.AddContactAsCompany)),
    onSubmitPass: code => dispatch(getCorporatePass(code)),
    onChangeTab: tab => {
      dispatch(changeTab(tab));
      dispatch(resetCorporatePass());
    },
    onUnmountPassComponent: () => dispatch(getAmount()),
  };
};

export const Container = connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps,
)(Form);

export default Container;

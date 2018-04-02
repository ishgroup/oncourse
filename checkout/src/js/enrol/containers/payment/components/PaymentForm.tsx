import * as React from "react";
import {reduxForm, FormProps, FormErrors, DataShape} from "redux-form";
import Rodal from "rodal";
import classnames from "classnames";
import CreditCardComp from "./CreditCardComp";
import CorporatePassComp from "./CorporatePassComp";
import PayLaterComp from "./PayLaterComp";
import {CvvHelp} from "./CvvHelp";
import {Conditions} from "./Conditions";
import {FieldName, CreditCardFormValues, CorporatePassFormValues} from "../services/PaymentService";
import {
  changeTab, getCorporatePass, submitPaymentCreditCard, resetCorporatePass,
  submitPaymentCorporatePass, updatePaymentStatus,
} from "../actions/Actions";
import {connect} from "react-redux";
import {
  changePhase, getAmount, setPayer, showSyncErrors, togglePayNowVisibility, updatePayNow,
} from "../../../actions/Actions";
import {Phase} from "../../../reducers/State";
import CheckoutService from "../../../services/CheckoutService";
import {IshState} from "../../../../services/IshState";
import {Tabs} from "../reducers/State";
import {PaymentStatus, CorporatePass, Contact, Amount} from "../../../../model";
import {getAllContactNodesFromBackend} from "../../summary/actions/Actions";

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
  corporatePassError?: string;
  fetching?: boolean;
  currentTab: Tabs;
  resetPass?: () => void;
  onSetPayer?: () => void;
  onAddPayer?: () => void;
  onAddCompany?: () => void;
  onChangeTab?: (tab) => void;
  onUnmountPassComponent?: () => void;
  corporatePassAvailable?: boolean;
  creditCardAvailable?: boolean;
  payLaterAvailable?: boolean;
  updatePayNow: (val, validate) => void;
  conditions?: {
    refundPolicyUrl?: string,
    featureEnrolmentDisclosure?: string,
  };
}

export const NAME = "PaymentForm";

class PaymentForm extends React.Component<Props, any> {

  constructor(props) {
    super(props);

    this.state = {
      showCvvHelp: false,
    };
  }

  componentDidMount() {
    const {onChangeTab} = this.props;
    const validCurrentTab = this.getValidTab();
    onChangeTab(validCurrentTab);
  }

  getValidTab = () => {
    const {currentTab, creditCardAvailable} = this.props;

    if (currentTab === Tabs.payLater) {
      return Tabs.payLater;
    }

    return currentTab === Tabs.creditCard && creditCardAvailable ? Tabs.creditCard : Tabs.corporatePass;
  }

  paymentTabOnClick = e => {
    e.preventDefault();
    const {currentTab, onChangeTab} = this.props;
    const nextTab = Number(e.target.getAttribute('href').replace('#', ''));

    if (currentTab === nextTab) return;

    this.props.reset();
    onChangeTab(nextTab);
  }

  openCvvHelp() {
    this.setState({showCvvHelp: true});
  }

  closeCvvHelp() {
    this.setState({showCvvHelp: false});
  }

  render() {
    const {
      handleSubmit, contacts, amount, invalid, pristine, submitting, onSubmitPass, corporatePass, corporatePassError,
      onSetPayer, payerId, onAddPayer, onAddCompany, voucherPayerEnabled, currentTab, corporatePassAvailable, fetching,
      onUnmountPassComponent, conditions, creditCardAvailable, payLaterAvailable, updatePayNow,
    } = this.props;

    const disabled = (pristine || submitting);

    return (
      <form onSubmit={handleSubmit} id="payment-form" className={classnames({submitting})}>

        {(Number(amount.payNow) !== 0 || (Number(amount.payNow) === 0 && corporatePass.id) || payLaterAvailable) &&
          <div>
            <Rodal
              visible={this.state.showCvvHelp}
              onClose={() => this.closeCvvHelp()}
              height={400}
              animation="flip"
            >
              <div className="rodal-content">
                <CvvHelp/>
              </div>
            </Rodal>

            <div id="tabable-container">
              <PaymentFormNav
                paymentTabOnClick={this.paymentTabOnClick}
                currentTab={currentTab}
                corporatePassAvailable={corporatePassAvailable}
                creditCardAvailable={creditCardAvailable}
                payLaterAvailable={payLaterAvailable}
              />

              <div className="tab-content">

                {currentTab === Tabs.creditCard && creditCardAvailable &&
                  <CreditCardComp
                    amount={amount}
                    contacts={contacts}
                    payerId={payerId}
                    onSetPayer={onSetPayer}
                    onAddPayer={onAddPayer}
                    onAddCompany={onAddCompany}
                    voucherPayerEnabled={voucherPayerEnabled}
                    openCvvHelp={() => this.openCvvHelp()}
                    onInit={() => Number(amount.payNow) === 0 ? updatePayNow(amount.subTotal, false) : undefined}
                  />
                }

                {currentTab === Tabs.corporatePass && corporatePassAvailable &&
                  <CorporatePassComp
                    onSubmitPass={onSubmitPass}
                    corporatePass={corporatePass}
                    onUnmount={onUnmountPassComponent}
                    corporatePassError={corporatePassError}
                    fetching={fetching}
                  />
                }

                {currentTab === Tabs.payLater && payLaterAvailable &&
                  <PayLaterComp
                    onInit={() => updatePayNow(0, false)}
                    contacts={contacts}
                    payerId={payerId}
                    onSetPayer={onSetPayer}
                    onAddPayer={onAddPayer}
                    onAddCompany={onAddCompany}
                    voucherPayerEnabled={voucherPayerEnabled}
                  />
                }

              </div>
            </div>
          </div>
        }

        <Conditions conditions={conditions}/>

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
      </form>
    );
  }
}

const PaymentFormNav = props => {
  const {paymentTabOnClick, currentTab, corporatePassAvailable, creditCardAvailable, payLaterAvailable} = props;

  return (
    <ul className="nav">
      {creditCardAvailable &&
        <li className={classnames("first", {active: currentTab === Tabs.creditCard})}>
          <a href={`#${Tabs.creditCard}`} onClick={paymentTabOnClick.bind(this)}>Credit card</a>
        </li>
      }

      {corporatePassAvailable &&
        <li className={classnames({active: currentTab === Tabs.corporatePass})}>
          <a href={`#${Tabs.corporatePass}`} onClick={paymentTabOnClick.bind(this)}>CorporatePass</a>
        </li>
      }

      {payLaterAvailable &&
        <li className={classnames({active: currentTab === Tabs.payLater})}>
          <a href={`#${Tabs.payLater}`} onClick={paymentTabOnClick.bind(this)}>Pay later</a>
        </li>
      }
    </ul>
  );
};

const validateCreditCard = (data, props) => {
  const errors = {};

  if (Number(props.amount.payNow) !== 0) {
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
};

const validateCorporatePass = (data, props) => {
  const errors = {};

  if (!props.corporatePass.id || !data.corporatePass) {
    errors['corporatePass'] = "";
  }

  return errors;
};

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
    if (Number(props.amount.payNow) !== 0 && props.currentTab === Tabs.creditCard) {
      data.creditCardNumber = data.creditCardNumber.replace(/\s+/g, "");
      data.creditCardCvv = data.creditCardCvv.replace(/\_/g, "");
    }

    if (props.currentTab === Tabs.corporatePass) {
      dispatch(updatePaymentStatus({status: PaymentStatus.IN_PROGRESS}));
      dispatch(submitPaymentCorporatePass(data));
    } else {
      dispatch(submitPaymentCreditCard(data));
    }
  },
  onSubmitFail: (errors, dispatch, submitError, props) => {
    if (errors && !submitError) {
      dispatch(showSyncErrors(errors));
    }
  },
})(PaymentForm);

const mapStateToProps = (state: IshState) => {
  const corporatePassError = state.checkout.error &&
    state.checkout.error.fieldsErrors && state.checkout.error.fieldsErrors.find(er => er.name === 'code');
  return {
    contacts: Object.values(state.checkout.contacts.entities.contact),
    amount: state.checkout.amount,
    payerId: state.checkout.payerId,
    voucherPayerEnabled: CheckoutService.hasActiveVoucherPayer(state.checkout),
    corporatePass: state.checkout.payment.corporatePass,
    corporatePassError: corporatePassError && corporatePassError.error,
    currentTab: state.checkout.payment.currentTab,
    fetching: state.checkout.payment.fetching,
    corporatePassAvailable: state.preferences.hasOwnProperty('corporatePassEnabled') ? state.preferences.corporatePassEnabled : true,
    creditCardAvailable: state.preferences.hasOwnProperty('creditCardEnabled') ? state.preferences.creditCardEnabled : true,
    payLaterAvailable: state.checkout.amount.isEditable && Number(state.checkout.amount.minPayNow) === 0,
    conditions: {
      refundPolicyUrl: state.config.termsAndConditions,
      featureEnrolmentDisclosure: state.config.featureEnrolmentDisclosure,
    },
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onSetPayer: id => {
      dispatch(setPayer(id));
      dispatch(getAllContactNodesFromBackend());
    },
    onAddPayer: () => dispatch(changePhase(Phase.AddContactAsPayer)),
    onAddCompany: () => dispatch(changePhase(Phase.AddContactAsCompany)),
    onSubmitPass: code => dispatch(getCorporatePass(code)),
    onChangeTab: tab => {
      dispatch(changeTab(tab));
    },
    onUnmountPassComponent: () => {
      dispatch(resetCorporatePass());
      dispatch(togglePayNowVisibility(true));
      dispatch(getAmount());
    },
    updatePayNow: (val, validate) => {
      dispatch(dispatch(updatePayNow(val, validate)));
    },
  };
};

export const Container = connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps,
)(Form);

export default Container;

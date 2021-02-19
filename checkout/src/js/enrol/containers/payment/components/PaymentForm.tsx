import * as React from "react";
import {reduxForm, FormProps, FormErrors, DataShape, getFormSyncErrors, getFormValues} from "redux-form";
import classnames from "classnames";
import CreditCardV2Comp from "./CreditCardV2Comp";
import CorporatePassComp from "./CorporatePassComp";
import PayLaterComp from "./PayLaterComp";
import {Conditions} from "./Conditions";
import {CreditCardFormValues, CorporatePassFormValues, FieldName} from "../services/PaymentService";
import {
  changeTab, getCorporatePass, resetCorporatePass,
  submitPaymentCorporatePass, updatePaymentStatus, processPaymentV2,
} from "../actions/Actions";
import {connect, Dispatch} from "react-redux";
import {
  changePhase, getAmount, getCheckoutModelFromBackend, setPayer, showSyncErrors, togglePayNowVisibility, updatePayNow,
} from "../../../actions/Actions";
import {Phase} from "../../../reducers/State";
import CheckoutService from "../../../services/CheckoutService";
import {IshState} from "../../../../services/IshState";
import {Tabs} from "../reducers/State";
import {PaymentStatus, CorporatePass, Contact, Amount, ValidationError} from "../../../../model";
import {getAllContactNodesFromBackend, setResultDetailsCorporatePass} from "../../summary/actions/Actions";

/**
 * @Deprecated will be remove, now it is used only as example
 */

interface Props extends FormProps<DataShape, any, any> {
  contacts: Contact[];
  amount: Amount;
  onSubmit: (data, dispatch, props) => any;
  payerId: string;
  voucherPayerEnabled: boolean;
  processPaymentV2?: (xValidateOnly: boolean, sessionId: string) => any;
  onSubmitPass?: (code: string) => any;
  corporatePass?: CorporatePass;
  corporatePassError?: string;
  fetching?: boolean;
  iframeUrl?: string;
  currentTab: Tabs;
  resetPass?: () => void;
  onSetPayer?: () => void;
  onAddPayer?: () => void;
  onAddCompany?: () => void;
  onChangeTab?: (tab) => void;
  onUnmountPassComponent?: () => void;
  corporatePassAvailable?: boolean;
  amexEnabled?: boolean;
  creditCardAvailable?: boolean;
  payLaterAvailable?: boolean;
  updatePayNow: (val, validate) => void;
  conditions?: {
    refundPolicyUrl?: string,
    featureEnrolmentDisclosure?: string,
  };
  formSyncErrors?: FormErrors<any>;
  dispatch?: Dispatch<any>;
  stateErrors?: ValidationError;
  formValues?: any;
}

export const NAME = "PaymentForm";

class PaymentForm extends React.Component<Props, any> {
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
    onChangeTab(nextTab);
  }

  render() {
    const {
      handleSubmit, contacts, amount, pristine, submitting, onSubmitPass, corporatePass, corporatePassError,
      onSetPayer, payerId, onAddPayer, onAddCompany, voucherPayerEnabled, currentTab, corporatePassAvailable, fetching,
      onUnmountPassComponent, conditions, creditCardAvailable, payLaterAvailable, updatePayNow,
      iframeUrl, processPaymentV2, formSyncErrors, dispatch, formValues
    } = this.props;


    const disabled = (pristine || submitting);

    const agreementChecked = formValues && formValues[FieldName.agreementFlag];

    return (
      <form onSubmit={handleSubmit} id="payment-form" className={classnames({submitting: fetching || submitting})}>

        <Conditions conditions={conditions} agreementChecked={agreementChecked}/>

        {agreementChecked && (Number(amount.ccPayment) !== 0 || (Number(amount.ccPayment) === 0 && corporatePass.id) || payLaterAvailable) &&
          <div>
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
                  <CreditCardV2Comp
                    amount={amount}
                    contacts={contacts}
                    payerId={payerId}
                    onSetPayer={onSetPayer}
                    onAddPayer={onAddPayer}
                    onAddCompany={onAddCompany}
                    voucherPayerEnabled={voucherPayerEnabled}
                    iframeUrl={iframeUrl}
                    processPaymentV2={processPaymentV2}
                    disabled={formSyncErrors}
                    dispatch={dispatch}
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

        {agreementChecked && !(currentTab === Tabs.creditCard && creditCardAvailable && amount.ccPayment > 0)
          && <div className="form-controls enrolmentsSelected">
                <input
                  disabled={disabled}
                  value="Confirm"
                  className={classnames("btn btn-primary", {disabled})}
                  id="paymentSubmit"
                  name="paymentSubmit"
                  type="submit"
                />
              </div>
        }
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
          <a href={`#${Tabs.corporatePass}`} onClick={paymentTabOnClick.bind(this)}>Corporate Pass</a>
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

const validateCorporatePass = (data, props) => {
  const errors = {};

  if (!props.corporatePass.id || !data.corporatePass) {
    errors['corporatePass'] = "";
  }

  return errors;
};

const Form = reduxForm({
  form: NAME,
  validate: (data:CorporatePassFormValues, props: Props): FormErrors<FormData> => {
    const errors = {};

    if (props.currentTab === Tabs.corporatePass) {
      return {...errors, ...validateCorporatePass(data, props)};
    }

    return errors;
  },
  onSubmit: (data: CreditCardFormValues & CorporatePassFormValues, dispatch, props): void => {
    if (props.currentTab === Tabs.corporatePass && Number(props.amount.subTotal) !== 0) {
      dispatch(updatePaymentStatus({status: PaymentStatus.IN_PROGRESS}));
      dispatch(submitPaymentCorporatePass(data));
      dispatch(setResultDetailsCorporatePass(props.corporatePass));
    } else {
      dispatch(processPaymentV2(false));
      dispatch(setResultDetailsCorporatePass(null));
    }
  },
  onSubmitFail: (errors, dispatch, submitError) => {
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
    iframeUrl: state.checkout.payment.iframeUrl,
    fetching: state.checkout.payment.fetching,
    corporatePassAvailable: state.preferences.hasOwnProperty('corporatePassEnabled') ? state.preferences.corporatePassEnabled : true,
    creditCardAvailable: state.preferences.hasOwnProperty('creditCardEnabled') ? state.preferences.creditCardEnabled : true,
    payLaterAvailable: state.checkout.amount.isEditable && Number(state.checkout.amount.minPayNow) === 0,
    conditions: {
      refundPolicyUrl: state.config.termsAndConditions,
      featureEnrolmentDisclosure: state.config.featureEnrolmentDisclosure,
    },
    formSyncErrors: getFormSyncErrors(NAME)(state),
    stateErrors: state.checkout.error,
    formValues: getFormValues(NAME)(state)
  };
};

const mapDispatchToProps = dispatch => {
  return {
    onSetPayer: id => {
      dispatch(setPayer(id));
      dispatch(getCheckoutModelFromBackend());
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
      dispatch(updatePayNow(val, validate));
    },
    processPaymentV2: (xValidateOnly, sessionId) => {
      dispatch(processPaymentV2(xValidateOnly, sessionId));
    },
  };
};

export const Container = connect<any, any, any>(
  mapStateToProps,
  mapDispatchToProps,
)(Form);

export default Container;

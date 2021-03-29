import * as React from "react";
import {connect} from "react-redux";
import {Dispatch} from "redux";
import ContactAddForm from "./contact-add/ContactAddForm";
import CompanyAddForm from "./contact-add/CompanyAddForm";
import Concession from './concession/Concession';
import ContactEditForm from "./contact-edit/ContactEditForm";
import {Messages, Progress} from "./Functions";
import {CheckoutState, Phase} from "../reducers/State";
import Summary from "./summary/Summary";
import {Payment} from "./payment/Payment";
import {Result} from "./result/Result";
import {changePhase, sendInitRequest} from "../actions/Actions";
import {submitAddContact} from "./contact-add/actions/Actions";
import CheckoutService from "../services/CheckoutService";
import {FieldSet} from "../../model/field/FieldSet";
import {IshState} from "../../services/IshState";
import {updatePaymentSuccessUrl} from "../../common/actions/Actions";

export const isOldIE = () => {
  const ua = window.navigator.userAgent;
  const  msie = ua.indexOf('MSIE ');
  return msie !== -1;
};


interface Props {
  phase: Phase;
  page: number;
  onInit: () => void;
  updatePaymentSuccessUrl: (url: string) => void;
  onProceedToPayment: () => void;
  changePhase: (phase) => void;
  isNewContact: boolean;
  fetching: boolean;
  childName: any;
  fieldset: FieldSet;
  minAge: number;
  isFirstContact: boolean;
  isEmptyProducts: boolean;
  isOnlyWaitingCoursesInCart: boolean;
}

export class Checkout extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();

    const urlParsms = new URLSearchParams(window.location.search);
    const sourcePath = urlParsms.get("sourcePath");

    if (sourcePath) {
      history.replaceState(null,null, window.location.origin + window.location.pathname);
      this.props.updatePaymentSuccessUrl(sourcePath);
    }
  }

  render() {
    const {
      phase, page, isNewContact, fetching, childName, fieldset, minAge, isFirstContact, isEmptyProducts, isOnlyWaitingCoursesInCart
    } = this.props;

    return (
      <div>
        {isOldIE() && <div
            style={{
              backgroundColor: "#E57373",
              color: "white",
              padding: "5px",
              borderRadius: "3px",
              lineHeight: "17px",
              marginBottom: "20px",
            }}
          >
          {/* tslint:disable-next-line:max-line-length */}
            Your current browser is not supported. Please ensure you are using an up to date version of Chrome, Firefox or Edge.
          </div>
        }
        <Progress/>
        <Messages/>

        {(phase === Phase.AddPayer || phase === Phase.AddContact || phase === Phase.AddContactAsPayer ||
          phase === Phase.AddParent || phase === Phase.ChangeParent) &&
        <ContactAddForm
          childName={childName}
          phase={phase}
          isFirst={isFirstContact}
          isEmptyProducts={isEmptyProducts}
          isOnlyWaitingCoursesInCart={isOnlyWaitingCoursesInCart}
          onSuccess={submitAddContact}
          onCancel={!isNewContact ? () => this.props.changePhase(page) : undefined}
          fetching={fetching}
          fieldset={fieldset}
          minAge={minAge}
        />
        }
        {phase === Phase.AddContactAsCompany &&
        <CompanyAddForm
          onSuccess={submitAddContact}
          onCancel={() => this.props.changePhase(page)}
          fetching={fetching}
          fieldset={fieldset}
        />
        }

        {phase === Phase.AddConcession &&
        <Concession />
        }


        {(phase === Phase.EditContact || phase === Phase.ComplementEditContact) && <ContactEditForm/>}
        {phase === Phase.Summary && <Summary/>}
        {phase === Phase.Payment && <Payment/>}
        {phase === Phase.Result && <Result/>}
      </div>
    );
  }
}

const getChildFromProps = (state: CheckoutState) => {
  const childId = state.contactAddProcess.forChild;
  if (!childId) return null;

  const child = state.contacts.entities.contact && state.contacts.entities.contact[childId];
  return child ? `${child.firstName} ${child.lastName}` : '';
}

const mapStateToProps = (state: IshState) => ({
  phase: state.checkout.phase,
  page: state.checkout.page,
  isNewContact: !state.checkout.contacts.result.length,
  fetching: state.checkout.fetching,
  childName: getChildFromProps(state.checkout),
  fieldset: CheckoutService.isOnlyWaitingCoursesInCart(state.cart) ? FieldSet.WAITINGLIST : FieldSet.ENROLMENT,
  minAge: state.preferences.minAge,
  isFirstContact: !state.checkout.summary.result.length,
  isEmptyProducts: !state.cart.products.result.length,
  isOnlyWaitingCoursesInCart: CheckoutService.isOnlyWaitingCoursesInCart(state.cart)
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: (): void => {
      dispatch(sendInitRequest());
    },
    changePhase: phase => {
      dispatch(changePhase(phase));
    },
    updatePaymentSuccessUrl: url => {
      dispatch(updatePaymentSuccessUrl(url))
    }
  };
};

const Container = connect<any, any, any, IshState>(mapStateToProps, mapDispatchToProps)(Checkout);

export default Container;

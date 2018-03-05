import * as React from "react";
import {connect, Dispatch} from "react-redux";

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


interface Props {
  phase: Phase;
  page: number;
  onInit: () => void;
  onProceedToPayment: () => void;
  changePhase: (phase) => void;
  isNewContact: boolean;
  fetching: boolean;
  childName: any;
  fieldset: FieldSet;
  minAge: number;
}

export class Checkout extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {phase, page, isNewContact, fetching, childName, fieldset, minAge} = this.props;

    return (
      <div>
        <Progress/>
        <Messages/>

        {(phase === Phase.AddPayer || phase === Phase.AddContact || phase === Phase.AddContactAsPayer ||
          phase === Phase.AddParent || phase === Phase.ChangeParent) &&
        <ContactAddForm
          childName={childName}
          phase={phase}
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


        {phase === Phase.EditContact && <ContactEditForm/>}
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
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: (): void => {
      dispatch(sendInitRequest());
    },
    changePhase: phase => {
      dispatch(changePhase(phase));
    },
  };
};

const Container = connect(mapStateToProps, mapDispatchToProps)(Checkout);

export default Container;

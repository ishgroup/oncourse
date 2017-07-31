import * as React from "react";
import {connect, Dispatch} from "react-redux";

import ContactAddForm from "./contact-add/ContactAddForm";
import CompanyAddForm from "./contact-add/CompanyAddForm";
import Concession from './concession/Concession';
import ContactEditForm from "./contact-edit/ContactEditForm";
import {Messages, Progress} from "./Functions";
import {Phase} from "../reducers/State";
import Summary from "./summary/Summary";
import {Payment} from "./payment/Payment";
import {Result} from "./result/Result";
import {changePhase, sendInitRequest} from "../actions/Actions";
import {submitAddContact} from "./contact-add/actions/Actions";

interface Props {
  phase: Phase;
  page: number;
  onInit: () => void;
  onProceedToPayment: () => void;
  changePhase: (phase) => void;
  isNewContact: boolean;
  fetching: boolean;
}

export class Checkout extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {phase, page, isNewContact, fetching} = this.props;

    return (
      <div id="checkout" className="col-xs-24 payments">
        <Progress/>
        <Messages/>

        {(phase === Phase.AddPayer || phase === Phase.AddContact || phase === Phase.AddContactAsPayer) &&
        <ContactAddForm
          onSuccess={submitAddContact}
          onCancel={!isNewContact ? () => this.props.changePhase(page) : undefined}
          fetching={fetching}
        />
        }
        {phase === Phase.AddContactAsCompany &&
        <CompanyAddForm
          onSuccess={submitAddContact}
          onCancel={() => this.props.changePhase(page)}
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


const mapStateToProps = state => ({
  phase: state.checkout.phase,
  page: state.checkout.page,
  isNewContact: !state.checkout.contacts.result.length,
  fetching: state.checkout.fetching,
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

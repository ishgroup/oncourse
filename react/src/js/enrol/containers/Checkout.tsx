import * as React from "react";
import {connect, Dispatch} from "react-redux";

import ContactAddForm from "./contact-add/ContactAddForm";
import ContactEditForm from "./contact-edit/ContactEditForm";
import {Messages, Progress} from "./Functions";
import {Phase} from "../reducers/State";
import {IshState} from "../../services/IshState";
import Summary from "./summary/Summary";
import {Payment} from "./payment/Payment";
import {Result} from "./result/Result";
import {changePhase, sendInitRequest} from "../actions/Actions";
import {submitAddContactAsPayer, submitAddContact} from "./contact-add/actions/Actions";
import CheckoutService from "../services/CheckoutService";


interface Props {
  phase: Phase;
  onInit: () => void;
  onProceedToPayment: () => void;
  changePhase: (phase) => void;
}

export class Checkout extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {phase} = this.props;
    return (
      <div id="checkout" className="col-xs-24 payments">
        <Progress/>
        <Messages/>
        {phase === Phase.AddPayer &&
        <ContactAddForm
          onSubmit={CheckoutService.createOrGetContact}
          onSuccess={submitAddContact}
        />
        }
        {phase === Phase.AddContact &&
        <ContactAddForm
          onSubmit={CheckoutService.createOrGetContact}
          onSuccess={submitAddContact}
          onCancel={() => this.props.changePhase(Phase.Summary)}
        />
        }
        {phase === Phase.AddContactAsPayer &&
        <ContactAddForm
          onSubmit={CheckoutService.createOrGetContact}
          onSuccess={submitAddContactAsPayer}
          onCancel={abc => this.props.changePhase(Phase.Payment)}
        />
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

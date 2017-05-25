import * as React from "react";
import {connect, Dispatch} from "react-redux";

import ContactAddForm from "./contact-add/ContactAddForm";
import ContactEditForm from "./contact-edit/ContactEditForm";
import {MessagesRedux, ProgressRedux} from "./Functions";
import {Phase} from "../reducers/State";
import * as Actions from "../../enrol/actions/Actions";
import {IshState} from "../../services/IshState";
import Summary from "./summary/Summary";
import {Props as ContactProps} from "./summary/components/ContactComp";
import {Amount} from "../../model/checkout/Amount";
import {OpenProceedToPaymentRequest} from "./payment/actions/Actions";
import Payment from "./payment/Payment";

interface Props {
  phase: Phase;
  onInit: () => void;
  onProceedToPayment: () => void;
}

export class Checkout extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {phase, onProceedToPayment} = this.props;
    const contacts: ContactProps[] = [];
    const amount: Amount = {};
    const onAddContact = () => {
    };

    return (
      <div id="checkout" className="col-xs-24 payments">
        <ProgressRedux/>
        <MessagesRedux/>
        {phase === Phase.AddContact && <ContactAddForm/>}
        {phase === Phase.EditContactDetails && <ContactEditForm/>}
        {phase === Phase.Summary && <Summary contacts={contacts} amount={amount}
                                             onAddContact={onAddContact}
                                             onProceedToPayment={onProceedToPayment}
        />}
        {phase === Phase.ProceedToPayment && <Payment contacts={contacts} amount={amount} />}
      </div>
    )
  }
}


const mapStateToProps = (state: IshState) => {
  return {
    phase: state.checkout.phase
  };
};

const mapDispatchToProps = (dispatch: Dispatch<any>) => {
  return {
    onInit: (): void => {
      dispatch({type: Actions.InitRequest})
    },
    onProceedToPayment : (): void => {
      dispatch({type: OpenProceedToPaymentRequest})
    }
  }
};

const Container = connect(mapStateToProps, mapDispatchToProps)(Checkout);

export default Container
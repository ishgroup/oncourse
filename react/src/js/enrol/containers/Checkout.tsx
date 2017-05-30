import * as React from "react";
import {connect, Dispatch} from "react-redux";

import ContactAddForm from "./contact-add/ContactAddForm";
import ContactEditForm from "./contact-edit/ContactEditForm";
import {Messages, Progress} from "./Functions";
import {Phase} from "../reducers/State";
import * as Actions from "../../enrol/actions/Actions";
import {IshState} from "../../services/IshState";
import Summary from "./summary/Summary";
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
    const {phase} = this.props;
    return (
      <div id="checkout" className="col-xs-24 payments">
        <Progress/>
        <Messages/>
        {phase === Phase.AddContact && <ContactAddForm/>}
        {phase === Phase.EditContact && <ContactEditForm/>}
        {phase === Phase.Summary && <Summary/>}
        {phase === Phase.Payment && <Payment/>}
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
  }
};

const Container = connect(mapStateToProps, mapDispatchToProps)(Checkout);

export default Container
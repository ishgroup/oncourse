import * as React from "react";
import {connect, Dispatch} from "react-redux";

import ContactAddForm from "./contact-add/ContactAddForm";
import ContactEditForm from "./contact-edit/ContactEditForm"
import {MessagesRedux, ProgressRedux} from "./Functions";
import {Phase} from "../reducers/State";
import * as Actions from "../../enrol/actions/Actions";
import {IshState} from "../../services/IshState";
import {Summary} from "../components/summary/Summary";

interface Props {
  phase: Phase;
  onInit: () => void;
}

export class Checkout extends React.Component<Props, any> {

  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {phase} = this.props;
    return (
      <div id="checkout" className="payments">
        <ProgressRedux/>
        <MessagesRedux/>
        {phase === Phase.AddContact && <ContactAddForm/>}
        {phase === Phase.EditContactDetails && <ContactEditForm/>}
        {phase === Phase.Summary && <Summary/>}
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
    }
  }
};

const Container = connect(mapStateToProps, mapDispatchToProps)(Checkout);

export default Container
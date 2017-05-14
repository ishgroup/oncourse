import * as React from "react";
import {connect, Dispatch} from "react-redux";

import AddContactForm from "../contact/AddContactForm";
import {MessagesRedux, ProgressRedux} from "./Functions";
import {Phase} from "../../reducers/State";
import * as Actions from "../../../enrol/actions/Actions";
import {IshState} from "../../../services/IshState";

interface Props {
  phase: Phase;
  onInit: () => void;
}

export class Checkout extends React.Component<Props, any> {

  componentWillMount() {
    this.props.onInit();
  }

  render() {
    const {phase} = this.props;
    return (
      <div id="checkout" className="payments">
        <ProgressRedux/>
        <MessagesRedux/>
        {phase === Phase.AddContact && <AddContactForm/>}
      </div>
    )
  }
}


const mapStateToProps = (state: IshState) => {
  return {
    phase: state.enrol.phase
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
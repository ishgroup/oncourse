import * as React from "react";
import {Summary} from "../summary/Summary";
import {AddContact} from "../../../containers/enrol/AddContact";
import {AddPayer} from "../../../containers/enrol/AddPayer";
import {EditContact} from "../../../containers/enrol/EditContact";

export class Checkout extends React.Component<CheckoutProps, CheckoutState> {

  constructor() {
    super();

    this.state = {
      tab: 1
    }
  }

  render() {
    return (
      <div className="payments" id="checkout">
        <div className="progress-steps">
          <ul>
            <li className="active"><a onClick={this.switchTab(1)} href="#">Your details</a></li>
            <li className="disable"><a onClick={this.switchTab(2)} href="#">Summary</a></li>
            <li className="last disable"><a onClick={this.switchTab(3)} href="#">Payment</a></li>
          </ul>
        </div>
        {getComponent(this.state.tab)}
      </div>
    )
  }

  private switchTab(tab: number) {
    return () => this.setState({tab})
  }
}

function getComponent(tab: number) {
  if (tab == 2) {
    return <EditContact/>;
  } else if (tab == 3) {
    return <Summary/>;
  } else {
    return (
    <div>
      <AddContact/>
      <AddPayer/>
    </div>
    );
  }
}

interface CheckoutProps {
}

interface CheckoutState {
  tab: number;
}

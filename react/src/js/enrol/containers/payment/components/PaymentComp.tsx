import React from "react";
import AmountComp from "../../../components/AmountComp";
import {Amount} from "../../../../model/checkout/Amount";
import {FormDecorator} from "redux-form";

export interface Props {
  paymentForm: FormDecorator<FormData, any, any>
  amount: Amount;
}

export class PaymentComp extends React.Component<Props, any> {
  render() {
    const {amount, paymentForm} = this.props;
    return (
      <div>
        <div className="row">
          <div className="col-xs-24">
            <div className="amount-container">
              <AmountComp amount={amount}/>
            </div>
          </div>
        </div>
        {paymentForm}
      </div>
    )
  }
}

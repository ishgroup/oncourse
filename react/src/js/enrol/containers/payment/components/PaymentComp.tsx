import * as React from "react";
import AmountComp from "../../../components/AmountComp";
import {Amount} from "../../../../model/checkout/Amount";
import {FormDecorator} from "redux-form";
import {Promotion} from "../../../../model/web/Promotion";

export interface Props {
  paymentForm: FormDecorator<FormData, any, any>;
  amount: Amount;
  onAddCode:(code: string) => void;
  promotions: Promotion[];
  onUpdatePayNow?: (amount, val) => void;

}

export class PaymentComp extends React.Component<Props, any> {
  render() {
    const {amount,onAddCode, paymentForm, promotions, onUpdatePayNow} = this.props;
    return (
      <div>
        <div className="row">
          <div className="col-xs-24">
            <div className="amount-container">
              <AmountComp
                amount={amount}
                onAddCode={onAddCode}
                promotions={promotions}
                onUpdatePayNow={val => amount.isEditable ? onUpdatePayNow(amount, val) : undefined}
              />
            </div>
          </div>
        </div>
        {paymentForm}
      </div>
    );
  }
}

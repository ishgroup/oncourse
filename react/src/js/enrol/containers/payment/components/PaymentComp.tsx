import * as React from "react";
import AmountComp from "../../../components/AmountComp";
import {FormDecorator} from "redux-form";
import {Promotion, RedeemVoucher, Amount} from "../../../../model";

export interface Props {
  paymentForm: FormDecorator<FormData, any, any>;
  amount: Amount;
  onAddCode:(code: string) => void;
  promotions: Promotion[];
  redeemVouchers?: RedeemVoucher[];
  onToggleVoucher?: (redeemVoucher, enabled) => void;
  onUpdatePayNow?: (amount, val) => void;
  onInit?: () => void;
}

export class PaymentComp extends React.Component<Props, any> {
  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {amount,onAddCode, paymentForm, promotions, onUpdatePayNow, onToggleVoucher, redeemVouchers} = this.props;
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
                onToggleVoucher={onToggleVoucher}
                redeemVouchers={redeemVouchers}
              />
            </div>
          </div>
        </div>
        {paymentForm}
      </div>
    );
  }
}

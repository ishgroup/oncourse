import * as React from "react";
import AmountComp from "../../../components/AmountComp";
import {FormDecorator} from "redux-form";
import {Promotion, RedeemVoucher, Amount} from "../../../../model";
import {Tabs} from "../reducers/State";

export interface Props {
  paymentForm: FormDecorator<FormData, any, any>;
  amount: Amount;
  onAddCode:(code: string) => void;
  promotions: Promotion[];
  redeemVouchers?: RedeemVoucher[];
  onToggleVoucher?: (redeemVoucher, enabled) => void;
  onUpdatePayNow?: (val, validate?: boolean) => void;
  onInit?: () => void;
  currentTab: Tabs;
}

export class PaymentComp extends React.Component<Props, any> {
  render() {
    const {amount,onAddCode, paymentForm, promotions, onUpdatePayNow, onToggleVoucher, redeemVouchers, currentTab} = this.props;
    return (
      <div>
        <div className="row">
          <div className="col-xs-24">
            <div className="amount-container">
              <AmountComp
                amount={amount}
                onAddCode={onAddCode}
                promotions={promotions}
                onUpdatePayNow={amount.isEditable ? onUpdatePayNow : undefined}
                onToggleVoucher={onToggleVoucher}
                redeemVouchers={redeemVouchers}
                currentTab={currentTab}
              />
            </div>
          </div>
        </div>
        {paymentForm}
      </div>
    );
  }
}

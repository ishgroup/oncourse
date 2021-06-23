import * as React from "react";
import AmountComp from "../../../components/AmountComp";
import {FormDecorator} from "redux-form";
import { Promotion, RedeemVoucher, Amount, RedeemVoucherProduct } from "../../../../model";
import {Tabs} from "../reducers/State";
import {scrollToTop} from "../../../../common/utils/DomUtils";

export interface Props {
  paymentForm: FormDecorator<FormData, any, any>;
  amount: Amount;
  onAddCode:(code: string) => void;
  promotions: Promotion[];
  redeemVouchers?: RedeemVoucher[];
  redeemedVoucherProducts?: RedeemVoucherProduct[];
  onToggleVoucher?: (redeemVoucher, enabled) => void;
  onToggleVoucherProduct?: (redeemVoucher, enabled) => void;
  onUpdatePayNow?: (val, validate?: boolean) => void;
  onInit?: () => void;
  currentTab: Tabs;
}

export class PaymentComp extends React.Component<Props, any> {
  componentDidMount() {
    scrollToTop();
  }

  render() {
    const {amount,onAddCode, paymentForm, promotions, onUpdatePayNow, onToggleVoucher,
      redeemVouchers, redeemedVoucherProducts, onToggleVoucherProduct, currentTab} = this.props;
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
                onToggleVoucherProduct={onToggleVoucherProduct}
                redeemVouchers={redeemVouchers}
                redeemedVoucherProducts={redeemedVoucherProducts}
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

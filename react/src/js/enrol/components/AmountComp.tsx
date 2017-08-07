import * as React from "react";
import classnames from "classnames";

import {Amount, Promotion, RedeemVoucher as RedeemVoucherModel} from "../../model";
import AddCodeComp from "./AddCodeComp";
import CheckoutService from "../services/CheckoutService";
import {Tabs} from "../containers/payment/reducers/State";

interface Props {
  amount: Amount;
  onAddCode: (code: string) => void;
  promotions: Promotion[];
  redeemVouchers?: any;
  onUpdatePayNow?: (val, validate?: boolean) => void;
  onToggleVoucher?: (redeemVoucher, enabled) => void;
  currentTab?: Tabs;
}

class AmountComp extends React.Component<Props, any> {
  private payNowInput;

  constructor(props) {
    super(props);
  }

  handleChangePayNow(val) {
    const {onUpdatePayNow, amount} = this.props;
    const reg = (/^[0-9]+\.?[0-9]*$/);

    if ((val > 0 || Number(val) === 0 && amount.minPayNow === 0) && reg.test(val)) {
      onUpdatePayNow(val);
    }
  }

  handleBlur(value) {
    const {onUpdatePayNow} = this.props;
    onUpdatePayNow(Number(value).toFixed(2), true);
  }

  render(): JSX.Element {
    const {amount, onAddCode, promotions, onUpdatePayNow, redeemVouchers, onToggleVoucher, currentTab} = this.props;
    const activeVoucherWithPayer = redeemVouchers && redeemVouchers.find(v => v.payer && v.enabled);

    return (
      <div className="row">
        <AddCodeComp onAdd={onAddCode} promotions={promotions}/>
        <div className="col-xs-24 col-sm-8 amount-content text-right">
          { amount && (amount.subTotal || amount.subTotal === 0) && <Total total={amount.subTotal}/> }

          { amount && redeemVouchers &&
            redeemVouchers.map(v => (
              <RedeemVoucher
                key={v.id}
                redeemVoucher={v}
                voucherPayment={amount.voucherPayments && amount.voucherPayments.find(
                  vp => vp.redeemVoucherId === v.id,
                )}
                disabled={!!(activeVoucherWithPayer && activeVoucherWithPayer.id !== v.id)}
                onChange={onToggleVoucher}
              />
            ))}

          { amount && amount.discount !== 0 && <Discount discount={amount.discount}/>}

          { (amount && currentTab !== Tabs.corporatePass) &&
            (amount.payNow || amount.payNow === 0) && amount.payNowVisibility &&
          <PayNow
            payNow={amount.payNow}
            onChange={onUpdatePayNow ? val => this.handleChangePayNow(val) : undefined}
            onBlur={el => this.handleBlur(el)}
          />
          }
        </div>
      </div>
    );
  }
}

const Total = props => {
  // show subTotal instead total
  return (
    <div className="row total-amount">
      <label className="col-xs-12">Total</label>
      <span className="col-xs-12">${parseFloat(props.total).toFixed(2)}</span>
    </div>
  );
};

const Discount = props => {
  return (
    <div className="row total-discount">
      <label className="col-xs-12">Discount</label>
      <span className="col-xs-12">${parseFloat(props.discount).toFixed(2)}</span>
    </div>
  );
};

const RedeemVoucher = props => {
  const {redeemVoucher, voucherPayment, onChange, disabled} = props;

  return (
    <div className="row">
      <label htmlFor="">
        <input
          type="checkbox"
          value="true"
          onChange={e => onChange(redeemVoucher, e.target.checked)}
          checked={redeemVoucher.enabled}
          disabled={disabled}
        />
        {redeemVoucher.name}
      </label>
      <span className="col-xs-12">${redeemVoucher.enabled ? voucherPayment.amount : 0}</span>
    </div>
  );
};

const PayNow = props => {

  const renderPayNowInput = () => (
    <span className="col-xs-12">
      <input
        type="text"
        value={`$${props.payNow}`}
        onChange={e => props.onChange(e.target.value.replace('$', ''))}
        onBlur={e => props.onBlur(this.payNowInput.value.replace('$', ''))}
        ref={el => this.payNowInput = el}
      />

    </span>
  );

  const renderPayNowValue = () => (
    <span className="col-xs-12">${parseFloat(props.payNow).toFixed(2)}</span>
  );

  return (
    <div className="row grand-total">
      <label className="col-xs-12">Pay Now</label>
      {props.onChange ? renderPayNowInput() : renderPayNowValue()}
    </div>
  );
};

export default AmountComp;

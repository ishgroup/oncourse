import * as React from "react";
import debounce from "debounce-promise";
import {Promotion} from "../../model";
import AddCodeComp from "./AddCodeComp";
import {Tabs} from "../containers/payment/reducers/State";
import {AmountState} from "../reducers/State";
import NumberFormat from "react-number-format";

interface Props {
  amount: AmountState;
  onAddCode: (code: string) => void;
  promotions: Promotion[];
  redeemVouchers?: any;
  onUpdatePayNow?: (val, validate?: boolean) => void;
  onToggleVoucher?: (redeemVoucher, enabled) => void;
  currentTab?: Tabs;
}

class AmountComp extends React.Component<Props> {
  handleChange = debounce((value): Promise<any> => {
    const {onUpdatePayNow} = this.props;

    const payNowVal = parseFloat(value);

    if (!isNaN(payNowVal) && payNowVal > 0) {
      onUpdatePayNow(payNowVal.toFixed(2), true);
    }

    return Promise.resolve();
  },600);

  handleBlur = () => {
    this.forceUpdate();
  }

  render(): JSX.Element {
    const {amount, onAddCode, promotions, redeemVouchers, onToggleVoucher, currentTab} = this.props;
    const activeVoucherWithPayer = redeemVouchers && redeemVouchers.find(v => v.payer && v.enabled);

    return (
      <div className="row">
        <AddCodeComp onAdd={onAddCode} promotions={promotions}/>
        <div className="col-xs-24 col-sm-8 amount-content text-right">
          {amount && (amount.subTotal || amount.subTotal === 0) && <Total total={amount.subTotal}/>}
          {amount && amount.discount !== 0 && <Discount discount={amount.discount}/>}

          {amount && redeemVouchers &&
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

          {(amount && currentTab !== Tabs.corporatePass) && amount.credit !== 0 && <Credit credit={amount.credit}/> }

          {(amount && currentTab !== Tabs.corporatePass) && ((amount.voucherPayments && amount.voucherPayments.length !== 0) || amount.credit !== 0)
          && amount.ccPayment !== 0 && <CCPayment credit={amount.ccPayment}/> }

          {(amount && currentTab !== Tabs.corporatePass) &&
          (amount.payNow || amount.payNow === 0) && amount.payNowVisibility &&
          <PayNow
            payNow={amount.payNow}
            onChange={this.handleChange}
            onBlur={this.handleBlur}
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

const Credit = props => {
  return (
    <div className="row total-credit">
    <label className="col-xs-12">Previous credit</label>
    <span className="col-xs-12">${parseFloat(props.credit).toFixed(2)}</span>
    </div>
  );
};

const CCPayment = props => {
  return (
      <div className="row total-cc">
        <label className="col-xs-12">Credit Card Amount</label>
        <span className="col-xs-12">${parseFloat(props.credit).toFixed(2)}</span>
      </div>
  );
};

const RedeemVoucher = props => {
  const {redeemVoucher, voucherPayment, onChange, disabled} = props;

  return (
    <div className="row">
      <label htmlFor="">
        <span>
        <input
          type="checkbox"
          value="true"
          onChange={e => onChange(redeemVoucher, e.target.checked)}
          checked={redeemVoucher.enabled}
          disabled={disabled}
        />
          {redeemVoucher.name}
        </span>
      </label>
      <span className="col-xs-12">${redeemVoucher.enabled ? voucherPayment && voucherPayment.amount : 0}</span>
    </div>
  );
};

const PayNow = props => {

  const renderPayNowInput = () => (
    <span className="col-xs-12">
      <NumberFormat
        type="text"
        name="priceValue"
        value={props.payNow}
        onChange={e => props.onChange(e.target.value.replace(/[$,]/g,""))}
        onBlur={e => props.onBlur(e)}
        style={{ margin: 0 }}
        prefix={'$ '}
        decimalScale={0}
        allowNegative={false}
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

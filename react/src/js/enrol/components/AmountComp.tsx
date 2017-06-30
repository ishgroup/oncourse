import * as React from "react";
import classnames from "classnames";

import {Amount} from "../../model/checkout/Amount";
import AddCodeComp from "./AddCodeComp";
import {Promotion} from "../../model/web/Promotion";
import CheckoutService from "../services/CheckoutService";
import log = Handlebars.log;

interface Props {
  amount: Amount;
  onAddCode: (code: string) => void;
  promotions: Promotion[];
  onUpdatePayNow?: (val) => void;
}

class AmountComp extends React.Component<Props, any> {
  constructor(props) {
    super(props);

    this.state = {errors: []};
  }

  private handleChangePayNow(val) {
    const {onUpdatePayNow, amount} = this.props;
    const reg = (/^[0-9]+\.?[0-9]*$/);

    if (val > 0 && reg.test(val)) {
      onUpdatePayNow(val);
      this.setState({errors: CheckoutService.validatePayNow({...amount, payNow: val})});
    }
  }

  public render(): JSX.Element {
    const {amount, onAddCode, promotions, onUpdatePayNow} = this.props;

    return (
      <div className="row">
        <AddCodeComp onAdd={onAddCode} promotions={promotions}/>
        <div className="col-xs-24 col-sm-8 amount-content text-right">
          { amount && amount.total && <Total total={amount.total}/> }
          { amount && amount.discount && <Discount discount={amount.discount}/>}
          { amount && amount.payNow &&
          <PayNow
            payNow={amount.payNow}
            onChange={val => onUpdatePayNow ? this.handleChangePayNow(val) : undefined}
            errors={this.state.errors}
          />
          }
        </div>
      </div>
    );
  }
}

const Total = props => {
  return (
    <div className="row total-amount">
      <label className="col-xs-12">Total</label>
      <span className="col-xs-12">{props.total}</span>
    </div>
  );
};

const Discount = props => {
  return (
    <div className="row total-discount">
      <label className="col-xs-12">Discount</label>
      <span className="col-xs-12">{props.discount}</span>
    </div>
  );
};

const PayNow = props => {

  const renderPayNowInput = () => (
    <span className="col-xs-12">
      <input
        type="text"
        value={props.payNow}
        onChange={e => props.onChange(e.target.value)}
        className={classnames({'t-error': props.errors.length})}
      />
      {props.errors.map((text, i) => (
        <span key={i} className="validate-text">{text}</span>
      ))}
    </span>
  );

  const renderPayNowValue = () => (
    <span className="col-xs-12">{props.payNow}</span>
  );

  return (
    <div className="row grand-total">
      <label className="col-xs-12">Pay Now</label>
      {props.onChange ? renderPayNowInput() : renderPayNowValue()}
    </div>
  );
};

export default AmountComp;

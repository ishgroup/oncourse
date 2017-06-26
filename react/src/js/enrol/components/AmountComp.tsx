import * as React from "react";
import {Amount} from "../../model/checkout/Amount";
import AddCodeComp from "./AddCodeComp";
import {Promotion} from "../../model/web/Promotion";

interface Props {
  amount: Amount;
  onAddCode: (code: string) => void;
  promotions: Promotion[];
}

class AmountComp extends React.Component<Props, any> {

  public render(): JSX.Element {
    const {amount, onAddCode, promotions} = this.props;
    return (
      <div className="row">
        <AddCodeComp onAdd = {onAddCode} promotions={promotions}/>
        <div className="col-xs-24 col-sm-8 amount-content text-right">
          { amount && amount.total && <Total total={amount.total}/> }
          { amount && amount.discount && <Discount discount={amount.discount}/>}
          { amount && amount.payNow && <PayNow payNow={amount.payNow}/>}
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
  return (
    <div className="row grand-total">
      <label className="col-xs-12">Pay Now</label>
      <span className="col-xs-12">{props.payNow}</span>
    </div>);
};

export default AmountComp;

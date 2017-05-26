import React from "react";
import {Amount} from "../../model/checkout/Amount";

interface Props {
  amount: Amount
}

class AmountComp extends React.Component<Props, any> {
  handlePromoCodeSubmit = (e) => {
    e.preventDefault();
  }

  public render(): JSX.Element {
    const {amount} = this.props;
    return (
      <div className="row">
        <EnterPromotional handlePromoCodeSubmit={this.handlePromoCodeSubmit.bind(this)} />
        <div className="col-xs-24 col-sm-8 amount-content text-right">
          { amount && amount.total && <Total total={amount.total}/> }
          { amount && amount.discount && <Discount discount={amount.discount}/>}
          { amount && amount.payNow && <PayNow payNow={amount.payNow}/>}
        </div>
      </div>
    );
  }
}

const EnterPromotional = (props) => {
  return (
    <div className="col-xs-24 col-sm-16 code-info">
      <div className="form-inline mb-2">
        <form onSubmit={props.handlePromoCodeSubmit}>
          <input className="form-control mb-2 mr-sm-2 mb-sm-0 code_input" name="add_code" type="text"/>
          <button type="submit" className="btn btn-primary button" id="addCode">Add Code</button>
        </form>
      </div>
      <p>Promotional Code,Gift Certificate or Voucher</p>
    </div>
  )
};

const Total = (props) => {
  return (
    <div className="row total-amount">
      <label className="col-xs-12">Total</label>
      <span className="col-xs-12">{props.total}</span>
    </div>
  )
};

const Discount = (props) => {
  return (
    <div className="row total-discount">
      <label className="col-xs-12">Discount</label>
      <span className="col-xs-12">{props.discount}</span>
    </div>
  )
};

const PayNow = (props) => {
  return (
    <div className="row grand-total">
      <label className="col-xs-12">Pay Now</label>
      <span className="col-xs-12">{props.payNow}</span>
    </div>)
};

export default AmountComp;
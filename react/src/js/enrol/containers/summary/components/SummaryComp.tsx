import * as React from "react";
import classnames from "classnames";
import AmountComp from "../../../components/AmountComp";
import ContactComp, {Props as ContactProps} from "./ContactComp";
import {Amount} from "../../../../model/checkout/Amount";
import {Enrolment} from "../../../../model/checkout/Enrolment";
import {Voucher} from "../../../../model/checkout/Voucher";
import {Props as VoucherProps} from "./VoucherComp";
import {Membership} from "../../../../model/checkout/Membership";
import {Article} from "../../../../model/checkout/Article";

export interface Props {
  hasSelected: boolean,
  contacts: ContactProps[]
  amount: Amount
  onAddContact?: () => void
  onProceedToPayment?: () => void
  onSelect?: (item: Enrolment | Membership | Article | Voucher, selected: boolean) => void
  onPriceValueChange?: (item: any, product: VoucherProps[], productItem: Voucher) => void
}


export class SummaryComp extends React.Component<Props, any> {
  renderContact = (props: ContactProps) => {
    const {onSelect, onPriceValueChange} = this.props;
    return (<ContactComp key={props.contact.id} {...props}
                         onSelect={(item, selected) => onSelect(item, selected)}
                         onPriceValueChange={(item, product, productItem) => onPriceValueChange(item, product, productItem)} />)
  };

  render() {
    const {contacts, amount, onAddContact, onProceedToPayment, hasSelected} = this.props;

    return (
      <div className="payment-summary">
        {contacts.map((c) => this.renderContact(c))}
        <AddAnotherContact onAddContact={onAddContact}/>

        <div className="row">
          <div className="col-xs-24">
            <div className="amount-container">
              <AmountComp amount={amount}/>
              <ProceedToPayment disabled={!hasSelected} onProceedToPayment={onProceedToPayment}/>
            </div>
          </div>
        </div>

      </div>
    );
  }
}

const ProceedToPayment = (props) => {
  const {disabled, onProceedToPayment} = props;
  const className = classnames("btn", "btn-primary", {"disabled": disabled});
  const onClick = (e) => {
    e.preventDefault();
    onProceedToPayment();
  };
  return (
    <button className={className} onClick={onClick} disabled={disabled}>
      Proceed to Payment
    </button>
  );
};

const AddAnotherContact = (props) => {
  const {onAddContact} = props;
  const onClick = (e) => {
    e.preventDefault();
    onAddContact();
  };
  return (
    <div className="row" id="totals">
      <div className="col-xs-24">
        <a id="addContact" href="#addContact" onClick={onClick}>Add another student</a>
      </div>
    </div>
  )
};

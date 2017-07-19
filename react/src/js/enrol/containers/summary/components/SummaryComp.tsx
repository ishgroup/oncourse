import * as React from "react";
import classnames from "classnames";

import AmountComp from "../../../components/AmountComp";
import ContactComp, {Props as ContactProps} from "./ContactComp";
import {Amount, RedeemVoucher, Promotion, PurchaseItem} from "../../../../model";

export interface Props {
  hasSelected: boolean;
  contacts: ContactProps[];
  amount: Amount;
  onAddContact?: () => void;
  onAddCode: (code: string) => void;
  promotions: Promotion[];
  redeemVouchers?: RedeemVoucher[];
  onToggleVoucher?: (redeemVoucher: RedeemVoucher, enabled) => void;
  onProceedToPayment?: () => void;
  onSelect?: (item: PurchaseItem, selected: boolean) => void;
  onPriceValueChange?: (productItem: PurchaseItem, val: any) => void;
  onAddConcession?: () => void;
  onInit?: () => void;
  concessions?: any;
  onUpdatePayNow?: (amount, val) => void;
  needParent?: boolean;
}


export class SummaryComp extends React.Component<Props, any> {
  constructor(props) {
    super(props);
  }

  componentDidMount() {
    if (this.props.onInit) {
      this.props.onInit();
    }
  }

  renderContact = (props: ContactProps) => {
    const {onSelect, onPriceValueChange, onAddConcession, concessions} = this.props;

    return (
      <ContactComp
        {...props}
        key={props.contact.id}
        onSelect={(item, selected) => onSelect(item, selected)}
        onPriceValueChange={(productItem, val) => onPriceValueChange(productItem, val)}
        onAddConcession={onAddConcession}
        concessions={concessions[props.contact.id]}
      />
    );
  }

  render() {
    const {contacts, amount, onAddContact, onAddCode, onProceedToPayment,
      redeemVouchers, hasSelected, promotions, onUpdatePayNow, onToggleVoucher, needParent} = this.props;

    return (
      <div className="payment-summary">
        {contacts.map(c => this.renderContact(c))}
        <AddAnotherContact onAddContact={onAddContact}/>

        <div className="row">
          <div className="col-xs-24">
            <div className="amount-container">
              <AmountComp
                amount={amount}
                onUpdatePayNow={amount.isEditable ? onUpdatePayNow : undefined}
                onAddCode={onAddCode}
                onToggleVoucher={onToggleVoucher}
                redeemVouchers={redeemVouchers}
                promotions={promotions}
              />
              <ProceedToPayment
                needParent={needParent}
                disabled={!hasSelected}
                onProceedToPayment={() => needParent ? onAddContact() : onProceedToPayment()}
              />
            </div>
          </div>
        </div>

      </div>
    );
  }
}

const ProceedToPayment = props => {
  const {disabled, onProceedToPayment, needParent} = props;
  const className = classnames("btn", "btn-primary", {disabled});

  const onClick = e => {
    e.preventDefault();
    onProceedToPayment();
  };

  return (
    <button className={className} onClick={onClick} disabled={disabled}>
      {needParent ? 'Add Guardian' : 'Proceed to Payment'}
    </button>
  );
};

const AddAnotherContact = props => {
  const {onAddContact} = props;

  const onClick = e => {
    e.preventDefault();
    onAddContact();
  };

  return (
    <div className="row" id="totals">
      <div className="col-xs-24">
        <a id="addContact" href="#addContact" onClick={onClick}>Add another student</a>
      </div>
    </div>
  );
};

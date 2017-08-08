import * as React from "react";
import classnames from "classnames";

import AmountComp from "../../../components/AmountComp";
import ContactComp, {Props as ContactProps} from "./ContactComp";
import {Amount, RedeemVoucher, Promotion, PurchaseItem} from "../../../../model";
import {StudentMembership} from "../../../../model/checkout/StudentMembership";
import {Concession} from "../../../../model/checkout/concession/Concession";

export interface Props {
  hasSelected: boolean;
  contacts: ContactProps[];
  amount: Amount;
  onAddContact?: (guardian?: boolean) => void;
  onAddCode: (code: string) => void;
  promotions: Promotion[];
  redeemVouchers?: RedeemVoucher[];
  onToggleVoucher?: (redeemVoucher: RedeemVoucher, enabled) => void;
  onProceedToPayment?: () => void;
  onSelect?: (item: PurchaseItem, selected: boolean) => void;
  onPriceValueChange?: (productItem: PurchaseItem, val: any) => void;
  onAddConcession?: () => void;
  onInit?: () => void;
  concessions?: Concession[];
  memberships?: StudentMembership[];
  onUpdatePayNow?: (val, validate?: boolean) => void;
  needParent?: boolean;
  fetching?: boolean;
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
    const {onSelect, onPriceValueChange, onAddConcession, concessions, memberships, onAddContact} = this.props;

    return (
      <ContactComp
        {...props}
        key={props.contact.id}
        onSelect={(item, selected) => onSelect(item, selected)}
        onPriceValueChange={(productItem, val) => onPriceValueChange(productItem, val)}
        onChangeParent={() => onAddContact(true)}
        onAddConcession={onAddConcession}
        concessions={concessions.filter(item => item.contactId === props.contact.id)}
        studentMemberships={memberships.filter(item => item.contactId === props.contact.id)}
      />
    );
  }

  render() {
    const {contacts, amount, onAddContact, onAddCode, onProceedToPayment, fetching,
      redeemVouchers, hasSelected, promotions, onUpdatePayNow, onToggleVoucher, needParent} = this.props;

    return (
      <div className="payment-summary">
        <div className={classnames("contacts-summary", {fetching})}>
          {contacts.map(c => this.renderContact(c))}
        </div>
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
        <a className="new-student-link" id="addContact" href="#addContact" onClick={onClick}>Add another student</a>
      </div>
    </div>
  );
};

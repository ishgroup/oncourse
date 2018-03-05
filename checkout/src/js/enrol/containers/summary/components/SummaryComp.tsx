import * as React from "react";
import classnames from "classnames";

import AmountComp from "../../../components/AmountComp";
import ContactComp, {Props as ContactProps} from "./ContactComp";
import {Amount, RedeemVoucher, Promotion, PurchaseItem, ConcessionType} from "../../../../model";
import {StudentMembership} from "../../../../model/checkout/StudentMembership";
import {Concession} from "../../../../model/checkout/concession/Concession";
import {scrollToTop} from "../../../../common/utils/DomUtils";

export interface Props {
  hasSelected: boolean;
  contacts: ContactProps[];
  amount: Amount;
  onAddContact?: () => void;
  onAddParent?: () => void;
  onChangeParent?: (contactId: string) => void;
  onAddCode: (code: string) => void;
  promotions: Promotion[];
  redeemVouchers?: RedeemVoucher[];
  onToggleVoucher?: (redeemVoucher: RedeemVoucher, enabled) => void;
  onProceedToPayment?: (forms) => void;
  onProceedToJoin?: (forms) => void;
  onSelect?: (item: PurchaseItem, selected: boolean) => void;
  onPriceValueChange?: (productItem: PurchaseItem, val: any) => void;
  onUpdateWaitingCourse?: () => void;
  onAddConcession?: () => void;
  onInit?: () => void;
  concessions?: Concession[];
  concessionTypes?: ConcessionType[];
  memberships?: StudentMembership[];
  onUpdatePayNow?: (val, validate?: boolean) => void;
  needParent?: boolean;
  fetching?: boolean;
  onChangeEnrolmentFields?: (form, type) => any;
  forms?: any;
  isOnlyWaitingLists?: boolean;
}


export class SummaryComp extends React.Component<Props, any> {
  constructor(props) {
    super(props);
  }

  componentDidMount() {
    scrollToTop();
    
    if (this.props.onInit) {
      this.props.onInit();
    }
  }

  renderContact = (props: ContactProps) => {
    const {onSelect, onPriceValueChange, onAddConcession, concessions, memberships, onChangeParent,
      onUpdateWaitingCourse, onChangeEnrolmentFields, concessionTypes} = this.props;

    return (
      <ContactComp
        {...props}
        key={props.contact.id}
        onSelect={(item, selected) => onSelect(item, selected)}
        onPriceValueChange={(productItem, val) => onPriceValueChange(productItem, val)}
        onChangeParent={onChangeParent}
        onAddConcession={onAddConcession}
        concessions={concessions.filter(item => item.contactId === props.contact.id)}
        concessionTypes={concessionTypes}
        studentMemberships={memberships.filter(item => item.contactId === props.contact.id)}
        onUpdateWaitingCourse={onUpdateWaitingCourse}
        onChangeEnrolmentFields={onChangeEnrolmentFields}
      />
    );
  }

  render() {
    const {
      contacts, amount, onAddContact, onAddCode, onProceedToPayment, fetching, onAddParent, forms, onProceedToJoin,
      redeemVouchers, hasSelected, promotions, onUpdatePayNow, onToggleVoucher, needParent, isOnlyWaitingLists,
    } = this.props;

    const buttonLabel = needParent && !isOnlyWaitingLists ? 'Add Guardian' :
      !Number(amount.total) ? 'Proceed' : 'Proceed to Payment';

    const onProceed = needParent && !isOnlyWaitingLists ? () => onAddParent() :
      isOnlyWaitingLists ? () => onProceedToJoin(forms) : () => onProceedToPayment(forms);

    const haveTotal = !!(amount && Number(amount.total));

    return (
      <div className="payment-summary">
        <div className={classnames("contacts-summary", {fetching})}>
          {contacts.map(c => this.renderContact(c))}
        </div>
        <AddAnotherContact onAddContact={onAddContact}/>

        <div className="row">
          <div className="col-xs-24">
            <div className={classnames("amount-container", {"zero-total": !haveTotal})}>

              {haveTotal &&
                <AmountComp
                  amount={amount}
                  onUpdatePayNow={amount.isEditable ? onUpdatePayNow : undefined}
                  onAddCode={onAddCode}
                  onToggleVoucher={onToggleVoucher}
                  redeemVouchers={redeemVouchers}
                  promotions={promotions}
                />
              }

              <ProceedToPayment
                buttonLabel={buttonLabel}
                disabled={!hasSelected}
                onProceedToPayment={onProceed}
              />
            </div>
          </div>
        </div>

      </div>
    );
  }
}

const ProceedToPayment = props => {
  const {disabled, onProceedToPayment, buttonLabel} = props;
  const className = classnames("btn", "btn-primary", {disabled});

  const onClick = e => {
    e.preventDefault();
    onProceedToPayment();
  };

  return (
    <button className={className} onClick={onClick} disabled={disabled}>
      {buttonLabel}
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

import * as React from "react";
import classnames from "classnames";
import AmountComp from "../../../components/AmountComp";
import ContactComp, {Props as ContactProps} from "./ContactComp";
import {Amount, RedeemVoucher, Promotion, PurchaseItem, ConcessionType, Voucher, Article} from "../../../../model";
import {StudentMembership} from "../../../../model";
import {Concession} from "../../../../model";
import {scrollToTop} from "../../../../common/utils/DomUtils";

export interface Props {
  hasSelected: boolean;
  contacts: ContactProps[];
  amount: Amount;
  onAddContact?: () => void;
  onAddParent?: () => void;
  onRemoveContact?: () => void;
  onChangeParent?: (contactId: string) => void;
  onAddCode: (code: string) => void;
  promotions: Promotion[];
  redeemVouchers?: RedeemVoucher[];
  onToggleVoucher?: (redeemVoucher: RedeemVoucher, enabled) => void;
  onProceedToPayment?: (forms) => void;
  onProceedToJoin?: (forms) => void;
  onSelect?: (item: PurchaseItem, selected: boolean) => void;
  onPriceValueChange?: (productItem: Voucher, val: number) => void;
  onQuantityValueChange?: (productItem: Voucher|Article, val: number) => void;
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
    this.state = {
      isAutoSubmitting: false
    }
  }

  componentDidMount() {
    const { onInit } = this.props;

    scrollToTop();

    if (onInit) {
      onInit();
    }
  }

  componentDidUpdate() {
    const { contacts, isOnlyWaitingLists, forms, onProceedToJoin } = this.props;
    const { isAutoSubmitting } = this.state;

    if (!isAutoSubmitting && contacts.length && isOnlyWaitingLists) {
      const hasNoFormFieldsOrErrors = !contacts.some(c => c.waitingLists.some(wl =>
        wl.waitingList.fieldHeadings.length || wl.waitingList.errors.length)
      )

      if(hasNoFormFieldsOrErrors) {
        this.setState({
          isAutoSubmitting: true
        })
        onProceedToJoin(forms)
      }
    }
  }

  renderContact = (props: ContactProps) => {
    const {onSelect, onPriceValueChange, onQuantityValueChange, onAddConcession, concessions, memberships, onChangeParent,
      onUpdateWaitingCourse, onChangeEnrolmentFields, concessionTypes, onRemoveContact} = this.props;

    return (
      <ContactComp
        {...props}
        key={props.contact.id}
        onSelect={(item, selected) => onSelect(item, selected)}
        onPriceValueChange={(productItem, val) => onPriceValueChange(productItem, val)}
        onQuantityValueChange={(productItem, val) => onQuantityValueChange(productItem, val)}
        onChangeParent={onChangeParent}
        onAddConcession={onAddConcession}
        concessions={concessions.filter(item => item.contactId === props.contact.id)}
        concessionTypes={concessionTypes}
        studentMemberships={memberships.filter(item => item.contactId === props.contact.id)}
        onUpdateWaitingCourse={onUpdateWaitingCourse}
        onChangeEnrolmentFields={onChangeEnrolmentFields}
        onRemoveContact={onRemoveContact}
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
          {contacts.map(c => c.contact && this.renderContact(c))}
        </div>
        <AddAnotherContact onAddContact={onAddContact}/>

        <div className="row">
          <div className="col-xs-24">
            {hasSelected &&
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
            }
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

  return (!disabled &&
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

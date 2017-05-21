import * as React from "react";
import * as L from "lodash";
import classnames from "classnames";
import AmountComp from "../../components/AmountComp";
import {default as ContactComp, Props as ContactProps} from "./components/ContactComp";
import {Amount} from "../../../model/checkout/Amount";

interface Props {
  contacts: ContactProps[]
  amount: Amount,
  onAddContact: () => void;
  onProceedToPayment: () => void;
}


class Summary extends React.Component<Props, any> {
  private hasEnabled = (contacts: ContactProps[]): boolean => {
    return L.map(contacts, (c) => {
        c.enrolments.find((e) => (e.selected && e.enrolment.errors.length === 0))
      }).length > 0;
  };

  render() {
    const {contacts, amount, onAddContact, onProceedToPayment} = this.props;
    const hasEnabled: boolean = this.hasEnabled(contacts);
    return (
      <div className="col-xs-24">
        <div className="row">
          {contacts.map((c, i) => <ContactComp key={i} {...c}/>)}
          <AddAnotherContact onAddContact={onAddContact}/>
          <AmountComp amount={amount}/>
          <ProceedToPayment disabled={!hasEnabled} onProceedToPayment={onProceedToPayment}/>
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
    <button className={className} onClick={onClick}>
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

export default Summary
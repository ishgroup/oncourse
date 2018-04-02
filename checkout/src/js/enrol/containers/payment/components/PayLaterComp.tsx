import React from "react";
import {Contact} from "../../../../model";
import {PayerSelect} from "./PayerSelect";
import {PayerAdd} from "./PayerAdd";

interface Props {
  contacts: Contact[];
  onSetPayer?: (id: string) => void;
  onAddPayer?: () => any;
  onAddCompany?: () => any;
  payerId?: string;
  voucherPayerEnabled?: boolean;
  onInit: () => void;
}

class PayLaterComp extends React.Component<Props, any> {
  componentDidMount() {
    this.props.onInit();
  }

  render() {
    const {contacts, onSetPayer, payerId, onAddPayer, onAddCompany, voucherPayerEnabled} = this.props;

    return (
      <div id="credit-card" className="single-tab active">
        <div id="paymentEditor">
          <div className="enrolmentsSelected">
            <fieldset>
              <div className="form-details">
                <PayerSelect
                  contacts={contacts}
                  payer={contacts.find(c => c.id === payerId)}
                  onChange={onSetPayer}
                  disabled={voucherPayerEnabled}
                />

                <PayerAdd
                  onAddPayer={onAddPayer}
                  onAddCompany={onAddCompany}
                  disabled={voucherPayerEnabled}
                />

              </div>
            </fieldset>
          </div>
        </div>
      </div>
    );
  }
}

export default PayLaterComp;

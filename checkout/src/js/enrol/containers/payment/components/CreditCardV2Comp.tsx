
import React from "react";
import {Amount, Contact} from "../../../../model";
import {PayerSelect} from "./PayerSelect";
import {PayerAdd} from "./PayerAdd";
import {PaymentRequest} from "../../../../model/v2/checkout/payment/PaymentRequest";
import {AmexImg, VisaMasterCardImg} from "./CreditCardImages";
import CreditCardHeader from "./CreditCardHeader";

interface Props {
  contacts: Contact[];
  amount: Amount;
  onSetPayer?: (id: string) => void;
  onAddPayer?: () => any;
  onAddCompany?: () => any;
  onInit?: () => any;
  payerId?: string;
  voucherPayerEnabled?: boolean;
  iframeUrl: string;
  processPaymentV2: (xValidateOnly: boolean, payerId: string, referer: string) => void;
}


class CreditCardV2Comp extends React.Component<Props, any> {

  componentDidMount() {
    const {
       payerId, processPaymentV2
    } = this.props;

    processPaymentV2(true, payerId, "");
  }

  componentDidUpdate(prev) {
    const {
      amount, payerId, processPaymentV2
    } = this.props;

    if (prev.amount !== amount || prev.payerId !== payerId) {
      processPaymentV2(true, payerId, "");
    }
  }

  render() {
    const {
      contacts, amount, onSetPayer, payerId, onAddPayer,
      onAddCompany, voucherPayerEnabled,
      iframeUrl,
    } = this.props;

    return (
      <div id="credit-card" className="single-tab active">
        <CreditCardHeader/>
        <div className="enrolmentsSelected">
          <fieldset>
            <div className="form-details">
              <p>
                <label>Pay now</label>
                <span style={{bottom: "-4px"}}>
                  ${Number(amount.ccPayment).toFixed(2)}
                </span>
              </p>

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

                {iframeUrl && <iframe src={iframeUrl} frameBorder={0} height="575" width="auto"/>}
              </div>
            </fieldset>
          </div>
        </div>
    );
  }
}

export default CreditCardV2Comp;

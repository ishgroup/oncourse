
import React from "react";
import {Amount, Contact} from "../../../../model";
import {PayerSelect} from "./PayerSelect";
import {PayerAdd} from "./PayerAdd";
import {VisaMasterCardImg, AmexImg, CvvImg} from "./CreditCardImages";
import {PaymentRequest} from "../../../../model/v2/checkout/payment/PaymentRequest";

interface Props {
  contacts: Contact[];
  amount: Amount;
  onSetPayer?: (id: string) => void;
  onAddPayer?: () => any;
  onAddCompany?: () => any;
  onInit?: () => any;
  payerId?: string;
  voucherPayerEnabled?: boolean;
  amexEnabled?: boolean;
  iframeUrl: string;
  processPaymentV2: (paymentRequest: PaymentRequest, xValidateOnly: boolean, payerId: string, referer: string) => void;
}


class CreditCardV2Comp extends React.Component<Props, any> {

  componentDidMount() {
    const {
      amount, payerId
    } = this.props;



  }

  render() {
    const {
      contacts, amount, onSetPayer, payerId, onAddPayer,
      onAddCompany, voucherPayerEnabled, amexEnabled,
      iframeUrl
    } = this.props;

    return (
      <div id="credit-card" className="single-tab active">
        <div id="paymentEditor">
          <div className="enrolmentsSelected">
            <fieldset>
              <div className="form-details">
                <p>
                  <label>Pay now</label>
                  <span id="cardtotalstring">
                    ${Number(amount.ccPayment).toFixed(2)}
                    <VisaMasterCardImg/>
                    {amexEnabled && <AmexImg/>}
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
      </div>
    );
  }
}

export default CreditCardV2Comp;

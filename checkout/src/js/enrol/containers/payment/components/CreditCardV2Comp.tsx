import React from "react";
import {Amount, Contact} from "../../../../model";
import {PayerSelect} from "./PayerSelect";
import {PayerAdd} from "./PayerAdd";
import CreditCardHeader from "./CreditCardHeader";
import {processPaymentV2FailedStatus, setIframeUrl} from "../actions/Actions";
import {Dispatch} from "redux";

interface Props {
  contacts: Contact[];
  amount: Amount;
  onSetPayer?: (id: string) => void;
  onAddPayer?: () => any;
  onAddCompany?: () => any;
  onInit?: () => any;
  payerId?: string;
  voucherPayerEnabled?: boolean;
  disabled?: boolean;
  iframeUrl: string;
  processPaymentV2: (xValidateOnly: boolean, sessionId: string) => void;
  dispatch: Dispatch<any>;
}


class CreditCardV2Comp extends React.Component<Props, any> {
  iframeRef = (React as any).createRef();

  onMessage = e => {
    const {dispatch, processPaymentV2} = this.props;
    const paymentData = e.data.payment;
    if (paymentData && paymentData.status && paymentData.sessionId) {
      dispatch(setIframeUrl(null));
      if (paymentData.status === "success") {
        processPaymentV2(false, paymentData.sessionId);
      } else {
        dispatch(processPaymentV2FailedStatus());
      }
    }
  }

  componentDidMount() {
    const {
      processPaymentV2
    } = this.props;

    processPaymentV2(true, null);

    window.addEventListener("message", this.onMessage);
  }

  componentWillUnmount() {
    window.removeEventListener("message", this.onMessage);
  }

  componentDidUpdate(prev) {
    const {
      iframeUrl
    } = this.props;

    if (this.iframeRef.current && prev.iframeUrl !== iframeUrl) {
      this.iframeRef.current.contentWindow.location.replace(iframeUrl);
    }
  }

  render() {
    const {
      contacts, amount, onSetPayer, payerId, onAddPayer,
      onAddCompany, voucherPayerEnabled,
      iframeUrl, disabled,
    } = this.props;

    return (
      <div id="credit-card" className="single-tab active">
        <CreditCardHeader />
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
              {iframeUrl && <iframe ref={this.iframeRef} frameBorder={0} height="575" width="100%" style={
                disabled ? {
                  opacity: 0.6,
                  pointerEvents: "none",
                } : null}/>}
              </div>
            </fieldset>
          </div>
        </div>
    );
  }
}

export default CreditCardV2Comp;

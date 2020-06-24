import React, {useCallback, useEffect, useState} from "react";
import CheckoutService from "../services/CheckoutService";
import {getPaymentRequest} from "../utils";
import debounce from "lodash.debounce";
import {fail} from "assert";


const getPaymentMessage = ({status, message}: any) => {
  switch (status) {
    case "success":
      return <div className="alert alert-success" role="alert"><strong>Success. </strong>{message || "Payment processed"}</div>;
    case "fail":
      return <div className="alert alert-danger" role="alert"><strong>Error. </strong>{message || "Payment failed"}</div>;
    case "cancel":
      return <div className="alert alert-warning" role="alert">{message || "Payment canceled"}</div>;
  }
};

const PaymentForm: React.FC<any> = ({}) => {
  const [iframeUrl, setIframeUrl] = useState<any>( undefined);
  const [amountError, setAmountError] = useState<any>( null);
  const [amountInitial, setAmountInitial] = useState<any>( null);
  const [amount, setAmount] = useState<any>( null);
  const [amountValue, setAmountValue] = useState<any>( null);
  const [payerId, setPayerId] = useState<any>( null);
  const [paymentStatus, setPaymentStatus] = useState<any>( null);

  const onMessage = (e: any) => {
    const paymentDetails = e.data.payment;
    if (paymentDetails && paymentDetails.status) {
      setPaymentStatus(paymentDetails);
    }
  };

  useEffect(() => {
    window.addEventListener("message", onMessage);
    return () => {
      window.removeEventListener("message", onMessage);
    };
  },        []);

  useEffect(() => {
    const root = document.getElementById("react-payment-form");


    if (root) {
      const payerId = root.getAttribute("data-payerId");
      const amount = root.getAttribute("data-balance");

      if (payerId && amount) {
        setPayerId(payerId);
        const amountParsed = parseFloat(amount);
        setAmountInitial(amountParsed);
        setAmount(amountParsed);
        setAmountValue(amountParsed);
      }
    }
  },        []);

  const validateAmount = (amount: number) => {
    if (amount > amountInitial || amount <= 0) {
      if (!amountError) {
        setAmountError(true);
      }
      return false;
    }
    if (amountError) {
      setAmountError(false);
    }
    return true;
  };

  useEffect(() => {
    if (payerId && amount && validateAmount(amount)) {
      setIframeUrl(null);
      CheckoutService.makePayment(getPaymentRequest(amount, payerId), true, payerId)
        .then(res => {
          setIframeUrl(res);
        })
        .catch(() => {
          setPaymentStatus({status: "fail", message: "Failed to connect payment gateway"});
        });
    }
  },        [amount]);

  const debounceAmountChange = useCallback(debounce((amount: any) => {
    setAmount(amount);
  },                                                600),                                 []);

  const onAmountChange = (e: any) => {
    validateAmount(e.target.value);
    setAmountValue(e.target.value);
    debounceAmountChange(e.target.value);
  };

  return (
    <div>

      <div className="row">
        <div className="col-sm-3">
          <div className="form-group">
            <div className={amountError ? "has-error" : "valid"}>
              <input
                onChange={onAmountChange}
                value={amountValue}
                className="form-control"
                type="number"
                name="amount"
                placeholder="Amount"
              />
            </div>
          </div>
        </div>
      </div>

      {paymentStatus ?
        getPaymentMessage(paymentStatus)
        : (iframeUrl ?
        <iframe src={iframeUrl}  title="windcave-frame" />
        : <div id="payment-progress-bar">
            <div className="progress progress-striped active ">
              <div className="progress-bar progress-bar-warning progress-bar-striped " role="progressbar"
                   aria-valuenow={100} aria-valuemin={0} aria-valuemax={100} style={{width: "100%"}}>
              </div>
            </div>
        </div>)
      }
    </div>);
};

export default PaymentForm;

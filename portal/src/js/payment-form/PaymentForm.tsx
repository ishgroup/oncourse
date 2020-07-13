import React, {useCallback, useEffect, useState} from "react";
import CheckoutService from "../services/CheckoutService";
import {getPaymentRequest} from "../utils";
import debounce from "lodash.debounce";
import {fail} from "assert";
import {getErrorMessage} from "../services/ApiErrorHandler";
import {PaymentResponse} from "../model/api";


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
  const [totalBalance, setTotalBalance] = useState<any>( null);
  const [overdue, setOverdue] = useState<any>( null);
  const [amount, setAmount] = useState<any>( null);
  const [amountValue, setAmountValue] = useState<any>( null);
  const [payerId, setPayerId] = useState<any>( null);
  const [paymentStatus, setPaymentStatus] = useState<any>( null);
  const [paymentFetching, setPaymentFetching] = useState<any>( null);
  const [paymentDetails, setPaymentDetails] = useState<PaymentResponse>(null);

  const onMessage = (e: any) => {
    const paymentData = e.data.payment;
    if (paymentData && paymentData.status) {
      setIframeUrl(null);
      setPaymentFetching(true);
      if (paymentData.status === "success") {
        CheckoutService.makePayment(getPaymentRequest(amount, paymentDetails), false, payerId).then(res => {
          setPaymentStatus({status: res.status === "FAILED" ? "fail" : "success",
            message: <span>
              Payment processed. Payment reference:
              {" "}
              <strong>{res.reference}</strong>
              . History can be updated</span>,
          });
          setPaymentFetching(false);
        })
        .catch(res => {
          setPaymentFetching(false);
          setPaymentStatus({status: "fail", message: getErrorMessage(res) || "Payment was not processed"});
        });
      } else {
        CheckoutService.getPaymentStatus(paymentData.sessionId, payerId).then(res => {
          setPaymentStatus({status: paymentData.status, message: res.responseText});
          setPaymentFetching(false);
        })
        .catch(res => {
          setPaymentStatus({status: paymentData.status, message: res.message});
          setPaymentFetching(false);
        });
      }
    }
  };

  useEffect(() => {
    window.addEventListener("message", onMessage);
    return () => {
      window.removeEventListener("message", onMessage);
    };
  },        [amount,payerId,paymentDetails]);

  useEffect(() => {
    const root = document.getElementById("react-payment-form");


    if (root) {
      const payerId = root.getAttribute("data-payerId");
      const balance = root.getAttribute("data-balance");
      const overdue = root.getAttribute("data-overdue");

      if (payerId && balance && overdue) {
        setPayerId(payerId);
        const balanceParsed = parseFloat(balance);
        const overdueParsed = parseFloat(overdue);
        setOverdue(overdueParsed);
        setTotalBalance(balanceParsed);
        setAmount(overdueParsed);
        setAmountValue(overdueParsed);
      }
    }

    return () => {
      setIframeUrl(null);
      setAmountError(null);
      setTotalBalance(null);
      setAmount(null);
      setAmountValue(null);
      setPaymentStatus(null);
      setPaymentDetails(null);
    };
  },        []);

  const validateAmount = (amount: number) => {
    if (amount > totalBalance || amount <= 0) {
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
      setPaymentStatus(null);

      setPaymentFetching(true);

      CheckoutService.makePayment(getPaymentRequest(amount), true, payerId)
        .then(res => {
          setIframeUrl(res.paymentFormUrl);
          setPaymentFetching(false);
          setPaymentDetails(res);
        })
        .catch(res => {
          setPaymentFetching(false);
          setPaymentStatus({status: "fail", message: getErrorMessage(res) || "Failed to connect payment gateway"});
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

  return Boolean(totalBalance && totalBalance > 0) ? (
    <div className={amountError ? "has-error" : "valid"}>
      <div className="info-label">Payment due: ${overdue} </div>

      {!paymentStatus && !paymentFetching && <div>
        <div className="amount-container">
          <div className="row">
            <div className="col-xs-5 amount-label">
              <label>
                Amount:
                <span>*</span>
              </label>
            </div>
            <div>
              <input
                required
                min={1}
                max={totalBalance}
                onChange={onAmountChange}
                value={amountValue}
                type="number"
                name="amount"
                placeholder="Amount"
                className="amount-input"
                pattern="^\d*(\.\d{2}$)?"
              />
            </div>
          </div>
        </div>
      </div>}

      {paymentStatus && getPaymentMessage(paymentStatus)}
      {iframeUrl &&  <iframe src={iframeUrl}  title="windcave-frame" />}
      {paymentFetching && <div id="payment-progress-bar">
        <div className="progress progress-striped active ">
          <div className="progress-bar progress-bar-warning progress-bar-striped " role="progressbar"
               aria-valuenow={100} aria-valuemin={0} aria-valuemax={100} style={{width: "100%"}}>
          </div>
        </div>
      </div>}
    </div>) : null;
};

export default PaymentForm;

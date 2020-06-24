import React, {useCallback, useEffect, useState} from "react";
import CheckoutService from "../services/CheckoutService";
import {getPaymentRequest} from "../utils";
import {getErrorMessage} from "../services/ApiErrorHandler";
import debounce from "lodash.debounce";

const PaymentForm: React.FC<any> = ({}) => {
  const [iframeUrl, setIframeUrl] = useState<any>( undefined);
  const [paymentError, setPaymentError] = useState<any>( null);
  const [amount, setAmount] = useState<any>( null);
  const [amountValue, setAmountValue] = useState<any>( null);
  const [payerId, setPayerId] = useState<any>( null);

  useEffect(() => {
    if (payerId && amount) {
      setIframeUrl(null);
      CheckoutService.makePayment(getPaymentRequest(amount, payerId), true, payerId)
        .then(res => {
          setIframeUrl(res);
        })
        .catch(res => {
          setPaymentError(getErrorMessage(res) || "Failed to connect payment gateway");
        });
    }
  },        [amount]);

  useEffect(() => {
    const root = document.getElementById("react-payment-form");


    if (root) {
      const payerId = root.getAttribute("data-payerId");
      const amount = root.getAttribute("data-balance");

      if (payerId && amount) {
        setPayerId(payerId);
        setAmount(parseFloat(amount));
        setAmountValue(parseFloat(amount));
      }
    }
  },        []);

  const debounceAmountChange = useCallback(debounce((amount: any) => {
    setAmount(amount);
  },                                                600),                                  []);

  const onAmountChange = (e: any) => {
    setAmountValue(e.target.value);
    debounceAmountChange(e.target.value);
  };

  return (
    <div>

      <div className="row">
        <div className="col-sm-3">
          <div className="form-group">
            <div className="valid">
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

      {paymentError ? <div className="alert alert-danger" role="alert"><strong>Error. </strong>{paymentError}</div> : (iframeUrl ?
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

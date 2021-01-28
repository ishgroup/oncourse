/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React from "react";
import clsx from "clsx";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import withStyles from "@material-ui/core/styles/withStyles";
import Typography from "@material-ui/core/Typography";
import {
  CheckoutPayment,
  CheckoutPaymentProcess,
  CheckoutSummary
} from "../../../../../../model/checkout";
import { State } from "../../../../../../reducers/state";
import {
  checkoutClearPaymentStatus, checkoutGetPaymentStatusDetails,
  checkoutPaymentSetCustomStatus, checkoutProcessCcPayment,
  checkoutSetPaymentSuccess,
  clearCcIframeUrl
} from "../../../../actions/checkoutPayment";
import PaymentMessageRenderer from "../PaymentMessageRenderer";
import { BooleanArgFunction, StringArgFunction } from "../../../../../../model/common/CommonFunctions";
import { formatCurrency } from "../../../../../../common/utils/numbers/numbersNormalizing";
import styles from "./styles";

interface CreditCardPaymentPageProps {
  classes?: any;
  summary?: CheckoutSummary;
  payment?: CheckoutPayment;
  isPaymentProcessing?: boolean;
  disablePayment?: boolean;
  setPaymentSuccess?: BooleanArgFunction;
  currencySymbol?: any;
  iframeUrl?: string;
  xPaymentSessionId?: string;
  merchantReference?: string;
  checkoutProcessCcPayment?: (xValidateOnly: boolean, xPaymentSessionId: string, xOrigin: string) => void;
  clearCcIframeUrl: () => void;
  checkoutGetPaymentStatusDetails: StringArgFunction;
  checkoutPaymentSetCustomStatus: StringArgFunction;
  onCheckoutClearPaymentStatus: () => void;
  process?: CheckoutPaymentProcess;
  paymentInvoice?: any;
  paymentId?: number;
  payerName: string;
}

const CreditCardPaymentPage: React.FC<CreditCardPaymentPageProps> = props => {
  const {
    classes,
    summary,
    isPaymentProcessing,
    disablePayment,
    setPaymentSuccess,
    currencySymbol,
    iframeUrl,
    xPaymentSessionId,
    merchantReference,
    checkoutProcessCcPayment,
    clearCcIframeUrl,
    payment,
    onCheckoutClearPaymentStatus,
    checkoutGetPaymentStatusDetails,
    checkoutPaymentSetCustomStatus,
    process,
    payerName
  } = props;

  const [validatePayment, setValidatePayment] = React.useState(true);

  const proceedPayment = React.useCallback(() => {
    onCheckoutClearPaymentStatus();
    setValidatePayment(true);
    checkoutProcessCcPayment(true, xPaymentSessionId, window.location.origin);
  }, [summary.payNowTotal, merchantReference]);

  const onMessage = e => {
    const paymentDetails = e.data.payment;
    if (paymentDetails && paymentDetails.status) {
      if (paymentDetails.status === "success") {
        setPaymentSuccess(true);
        setValidatePayment(false);
        if (merchantReference !== "") {
          checkoutProcessCcPayment(false, paymentDetails.sessionId, window.location.origin);
        }
      }
      checkoutGetPaymentStatusDetails(paymentDetails.sessionId);
      checkoutPaymentSetCustomStatus(paymentDetails.status);
      clearCcIframeUrl();
    }
  };

  React.useEffect(() => {
    window.addEventListener("message", onMessage);
    return () => {
      window.removeEventListener("message", onMessage);
    };
  }, [merchantReference, summary, payment]);

  React.useEffect(() => {
    if (summary.payNowTotal > 0) {
      proceedPayment();
    }
  }, [summary.payNowTotal, summary.allowAutoPay]);

  return (
    <div
      style={disablePayment ? { pointerEvents: "none" } : null}
      className={clsx("p-3 d-flex flex-fill justify-content-center", classes.content)}
    >
      {iframeUrl && !process.status && (
      <div className="flex-column justify-content-center w-100">
        <div className={clsx("centeredFlex justify-content-center", classes.payerLastCardMargin)}>
          <div className={clsx("w-100", classes.paymentDetails)}>
            <div className={classes.fieldCardRoot}>
              <div className={classes.contentRoot}>
                <h1>
                  Details
                </h1>
                <div className={clsx("centeredFlex", classes.cardLabelPadding)}>
                  <span className={classes.legend}>
                    Amount:
                  </span>
                  <Typography variant="body2" component="span" className="money fontWeight600">
                    {formatCurrency(summary.payNowTotal, currencySymbol)}
                  </Typography>
                </div>
                <div className={clsx("centeredFlex", classes.cardLabelPadding)}>
                  <span className={classes.legend}>
                    Payer:
                  </span>
                  <b>{payerName}</b>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div className={clsx("w-100", classes.iframeWrapper)}>
          <iframe src={iframeUrl} className={clsx("w-100 h-100", classes.iframe)} title="windcave-frame" />
        </div>
      </div>
        )}
      {!iframeUrl && process.status !== "" && !isPaymentProcessing && (
      <PaymentMessageRenderer
        tryAgain={proceedPayment}
        payment={payment}
        validatePayment={validatePayment}
        summary={summary}
      />
        )}
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  payment: state.checkout.payment,
  paymentInvoice: state.checkout.payment.invoice,
  paymentId: state.checkout.payment.paymentId,
  currencySymbol: state.currency && state.currency.shortCurrencySymbol,
  iframeUrl: state.checkout.payment.wcIframeUrl,
  xPaymentSessionId: state.checkout.payment.xPaymentSessionId,
  merchantReference: state.checkout.payment.merchantReference,
  process: state.checkout.payment.process
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setPaymentSuccess: (isSuccess: boolean) => dispatch(checkoutSetPaymentSuccess(isSuccess)),
  checkoutProcessCcPayment: (xValidateOnly: boolean, xPaymentSessionId: string, xOrigin: string) => {
    dispatch(checkoutProcessCcPayment(xValidateOnly, xPaymentSessionId, xOrigin));
  },
  clearCcIframeUrl: () => dispatch(clearCcIframeUrl()),
  onCheckoutClearPaymentStatus: () => dispatch(checkoutClearPaymentStatus()),
  checkoutGetPaymentStatusDetails: (sessionId: string) => dispatch(checkoutGetPaymentStatusDetails(sessionId)),
  checkoutPaymentSetCustomStatus: (status: string) => dispatch(checkoutPaymentSetCustomStatus(status))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(CreditCardPaymentPage));

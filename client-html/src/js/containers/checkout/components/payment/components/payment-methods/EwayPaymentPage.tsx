/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import React, { useEffect } from "react";
import { Dispatch } from "redux";
import { connect } from "react-redux";
import { change } from "redux-form";
import {
  CheckoutPayment,
  CheckoutPaymentProcess,
  CheckoutSummary
} from "../../../../../../model/checkout";
import { State } from "../../../../../../reducers/state";
import {
  checkoutClearPaymentStatus, checkoutGetPaymentStatusDetails,
  checkoutPaymentSetCustomStatus, checkoutProcessPayment, checkoutSetPaymentMethod,
  checkoutSetPaymentSuccess,
  clearCcIframeUrl
} from "../../../../actions/checkoutPayment";
import PaymentMessageRenderer from "../PaymentMessageRenderer";
import { BooleanArgFunction, StringArgFunction } from "../../../../../../model/common/CommonFunctions";
import { FORM } from "../../../CheckoutSelection";
import { Button } from "@mui/material";

interface CreditCardPaymentPageProps {
  classes?: any;
  summary?: CheckoutSummary;
  payment?: CheckoutPayment;
  isPaymentProcessing?: boolean;
  disablePayment?: boolean;
  setPaymentSuccess?: BooleanArgFunction;
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
  dispatch?: Dispatch;
}

const EwayPaymentPage: React.FC<CreditCardPaymentPageProps> = props => {
  const {
    summary,
    isPaymentProcessing,
    disablePayment,
    setPaymentSuccess,
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
    dispatch
  } = props;

  const [validatePayment, setValidatePayment] = React.useState(true);

  const proceedPayment = React.useCallback(() => {
    onCheckoutClearPaymentStatus();
    setValidatePayment(true);
    checkoutProcessCcPayment(true, xPaymentSessionId, window.location.origin);
  }, [summary.payNowTotal, merchantReference]);

  const pymentCallback = result => {
    const urlParams = new URL(iframeUrl).searchParams;
    const sessionId = urlParams.get("AccessCode");

    if (result === "Cancel") return;

    if (result === "Complete") {
      setPaymentSuccess(true);
      setValidatePayment(false);
      if (merchantReference !== "") {
        checkoutProcessCcPayment(false, sessionId, window.location.origin);
      }
      checkoutPaymentSetCustomStatus("success");
    }

    if (result === "Error") {
      dispatch(change(FORM, "payment_method", null));
      dispatch(checkoutSetPaymentMethod(null));
      checkoutPaymentSetCustomStatus("fail");
    }

    checkoutGetPaymentStatusDetails(sessionId);
    clearCcIframeUrl();
  };

  useEffect(() => {
    if (summary.payNowTotal > 0) {
      proceedPayment();
    }
  }, [summary.payNowTotal, summary.allowAutoPay, summary.paymentDate, summary.invoiceDueDate]);
  
  const openCardFrame = () => {
    (window as any).eCrypt.showModalPayment({
      sharedPaymentUrl: iframeUrl,
    }, pymentCallback);
  };

  return (
    <div
      style={disablePayment ? { pointerEvents: "none" } : null}
      className="d-flex flex-fill h-100 justify-content-center"
    >
      {iframeUrl && !process.status &&
        <Button
          size="large"
          variant="contained"
          color="primary"
          className="mt-auto mb-auto"
          onClick={openCardFrame}
        >
          Enter card details
        </Button>
      }
      {process.status !== "" && !isPaymentProcessing && (
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
  iframeUrl: state.checkout.payment.wcIframeUrl,
  xPaymentSessionId: state.checkout.payment.xPaymentSessionId,
  merchantReference: state.checkout.payment.merchantReference,
  process: state.checkout.payment.process
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  dispatch,
  setPaymentSuccess: (isSuccess: boolean) => dispatch(checkoutSetPaymentSuccess(isSuccess)),
  checkoutProcessCcPayment: (xValidateOnly: boolean, xPaymentSessionId: string, xOrigin: string) => {
    dispatch(checkoutProcessPayment(xValidateOnly, xPaymentSessionId, xOrigin));
  },
  clearCcIframeUrl: () => dispatch(clearCcIframeUrl()),
  onCheckoutClearPaymentStatus: () => dispatch(checkoutClearPaymentStatus()),
  checkoutGetPaymentStatusDetails: (sessionId: string) => dispatch(checkoutGetPaymentStatusDetails(sessionId)),
  checkoutPaymentSetCustomStatus: (status: string) => dispatch(checkoutPaymentSetCustomStatus(status))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(EwayPaymentPage);
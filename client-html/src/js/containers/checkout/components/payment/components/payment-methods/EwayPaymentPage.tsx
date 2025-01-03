/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import LoadingButton from '@mui/lab/LoadingButton';
import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { change } from 'redux-form';
import { CreditCardPaymentPageProps } from '../../../../../../model/checkout';
import { State } from '../../../../../../reducers/state';
import {
  checkoutClearPaymentStatus,
  checkoutGetPaymentStatusDetails,
  checkoutPaymentSetCustomStatus,
  checkoutProcessPayment,
  checkoutSetPaymentMethod,
  clearCcIframeUrl
} from '../../../../actions/checkoutPayment';
import { FORM } from '../../../CheckoutSelection';
import PaymentMessageRenderer from '../PaymentMessageRenderer';


const EwayPaymentPage: React.FC<CreditCardPaymentPageProps> = props => {
  const {
    summary,
    isPaymentProcessing,
    disablePayment,
    iframeUrl,
    xPaymentSessionId,
    checkoutProcessCcPayment,
    clearCcIframeUrl,
    payment,
    onCheckoutClearPaymentStatus,
    checkoutGetPaymentStatusDetails,
    checkoutPaymentSetCustomStatus,
    process,
    dispatch
  } = props;

  const [frameOpening, setFrameOpening] = React.useState(false);

  const proceedPayment = () => {
    onCheckoutClearPaymentStatus();
    checkoutProcessCcPayment(true, xPaymentSessionId, window.location.origin);
  };

  const pymentCallback = result => {
    setFrameOpening(false);

    const urlParams = new URL(iframeUrl).searchParams;
    const sessionId = urlParams.get("AccessCode");

    if (result === "Cancel") return;

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
    setFrameOpening(true);
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
        <LoadingButton
          loading={frameOpening}
          size="large"
          variant="contained"
          color="primary"
          className="mt-auto mb-auto"
          onClick={openCardFrame}
        >
          Enter card details
        </LoadingButton>
      }
      {process.status !== "" && !isPaymentProcessing && (
        <PaymentMessageRenderer
          tryAgain={proceedPayment}
          payment={payment}
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
  process: state.checkout.payment.process
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  dispatch,
  checkoutProcessCcPayment: (xValidateOnly: boolean, xPaymentSessionId: string, xOrigin: string) => {
    dispatch(checkoutProcessPayment(xValidateOnly, xPaymentSessionId, xOrigin));
  },
  clearCcIframeUrl: () => dispatch(clearCcIframeUrl()),
  onCheckoutClearPaymentStatus: () => dispatch(checkoutClearPaymentStatus()),
  checkoutGetPaymentStatusDetails: (sessionId: string) => dispatch(checkoutGetPaymentStatusDetails(sessionId)),
  checkoutPaymentSetCustomStatus: (status: string) => dispatch(checkoutPaymentSetCustomStatus(status))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(EwayPaymentPage);
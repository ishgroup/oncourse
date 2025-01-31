/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import Typography from '@mui/material/Typography';
import clsx from 'clsx';
import { formatCurrency } from 'ish-ui';
import React from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { isInvalid } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import { CreditCardPaymentPageProps } from '../../../../../../model/checkout';
import { State } from '../../../../../../reducers/state';
import {
  checkoutClearPaymentStatus,
  checkoutGetPaymentStatusDetails,
  checkoutProcessPayment,
  clearCcIframeUrl
} from '../../../../actions/checkoutPayment';
import { CHECKOUT_SELECTION_FORM_NAME as CheckoutSelectionForm } from '../../../CheckoutSelection';
import PaymentMessageRenderer from '../PaymentMessageRenderer';
import styles from './styles';

const WindcavePaymentPage: React.FC<CreditCardPaymentPageProps> = props => {
  const {
    classes,
    summary,
    isPaymentProcessing,
    disablePayment,
    currencySymbol,
    iframeUrl,
    xPaymentSessionId,
    merchantReference,
    checkoutProcessCcPayment,
    clearCcIframeUrl,
    payment,
    onCheckoutClearPaymentStatus,
    checkoutGetPaymentStatusDetails,
    process,
    payerName,
    hasSummarryErrors
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
      checkoutGetPaymentStatusDetails(paymentDetails.sessionId);
      setValidatePayment(false);
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
  }, [summary.payNowTotal, summary.allowAutoPay, summary.paymentDate, summary.invoiceDueDate]);

  return (
    <div
      style={disablePayment ? { pointerEvents: "none" } : null}
      className={clsx("d-flex flex-fill justify-content-center", classes.content)}
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
        <div className={clsx("w-100", classes.iframeWrapper, hasSummarryErrors && "disabled")}>
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
  process: state.checkout.payment.process,
  hasSummarryErrors: isInvalid(CheckoutSelectionForm)(state)
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  checkoutProcessCcPayment: (xValidateOnly: boolean, xPaymentSessionId: string, xOrigin: string) => {
    dispatch(checkoutProcessPayment(xValidateOnly, xPaymentSessionId, xOrigin));
  },
  clearCcIframeUrl: () => dispatch(clearCcIframeUrl()),
  onCheckoutClearPaymentStatus: () => dispatch(checkoutClearPaymentStatus()),
  checkoutGetPaymentStatusDetails: (sessionId: string) => dispatch(checkoutGetPaymentStatusDetails(sessionId))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(WindcavePaymentPage, styles));
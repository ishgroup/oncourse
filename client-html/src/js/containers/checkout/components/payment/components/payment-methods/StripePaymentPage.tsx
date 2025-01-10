/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import { EmbeddedCheckout, EmbeddedCheckoutProvider } from '@stripe/react-stripe-js';
import { loadStripe, Stripe } from '@stripe/stripe-js';
import clsx from 'clsx';
import { makeAppStyles } from 'ish-ui';
import React, { useEffect, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import InstantFetchErrorHandler from '../../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler';
import { useAppSelector } from '../../../../../../common/utils/hooks';
import history from '../../../../../../constants/History';
import { CreditCardPaymentPageProps } from '../../../../../../model/checkout';
import { State } from '../../../../../../reducers/state';
import {
  checkoutClearPaymentStatus,
  checkoutGetPaymentStatusDetails,
  checkoutProcessPayment,
  clearCcIframeUrl
} from '../../../../actions/checkoutPayment';
import CheckoutService from '../../../../services/CheckoutService';
import { clearStoredPaymentsState } from '../../../../utils';
import PaymentMessageRenderer from '../PaymentMessageRenderer';

const useStyles = makeAppStyles()({
  iframe: {
    display: 'flex',
    justifyContent: 'center',
    '& > div': {
      flexBasis: '400px'
    }
  }
});

const StripePaymentPage: React.FC<CreditCardPaymentPageProps> = props => {
  const {
    summary,
    isPaymentProcessing,
    disablePayment,
    xPaymentSessionId,
    checkoutProcessCcPayment,
    payment,
    onCheckoutClearPaymentStatus,
    checkoutGetPaymentStatusDetails,
    clearCcIframeUrl,
    process,
    dispatch
  } = props;

  const query = new URLSearchParams(window.location.search);
  const sessionId = query.get("sessionId");

  const [stripePromise, setStripePromise] = useState<Promise<Stripe | null>>(null);
  const { classes } = useStyles();
  
  useEffect(() => {
    if (sessionId) {
      checkoutGetPaymentStatusDetails(sessionId);
      setStripePromise(null);
      query.delete('sessionId');
      history.replace({
        pathname: history.location.pathname,
        search: query.toString()
      });
    } else {
      CheckoutService.getClientKey()
        .then(res => setStripePromise(loadStripe(res)))
        .catch(res => InstantFetchErrorHandler(dispatch, res));
    }
  }, []);

  const clientSecret = useAppSelector(state => state.checkout.payment.clientSecret);

  const proceedPayment = () => {
    onCheckoutClearPaymentStatus();
    clearCcIframeUrl();
    clearStoredPaymentsState();
    checkoutProcessCcPayment(true, xPaymentSessionId, window.location.origin);
  };

  useEffect(() => {
    if (summary.payNowTotal > 0) {
      proceedPayment();
    }
  }, [
    summary.payNowTotal,
    summary.allowAutoPay,
    summary.paymentDate,
    summary.invoiceDueDate
  ]);

  useEffect(() => {
    if (process.status === 'success' && !isPaymentProcessing) {
      clearStoredPaymentsState();
    }
  }, [process.status, isPaymentProcessing]);

  return (
    <div
      style={disablePayment ? { pointerEvents: "none" } : null}
      className={clsx(stripePromise && classes.iframe)}
    >
      {stripePromise && clientSecret &&
        <EmbeddedCheckoutProvider
          stripe={stripePromise}
          options={{
            clientSecret
          }}
        >
          <EmbeddedCheckout />
        </EmbeddedCheckoutProvider>
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
  checkoutGetPaymentStatusDetails: (sessionId: string) => dispatch(checkoutGetPaymentStatusDetails(sessionId))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(StripePaymentPage);
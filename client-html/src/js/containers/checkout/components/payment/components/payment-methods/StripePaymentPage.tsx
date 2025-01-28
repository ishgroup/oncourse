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
import { CreditCardPaymentPageProps } from '../../../../../../model/checkout';
import { State } from '../../../../../../reducers/state';
import {
  checkoutClearPaymentStatus,
  checkoutProcessPayment,
  clearCcIframeUrl
} from '../../../../actions/checkoutPayment';
import CheckoutService from '../../../../services/CheckoutService';
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
    clearCcIframeUrl,
    process,
    dispatch
  } = props;

  const query = new URLSearchParams(window.location.search);
  const sessionId = query.get("sessionId");

  const [stripePromise, setStripePromise] = useState<Promise<Stripe | null>>(null);
  const { classes } = useStyles();
  
  useEffect(() => {
    CheckoutService.getClientKey()
      .then(res => setStripePromise(loadStripe(res)))
      .catch(res => InstantFetchErrorHandler(dispatch, res));
  }, []);

  const clientSecret = useAppSelector(state => state.checkout.payment.clientSecret);

  const proceedPayment = () => {
    onCheckoutClearPaymentStatus();
    clearCcIframeUrl();
    checkoutProcessCcPayment();
  };

  useEffect(() => {
    if (!sessionId && summary.payNowTotal > 0) {
      proceedPayment();
    }
  }, [
    summary.payNowTotal,
    summary.allowAutoPay,
    summary.paymentDate,
    summary.invoiceDueDate
  ]);

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
  checkoutProcessCcPayment: () => dispatch(checkoutProcessPayment()),
  clearCcIframeUrl: () => dispatch(clearCcIframeUrl()),
  onCheckoutClearPaymentStatus: () => dispatch(checkoutClearPaymentStatus()),
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(StripePaymentPage);
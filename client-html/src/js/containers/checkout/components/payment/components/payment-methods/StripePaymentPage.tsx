/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import { CheckoutCCResponse } from '@api/model';
import LoadingButton from '@mui/lab/LoadingButton';
import { Elements, PaymentElement, useElements, useStripe, } from '@stripe/react-stripe-js';
import { loadStripe, Stripe } from '@stripe/stripe-js';
import clsx from 'clsx';
import { decimalMul, makeAppStyles } from 'ish-ui';
import React, { useEffect, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { showMessage } from '../../../../../../common/actions';
import InstantFetchErrorHandler from '../../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler';
import history from '../../../../../../constants/History';
import { CreditCardPaymentPageProps } from '../../../../../../model/checkout';
import { State } from '../../../../../../reducers/state';
import {
  checkoutClearPaymentStatus,
  checkoutGetPaymentStatusDetails,
  checkoutProcessPaymentFulfilled,
  checkoutProcessStripeCCPayment,
  checkoutSetPaymentProcessing,
  clearCcIframeUrl
} from '../../../../actions/checkoutPayment';
import { checkoutUpdateSummaryPrices } from '../../../../actions/checkoutSummary';
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

const StripePaymentForm = ({ isPaymentProcessing, handleError, setLoading, checkoutProcessStripeCCPayment }) => {
  const stripe = useStripe();
  const elements = useElements();
  const [ready, setReady] = useState(false);

  const handleSubmit = async event => {
    event.preventDefault();

    if (!stripe || !elements) {
      return;
    }

    setLoading(true);

    const { error: submitError } = await elements.submit();

    if (submitError) {
      handleError(submitError);
      return;
    }

    const { paymentMethod, error } = await stripe.createPaymentMethod({
      elements
    });

    if (error) {
      handleError(error);
      return;
    }

    checkoutProcessStripeCCPayment(paymentMethod.id, stripe);
  };

  return <form onSubmit={handleSubmit} className="flex-column">
    <PaymentElement onReady={() => setReady(true)} />
    <LoadingButton
      variant="contained"
      color="primary"
      size="large"
      type="submit"
      className="mt-3 ml-auto mr-auto"
      disabled={!ready || !stripe || isPaymentProcessing}
      loading={isPaymentProcessing}>
      Finalize checkout
    </LoadingButton>
  </form>;
};

const StripePaymentPage: React.FC<CreditCardPaymentPageProps> = props => {
  const {
    summary,
    completePayment,
    isPaymentProcessing,
    disablePayment,
    payment,
    onCheckoutClearPaymentStatus,
    checkoutUpdateSummaryPrices,
    checkoutProcessStripeCCPayment,
    process,
    dispatch
  } = props;

  const query = new URLSearchParams(window.location.search);
  const transactionId = query.get("payment_intent");
  const onCoursePaymentSessionId = query.get("onCourseSessionId");

  const [stripePromise, setStripePromise] = useState<Promise<Stripe | null>>(null);
  const { classes } = useStyles();
  
  useEffect(() => {
    if (transactionId) {
      dispatch(checkoutSetPaymentProcessing(true));
      history.replace({
        pathname: history.location.pathname,
        search: ""
      });
      CheckoutService.submitCreditCardPayment({
        onCoursePaymentSessionId,
        paymentMethodId: null,
        transactionId,
        merchantReference: null,
        origin: window.location.origin
      })
        .then(res => completePayment(res, onCoursePaymentSessionId))
        .catch(error => {
          dispatch(checkoutSetPaymentProcessing(false));
          InstantFetchErrorHandler(dispatch, error);
        });
    } else {
      CheckoutService.getClientKey()
        .then(res => setStripePromise(loadStripe(res)))
        .catch(res => InstantFetchErrorHandler(dispatch, res));
    }
  }, []);

  const setLoading = (loading: boolean) => {
    dispatch(checkoutSetPaymentProcessing(loading));
  };

  const proceedPayment = () => {
    onCheckoutClearPaymentStatus();
    clearCcIframeUrl();
    checkoutUpdateSummaryPrices();
  };
  
  const handleError = error => {
    setLoading(false);
    dispatch(showMessage({ message: error?.message }));
  };

  useEffect(() => {
    if (!process.status && summary.payNowTotal > 0) {
      proceedPayment();
    }
  }, [
    process.status,
    summary.payNowTotal,
    summary.allowAutoPay,
    summary.paymentDate,
    summary.invoiceDueDate
  ]);

  return (
    <div
      style={disablePayment ? { pointerEvents: "none" } : null}
      className={clsx(stripePromise && !process.status && classes.iframe)}
    >
      {!process.status && stripePromise && summary.payNowTotal > 0 &&
        <Elements
          stripe={stripePromise}
          options={{
            mode: 'payment',
            currency: 'aud',
            paymentMethodCreation: 'manual',
            amount: decimalMul(summary.payNowTotal, 100)
          }}
        >
          <StripePaymentForm
            isPaymentProcessing={isPaymentProcessing}
            checkoutProcessStripeCCPayment={checkoutProcessStripeCCPayment}
            handleError={handleError}
            setLoading={setLoading}
          />
        </Elements>
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
  process: state.checkout.payment.process
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  dispatch,
  checkoutProcessStripeCCPayment: (stripePaymentMethodId: string, stripe: Stripe) => {
    dispatch(checkoutProcessStripeCCPayment(stripePaymentMethodId, stripe));
  },
  checkoutUpdateSummaryPrices: () => dispatch(checkoutUpdateSummaryPrices()),
  clearCcIframeUrl: () => dispatch(clearCcIframeUrl()),
  onCheckoutClearPaymentStatus: () => dispatch(checkoutClearPaymentStatus()),
  completePayment: (checkoutCCResponse: CheckoutCCResponse, sessionId: string) => {
    dispatch(checkoutGetPaymentStatusDetails(sessionId));
    dispatch(checkoutProcessPaymentFulfilled(checkoutCCResponse));
    dispatch(checkoutSetPaymentProcessing(false));
  }
});

export default connect(mapStateToProps, mapDispatchToProps)(StripePaymentPage);
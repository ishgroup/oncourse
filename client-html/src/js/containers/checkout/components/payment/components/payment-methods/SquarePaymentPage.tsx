/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import LoadingButton from '@mui/lab/LoadingButton';
import { Stripe } from '@stripe/stripe-js';
import clsx from 'clsx';
import { makeAppStyles } from 'ish-ui';
import React, { useEffect, useState } from 'react';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import square from '../../../../../../../images/square.svg';
import squareLight from '../../../../../../../images/squareLight.svg';
import { showMessage } from '../../../../../../common/actions';
import { CreditCardPaymentPageProps } from '../../../../../../model/checkout';
import { State } from '../../../../../../reducers/state';
import {
  checkoutClearPaymentStatus,
  checkoutProcessSquareCcPayment,
  checkoutProcessStripeCCPayment,
  checkoutSetPaymentProcessing,
  clearCcIframeUrl
} from '../../../../actions/checkoutPayment';
import { checkoutUpdateSummaryPrices } from '../../../../actions/checkoutSummary';
import PaymentMessageRenderer from '../PaymentMessageRenderer';

const useStyles = makeAppStyles()({
  iframe: {
    display: 'flex',
    justifyContent: 'center',
    '& > div': {
      flexBasis: '400px'
    }
  },
  logo: {
    width: '180px',
    marginRight: 'auto',
    marginLeft: 'auto',
    marginBottom: '40px'
  }
});

const SquarePaymentPage: React.FC<CreditCardPaymentPageProps> = props => {
  const {
    summary,
    isPaymentProcessing,
    disablePayment,
    payment,
    onCheckoutClearPaymentStatus,
    checkoutUpdateSummaryPrices,
    checkoutProcessStripeCCPayment,
    process,
    dispatch
  } = props;

  const [ready, setReady] = useState<boolean>(false);
  const [card, setCard] = useState<any>(null);
  const [payments, setPayments] = useState<any>(null);
  const { classes, theme } = useStyles();
  
  const init = async () => {
    const squarePayments = (window as any).Square.payments('sandbox-sq0idb--dGorAER1MMCm2wwAZkELA', 'L12H8VNBMK0XE');
    const squareCard = await squarePayments.card();
    setCard(squareCard);
    setPayments(squarePayments);
    await squareCard.attach('#card-container');
    setReady(true);
  };

  useEffect(() => {
    init();
    // CheckoutService.getClientKey()
    //   .then(res => setStripePromise(loadStripe(res)))
    //   .catch(res => InstantFetchErrorHandler(dispatch, res));
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
  
  const verifyBuyer = async token => {
    const verificationDetails = {
      amount: summary.payNowTotal.toFixed(2),
      billingContact: {
        email: summary.list.find(l => l.payer)?.contact?.email,
      },
      currencyCode: 'AUD',
      intent: 'CHARGE',
    };

    const verificationResults = await payments.verifyBuyer(
      token,
      verificationDetails
    );
    return verificationResults;
  };
  
  const processSquarePayment = async () => {
    dispatch(checkoutSetPaymentProcessing(true));
    try {
      const result = await card.tokenize();
      const verificationResults = await verifyBuyer(result.token);
      if (result.status === 'OK') {
        dispatch(checkoutProcessSquareCcPayment(result.token, verificationResults.token));
      } else {
        dispatch(checkoutSetPaymentProcessing(false));
        if (result.errors) {
          dispatch(showMessage({
            message: result.errors.reduce((p, c) => p + c.message + "\n", '')
          }));
        }
      }
    } catch (e) {
      console.error(e);
      dispatch(checkoutSetPaymentProcessing(false));
    }
  };

  return (
    <div
      style={disablePayment ? { pointerEvents: "none" } : null}
      className={clsx(!process.status && classes.iframe)}
    >
      {!process.status && summary.payNowTotal > 0 &&
        <div id="payment-form" className="flex-column">
          <img alt="SquareLogo" src={theme.palette.mode === 'light' ? square : squareLight} className={classes.logo} />
          <div id="card-container"></div>
          <LoadingButton
            variant="contained"
            color="primary"
            size="large"
            disabled={!ready || isPaymentProcessing}
            loading={isPaymentProcessing}
            onClick={processSquarePayment}
          >
            Finalize checkout
          </LoadingButton>
        </div>
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
  process: state.checkout.payment.process,
  isPaymentProcessing: state.checkout.payment.isProcessing
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  dispatch,
  checkoutProcessStripeCCPayment: (stripePaymentMethodId: string, stripe: Stripe) => {
    dispatch(checkoutProcessStripeCCPayment(stripePaymentMethodId, stripe));
  },
  checkoutUpdateSummaryPrices: () => dispatch(checkoutUpdateSummaryPrices()),
  clearCcIframeUrl: () => dispatch(clearCcIframeUrl()),
  onCheckoutClearPaymentStatus: () => dispatch(checkoutClearPaymentStatus())
});

export default connect(mapStateToProps, mapDispatchToProps)(SquarePaymentPage);
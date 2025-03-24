/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import LoadingButton from '@mui/lab/LoadingButton';
import clsx from 'clsx';
import { makeAppStyles } from 'ish-ui';
import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import $t from '@t';
import { connect } from 'react-redux';
import { Dispatch } from 'redux';
import { showMessage } from '../../../../../../common/actions';
import InstantFetchErrorHandler from '../../../../../../common/api/fetch-errors-handlers/InstantFetchErrorHandler';
import { CreditCardPaymentPageProps } from '../../../../../../model/checkout';
import { State } from '../../../../../../reducers/state';
import {
  checkoutClearPaymentStatus,
  checkoutGetPaymentStatusDetails,
  checkoutPaymentSetCustomStatus,
  checkoutProcessEwayCCPayment
} from '../../../../actions/checkoutPayment';
import { checkoutUpdateSummaryPrices } from '../../../../actions/checkoutSummary';
import CheckoutService from '../../../../services/CheckoutService';
import PaymentMessageRenderer from '../PaymentMessageRenderer';

const validationCodes = {
  "V6021": "Cardholder Name Required",
  "V6022": "Valid Card Number Required",
  "V6023": "Valid Card CVN Required",
  "V6033": "Invalid Expiry Date",
};

const useStyles = makeAppStyles()(theme => ({
  iframe: {
    display: 'flex',
    justifyContent: 'center'
  },
  submit: {
    width: '330px',
    transform: 'translateX(15px)'
  },
  logo: {
    width: '240px',
    alignSelf: 'center',
    marginBottom: theme.spacing(2)
  }
}));

const rowStyle = "width: 300px; margin-right: auto; margin-left: auto;";

const EwayPaymentPage: React.FC<CreditCardPaymentPageProps> = props => {
  const {
    summary,
    isPaymentProcessing,
    disablePayment,
    checkoutProcessEwayCCPayment,
    payment,
    onCheckoutClearPaymentStatus,
    checkoutUpdateSummaryPrices,
    process,
    dispatch
  } = props;

  const formRef = useRef<HTMLFormElement>(null);
  const secureCodeRef = useRef<string>(null);
  const [publicApiKey, setPublicApiKey] = useState(null);

  useEffect(() => {
    CheckoutService.getClientPreferences()
      .then(res => setPublicApiKey(res.clientKey))
      .catch(res => InstantFetchErrorHandler(dispatch, res));
  }, []);

  const [ready, setReady] = useState(false);

  const { classes, theme } = useStyles();

  const cardStyles = useMemo(() => `
    font: inherit;
    letter-spacing: inherit;
    color: ${theme.palette.text.primary};
    border: 1px solid ${theme.palette.divider};
    font-family: ${theme.typography.fontFamily};
    background: ${theme.palette.background.paper};
    margin-bottom: ${theme.spacing(2)};
    font-weight: 400;
    font-size: 16px;
    box-sizing: content-box;
    height: 8px;
    -webkit-tap-highlight-color: transparent;
    animation-name: mui-auto-fill-cancel;
    animation-duration: 10ms;
    padding: 16.5px 14px;
    letter-spacing: 0.00938em;
    cursor: text;
    -webkit-box-align: center;
    align-items: center;
    position: relative;
    border-radius: 0px;
    box-shadow: none;
  `, [theme]);

  const groupFieldConfig = useMemo(() => ({
    publicApiKey,
    fieldDivId: "eway-secure-panel",
    fieldType: "group",
    layout : {
      rows : [ {
        styles: rowStyle,
        cells:  [
          {
            colSpan: 12,
            field: { fieldType: "name", fieldColSpan: 12,  styles: cardStyles, autocomplete: "true" }
          }
        ],
      },
      {
        styles: rowStyle,
        cells: [
          {
            colSpan: 12,
            field: { fieldType: "card", fieldColSpan: 12,  styles: cardStyles, autocomplete: "true" }
          }
        ]
      },
      {
        styles: rowStyle,
        cells: [
          {
            styles: "width: 127px",
            colSpan: 6,
            field: {  fieldType: "expirytext", fieldColSpan: 12, styles: cardStyles, autocomplete: "true" }
          },
          {
            styles: "width: 127px; float: right;",
            colSpan: 6,
            field: {  fieldType: "cvn", fieldColSpan: 12,  styles: cardStyles, autocomplete: "true" }
          }
        ]
      },
    ]
    }
  }), [cardStyles, publicApiKey]);

  const securePanelCallback = useCallback(event => {
    if (!event.fieldValid) {
      setReady(false);
      dispatch(showMessage({
        message: event.errors.split(" ").reduce((p, c) => p + `${validationCodes[c.trim()]}\n`, '')
      }));
    } else {
      setReady(true);
      secureCodeRef.current = event.secureFieldCode;
    }
  }, [secureCodeRef.current]);

  useEffect(() => {
    if (publicApiKey) {
      (window as any).eWAY.setupSecureField(groupFieldConfig, securePanelCallback);
    }
  }, [publicApiKey]);

  const pymentCallback = () => {
    checkoutProcessEwayCCPayment(secureCodeRef.current);
  };

  useEffect(() => {
    if (!process.status && summary.payNowTotal > 0) {
      checkoutUpdateSummaryPrices();
    }
  }, [process.status, summary.payNowTotal, summary.allowAutoPay, summary.paymentDate, summary.invoiceDueDate]);

  const handleSubmit = e => {
    e.preventDefault();
    (window as any).eWAY.saveAllFields(pymentCallback, 2000);
    return false;
  };

  return (
    <div
      style={disablePayment ? { pointerEvents: "none" } : null}
    >
      {!process.status && <form onSubmit={handleSubmit} ref={formRef} className="d-flex flex-column justify-content-center">
        <img alt="EwayLogo" className={classes.logo} src="https://eway.io/api-v3/images/eway-logo-yellow-ec3677c6.png" />
        <div id="eway-secure-panel" />
        <LoadingButton
          variant="contained"
          color="primary"
          size="large"
          type="submit"
          className={clsx("mt-2 ml-auto mr-auto", classes.submit)}
          disabled={!ready || isPaymentProcessing}
          loading={isPaymentProcessing}
        >
          {$t('finalize_checkout')}
        </LoadingButton>
      </form>}

      {process.status !== "" && !isPaymentProcessing && (
        <PaymentMessageRenderer
          tryAgain={onCheckoutClearPaymentStatus}
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
  xPaymentSessionId: state.checkout.payment.sessionId,
  process: state.checkout.payment.process
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  dispatch,
  checkoutProcessEwayCCPayment: (code: string) => {
    dispatch(checkoutProcessEwayCCPayment(code));
  },
  onCheckoutClearPaymentStatus: () => dispatch(checkoutClearPaymentStatus()),
  checkoutUpdateSummaryPrices: () => dispatch(checkoutUpdateSummaryPrices()),
  checkoutGetPaymentStatusDetails: (sessionId: string) => dispatch(checkoutGetPaymentStatusDetails(sessionId)),
  checkoutPaymentSetCustomStatus: (status: string) => dispatch(checkoutPaymentSetCustomStatus(status))
});

export default connect<any, any, any>(mapStateToProps, mapDispatchToProps)(EwayPaymentPage);
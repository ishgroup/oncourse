/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */
import Typography from '@mui/material/Typography';
import $t from '@t';
import clsx from 'clsx';
import { format } from 'date-fns';
import { BooleanArgFunction, formatCurrency, NoArgFunction, YYYY_MM_DD_MINUSED } from 'ish-ui';
import React, { Dispatch } from 'react';
import { connect } from 'react-redux';
import { InjectedFormProps, isInvalid, reduxForm } from 'redux-form';
import { withStyles } from 'tss-react/mui';
import { CheckoutPayment, CheckoutSummary } from '../../../../../../model/checkout';
import { State } from '../../../../../../reducers/state';
import {
  checkoutClearPaymentStatus,
  checkoutProcessPayment,
  checkoutSetPaymentSuccess
} from '../../../../actions/checkoutPayment';
import { checkoutUpdateSummaryPrices } from '../../../../actions/checkoutSummary';
import { CHECKOUT_SELECTION_FORM_NAME as CheckoutSelectionForm } from '../../../CheckoutSelection';
import PaymentMessageRenderer from '../PaymentMessageRenderer';
import styles from './styles';

const CHECKOUT_CASH_PAYMENT_FORM = "checkoutCashPaymentForm";

const initialValues = {
  payment_date: format(new Date(), YYYY_MM_DD_MINUSED)
};

interface CashPaymentPageProps {
  priceToPay?: any;
  classes?: any;
  currencySymbol?: any;
  paymentType?: any;
  summary?: CheckoutSummary;
  payment?: CheckoutPayment;
  paymentInvoice?: any;
  checkoutUpdateSummaryPrices?: NoArgFunction;
  setPaymentSuccess?: BooleanArgFunction;
  checkoutProcessPayment?: () => void;
  onCheckoutClearPaymentStatus?: () => void;
  paymentStatus?: any;
  hasSummarryErrors?: boolean;
  paymentId?: number;
  payerName: string;
}

const PaymentForm: React.FC<CashPaymentPageProps & InjectedFormProps> = props => {
  const {
    priceToPay,
    paymentType,
    classes,
    currencySymbol,
    invalid,
    summary,
    checkoutProcessPayment,
    payment,
    onCheckoutClearPaymentStatus,
    hasSummarryErrors,
    paymentStatus,
    checkoutUpdateSummaryPrices,
    payerName
  } = props;

  const [finalized, setFinalized] = React.useState(false);

  const proceedPayment = React.useCallback(() => {
    onCheckoutClearPaymentStatus();

    setFinalized(true);

    checkoutProcessPayment();
  }, [summary.payNowTotal]);

  React.useEffect(() => {
    if (hasSummarryErrors || (paymentType === "No payment" && summary.payNowTotal > 0)) {
      return;
    }

    checkoutUpdateSummaryPrices();
  }, [summary.payNowTotal, hasSummarryErrors, summary.paymentDate, summary.invoiceDueDate, paymentType]);

  return (
    <div className={clsx("d-flex flex-fill justify-content-center", classes.content)}>
      {!paymentStatus ? (
        <form autoComplete="off" className="w-100">
          <div className="p-3 mt-3 h-100 w-100">
            <div className={clsx("centeredFlex justify-content-center", classes.payerCardMargin)}>
              <div className={clsx("w-100", classes.paymentDetails)}>
                <div className={classes.fieldCardRoot}>
                  <div className={classes.contentRoot}>
                    <h1>
                      {$t('details')}
                    </h1>
                    <div className={clsx("centeredFlex", classes.cardLabelPadding)}>
                      <span className={classes.legend}>
                        {$t('amount2')}
                      </span>
                      <Typography variant="body2" component="span" className="money fontWeight600">
                        {formatCurrency(priceToPay, currencySymbol)}
                      </Typography>
                    </div>
                    <div className={clsx("centeredFlex", classes.cardLabelPadding)}>
                      <span className={classes.legend}>
                        {$t('payer2')}
                      </span>
                      <b>{payerName}</b>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div className={clsx("centeredFlex justify-content-center", classes.payerCardMargin)}>
              <div className={clsx("w-100", classes.paymentDetails)}>
                <div className={classes.fieldCardRoot}>
                  <div className={classes.contentRoot}>
                    {payment.savedCreditCard && paymentType === "Saved credit card"
                        && (
                        <>
                          <h1>
                            {$t('pay_with_saved_credit_card')}
                          </h1>
                          <div className={clsx("centeredFlex", classes.cardLabelPadding)}>
                            <span className={classes.legend}>
                              {$t('name_on_card')}
                            </span>
                            <b>
                              {payment.savedCreditCard.creditCardName}
                            </b>
                          </div>
                          <div className={clsx("centeredFlex", classes.cardLabelPadding, classes.legendLastMargin)}>
                            <span className={classes.legend}>
                              {$t('card_number')}
                            </span>
                            <b>
                              {payment.savedCreditCard.creditCardType}
                              {" "}
                              {payment.savedCreditCard.creditCardNumber}
                            </b>
                          </div>
                        </>
                      )}

                    <div
                      className={clsx("p-2 cursor-pointer text-uppercase d-block text-center fs2", classes.payButton,
                          (invalid
                            || finalized
                            || hasSummarryErrors
                            || (
                              !summary.list.some(l => l.items.some(li => li.checked))
                              && !summary.voucherItems.some(i => i.checked)
                              && summary.previousOwing.invoiceTotal === 0)) && "disabled")}
                      onClick={() => proceedPayment()}
                    >
                      {$t('finalise_checkout')}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </form>
        ) : paymentStatus !== "" ? (
          <PaymentMessageRenderer
            tryAgain={() => proceedPayment()}
            payment={payment}
            validatePayment={false}
            summary={summary}
          />
        ) : null}
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  hasSummarryErrors: isInvalid(CheckoutSelectionForm)(state),
  priceToPay: state.checkout.summary.payNowTotal,
  currencySymbol: state.location.currency && state.location.currency.shortCurrencySymbol,
  payment: state.checkout.payment,
  paymentId: state.checkout.payment.paymentId,
  paymentInvoice: state.checkout.payment.invoice,
  paymentStatus: state.checkout.payment.process.status
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setPaymentSuccess: (isSuccess: boolean) => dispatch(checkoutSetPaymentSuccess(isSuccess)),
  checkoutProcessPayment: () => {
    dispatch(checkoutProcessPayment());
  },
  onCheckoutClearPaymentStatus: () => dispatch(checkoutClearPaymentStatus()),
  checkoutUpdateSummaryPrices: () => dispatch(checkoutUpdateSummaryPrices())
});

export default reduxForm<any, CashPaymentPageProps>({
  form: CHECKOUT_CASH_PAYMENT_FORM,
  initialValues
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(PaymentForm, styles)));

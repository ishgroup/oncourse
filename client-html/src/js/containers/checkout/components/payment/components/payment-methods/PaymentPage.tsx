/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import React, { Dispatch } from "react";
import { connect } from "react-redux";
import { format } from "date-fns";
import { reduxForm, InjectedFormProps, isInvalid } from "redux-form";
import clsx from "clsx";
import withStyles from "@material-ui/core/styles/withStyles";
import Typography from "@material-ui/core/Typography";
import {
  CheckoutPayment, CheckoutSummary
} from "../../../../../../model/checkout";
import { BooleanArgFunction } from "../../../../../../model/common/CommonFunctions";
import { State } from "../../../../../../reducers/state";
import { YYYY_MM_DD_MINUSED } from "../../../../../../common/utils/dates/format";
import { formatCurrency } from "../../../../../../common/utils/numbers/numbersNormalizing";
import {
  checkoutClearPaymentStatus,
  checkoutProcessPayment,
  checkoutSetPaymentSuccess
} from "../../../../actions/checkoutPayment";
import { FORM as CheckoutSelectionForm } from "../../../CheckoutSelection";
import PaymentMessageRenderer from "../PaymentMessageRenderer";
import styles from "./styles";

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
  setPaymentSuccess?: BooleanArgFunction;
  checkoutProcessCcPayment?: (xValidateOnly: boolean, xPaymentSessionId: string, xOrigin: string) => void;
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
    checkoutProcessCcPayment,
    payment,
    onCheckoutClearPaymentStatus,
    hasSummarryErrors,
    paymentStatus,
    payerName
  } = props;

  const [finalized, setFinalized] = React.useState(false);
  const [validatePayment, setValidatePayment] = React.useState(true);

  const proceedPayment = React.useCallback(validate => {
    onCheckoutClearPaymentStatus();

    setValidatePayment(validate);

    if (!validate) setFinalized(true);

    checkoutProcessCcPayment(validate, null, window.location.origin);
  }, [summary.payNowTotal]);

  React.useEffect(() => {
    if (hasSummarryErrors || (paymentType === "No payment" && summary.payNowTotal > 0)) {
      return;
    }
    proceedPayment(true);
  }, [summary.payNowTotal, paymentType]);

  return (
    <div className={clsx("p-3 d-flex flex-fill justify-content-center", classes.content)}>
      {!paymentStatus ? (
        <form autoComplete="off" className="w-100">
          <div className="p-3 mt-3 h-100 w-100">
            <div className={clsx("centeredFlex justify-content-center", classes.payerCardMargin)}>
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
                        {formatCurrency(priceToPay, currencySymbol)}
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

            <div className={clsx("centeredFlex justify-content-center", classes.payerCardMargin)}>
              <div className={clsx("w-100", classes.paymentDetails)}>
                <div className={classes.fieldCardRoot}>
                  <div className={classes.contentRoot}>
                    {payment.savedCreditCard && paymentType === "Saved credit card"
                        && (
                        <>
                          <h1>
                            Pay with saved credit card
                          </h1>
                          <div className={clsx("centeredFlex", classes.cardLabelPadding)}>
                            <span className={classes.legend}>
                              Name on card:
                            </span>
                            <b>
                              {payment.savedCreditCard.creditCardName}
                            </b>
                          </div>
                          <div className={clsx("centeredFlex", classes.cardLabelPadding, classes.legendLastMargin)}>
                            <span className={classes.legend}>
                              Card number:
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
                      onClick={() => proceedPayment(false)}
                    >
                      Finalise checkout
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </form>
        ) : paymentStatus !== "" ? (
          <PaymentMessageRenderer
            tryAgain={() => proceedPayment(true)}
            payment={payment}
            validatePayment={validatePayment}
            summary={summary}
          />
        ) : null}
    </div>
  );
};

const mapStateToProps = (state: State) => ({
  hasSummarryErrors: isInvalid(CheckoutSelectionForm)(state),
  priceToPay: state.checkout.summary.payNowTotal,
  currencySymbol: state.currency && state.currency.shortCurrencySymbol,
  payment: state.checkout.payment,
  paymentId: state.checkout.payment.paymentId,
  paymentInvoice: state.checkout.payment.invoice,
  paymentStatus: state.checkout.payment.process.status
});

const mapDispatchToProps = (dispatch: Dispatch<any>) => ({
  setPaymentSuccess: (isSuccess: boolean) => dispatch(checkoutSetPaymentSuccess(isSuccess)),
  checkoutProcessCcPayment: (xValidateOnly: boolean, xPaymentSessionId: string, xOrigin: string) => {
    dispatch(checkoutProcessPayment(xValidateOnly, xPaymentSessionId, xOrigin));
  },
  onCheckoutClearPaymentStatus: () => dispatch(checkoutClearPaymentStatus())
});

export default reduxForm<any, CashPaymentPageProps>({
  form: CHECKOUT_CASH_PAYMENT_FORM,
  initialValues
})(connect<any, any, any>(mapStateToProps, mapDispatchToProps)(withStyles(styles)(PaymentForm)));

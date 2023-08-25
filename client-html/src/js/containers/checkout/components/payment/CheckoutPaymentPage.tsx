/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import { connect } from "react-redux";
import AppBarContainer from "../../../../common/components/layout/AppBarContainer";
import LoadingIndicator from "../../../../common/components/progress/LoadingIndicator";
import { CheckoutDiscount, CheckoutItem, CheckoutPayment, CheckoutSummary } from "../../../../model/checkout";
import { State } from "../../../../reducers/state";
import { getContactFullName } from "../../../entities/contacts/utils";
import { CheckoutPage } from "../../constants";
import CheckoutAppBar from "../CheckoutAppBar";
import RestartButton from "../RestartButton";
import CheckoutPreviousInvoiceList from "../summary/CheckoutPreviousInvoiceList";
import CheckoutDiscountEditView from "../summary/promocode/CheckoutDiscountEditView";
import EwayPaymentPage from "./components/payment-methods/EwayPaymentPage";
import PaymentPage from "./components/payment-methods/PaymentPage";
import WindcavePaymentPage from "./components/payment-methods/WindcavePaymentPage";

interface PaymentPageProps {
  payment?: CheckoutPayment;
  summary?: CheckoutSummary;
  summaryVouchers?: CheckoutDiscount[];
  selectedDiscount?: CheckoutItem;
  isPaymentProcessing?: boolean;
  disablePayment?: boolean;
  activeField: any;
  titles: any;
  isEway?: boolean;
}

const CheckoutPaymentPage = React.memo<PaymentPageProps>(props => {
  const {
 payment, activeField, titles, summary, selectedDiscount, summaryVouchers, isPaymentProcessing, disablePayment, isEway
} = props;

  const selectedPaymentType = payment.availablePaymentTypes.find(t => t.name === payment.selectedPaymentType);

  const voucherItem = selectedDiscount ? summaryVouchers.find(v => v.id === selectedDiscount.id) : null;

  const payerName = useMemo(() => {
    const payer = summary.list.find(l => l.payer);
    return payer ? getContactFullName(payer.contact as any) : "";
  }, [summary.list]);

  const title = payment.process.status === "success" ? "Transaction successful"
    : payment.process.status === "fail" ? "Transaction failed"
      : payment.process.status === "cancel"
        ? "Transaction cancel"
        : selectedPaymentType
          ? selectedPaymentType.name
          : payment.selectedPaymentType || "Select payment method";

  return (
    <>
      {[CheckoutPage.previousCredit, CheckoutPage.previousOwing].includes(activeField) ? (
        <CheckoutPreviousInvoiceList activeField={activeField} titles={titles} previousInvoices={summary[activeField]} />
      ) : activeField === "vouchers" && voucherItem
        ? <CheckoutDiscountEditView type="voucher" selectedDiscount={voucherItem} />
        : (
          <div className="root">
            <LoadingIndicator />
            <AppBarContainer
              hideHelpMenu
              hideSubmitButton
              disableInteraction
              title={(
                <CheckoutAppBar title={title} />
              )}
              actions={
                payment.process.status === "success" && <RestartButton />
              }
            >
              {selectedPaymentType && selectedPaymentType.type === "Credit card"
                && (isEway
                ? (<EwayPaymentPage
                    isPaymentProcessing={isPaymentProcessing}
                    payerName={payerName}
                    summary={summary}
                    disablePayment={disablePayment}
                  />)
                : (
                  <WindcavePaymentPage
                    isPaymentProcessing={isPaymentProcessing}
                    payerName={payerName}
                    summary={summary}
                    disablePayment={disablePayment}
                  />
                ))}
              {((selectedPaymentType && selectedPaymentType.type !== "Credit card")
                || (!selectedPaymentType && ["No payment", "Saved credit card"].includes(payment.selectedPaymentType)))
              && <PaymentPage paymentType={payment.selectedPaymentType} payerName={payerName} summary={summary} />}
            </AppBarContainer>
          </div>
      )}
    </>
  );
});

const mapStateToProps = (state: State) => ({
  payment: state.checkout.payment,
  summary: state.checkout.summary,
  summaryVouchers: state.checkout.summary.vouchers,
  isPaymentProcessing: state.checkout.payment.isProcessing,
  isEway: ["EWAY", "EWAY_TEST"].includes(state.userPreferences['payment.gateway.type'])
});

export default connect<any, any, any>(mapStateToProps, null)(CheckoutPaymentPage);
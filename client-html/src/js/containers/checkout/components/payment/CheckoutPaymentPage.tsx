/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import React, { useMemo } from "react";
import { connect } from "react-redux";
import CustomAppBar from "../../../../common/components/layout/CustomAppBar";
import LoadingIndicator from "../../../../common/components/layout/LoadingIndicator";
import {
 CheckoutDiscount, CheckoutItem, CheckoutPayment, CheckoutSummary
} from "../../../../model/checkout";
import { State } from "../../../../reducers/state";
import { getContactName } from "../../../entities/contacts/utils";
import CheckoutPreviousInvoiceList from "../summary/CheckoutPreviousInvoiceList";
import CheckoutDiscountEditView from "../summary/promocode/CheckoutDiscountEditView";
import CreditCardPaymentPage from "./components/payment-methods/CreditCardPaymentPage";
import PaymentPage from "./components/payment-methods/PaymentPage";
import CheckoutAppBar from "../CheckoutAppBar";
import RestartButton from "../RestartButton";
import { CheckoutPage } from "../../constants";

interface PaymentPageProps {
  payment?: CheckoutPayment;
  summary?: CheckoutSummary;
  summaryVouchers?: CheckoutDiscount[];
  selectedDiscount?: CheckoutItem;
  isPaymentProcessing?: boolean;
  disablePayment?: boolean;
  activeField: any;
  titles: any;
}

const CheckoutPaymentPage = React.memo<PaymentPageProps>(props => {
  const {
 payment, activeField, titles, summary, selectedDiscount, summaryVouchers, isPaymentProcessing, disablePayment
} = props;

  const selectedPaymentType = payment.availablePaymentTypes.find(t => t.name === payment.selectedPaymentType);

  const voucherItem = selectedDiscount ? summaryVouchers.find(v => v.id === selectedDiscount.id) : null;

  const payerName = useMemo(() => {
    const payer = summary.list.find(l => l.payer);
    return payer ? getContactName(payer.contact) : "";
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
            <CustomAppBar>
              <CheckoutAppBar title={title} />
              {payment.process.status === "success" && <RestartButton />}
            </CustomAppBar>
            <LoadingIndicator customLoading={isPaymentProcessing} />

            {selectedPaymentType && selectedPaymentType.type === "Credit card"
              && (
              <CreditCardPaymentPage
                isPaymentProcessing={isPaymentProcessing}
                payerName={payerName}
                summary={summary}
                disablePayment={disablePayment}
              />
            )}

            {((selectedPaymentType && selectedPaymentType.type !== "Credit card")
            || (!selectedPaymentType && ["No payment", "Saved credit card"].includes(payment.selectedPaymentType)))
            && <PaymentPage paymentType={payment.selectedPaymentType} payerName={payerName} summary={summary} />}

          </div>
      )}
    </>
  );
});

const mapStateToProps = (state: State) => ({
  payment: state.checkout.payment,
  summary: state.checkout.summary,
  summaryVouchers: state.checkout.summary.vouchers,
  isPaymentProcessing: state.checkout.payment.isProcessing
});

export default connect<any, any, any>(mapStateToProps, null)(CheckoutPaymentPage);

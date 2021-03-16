/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { getFormValues } from "redux-form";
import { Epic } from "redux-observable";
import { CheckoutResponse } from "@api/model";
import { SHOW_MESSAGE } from "../../../../common/actions";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import {
  CHECKOUT_EMPTY_PAYMENT_ACTION,
  CHECKOUT_PROCESS_PAYMENT,
  CHECKOUT_PROCESS_PAYMENT_FULFILLED,
  checkoutPaymentSetCustomStatus,
  checkoutPaymentSetStatus,
  checkoutSetPaymentProcessing
} from "../../actions/checkoutPayment";
import { CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM } from "../../components/fundingInvoice/CheckoutFundingInvoiceSummaryList";
import { CHECKOUT_SUMMARY_FORM } from "../../components/summary/CheckoutSummaryList";
import CheckoutService from "../../services/CheckoutService";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { getCheckoutModel } from "../../utils";

const request: EpicUtils.Request<any, { xValidateOnly: boolean, xPaymentSessionId: string, xOrigin: string }> = {
  type: CHECKOUT_PROCESS_PAYMENT,
  getData: ({
  xValidateOnly, xPaymentSessionId, xOrigin
  }, s) => {
    const checkoutModel = getCheckoutModel(
      s.checkout,
      (getFormValues(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM)(s) as any).fundingInvoices,
      (getFormValues(CHECKOUT_SUMMARY_FORM)(s) as any)
    );
    return CheckoutService.checkoutSubmitPayment(checkoutModel, xValidateOnly, xPaymentSessionId, xOrigin);
  },
  processData: (checkoutResponse: CheckoutResponse, s, { xValidateOnly }) => {
    const paymentMethod = s.checkout.payment.availablePaymentTypes.find(t => t.name === s.checkout.payment.selectedPaymentType);
    const paymentType = paymentMethod ? paymentMethod.type : s.checkout.payment.selectedPaymentType;

    return [
      paymentType !== "Credit card" && !xValidateOnly
        ? checkoutPaymentSetCustomStatus("success")
        : { type: CHECKOUT_EMPTY_PAYMENT_ACTION },
      {
        type: CHECKOUT_PROCESS_PAYMENT_FULFILLED,
        payload: { ...checkoutResponse }
      },
      checkoutSetPaymentProcessing(false),
    ];
  },
  processError: (response, { xValidateOnly }) => (response ? [
    checkoutSetPaymentProcessing(false),
    ...xValidateOnly ? [] : [checkoutPaymentSetStatus("fail", response.status, response.statusText, response.data)],
    ...Array.isArray(response.data) ? [{
      type: SHOW_MESSAGE,
      payload: {
        message: response.data.reduce((p, c, i) => p + c.error + (i === response.data.length - 1 ? "" : "\n\n"), ""),
        persist: true
      }
    }] : FetchErrorHandler(response,
      response.data.responseText
        ? response.data.responseText
        : /(4|5)+/.test(response.status)
        ? response.error
          ? response.error
          : "Payment gateway cannot be contacted. Please try again later or contact ish support."
        : null)
  ] : [
    checkoutSetPaymentProcessing(false),
    ...FetchErrorHandler(response, "Payment gateway cannot be contacted. Please try again later or contact ish support.")])
};

export const EpicCheckoutProcessPayment: Epic<any, any> = EpicUtils.Create(request);

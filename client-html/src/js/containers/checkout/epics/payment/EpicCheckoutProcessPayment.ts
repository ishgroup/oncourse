/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CheckoutResponse } from '@api/model';
import { format } from 'date-fns';
import { YYYY_MM_DD_MINUSED } from 'ish-ui';
import { getFormValues } from 'redux-form';
import { Epic } from 'redux-observable';
import { SHOW_MESSAGE } from '../../../../common/actions';
import FetchErrorHandler from '../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import * as EpicUtils from '../../../../common/epics/EpicUtils';
import {
  CHECKOUT_EMPTY_PAYMENT_ACTION,
  CHECKOUT_PROCESS_PAYMENT,
  checkoutPaymentSetCustomStatus,
  checkoutPaymentSetStatus,
  checkoutProcessPaymentFulfilled,
  checkoutSetPaymentProcessing
} from '../../actions/checkoutPayment';
import { CHECKOUT_SELECTION_FORM_NAME } from '../../components/CheckoutSelection';
import {
  CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM
} from '../../components/fundingInvoice/CheckoutFundingInvoiceSummaryList';
import { CHECKOUT_SUMMARY_FORM } from '../../components/summary/CheckoutSummaryList';
import CheckoutService from '../../services/CheckoutService';
import { getCheckoutModel, getPaymentErrorMessage, paymentErrorMessageDefault } from '../../utils';

const request: EpicUtils.Request<any, { xValidateOnly: boolean, xPaymentSessionId: string, xOrigin: string }> = {
  type: CHECKOUT_PROCESS_PAYMENT,
  getData: async ({
  xValidateOnly, xPaymentSessionId
  }, s) => {
    
    const paymentPlans = (getFormValues(CHECKOUT_SELECTION_FORM_NAME)(s) as any)?.paymentPlans || [];

    const checkoutModel = getCheckoutModel(
      s.checkout,
      paymentPlans.filter(p => p.amount && p.date).map(p => ({ amount: p.amount, date: format(new Date(p.date), YYYY_MM_DD_MINUSED) })),
      (getFormValues(CHECKOUT_FUNDING_INVOICE_SUMMARY_LIST_FORM)(s) as any).fundingInvoices,
      (getFormValues(CHECKOUT_SUMMARY_FORM)(s) as any)
    );

    const sessionResponse = await CheckoutService.createSession(checkoutModel);

    return CheckoutService.submitPayment(sessionResponse.sessionId, null, xPaymentSessionId, sessionResponse.merchantReference);
  },
  processData: (checkoutResponse: CheckoutResponse, s, { xValidateOnly }) => {
    const paymentMethod = s.checkout.payment.availablePaymentTypes.find(t => t.name === s.checkout.payment.selectedPaymentType);
    const paymentType = paymentMethod ? paymentMethod.type : s.checkout.payment.selectedPaymentType;

    return [
      paymentType !== "Credit card" && !xValidateOnly
        ? checkoutPaymentSetCustomStatus("success")
        : { type: CHECKOUT_EMPTY_PAYMENT_ACTION },
      checkoutProcessPaymentFulfilled(checkoutResponse),
      checkoutSetPaymentProcessing(false),
    ];
  },
  processError: (response, { xValidateOnly }) => {
    const actions: any = [
      checkoutSetPaymentProcessing(false),
      checkoutProcessPaymentFulfilled({
        sessionId: null,
        ccFormUrl: null,
        merchantReference: null,
        paymentId: null,
        invoice: null,
      })
    ];
    
    if (response) {
      if (!xValidateOnly) {
        actions.push(
          checkoutPaymentSetStatus(
            "fail",
            response.status,
            response.statusText,
            { ...response.data, responseText: getPaymentErrorMessage(response) }
          )
        );
      }
      if (Array.isArray(response.data)) {
        actions.push({
          type: SHOW_MESSAGE,
          payload: {
            message: getPaymentErrorMessage(response),
            persist: true
          }
        });
      } else {
        actions.push(
          ...FetchErrorHandler(response, getPaymentErrorMessage(response))
        );
      }
    } else {
      actions.push(...FetchErrorHandler(response, paymentErrorMessageDefault));
    }

    return actions;
  }
};

export const EpicCheckoutProcessPayment: Epic<any, any> = EpicUtils.Create(request);
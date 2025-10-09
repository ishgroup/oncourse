/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { CheckoutResponse } from '@api/model';
import { Epic } from 'redux-observable';
import { SHOW_MESSAGE } from '../../../../common/actions';
import FetchErrorHandler from '../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import * as EpicUtils from '../../../../common/epics/EpicUtils';
import InvoiceService from '../../../entities/invoices/services/InvoiceService';
import PaymentInService from '../../../entities/paymentsIn/services/PaymentInService';
import {
  CHECKOUT_GET_PAYMENT_DETAILS_BY_REFERENCE,
  checkoutProcessPaymentFulfilled,
  checkoutSetPaymentProcessing
} from '../../actions/checkoutPayment';
import { getPaymentErrorMessage, paymentErrorMessageDefault } from '../../utils';

const request: EpicUtils.Request<CheckoutResponse, string> = {
  type: CHECKOUT_GET_PAYMENT_DETAILS_BY_REFERENCE,
  getData: async merchantReference => {
    const paymentIn = await PaymentInService.getPerformedPaymentInfo(merchantReference);

    const invoice = await InvoiceService.getInvoice(paymentIn.invoices[0].id);

    return {
      paymentId: paymentIn.id,
      invoice
    };
  },
  processData: checkoutResponse => [
    checkoutProcessPaymentFulfilled(checkoutResponse),
    checkoutSetPaymentProcessing(false)
  ],
  processError: response => {
    const actions: any = [
      checkoutSetPaymentProcessing(false),
      checkoutProcessPaymentFulfilled({
        paymentId: null,
        invoice: null,
      })
    ];

    if (response) {
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

export const EpicGetPaymentInfoByReference: Epic<any, any> = EpicUtils.Create(request);
/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { CheckoutCCResponse } from '@api/model';
import { Stripe } from '@stripe/stripe-js';
import { getFormValues } from 'redux-form';
import { Epic } from 'redux-observable';
import { SHOW_MESSAGE } from '../../../../common/actions';
import FetchErrorHandler from '../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import * as EpicUtils from '../../../../common/epics/EpicUtils';
import { LSSetItem } from '../../../../common/utils/storage';
import {
  CHECKOUT_PROCESS_STRIPE_CC_PAYMENT,
  checkoutClearPaymentStatus,
  checkoutGetPaymentStatusDetails,
  checkoutPaymentSetStatus,
  checkoutProcessPaymentFulfilled,
  checkoutSetPaymentProcessing,
  clearCcIframeUrl
} from '../../actions/checkoutPayment';
import { CHECKOUT_SELECTION_FORM_NAME } from '../../components/CheckoutSelection';
import CheckoutService from '../../services/CheckoutService';
import {
  getCheckoutModel,
  getPaymentErrorMessage,
  getStoredPaymentStateKey,
  paymentErrorMessageDefault
} from '../../utils';

const request: EpicUtils.Request<{ checkoutResponse: CheckoutCCResponse, sessionId: string }, { stripePaymentMethodId: string, stripe: Stripe }> = {
  type: CHECKOUT_PROCESS_STRIPE_CC_PAYMENT,
  getData: async ({
    stripePaymentMethodId,
    stripe
  }, s) => {

    const checkoutModel = getCheckoutModel(
      s
    );

    const sessionResponse = await CheckoutService.createSession(checkoutModel, s.checkout.payment.sessionId);

    const checkoutResponse = await CheckoutService.submitCreditCardPayment({
      onCoursePaymentSessionId: sessionResponse.sessionId,
      paymentMethodId: stripePaymentMethodId,
      transactionId: null,
      merchantReference: sessionResponse.merchantReference,
      origin: window.location.origin
    });

    if (checkoutResponse.actionRequired) {
      LSSetItem(getStoredPaymentStateKey(sessionResponse.sessionId), JSON.stringify({
        checkoutState: s.checkout,
        selectionForm: getFormValues(CHECKOUT_SELECTION_FORM_NAME)(s)
      }));

      const {
        error,
      } = await stripe.handleCardAction(checkoutResponse.clientSecret);

      if (error) {
        throw error;
      }
    }
    return { checkoutResponse, sessionId: checkoutResponse.paymentSystemSessionId };
  },
  processData: ({ checkoutResponse, sessionId }) => [
    checkoutGetPaymentStatusDetails(sessionId),
    checkoutProcessPaymentFulfilled(checkoutResponse.checkoutResponse),
    checkoutSetPaymentProcessing(false),
  ],
  processError: response => {
    const actions: any = [
      checkoutSetPaymentProcessing(false),
      checkoutClearPaymentStatus(),
      clearCcIframeUrl()
    ];

    if (response) {
      actions.push(
        checkoutPaymentSetStatus(
          "fail",
          response.status,
          response.statusText,
          { ...response.data, responseText: getPaymentErrorMessage(response) }
        )
      );
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

export const EpicCheckoutProcessStripeCCPayment: Epic<any, any> = EpicUtils.Create(request);
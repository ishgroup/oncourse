/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CheckoutCCResponse, CheckoutPaymentPlan, CheckoutResponse } from '@api/model';
import { Stripe } from '@stripe/stripe-js';
import { _toRequestType, FULFILLED } from '../../../common/actions/ActionUtils';

export const CHECKOUT_GET_ACTIVE_PAYMENT_TYPES = _toRequestType("checkout/get/peyment/method");
export const CHECKOUT_GET_ACTIVE_PAYMENT_TYPES_FULFILLED = FULFILLED(CHECKOUT_GET_ACTIVE_PAYMENT_TYPES);

export const CHECKOUT_GET_SAVED_CARD = _toRequestType("checkout/get/saved/card");
export const CHECKOUT_GET_SAVED_CARD_FULFILLED = FULFILLED(CHECKOUT_GET_SAVED_CARD);

export const CHECKOUT_SET_PAYMENT_TYPE = "checkout/set/paymentType";

export const CHECKOUT_SET_PAYMENT_PROCESSING = "checkout/set/payment/processing";
export const CHECKOUT_SET_PAYMENT_SUCCESS = "checkout/set/payment/success";
export const CHECKOUT_SET_PAYMENT_SET_STATUS = "checkout/set/payment/set/status";

export const CHECKOUT_GET_PAYMENT_STATUS_DETAILS = _toRequestType("checkout/get/payment/status/details");
export const CHECKOUT_GET_PAYMENT_DETAILS_BY_REFERENCE = _toRequestType("checkout/get/payment/details");
export const CHECKOUT_SET_PAYMENT_DETAILS_FETCHING = "checkout/set/payment/status/details/fetching";
export const CHECKOUT_SET_PAYMENT_STATUS = "checkout/get/payment/status";
export const CHECKOUT_SET_PAYMENT_STATUS_DETAILS = "checkout/set/payment/status/details";
export const CHECKOUT_CLEAR_PAYMENT_STATUS = _toRequestType("checkout/clear/payment/status");

export const CHECKOUT_PROCESS_PAYMENT = _toRequestType("checkout/process/cc/payment");
export const CHECKOUT_PROCESS_STRIPE_CC_PAYMENT = _toRequestType("checkout/process/stripe/cc/payment");
export const CHECKOUT_PROCESS_EWAY_CC_PAYMENT = _toRequestType("checkout/process/eway/cc/payment");
export const CHECKOUT_PROCESS_PAYMENT_FULFILLED = FULFILLED(CHECKOUT_PROCESS_PAYMENT);

export const CHECKOUT_CLEAR_CC_IFRAME_URL = "checkout/clear/wcIframe/url";

export const CHECKOUT_SET_PAYMENT_PLANS = "checkout/set/payment/plans";

export const checkoutProcessPaymentFulfilled = (response: CheckoutResponse | CheckoutCCResponse) => ({
  type: CHECKOUT_PROCESS_PAYMENT_FULFILLED,
  payload: response
});

export const checkoutGetPaymentDetailsByReference = (reference: string) => ({
  type: CHECKOUT_GET_PAYMENT_DETAILS_BY_REFERENCE,
  payload: reference
});

export const checkoutGetSavedCard = (payerId: number, paymentMethodId: number) => ({
  type: CHECKOUT_GET_SAVED_CARD,
  payload: { payerId, paymentMethodId }
});

export const checkoutGetActivePaymentMethods = () => ({
  type: CHECKOUT_GET_ACTIVE_PAYMENT_TYPES
});

export const checkoutSetPaymentMethod = (selectedType: string) => ({
  type: CHECKOUT_SET_PAYMENT_TYPE,
  payload: { selectedType }
});

export const checkoutSetPaymentStatusDetails = (data: any) => ({
  type: CHECKOUT_SET_PAYMENT_STATUS_DETAILS,
  payload: { data }
});

export const checkoutSetPaymentPlans = (paymentPlans: CheckoutPaymentPlan[]) => ({
  type: CHECKOUT_SET_PAYMENT_PLANS,
  payload: paymentPlans
});

export const checkoutSetPaymentProcessing = (isProcessing: boolean) => ({
  type: CHECKOUT_SET_PAYMENT_PROCESSING,
  payload: { isProcessing }
});

export const checkoutSetPaymentDetailsFetching = (isFetchingDetails: boolean) => ({
  type: CHECKOUT_SET_PAYMENT_DETAILS_FETCHING,
  payload: isFetchingDetails
});

export const checkoutSetPaymentSuccess = (isSuccess: boolean) => ({
  type: CHECKOUT_SET_PAYMENT_SUCCESS,
  payload: { isSuccess }
});

export const checkoutProcessStripeCCPayment = (
  stripePaymentMethodId: string,
  stripe: Stripe
) => ({
  type: CHECKOUT_PROCESS_STRIPE_CC_PAYMENT,
  payload: {
    stripePaymentMethodId,
    stripe
  }
});

export const checkoutProcessEwayCCPayment = (
  eWaySecureFieldCode: string
) => ({
  type: CHECKOUT_PROCESS_EWAY_CC_PAYMENT,
  payload: eWaySecureFieldCode
});

export const checkoutProcessPayment = () => ({
  type: CHECKOUT_PROCESS_PAYMENT
});

export const checkoutPaymentSetStatus = (status, statusCode, statusText, data) => ({
  type: CHECKOUT_SET_PAYMENT_SET_STATUS,
  payload: {
    status, statusCode, statusText, data
  }
});

export const checkoutPaymentSetCustomStatus = (status: string) => ({
  type: CHECKOUT_SET_PAYMENT_STATUS,
  payload: { status }
});

export const checkoutGetPaymentStatusDetails = (sessionId: string) => ({
  type: CHECKOUT_GET_PAYMENT_STATUS_DETAILS,
  payload: { sessionId }
});

export const checkoutClearPaymentStatus = () => ({
  type: CHECKOUT_CLEAR_PAYMENT_STATUS,
});

export const clearCcIframeUrl = () => ({
  type: CHECKOUT_CLEAR_CC_IFRAME_URL
});

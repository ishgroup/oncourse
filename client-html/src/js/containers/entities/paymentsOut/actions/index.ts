/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_ADD_PAYMENT_OUT_CONTACT = _toRequestType("get/paymentOut/contact");

export const GET_ACTIVE_PAYMENT_OUT_METHODS = _toRequestType("get/paymentOut/methods");
export const GET_ACTIVE_PAYMENT_OUT_METHODS_FULFILLED = FULFILLED(GET_ACTIVE_PAYMENT_OUT_METHODS);

export const GET_REFUNDABLE_PAYMENTS = _toRequestType("get/paymentOut/refundablePayments");
export const GET_REFUNDABLE_PAYMENTS_FULFILLED = FULFILLED(GET_REFUNDABLE_PAYMENTS);

export const GET_ADD_PAYMENT_OUT_VALUES = _toRequestType("get/paymentOut/customValues");

export const getAddPaymentOutContact = (id: number) => ({
  type: GET_ADD_PAYMENT_OUT_CONTACT,
  payload: id
});

export const getActivePaymentOutMethods = () => ({
  type: GET_ACTIVE_PAYMENT_OUT_METHODS
});
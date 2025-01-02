/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { _toRequestType, FULFILLED } from "../../../common/actions/ActionUtils";
import { CheckoutCourse, CheckoutItem } from "../../../model/checkout";

export const CHECKOUT_GET_ITEM_PRODUCT = _toRequestType("checkout/get/product");
export const CHECKOUT_GET_ITEM_PRODUCT_FULFILLED = FULFILLED(CHECKOUT_GET_ITEM_PRODUCT);

export const CHECKOUT_GET_ITEM_MEMBERSHIP = _toRequestType("checkout/get/membership");
export const CHECKOUT_GET_ITEM_MEMBERSHIP_FULFILLED = _toRequestType(CHECKOUT_GET_ITEM_MEMBERSHIP);

export const CHECKOUT_GET_ITEM_VOUCHER = _toRequestType("checkout/get/voucher");
export const CHECKOUT_GET_ITEM_VOUCHER_FULFILLED = FULFILLED(CHECKOUT_GET_ITEM_VOUCHER);

export const CLEAR_CHECKOUT_ITEM_RECORD = "clear/checkout/itemRecord";

export const CHECKOUT_GET_COURSE_CLASS_LIST = _toRequestType("checkout/get/courseClasses");
export const CHECKOUT_GET_COURSE_CLASS_LIST_FULFILLED = FULFILLED(CHECKOUT_GET_COURSE_CLASS_LIST);

export const CHECKOUT_CLEAR_COURSE_CLASS_LIST = "checkout/clear/courseClass";

export const CHECKOUT_GET_CLASS_PAYMENT_PLANS = _toRequestType("checkout/get/class/paymentPlan");

export const checkoutGetProduct = (id: number) => ({
  type: CHECKOUT_GET_ITEM_PRODUCT,
  payload: id
});

export const checkoutGetMembership = (id: number) => ({
  type: CHECKOUT_GET_ITEM_MEMBERSHIP,
  payload: { id }
});

export const checkoutGetVoucher = (item: CheckoutItem) => ({
  type: CHECKOUT_GET_ITEM_VOUCHER,
  payload: item
});

export const clearCheckoutItemRecord = () => ({
  type: CLEAR_CHECKOUT_ITEM_RECORD
});

export const checkoutGetCourseClassList = (search: string) => ({
  type: CHECKOUT_GET_COURSE_CLASS_LIST,
  payload: { search }
});

export const checkoutClearCourseClassList = () => ({
  type: CHECKOUT_CLEAR_COURSE_CLASS_LIST
});

export const checkoutGetClassPaymentPlans = (item: CheckoutCourse) => ({
  type: CHECKOUT_GET_CLASS_PAYMENT_PLANS,
  payload: {
    item
  }
});

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { VoucherProduct } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_VOUCHER_PRODUCT_ITEM = _toRequestType("get/voucherProduct");
export const GET_VOUCHER_PRODUCT_ITEM_FULFILLED = FULFILLED(GET_VOUCHER_PRODUCT_ITEM);

export const UPDATE_VOUCHER_PRODUCT_ITEM = _toRequestType("put/voucherProduct");
export const UPDATE_VOUCHER_PRODUCT_ITEM_FULFILLED = FULFILLED(UPDATE_VOUCHER_PRODUCT_ITEM);

export const CREATE_VOUCHER_PRODUCT_ITEM = _toRequestType("post/voucherProduct");
export const CREATE_VOUCHER_PRODUCT_ITEM_FULFILLED = FULFILLED(CREATE_VOUCHER_PRODUCT_ITEM);

export const GET_VOUCHER_PRODUCT_MIN_MAX_FEE = _toRequestType("get/voucherProduct/minMaxFee");
export const GET_VOUCHER_PRODUCT_MIN_MAX_FEE_FULFILLED = FULFILLED(GET_VOUCHER_PRODUCT_MIN_MAX_FEE);

export const CLEAR_VOUCHER_PRODUCT_MIN_MAX_FEE = "clear/voucherProduct/minMaxFee";

export const getVoucherProduct = (id: string) => ({
  type: GET_VOUCHER_PRODUCT_ITEM,
  payload: id
});

export const updateVoucherProduct = (id: string, voucherProduct: VoucherProduct) => ({
  type: UPDATE_VOUCHER_PRODUCT_ITEM,
  payload: { id, voucherProduct }
});

export const createVoucherProduct = (voucherProduct: VoucherProduct) => ({
  type: CREATE_VOUCHER_PRODUCT_ITEM,
  payload: { voucherProduct }
});

export const getMinMaxFee = (courseIds: string) => ({
  type: GET_VOUCHER_PRODUCT_MIN_MAX_FEE,
  payload: courseIds
});

export const clearMinMaxFee = () => ({
  type: CLEAR_VOUCHER_PRODUCT_MIN_MAX_FEE
});

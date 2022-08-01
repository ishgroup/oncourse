/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_VOUCHER_PRODUCT_MIN_MAX_FEE = _toRequestType("get/voucherProduct/minMaxFee");
export const GET_VOUCHER_PRODUCT_MIN_MAX_FEE_FULFILLED = FULFILLED(GET_VOUCHER_PRODUCT_MIN_MAX_FEE);

export const CLEAR_VOUCHER_PRODUCT_MIN_MAX_FEE = "clear/voucherProduct/minMaxFee";

export const getMinMaxFee = (courseIds: string) => ({
  type: GET_VOUCHER_PRODUCT_MIN_MAX_FEE,
  payload: courseIds
});

export const clearMinMaxFee = () => ({
  type: CLEAR_VOUCHER_PRODUCT_MIN_MAX_FEE
});
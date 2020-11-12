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

export const SEARCH_VOUCHER_PRODUCT_COURSES = _toRequestType("search/voucherProduct/courses");
export const SEARCH_VOUCHER_PRODUCT_COURSES_FULFILLED = FULFILLED(SEARCH_VOUCHER_PRODUCT_COURSES);

export const CLEAR_VOUCHER_PRODUCT_COURSES = "clear/voucherProduct/searchCourses";

export const GET_VOUCHER_PRODUCT_MIN_MAX_FEE = _toRequestType("get/voucherProduct/minMaxFee");
export const GET_VOUCHER_PRODUCT_MIN_MAX_FEE_FULFILLED = FULFILLED(GET_VOUCHER_PRODUCT_MIN_MAX_FEE);

export const CLEAR_VOUCHER_PRODUCT_MIN_MAX_FEE = "clear/voucherProduct/minMaxFee";

export const GET_PLAIN_VOUCHER_PRODUCTS = _toRequestType("get/plain/voucherProduct");
export const GET_PLAIN_VOUCHER_PRODUCTS_FULFILLED = FULFILLED(GET_PLAIN_VOUCHER_PRODUCTS);

export const SET_PLAIN_VOUCHER_PRODUCTS_SEARCH = _toRequestType("set/plain/voucherProduct/search");
export const CLEAR_PLAIN_VOUCHER_PRODUCTS_SEARCH = _toRequestType("clear/plain/voucherProduct/search");

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

export const searchCourses = (search: string) => ({
  type: SEARCH_VOUCHER_PRODUCT_COURSES,
  payload: search
});

export const clearSearchCourses = (pending: boolean) => ({
  type: CLEAR_VOUCHER_PRODUCT_COURSES,
  payload: pending
});

export const getMinMaxFee = (courseIds: string) => ({
  type: GET_VOUCHER_PRODUCT_MIN_MAX_FEE,
  payload: courseIds
});

export const clearMinMaxFee = () => ({
  type: CLEAR_VOUCHER_PRODUCT_COURSES
});

export const getPlainVoucherProducts = (offset?: number, columns?: string, ascending?: boolean, pageSize?: number) => ({
  type: GET_PLAIN_VOUCHER_PRODUCTS,
  payload: {
 offset, columns, ascending, pageSize
}
});

export const setPlainVoucherProductSearch = (search: string) => ({
  type: SET_PLAIN_VOUCHER_PRODUCTS_SEARCH,
  payload: { search }
});

export const clearPlainVoucherProductSearch = () => ({
  type: CLEAR_PLAIN_VOUCHER_PRODUCTS_SEARCH,
  payload: { search: "", items: [] }
});

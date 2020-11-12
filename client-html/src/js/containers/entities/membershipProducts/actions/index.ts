/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { MembershipProduct } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_MEMBERSHIP_PRODUCT_ITEM = _toRequestType("get/membershipProduct");
export const GET_MEMBERSHIP_PRODUCT_ITEM_FULFILLED = FULFILLED(GET_MEMBERSHIP_PRODUCT_ITEM);

export const UPDATE_MEMBERSHIP_PRODUCT_ITEM = _toRequestType("put/membershipProduct");
export const UPDATE_MEMBERSHIP_PRODUCT_ITEM_FULFILLED = FULFILLED(UPDATE_MEMBERSHIP_PRODUCT_ITEM);

export const CREATE_MEMBERSHIP_PRODUCT_ITEM = _toRequestType("post/membershipProduct");
export const CREATE_MEMBERSHIP_PRODUCT_ITEM_FULFILLED = FULFILLED(CREATE_MEMBERSHIP_PRODUCT_ITEM);

export const GET_MEMBERSHIP_PRODUCT_DISCOUNTS = _toRequestType("get/search/membershipDiscounts");
export const GET_MEMBERSHIP_PRODUCT_DISCOUNTS_FULFILLED = FULFILLED(GET_MEMBERSHIP_PRODUCT_DISCOUNTS);

export const CLEAR_DISCOUNTS_SEARCH = "clear/search/membershipDiscounts";

export const GET_MEMBERSHIP_PRODUCT_CONTACT_RELATION_TYPES = _toRequestType("get/membership/contactRelationTypes");
export const GET_MEMBERSHIP_PRODUCT_CONTACT_RELATION_TYPES_FULFILLED = FULFILLED(
  GET_MEMBERSHIP_PRODUCT_CONTACT_RELATION_TYPES
);

export const GET_PLAIN_MEMBERSHIP_PRODUCTS = _toRequestType("get/plain/membershipProduct");
export const GET_PLAIN_MEMBERSHIP_PRODUCTS_FULFILLED = FULFILLED(GET_PLAIN_MEMBERSHIP_PRODUCTS);

export const SET_PLAIN_MEMBERSHIP_PRODUCTS_SEARCH = _toRequestType("set/plain/membershipProduct/search");
export const CLEAR_PLAIN_MEMBERSHIP_PRODUCTS_SEARCH = _toRequestType("clear/plain/membershipProduct/search");

export const getMembershipProduct = (id: string) => ({
  type: GET_MEMBERSHIP_PRODUCT_ITEM,
  payload: id
});

export const updateMembershipProduct = (id: string, membershipProduct: MembershipProduct) => ({
  type: UPDATE_MEMBERSHIP_PRODUCT_ITEM,
  payload: { id, membershipProduct }
});

export const createMembershipProduct = (membershipProduct: MembershipProduct) => ({
  type: CREATE_MEMBERSHIP_PRODUCT_ITEM,
  payload: { membershipProduct }
});

export const searchDiscounts = (search: string) => ({
  type: GET_MEMBERSHIP_PRODUCT_DISCOUNTS,
  payload: search
});

export const clearDiscountsSearch = (pending: boolean) => ({
  type: CLEAR_DISCOUNTS_SEARCH,
  payload: { discountsPending: pending }
});

export const getMembershipProductContactRelationTypes = () => ({
  type: GET_MEMBERSHIP_PRODUCT_CONTACT_RELATION_TYPES
});

export const getPlainMembershipProducts = (offset?: number, columns?: string, ascending?: boolean, pageSize?: number) => ({
  type: GET_PLAIN_MEMBERSHIP_PRODUCTS,
  payload: {
 offset, columns, ascending, pageSize
}
});

export const setPlainMembershipProductSearch = (search: string) => ({
  type: SET_PLAIN_MEMBERSHIP_PRODUCTS_SEARCH,
  payload: { search }
});

export const clearPlainMembershipProductSearch = () => ({
  type: CLEAR_PLAIN_MEMBERSHIP_PRODUCTS_SEARCH,
  payload: { search: "", items: [] }
});

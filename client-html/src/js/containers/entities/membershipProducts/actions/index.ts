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

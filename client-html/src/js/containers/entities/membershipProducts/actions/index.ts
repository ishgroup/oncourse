/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { MembershipProduct } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const UPDATE_MEMBERSHIP_PRODUCT_ITEM = _toRequestType("put/membershipProduct");
export const UPDATE_MEMBERSHIP_PRODUCT_ITEM_FULFILLED = FULFILLED(UPDATE_MEMBERSHIP_PRODUCT_ITEM);

export const updateMembershipProduct = (id: string, membershipProduct: MembershipProduct) => ({
  type: UPDATE_MEMBERSHIP_PRODUCT_ITEM,
  payload: { id, membershipProduct }
});
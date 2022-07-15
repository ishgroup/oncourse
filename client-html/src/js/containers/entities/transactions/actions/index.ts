/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { _toRequestType } from "../../../../common/actions/ActionUtils";

export const GET_TRANSACTION_ITEM = _toRequestType("get/transaction");

export const getTransaction = (id: string) => ({
  type: GET_TRANSACTION_ITEM,
  payload: id
});
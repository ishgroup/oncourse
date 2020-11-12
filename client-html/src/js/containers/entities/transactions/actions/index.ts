/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { _toRequestType } from "../../../../common/actions/ActionUtils";
import { Transaction } from "@api/model";

export const GET_TRANSACTION_ITEM = _toRequestType("get/transaction");

export const CREATE_TRANSACTION_ITEM = _toRequestType("post/transaction");

export const getTransaction = (id: string) => ({
  type: GET_TRANSACTION_ITEM,
  payload: id
});

export const createTransaction = (transaction: Transaction) => ({
  type: CREATE_TRANSACTION_ITEM,
  payload: { transaction }
});

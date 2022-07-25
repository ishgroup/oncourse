/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Module } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const UPDATE_MODULE_ITEM = _toRequestType("put/module");
export const UPDATE_MODULE_ITEM_FULFILLED = FULFILLED(UPDATE_MODULE_ITEM);

export const updateModule = (id: string, module: Module) => ({
  type: UPDATE_MODULE_ITEM,
  payload: { id, module }
});
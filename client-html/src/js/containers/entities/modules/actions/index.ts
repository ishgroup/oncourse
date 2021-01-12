/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Module } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_MODULE_ITEM = _toRequestType("get/module");
export const GET_MODULE_ITEM_FULFILLED = FULFILLED(GET_MODULE_ITEM);

export const DELETE_MODULE_ITEM = _toRequestType("delete/module");
export const DELETE_MODULE_ITEM_FULFILLED = FULFILLED(DELETE_MODULE_ITEM);

export const UPDATE_MODULE_ITEM = _toRequestType("put/module");
export const UPDATE_MODULE_ITEM_FULFILLED = FULFILLED(UPDATE_MODULE_ITEM);

export const CREATE_MODULE_ITEM = _toRequestType("post/module");
export const CREATE_MODULE_ITEM_FULFILLED = FULFILLED(CREATE_MODULE_ITEM);

export const getModule = (id: string) => ({
  type: GET_MODULE_ITEM,
  payload: id
});

export const removeModule = (id: string) => ({
  type: DELETE_MODULE_ITEM,
  payload: id
});

export const updateModule = (id: string, module: Module) => ({
  type: UPDATE_MODULE_ITEM,
  payload: { id, module }
});

export const createModule = (module: Module) => ({
  type: CREATE_MODULE_ITEM,
  payload: { module }
});

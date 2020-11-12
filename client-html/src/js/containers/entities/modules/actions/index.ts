/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { Module } from "@api/model";

export const GET_MODULE_ITEM = _toRequestType("get/module");
export const GET_MODULE_ITEM_FULFILLED = FULFILLED(GET_MODULE_ITEM);

export const GET_MODULE_ITEMS = _toRequestType("get/modules");
export const GET_MODULE_ITEMS_FULFILLED = FULFILLED(GET_MODULE_ITEMS);

export const DELETE_MODULE_ITEM = _toRequestType("delete/module");
export const DELETE_MODULE_ITEM_FULFILLED = FULFILLED(DELETE_MODULE_ITEM);

export const UPDATE_MODULE_ITEM = _toRequestType("put/module");
export const UPDATE_MODULE_ITEM_FULFILLED = FULFILLED(UPDATE_MODULE_ITEM);

export const CREATE_MODULE_ITEM = _toRequestType("post/module");
export const CREATE_MODULE_ITEM_FULFILLED = FULFILLED(CREATE_MODULE_ITEM);

export const SET_MODULE_SEARCH = _toRequestType("set/module/search");

export const CLEAR_MODULE_ITEMS = _toRequestType("clear/module/items");

export const getModule = (id: string) => ({
  type: GET_MODULE_ITEM,
  payload: id
});

export const getModules = (offset?: number, columns?: string, ascending?: boolean, pageSize?: number) => ({
  type: GET_MODULE_ITEMS,
  payload: { offset, columns, ascending, pageSize }
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

export const setModuleSearch = (search: string) => ({
  type: SET_MODULE_SEARCH,
  payload: { search }
});

export const clearModuleItems = (loading?: boolean) => ({
  type: CLEAR_MODULE_ITEMS,
  payload: { loading }
});

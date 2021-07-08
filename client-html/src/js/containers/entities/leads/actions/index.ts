/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { Lead } from "@api/model";

export const GET_LEAD_ITEM = _toRequestType("get/lead");
export const GET_LEAD_ITEM_FULFILLED = FULFILLED(GET_LEAD_ITEM);

export const DELETE_LEAD_ITEM = _toRequestType("delete/lead");
export const DELETE_LEAD_ITEM_FULFILLED = FULFILLED(DELETE_LEAD_ITEM);

export const UPDATE_LEAD_ITEM = _toRequestType("put/lead");
export const UPDATE_LEAD_ITEM_FULFILLED = FULFILLED(UPDATE_LEAD_ITEM);

export const CREATE_LEAD_ITEM = _toRequestType("post/lead");
export const CREATE_LEAD_ITEM_FULFILLED = FULFILLED(CREATE_LEAD_ITEM);

export const GET_LEAD_DELETE_VALIDATION = _toRequestType("get/list/entity/lead/validation");
export const GET_LEAD_DELETE_VALIDATION_FULFILLED = FULFILLED(GET_LEAD_DELETE_VALIDATION);

export const validateDeleteLead = (id: string, callback: any) => ({
  type: GET_LEAD_DELETE_VALIDATION,
  payload: { id, callback }
});

export const getLead = (id: string) => ({
  type: GET_LEAD_ITEM,
  payload: id
});

export const removeLead = (id: string) => ({
  type: DELETE_LEAD_ITEM,
  payload: id
});

export const updateLead = (id: string, lead: Lead) => ({
  type: UPDATE_LEAD_ITEM,
  payload: { id, lead }
});

export const createLead = (lead: Lead) => ({
  type: CREATE_LEAD_ITEM,
  payload: { lead }
});

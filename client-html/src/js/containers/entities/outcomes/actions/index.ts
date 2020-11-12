/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Outcome } from "@api/model";
import { _toRequestType } from "../../../../common/actions/ActionUtils";

export const GET_OUTCOME_ITEM = _toRequestType("get/outcome");

export const GET_OUTCOME_TAGS = _toRequestType("get/outcome/tags");

export const UPDATE_OUTCOME_ITEM = _toRequestType("put/outcome");

export const DELETE_OUTCOME_ITEM = _toRequestType("delete/outcome");

export const CREATE_OUTCOME_ITEM = _toRequestType("post/outcome");

export const getOutcome = (id: string) => ({
  type: GET_OUTCOME_ITEM,
  payload: id
});

export const getOutcomeTags = () => ({
  type: GET_OUTCOME_TAGS,
});

export const updateOutcome = (id: string, outcome: Outcome) => ({
  type: UPDATE_OUTCOME_ITEM,
  payload: { id, outcome }
});

export const createOutcome = (outcome: Outcome) => ({
  type: CREATE_OUTCOME_ITEM,
  payload: { outcome }
});

export const deleteOutcome = (id: string) => ({
  type: DELETE_OUTCOME_ITEM,
  payload: id
});

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Assessment } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const UPDATE_ASSESSMENT_ITEM = _toRequestType("put/assessment");
export const UPDATE_ASSESSMENT_ITEM_FULFILLED = FULFILLED(UPDATE_ASSESSMENT_ITEM);

export const updateAssessment = (id: string, assessment: Assessment) => ({
  type: UPDATE_ASSESSMENT_ITEM,
  payload: { id, assessment }
});
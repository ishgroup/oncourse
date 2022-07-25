/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { AssessmentSubmission } from "@api/model";

export const UPDATE_ASSESSMENT_SUBMISSIONS_ITEM = "UPDATE_ASSESSMENT_SUBMISSIONS_ITEM";
export const UPDATE_ASSESSMENT_SUBMISSIONS_ITEM_FULFILLED = "UPDATE_ASSESSMENT_SUBMISSIONS_ITEM_FULFILLED";

export const updateAssessmentSubmissionsItem = (id: number, assessmentSubmission: AssessmentSubmission) => ({
  type: UPDATE_ASSESSMENT_SUBMISSIONS_ITEM,
  payload: { id, assessmentSubmission }
});
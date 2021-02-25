/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

// import { AssessmentSubmission } from "@api/model";

export const GET_ASSESSMENT_SUBMISSIONS_ITEM = "GET_ASSESSMENT_SUBMISSIONS_ITEM";
export const GET_ASSESSMENT_SUBMISSIONS_ITEM_FULFILLED = "GET_ASSESSMENT_SUBMISSIONS_ITEM_FULFILLED";

export const getAssessmentSubmissionsItem = (id: string) => ({
  type: GET_ASSESSMENT_SUBMISSIONS_ITEM,
  payload: id
});

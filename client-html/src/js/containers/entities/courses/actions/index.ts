/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { _toRequestType } from "../../../../common/actions/ActionUtils";

export const DUPLICATE_COURSE = _toRequestType("post/duplicate");

export const duplicateCourses = (ids: number[]) => ({
  type: DUPLICATE_COURSE,
  payload: ids
});
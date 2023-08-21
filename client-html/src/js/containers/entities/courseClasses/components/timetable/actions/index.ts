/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Session } from "@api/model";
import { _toRequestType } from "../../../../../../common/actions/ActionUtils";

export const GET_COURSE_CLASS_SESSIONS = _toRequestType("get/courseClass/sessions");

export const POST_COURSE_CLASS_SESSIONS = _toRequestType("post/courseClass/sessions");
export const COURSE_CLASS_SELECT_BULK_SESSION = "courseClass/select/bulk/session";
export const COURSE_CLASS_SELECT_SINGLE_SESSION = "courseClass/select/single/session";
export const COURSE_CLASS_OPEN_BULK_UPDATE_MODAL = "courseClass/open/bulk/update/modal";
export const COURSE_CLASS_CLOSE_BULK_UPDATE_MODAL = "courseClass/close/bulk/update/modal";

export const postCourseClassSessions = (classId: number, sessions: Session[]) => ({
  type: POST_COURSE_CLASS_SESSIONS,
  payload: { classId, sessions }
});

export const courseClassSelectAllSession = sessions => ({
  type: COURSE_CLASS_SELECT_BULK_SESSION,
  payload: { sessions }
});

export const courseClassSelectSingleSession = session => ({
  type: COURSE_CLASS_SELECT_SINGLE_SESSION,
  payload: { session }
});

export const courseClassOpenBulkUpdateModal = () => ({
  type: COURSE_CLASS_OPEN_BULK_UPDATE_MODAL,
});

export const courseClassCloseBulkUpdateModal = () => ({
  type: COURSE_CLASS_CLOSE_BULK_UPDATE_MODAL
});

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { reset } from "redux-form";
import { CourseClass } from "@api/model";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { clearActionsQueue, FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { processCustomFields } from "../../customFieldTypes/utils";
import { GET_COURSE_CLASS, UPDATE_COURSE_CLASS } from "../actions";
import CourseClassService from "../services/CourseClassService";
import { QueuedAction } from "../../../../model/common/ActionsQueue";
import { processCourseClassApiActions } from "../utils";

const request: EpicUtils.Request<any, { id: number; courseClass: CourseClass }> = {
  type: UPDATE_COURSE_CLASS,
  getData: ({ id, courseClass }) => {
    processCustomFields(courseClass);
    return CourseClassService.updateCourseClass(id, courseClass);
  },
  retrieveData: (p, s) => processCourseClassApiActions(s)
    .then(async (actions: QueuedAction[]) => {
      const syncActions = actions.filter(a => a.entity !== "Note");
      const noteActions = actions.filter(a => a.entity === "Note");
      await processNotesAsyncQueue(noteActions);
      return syncActions;
    }),
  processData: (actions: QueuedAction[], s, { id }) => [
      ...actions.map(a => a.actionBody),
      {
        type: FETCH_SUCCESS,
        payload: { message: "Class updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "CourseClass", listUpdate: true, ignoreSelection: true }
      },
      ...s.list.fullScreenEditView || s.list.records.layout === "Three column" ? [{
        type: GET_COURSE_CLASS,
        payload: id
      }] : [],
      clearActionsQueue()
    ],
  processError: response => [...FetchErrorHandler(response, "Class was not updated"), reset(LIST_EDIT_VIEW_FORM_NAME)]
};

export const EpicUpdateCourseClass: Epic<any, any> = EpicUtils.Create(request);

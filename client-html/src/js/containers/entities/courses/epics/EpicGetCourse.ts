/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import { Course } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_COURSE, GET_COURSE_FULFILLED } from "../actions";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { getEntityItemById } from "../../common/entityItemsService";
import { getNoteItems } from "../../../../common/components/form/notes/actions";
import { removeActionsFromQueue } from "../../../../common/actions";

const request: EpicUtils.Request<any, number> = {
  type: GET_COURSE,
  hideLoadIndicator: true,
  getData: id => getEntityItemById("Course", id),
  processData: (course: Course, s, id) => {
    return [
      {
        type: GET_COURSE_FULFILLED,
        payload: { course }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: course, name: `${course.name} ${course.code}` }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, course),
      getNoteItems("Course", id, LIST_EDIT_VIEW_FORM_NAME),
      ...(s.actionsQueue.queuedActions.length ? [removeActionsFromQueue([{ entity: "Note" }])] : [])
    ];
  },
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetCourse: Epic<any, any> = EpicUtils.Create(request);

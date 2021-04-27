/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import { CourseClass } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions/index";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { DELETE_COURSE_CLASS } from "../actions";
import CourseClassService from "../services/CourseClassService";

const request: EpicUtils.Request<any, { id: number }> = {
  type: DELETE_COURSE_CLASS,
  getData: ({ id }) => CourseClassService.deleteCourseClass(id),
  processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Class was deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "CourseClass", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ],
  processError: response => FetchErrorHandler(response, "Class was not deleted")
};

export const EpicDeleteCourseClass: Epic<any, any> = EpicUtils.Create(request);

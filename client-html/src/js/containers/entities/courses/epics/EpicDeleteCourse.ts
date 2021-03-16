/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import CourseService from "../services/CourseService";
import { DELETE_COURSE } from "../actions";
import { ENTITY_NAME as CoursesEntity } from "../Courses";

const request: EpicUtils.Request = {
  type: DELETE_COURSE,
  getData: id => CourseService.remove(id),
  processData: () => {
    return [
      {
        type: FETCH_SUCCESS,
        payload: { message: `${CoursesEntity} deleted` }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: CoursesEntity, listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => FetchErrorHandler(response, `${CoursesEntity} was not deleted`)
};

export const EpicDeleteCourse: Epic<any, any> = EpicUtils.Create(request);

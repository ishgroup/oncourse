/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { DUPLICATE_COURSE } from "../actions";
import { ENTITY_NAME as CoursesEntity } from "../Courses";
import CourseService from "../services/CourseService";

const request: EpicUtils.Request = {
  type: DUPLICATE_COURSE,
  getData: ids => CourseService.duplicate(ids),
  processData: () => {
    return [
      {
        type: FETCH_SUCCESS,
        payload: { message: `${CoursesEntity} duplicated successfully` }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: CoursesEntity }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => FetchErrorHandler(response, `${CoursesEntity} was not duplicated`)
};

export const EpicDuplicateCourse: Epic<any, any> = EpicUtils.Create(request);

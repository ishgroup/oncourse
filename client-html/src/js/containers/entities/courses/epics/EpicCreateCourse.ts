/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { Course } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { processCustomFields } from "../../customFieldTypes/utils";
import CourseService from "../services/CourseService";
import { CREATE_COURSE } from "../actions";
import { ENTITY_NAME as CoursesEntity } from "../Courses";

const request: EpicUtils.Request<any, { course: Course }> = {
  type: CREATE_COURSE,
  getData: ({ course }) => {
    processCustomFields(course);
    return CourseService.create(course);
  },
  processData: () => [
      {
        type: FETCH_SUCCESS,
        payload: { message: `New ${CoursesEntity} created` }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: CoursesEntity }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ],
  processError: (response, { course }) => [
      ...FetchErrorHandler(response, `${CoursesEntity} was not created`),
      initialize(LIST_EDIT_VIEW_FORM_NAME, course)
    ]
};

export const EpicCreateCourse: Epic<any, any> = EpicUtils.Create(request);

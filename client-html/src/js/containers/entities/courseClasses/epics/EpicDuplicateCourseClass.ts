/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize, reset } from "redux-form";
import { CourseClassDuplicate } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import CourseClassService from "../services/CourseClassService";
import {
  DUPLICATE_COURSE_CLASS,
  DUPLICATE_COURSE_CLASS_FULFILLED,
  setDuplicateCourseClassesSessions
} from "../actions";
import { DUPLICATE_COURSE_CLASS_FORM } from "../components/duplicate-courseClass/DuplicateCourseClassModal";

let prevSessions;
let prevEarliestSessionStart;
let prevZeroWages;

const request: EpicUtils.Request<any, { values: CourseClassDuplicate; onComplete?: any }> = {
  type: DUPLICATE_COURSE_CLASS,
  getData: (payload, s) => {
    prevSessions = s.courseClass.timetable.sessions;
    prevEarliestSessionStart = s.courseClass.timetable.earliest;
    prevZeroWages = s.courseClass.timetable.hasZeroWages;

    return CourseClassService.duplicate(payload.values);
  },
  processData: (v, s, { onComplete }) => {
    if (typeof onComplete === "function") {
      onComplete(v);
    }

    return [
      {
        type: DUPLICATE_COURSE_CLASS_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Course class duplicated successfully" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "CourseClass" }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => [
    {
      type: DUPLICATE_COURSE_CLASS_FULFILLED
    },
    reset(DUPLICATE_COURSE_CLASS_FORM),
    setDuplicateCourseClassesSessions([...prevSessions], new Date(prevEarliestSessionStart), prevZeroWages),
    ...FetchErrorHandler(response, "Failed to duplicate Course class")
  ]
};

export const EpicDuplicateCourseClass: Epic<any, any> = EpicUtils.Create(request);

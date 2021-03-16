/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../../../common/components/list-view/constants";
import {
  StudentAttendanceExtended,
  TrainingPlanExtended,
  TutorAttendanceExtended
} from "../../../../../../model/entities/CourseClass";
import { GET_COURSE_CLASS_ATTENDANCE } from "../actions";
import CourseClassAttendanceService from "../services/CourseClassAttendanceService";

const request: EpicUtils.Request<any, number> = {
  type: GET_COURSE_CLASS_ATTENDANCE,
  hideLoadIndicator: true,
  getData: id => CourseClassAttendanceService.getStudentAttendance(id).then((studentAttendance: StudentAttendanceExtended[]) => CourseClassAttendanceService.getTutorAttendance(id).then((tutorAttendance: TutorAttendanceExtended[]) => CourseClassAttendanceService.getTrainingPlans(id).then((trainingPlan: TrainingPlanExtended[]) => {
          studentAttendance.forEach((s, index) => {
            s.index = index;
          });

          tutorAttendance.forEach((t, index) => {
            t.index = index;
          });

          trainingPlan.forEach((t, index) => {
            t.index = index;
          });

          return {
            studentAttendance,
            tutorAttendance,
            trainingPlan
          };
        }))),
  processData: (attendance, s, id) => {
    if (!s.form[LIST_EDIT_VIEW_FORM_NAME] || !s.form[LIST_EDIT_VIEW_FORM_NAME].initial || s.form[LIST_EDIT_VIEW_FORM_NAME].initial.id !== Number(id)) {
      return [];
    }

    return [initialize(LIST_EDIT_VIEW_FORM_NAME, { ...s.form[LIST_EDIT_VIEW_FORM_NAME].values, ...attendance })];
  },
  processError: response => FetchErrorHandler(response, "Failed to get course class attendance")
};

export const EpicGetCourseClassAttendance: Epic<any, any> = EpicUtils.Create(request);

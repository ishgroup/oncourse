/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import * as EpicUtils from "../../../../../../common/epics/EpicUtils";
import { State } from "../../../../../../reducers/state";
import FetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { Epic } from "redux-observable";
import { UPDATE_COURSE_CLASS_TRAINING_PLANS } from "../actions";
import CourseClassAttendanceService from "../services/CourseClassAttendanceService";
import { TrainingPlan } from "@api/model";

const request: EpicUtils.Request<any, State, { id: number; trainingPlans: TrainingPlan[] }> = {
  type: UPDATE_COURSE_CLASS_TRAINING_PLANS,
  getData: ({ id, trainingPlans }) => CourseClassAttendanceService.updateTrainingPlans(id, trainingPlans),
  processData: () => [],
  processError: response => FetchErrorHandler(response, "Failed to update training plans")
};

export const EpicUpdateCourseClassTrainingPlans: Epic<any, any> = EpicUtils.Create(request);

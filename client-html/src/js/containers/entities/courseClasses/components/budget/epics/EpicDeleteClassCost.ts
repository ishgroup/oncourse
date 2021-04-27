/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { DELETE_COURSE_CLASS_COST } from "../actions";
import CourseClassCostService from "../services/ClassCostService";

const request: EpicUtils.Request<any, number> = {
  type: DELETE_COURSE_CLASS_COST,
  getData: id => CourseClassCostService.deleteCourseClassCost(id),
  processData: () => [],
  processError: response => FetchErrorHandler(response, "Failed to delte class cost")
};

export const EpicDeleteClassCost: Epic<any, any> = EpicUtils.Create(request);

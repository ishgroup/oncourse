/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { ClassCost } from "@api/model";
import * as EpicUtils from "../../../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { POST_COURSE_CLASS_COST } from "../actions";
import CourseClassCostService from "../services/ClassCostService";

const request: EpicUtils.Request<any, { cost: ClassCost }> = {
  type: POST_COURSE_CLASS_COST,
  getData: ({ cost }) => CourseClassCostService.postCourseClassCost(cost),
  processData: () => [],
  processError: response => FetchErrorHandler(response, "Failed to create class cost")
};

export const EpicCreateClassCost: Epic<any, any> = EpicUtils.Create(request);

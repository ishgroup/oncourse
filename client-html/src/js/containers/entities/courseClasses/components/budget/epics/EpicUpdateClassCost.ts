/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { ClassCost } from "@api/model";
import * as EpicUtils from "../../../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { PUT_COURSE_CLASS_COST } from "../actions";
import CourseClassCostService from "../services/ClassCostService";

const request: EpicUtils.Request<any, { cost: ClassCost }> = {
  type: PUT_COURSE_CLASS_COST,
  getData: ({ cost }) => CourseClassCostService.putCourseClassCost(cost),
  processData: () => [],
  processError: response => FetchErrorHandler(response, "Failed to update class cost")
};

export const EpicUpdateClassCost: Epic<any, any> = EpicUtils.Create(request);

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ClassCost } from "@api/model";
import { Epic } from "redux-observable";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_DUPLICATE_COURSE_CLASSES_BUDGET, SET_DUPLICATE_COURSE_CLASSES_BUDGET } from "../actions";
import CourseClassCostService from "../components/budget/services/ClassCostService";

const request: EpicUtils.Request<ClassCost[], number> = {
  type: GET_DUPLICATE_COURSE_CLASSES_BUDGET,
  hideLoadIndicator: true,
  getData: id => CourseClassCostService.getCourseClassCosts(id),
  processData: budget => {
    const studentFee = budget.find(b => b.invoiceToStudent);

    if (studentFee && studentFee.paymentPlan.length) {
      studentFee.paymentPlan.sort((a, b) => (a.dayOffset > b.dayOffset ? 1 : -1));
    }

    return [{
      type: SET_DUPLICATE_COURSE_CLASSES_BUDGET,
      payload: budget
    }];
  },
  processError: response => FetchErrorHandler(response, "Failed to get course class budget")
};

export const EpicGetDuplicateTraineeshipBudget: Epic<any, any> = EpicUtils.Create(request);

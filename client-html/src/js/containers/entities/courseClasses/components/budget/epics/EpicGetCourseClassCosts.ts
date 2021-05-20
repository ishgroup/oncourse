/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ClassCost } from "@api/model";
import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../../../common/epics/EpicUtils";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../../../common/components/list-view/constants";
import FetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import CourseClassCostService from "../services/ClassCostService";
import { GET_COURSE_CLASS_COSTS } from "../actions";

const request: EpicUtils.Request<ClassCost[], number> = {
  type: GET_COURSE_CLASS_COSTS,
  hideLoadIndicator: true,
  getData: id => CourseClassCostService.getCourseClassCosts(id),
  processData: (budget, s, id) => {
    const studentFee = budget.find(b => b.invoiceToStudent);

    if (studentFee && studentFee.paymentPlan.length) {
      studentFee.paymentPlan.sort((a, b) => (a.dayOffset > b.dayOffset ? 1 : -1));
    }

    if (!s.form[LIST_EDIT_VIEW_FORM_NAME] || !s.form[LIST_EDIT_VIEW_FORM_NAME].initial || s.form[LIST_EDIT_VIEW_FORM_NAME].initial.id !== Number(id)) {
      return [];
    }
    return [initialize(LIST_EDIT_VIEW_FORM_NAME, { ...s.form[LIST_EDIT_VIEW_FORM_NAME].values, budget })];
  },
  processError: response => FetchErrorHandler(response, "Failed to get course class budget")
};

export const EpicGetCourseClassCosts: Epic<any, any> = EpicUtils.Create(request);

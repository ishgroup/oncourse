/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { DataResponse } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_COURSE_CLASS_SALES, GET_COURSE_CLASS_SALES_FULFILLED } from "../actions";

const request: EpicUtils.Request<any, string> = {
  type: GET_COURSE_CLASS_SALES,
  hideLoadIndicator: true,
  getData: payload => EntityService.getPlainRecords(
      "CourseClass",
      "course.name,uniqueCode",
      `${
        payload ? `(${payload}) AND ` : ""
      }(isDistantLearningCourse is true OR endDateTime > now) AND isCancelled is false`
    ),

  processData: (response: DataResponse) => {
    const courseClassItems = response.rows.map(({ id, values }) => ({
      id: Number(id),
      name: values[0],
      code: values[1],
      type: "Class",
      active: true
    }));
    return [
      {
        type: GET_COURSE_CLASS_SALES_FULFILLED,
        payload: { courseClassItems }
      }
    ];
  }
};

export const EpicGetCourseClassSales: Epic<any, any> = EpicUtils.Create(request);

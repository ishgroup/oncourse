/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { CHECKOUT_GET_COURSE_CLASS_LIST, CHECKOUT_GET_COURSE_CLASS_LIST_FULFILLED } from "../../actions/chekoutItem";
import { CHECKOUT_COURSE_CLASS_COLUMNS } from "../../constants";
import { checkoutCourseClassMap } from "../../utils";

const request: EpicUtils.Request<any, any, any> = {
  type: CHECKOUT_GET_COURSE_CLASS_LIST,
  getData: ({
    search
  }) => EntityService.getPlainRecords(
    "CourseClass",
    CHECKOUT_COURSE_CLASS_COLUMNS,
    search,
    null,
    0,
    "startDateTime",
    true
  ),
  processData: ({ rows }) => {
    const courseClasses = rows.map(checkoutCourseClassMap);
    return [
      {
        type: CHECKOUT_GET_COURSE_CLASS_LIST_FULFILLED,
        payload: { courseClasses }
      }
    ];
  }
};

export const EpicGetCourseClassList: Epic<any, any> = EpicUtils.Create(request);

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { CourseClass } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_PLAIN_COURSE_CLASSES, GET_PLAIN_COURSE_CLASSES_FULFILLED } from "../actions";
import EntityService from "../../../../common/services/EntityService";
import { getCustomColumnsMap } from "../../../../common/utils/common";

const defaultCourseMap = ({ id, values }) => ({
  id: Number(id),
  courseName: values[0],
  code: values[1],
  price: values[3]
});

const request: EpicUtils.Request<any, any, any> = {
  type: GET_PLAIN_COURSE_CLASSES,
  hideLoadIndicator: true,
  getData: ({ offset, columns, ascending }, { courseClasses: { search } }) => EntityService.getPlainRecords(
      "CourseClass",
      columns || "course.name,course.code,code,feeIncGst",
      search,
      100,
      offset,
      "",
      ascending
    ),
  processData: ({ rows, offset, pageSize }, s, { columns }) => {
    const items: CourseClass[] = rows.map(columns ? getCustomColumnsMap(columns) : defaultCourseMap);

    return [
      {
        type: GET_PLAIN_COURSE_CLASSES_FULFILLED,
        payload: { items, offset, pageSize }
      }
    ];
  }
};

export const EpicGetPlainCourseClasses: Epic<any, any> = EpicUtils.Create(request);

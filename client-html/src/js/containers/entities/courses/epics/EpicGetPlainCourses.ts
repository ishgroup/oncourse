/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { Course } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import EntityService from "../../../../common/services/EntityService";
import { GET_PLAIN_COURSES, setPlainCourses } from "../actions";
import { getCustomColumnsMap } from "../../../../common/utils/common";

const defaultCourseMap = ({ id, values }) => ({
  id: Number(id),
  code: values[0],
  name: values[1],
  active: values[2] === "true" || values[3] === "true",
  type: "Course"
});

const request: EpicUtils.Request<any, any, {offset?: number, columns?: string, ascending?: boolean, pageSize?: number}> = {
  type: GET_PLAIN_COURSES,
  hideLoadIndicator: true,
  getData: ({
 offset, columns, ascending, pageSize
}, { courses: { search } }) => EntityService.getPlainRecords(
      "Course",
      columns || "code,name,currentlyOffered,isShownOnWeb",
      search,
      pageSize || 100,
      offset,
      "",
      ascending
    ),
  processData: ({ rows, offset, pageSize }, s, { columns }) => {
    const items: Course[] = rows.map(columns ? getCustomColumnsMap(columns) : defaultCourseMap);

    return [setPlainCourses(items, offset, pageSize, false)];
  }
};

export const EpicGetPlainCourses: Epic<any, any> = EpicUtils.Create(request);

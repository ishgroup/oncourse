/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { CourseClass } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_PLAIN_COURSE_CLASSES_BY_ID, GET_PLAIN_COURSE_CLASSES_BY_ID_FULFILLED } from "../actions";
import EntityService from "../../../../common/services/EntityService";
import { getCustomColumnsMap } from "../../../../common/utils/common";

const defaultCourseMap = ({ id, values }) => ({
  id: Number(id),
  name: `${values[1]}-${values[2]} ${values[0]}`,
  courseName: values[0],
  code: values[1],
  classCode: values[2],
  price: values[3],
  startDate: values[4],
  start: values[4],
  endDate: values[5],
  end: values[5],
  placesLeft: values[6],
  room: values[7],
  site: values[8],
  siteTimezone: values[9],
  tutors: [],
  classId: null
});

const request: EpicUtils.Request<any, any, any> = {
  type: GET_PLAIN_COURSE_CLASSES_BY_ID,
  hideLoadIndicator: true,
  getData: ({ offset, columns, sortings, ascending }, { courseClasses: { search } }) => {
    return EntityService.getPlainRecords(
      "CourseClass",
      columns || "course.name,course.code,code,feeIncGst,startDateTime,endDateTime,placesLeft,room.name,room.site.name,room.site.localTimezone",
      search ? search : null,
      100,
      offset,
      sortings || "",
      ascending
    );
  },
  processData: ({ rows, offset, pageSize }, s, { columns }) => {
    const items: CourseClass[] = rows.map(columns ? getCustomColumnsMap(columns) : defaultCourseMap);

    return [
      {
        type: GET_PLAIN_COURSE_CLASSES_BY_ID_FULFILLED,
        payload: { items, offset, pageSize }
      }
    ];
  }
};

export const EpicGetPlainCourseClassesById: Epic<any, any> = EpicUtils.Create(request);

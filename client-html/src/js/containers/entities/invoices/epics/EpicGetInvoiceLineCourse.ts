/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_INVOICE_LINE_COURSE, setInvoiceLineCourse } from "../actions";
import { Course, CourseClass, DataResponse } from "@api/model";
import CourseService from "../../courses/services/CourseService";
import EntityService from "../../../../common/services/EntityService";

const request: EpicUtils.Request<
  { selectedLineCourseClasses: CourseClass[]; selectedLineCourse: Course },
  number
> = {
  type: GET_INVOICE_LINE_COURSE,
  hideLoadIndicator: true,
  getData: id =>
    CourseService.getCourse(id).then(selectedLineCourse =>
      EntityService.getPlainRecords("CourseClass", "code", `course.id is ${id}`).then((data: DataResponse) => {
        const selectedLineCourseClasses = data.rows.map(r => ({
          id: Number(r.id),
          code: r.values[0]
        }));

        return { selectedLineCourseClasses, selectedLineCourse };
      })
    ),
  processData: ({ selectedLineCourseClasses, selectedLineCourse }) => {
    selectedLineCourseClasses.sort((a, b) => {
      const aN = Number(a.code);
      const bN = Number(b.code);

      if (!isNaN(aN) && !isNaN(bN)) {
        return aN > bN ? 1 : -1;
      }

      return a.code[0].toLowerCase() > b.code[0].toLowerCase() ? 1 : -1;
    });

    return [setInvoiceLineCourse(selectedLineCourse, selectedLineCourseClasses)];
  }
};

export const EpicGetInvoiceLineCourse: Epic<any, any> = EpicUtils.Create(request);

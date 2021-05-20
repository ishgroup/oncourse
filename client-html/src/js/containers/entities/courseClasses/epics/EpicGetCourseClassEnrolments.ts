/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { DataResponse, Enrolment } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_COURSE_CLASS_ENROLMENTS, setCourseClassEnrolments } from "../actions";
import EntityService from "../../../../common/services/EntityService";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request<any, number> = {
  type: GET_COURSE_CLASS_ENROLMENTS,
  hideLoadIndicator: true,
  getData: id => EntityService.getPlainRecords(
      "Enrolment",
      "createdOn,status,student.contact.fullName,student.contact.id",
      `courseClass.id is ${id} and ( status is IN_TRANSACTION or status is QUEUED or status is SUCCESS ) `
    ),
  processData: (response: DataResponse) => {
    const enrolments = response.rows.map(({ id, values }) => ({
      id,
      createdOn: values[0],
      status: values[1] as Enrolment["status"],
      student: values[2],
      contactId: values[3],
    }));

    enrolments.sort((a, b) => (a.createdOn > b.createdOn ? 1 : -1));

    return [setCourseClassEnrolments(enrolments)];
  },
  processError: response => FetchErrorHandler(response, "Failed to get course class enrolments")
};

export const EpicGetCourseClassEnrolments: Epic<any, any> = EpicUtils.Create(request);

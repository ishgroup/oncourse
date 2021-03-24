/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import { Diff } from "@api/model";
import { Epic } from "redux-observable";
import ContactsService from "../../../../containers/entities/contacts/services/ContactsService";
import CourseClassService from "../../../../containers/entities/courseClasses/services/CourseClassService";
import CourseService from "../../../../containers/entities/courses/services/CourseService";
import EnrolmentService from "../../../../containers/entities/enrolments/services/EnrolmentService";
import OutcomeService from "../../../../containers/entities/outcomes/services/OutcomeService";
import { FETCH_SUCCESS } from "../../../actions";
import FetchErrorHandler from "../../../api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../epics/EpicUtils";
import { BULK_CHANGE_RECORDS, GET_RECORDS_REQUEST } from "../actions";
import AssessmentSubmissionService
  from "../../../../containers/entities/assessmentSubmissions/service/AssessmentSubmissionService";
import { EntityName } from "../../../../model/entities/common";

const getBulkRequest = (entity: EntityName, diff: Diff): Promise<any> => {
  switch (entity) {
    case "Outcome": {
      return OutcomeService.bulkChange(diff);
    }
    case "CourseClass": {
      return CourseClassService.bulkChange(diff);
    }
    case "Course": {
      return CourseService.bulkEdit(diff);
    }
    case "Enrolment": {
      return EnrolmentService.bulkChange(diff);
    }
    case "Contact": {
      return ContactsService.bulkChange(diff);
    }
    case "AssessmentSubmission": {
      return AssessmentSubmissionService.bulkChange(diff);
    }
    default: {
      // eslint-disable-next-line prefer-promise-reject-errors
      return Promise.reject(`No bulk edit endpoint was found for ${entity}`);
    }
  }
};

const request: EpicUtils.Request<any, { entity: EntityName, diff: Diff }> = {
  type: BULK_CHANGE_RECORDS,
  getData: ({ entity, diff }) => getBulkRequest(entity, diff),
  processData: (v, s, { entity }) => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "Bulk change completed" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity, listUpdate: true }
      }
    ],
  processError: response => {
    const isClientReject = typeof response === "string";

    // @ts-ignore
    return FetchErrorHandler(...isClientReject ? [null, response] : [response]);
  }
};

export const EpicBulkChangeRecords: Epic<any, any> = EpicUtils.Create(request);

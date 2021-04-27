/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import * as EpicUtils from "../../../../../../common/epics/EpicUtils";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../../../common/components/list-view/constants";
import FetchErrorHandler from "../../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_COURSE_CLASS_TUTORS, setCourseClassTutorNamesWarnings } from "../actions";
import CourseClassTutorService from "../services/CourseClassTutorService";
import EntityService from "../../../../../../common/services/EntityService";
import { getCustomColumnsMap } from "../../../../../../common/utils/common";
import { getTutorNameWarning } from "../utils";

const columns = "firstName,lastName,email,birthDate,tutor.dateFinished,tutor.wwChildrenStatus";

const request: EpicUtils.Request<any, number> = {
  type: GET_COURSE_CLASS_TUTORS,
  hideLoadIndicator: true,
  getData: (id, state) => CourseClassTutorService.getCourseClassTutors(id).then(tutors => (tutors.length
        ? EntityService.getPlainRecords("Contact", columns, `id in (${tutors.map(t => t.contactId).join(",")})`).then(
            response => {
              const warnings = { ...state.courseClass.tutorNamesWarnings };

              const maped = response.rows.map(getCustomColumnsMap(columns));
              const { latestSession } = state.courseClass;

              maped.forEach(t => {
                const warning = getTutorNameWarning(t, latestSession);

                if (warning) {
                  warnings[t.id] = warning;
                }
              });

              return { tutors, warnings };
            }
          )
        : { tutors, warnings: {} })),
  processData: ({ tutors, warnings }, s, id) => {
    if (!s.form[LIST_EDIT_VIEW_FORM_NAME] || !s.form[LIST_EDIT_VIEW_FORM_NAME].initial || s.form[LIST_EDIT_VIEW_FORM_NAME].initial.id !== Number(id)) {
      return [];
    }
    return [
      setCourseClassTutorNamesWarnings(warnings),
      initialize(LIST_EDIT_VIEW_FORM_NAME, { ...s.form[LIST_EDIT_VIEW_FORM_NAME].values, tutors })
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to get course class tutors")
};

export const EpicGetCourseClassTutors: Epic<any, any> = EpicUtils.Create(request);

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import FetchErrorHandler from '../../../../../../common/api/fetch-errors-handlers/FetchErrorHandler';
import * as EpicUtils from '../../../../../../common/epics/EpicUtils';
import EntityService from '../../../../../../common/services/EntityService';
import { getCustomColumnsMap } from '../../../../../../common/utils/common';
import { GET_COURSE_CLASS_TUTORS_WARNINGS, setCourseClassTutorNamesWarnings } from '../actions';
import { getTutorNameWarning } from '../utils';

const columns = "firstName,lastName,email,birthDate,tutor.dateFinished,tutor.wwChildrenStatus";

const request: EpicUtils.Request<any, string> = {
  type: GET_COURSE_CLASS_TUTORS_WARNINGS,
  hideLoadIndicator: true,
  getData: (ids, state) => EntityService
    .getPlainRecords("Contact", columns, `id in (${ids})`)
    .then(
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

      return { warnings };
    }
  ),
  processData: ({ warnings }) => {
    return [
      setCourseClassTutorNamesWarnings(warnings),
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to get course class tutors warnings")
};

export const EpicGetCourseClassTutorsWarnings: Epic<any, any> = EpicUtils.Create(request);

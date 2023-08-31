/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DefinedTutorRole } from "@api/model";
import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_TUTOR_ROLE_FULFILLED, GET_TUTOR_ROLE_REQUEST } from "../../../actions";
import PreferencesService from "../../../services/PreferencesService";
import { TUTOR_ROLES_FORM_NAME } from "../TutorRoleFormContainer";

const request: EpicUtils.Request<any, any> = {
  type: GET_TUTOR_ROLE_REQUEST,
  getData: payload => PreferencesService.getTutorRole(payload.id),
  processData: (response: DefinedTutorRole) => {
    const payRates = (response.payRates && response.payRates.length > 0
        && response.payRates.sort((a, b) => (b.validFrom > a.validFrom ? 1 : -1)))
      || [];

    return [
      {
        type: GET_TUTOR_ROLE_FULFILLED
      },
      initialize(TUTOR_ROLES_FORM_NAME, { ...response, payRates })
    ];
  },
  processError: response => [...FetchErrorHandler(response, "Failed to get tutor role"), initialize(TUTOR_ROLES_FORM_NAME, null)]
};

export const EpicGetTutorRole: Epic<any, any> = EpicUtils.Create(request);

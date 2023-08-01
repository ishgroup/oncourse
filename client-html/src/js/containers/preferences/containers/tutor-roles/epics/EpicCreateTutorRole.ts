/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DefinedTutorRole } from "@api/model";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { CREATE_TUTOR_ROLE_FULFILLED, CREATE_TUTOR_ROLE_REQUEST, GET_TUTOR_ROLES_REQUEST } from "../../../actions";
import PreferencesService from "../../../services/PreferencesService";

const request: EpicUtils.Request<any,  { tutorRole: DefinedTutorRole }> = {
  type: CREATE_TUTOR_ROLE_REQUEST,
  getData: ({ tutorRole }) => PreferencesService.createTutorRole(tutorRole),
  processData: (v, s, { tutorRole }) => {
    return [
      {
        type: CREATE_TUTOR_ROLE_FULFILLED
      },
      {
        type: GET_TUTOR_ROLES_REQUEST,
        payload: { keyCodeToSelect: tutorRole.name }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Tutor role created" }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to create tutor role")
};

export const EpicCreateTutorRole: Epic<any, any> = EpicUtils.Create(request);

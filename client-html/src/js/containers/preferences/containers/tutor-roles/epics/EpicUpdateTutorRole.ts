/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import {
  GET_TUTOR_ROLE_REQUEST,
  GET_TUTOR_ROLES_REQUEST,
  UPDATE_TUTOR_ROLE_FULFILLED,
  UPDATE_TUTOR_ROLE_REQUEST
} from "../../../actions";
import PreferencesService from "../../../services/PreferencesService";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import { DefinedTutorRole } from "@api/model";

const request: EpicUtils.Request<any,  { tutorRole: DefinedTutorRole }> = {
  type: UPDATE_TUTOR_ROLE_REQUEST,
  getData: ({ tutorRole }) => PreferencesService.updateTutorRole(tutorRole.id, tutorRole),
  processData: (v, s, { tutorRole: { id } }) => {
    return [
      {
        type: UPDATE_TUTOR_ROLE_FULFILLED
      },
      {
        type: GET_TUTOR_ROLE_REQUEST,
        payload: { id }
      },
      {
        type: GET_TUTOR_ROLES_REQUEST
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Tutor role updated" }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to update tutor role")
};

export const EpicUpdateTutorRole: Epic<any, any> = EpicUtils.Create(request);

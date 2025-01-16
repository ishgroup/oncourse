/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { DefinedTutorRole } from "@api/model";

import * as _ from "lodash";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import history from "../../../../../constants/History";
import { DELETE_TUTOR_ROLE_FULFILLED, DELETE_TUTOR_ROLE_REQUEST, GET_TUTOR_ROLES_REQUEST } from "../../../actions";
import PreferencesService from "../../../services/PreferencesService";

const request: EpicUtils.Request<any,  { id: number; tutorRoles: DefinedTutorRole[] }> = {
  type: DELETE_TUTOR_ROLE_REQUEST,
  getData: ({ id }) => PreferencesService.removeTutorRole(id),
  processData: (v, s, p) => {
    const tutorRoles = p.tutorRoles;
    _.remove(tutorRoles, tr => Number(tr.id) === p.id);

    if (tutorRoles.length === 1) {
      history.push("/preferences");
    } else {
      history.push(`/preferences/tutorRoles/${tutorRoles[0].id}`);
    }

    return [
      {
        type: DELETE_TUTOR_ROLE_FULFILLED
      },
      {
        type: GET_TUTOR_ROLES_REQUEST
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Tutor role deleted" }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to delete tutor role")
};

export const EpicDeleteTutorRole: Epic<any, any> = EpicUtils.Create(request);

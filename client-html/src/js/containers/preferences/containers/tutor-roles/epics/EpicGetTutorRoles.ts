/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import EntityService from "../../../../../common/services/EntityService";
import { GET_TUTOR_ROLES_FULFILLED, GET_TUTOR_ROLES_REQUEST } from "../../../actions";
import history from "../../../../../constants/History";
import { getCustomColumnsMap } from "../../../../../common/utils/common";

const request: EpicUtils.Request<any,  { selectFirst: boolean; keyCodeToSelect: string; columns: string }> = {
  type: GET_TUTOR_ROLES_REQUEST,
  getData: request =>
    EntityService.getPlainRecords(
      "DefinedTutorRole",
      (request && request.columns) || "name,description,active",
      null,
      null,
      null,
      "name",
      true
    ),
  processData: (response, s, p) => {
    const tutorRoles = response.rows.map(
      p && p.columns
        ? getCustomColumnsMap(p.columns)
        : r => ({
            id: r.id,
            name: r.values[0],
            description: r.values[1],
            active: r.values[2],
            grayOut: r.values[2] === "false"
          })
    );

    if (p) {
      if (p.selectFirst) {
        history.push(`/preferences/tutorRoles`);
      }
      if (p.keyCodeToSelect) {
        history.push(`/preferences/tutorRoles/${tutorRoles.find(t => t.name === p.keyCodeToSelect).id}`);
      }
    }

    return [
      {
        type: GET_TUTOR_ROLES_FULFILLED,
        payload: { tutorRoles }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to get tutor role list")
};

export const EpicGetTutorRoles: Epic<any, any> = EpicUtils.Create(request);

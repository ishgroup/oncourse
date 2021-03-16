/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import PreferencesService from "../../../services/PreferencesService";
import { FETCH_FAIL, FETCH_SUCCESS, GET_LDAP_CONNECTION_REQUEST } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request = {
  type: GET_LDAP_CONNECTION_REQUEST,
  getData: payload =>
    PreferencesService.checkConnection(payload.host, payload.port, payload.isSsl, payload.baseDn, payload.user),
  processData: (result: boolean) => {
    if (result) {
      return [
        {
          type: FETCH_SUCCESS,
          payload: { message: "LDAP server contacted successfully" }
        }
      ];
    }
    return [
      {
        type: FETCH_FAIL,
        payload: { message: "LDAP server connection test failed, please check your settings and try again." }
      }
    ];
  },
  processError: response => {
    return FetchErrorHandler(response);
  }
};

export const EpicGetLdapConnection: Epic<any, any> = EpicUtils.Create(request);

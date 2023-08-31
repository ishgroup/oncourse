/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";

import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import UserPreferenceService from "../../../../../common/services/UserPreferenceService";
import { SERVER_TIMEZONE } from "../../../../../constants/Config";
import { GET_TIMEZONE, GET_TIMEZONE_FULFILLED } from "../actions";

const request: EpicUtils.Request = {
  type: GET_TIMEZONE,
  getData: () => UserPreferenceService.getUserPreferencesByKeys([SERVER_TIMEZONE]),
  processData: (response: { [key: string]: string }) => [
      {
        type: GET_TIMEZONE_FULFILLED,
        payload: { timeZone: response[SERVER_TIMEZONE] }
      }
    ],
  processError: response => FetchErrorHandler(response, "Failed to get default invoice terms")
};

export const EpicGetTimeZone: Epic<any, any> = EpicUtils.Create(request);

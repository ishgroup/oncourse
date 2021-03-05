/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { DELETE_API_TOKEN_REQUEST, getApiTokens } from "../../../actions";
import ApiTokensService from "../services/ApiTokensService";
import { FETCH_SUCCESS } from "../../../../../common/actions";

const request: EpicUtils.Request<any, any> = {
  type: DELETE_API_TOKEN_REQUEST,
  getData: id => ApiTokensService.deleteToken(id),
  processData: () => [
    {
      type: FETCH_SUCCESS,
      payload: { message: "API token deleted" }
    },
    getApiTokens()
  ]
};

export const EpicDeleteToken: Epic<any, any> = EpicUtils.Create(request);

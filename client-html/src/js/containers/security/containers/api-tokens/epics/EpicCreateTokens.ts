/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { ApiToken } from "@api/model";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { getApiTokens, UPDATE_API_TOKENS_REQUEST } from "../../../actions";
import ApiTokensService from "../services/ApiTokensService";
import { FETCH_SUCCESS } from "../../../../../common/actions";

const request: EpicUtils.Request<any, ApiToken[]> = {
  type: UPDATE_API_TOKENS_REQUEST,
  getData: tokens => ApiTokensService.createTokens(tokens),
  processData: (v, s, tokens) => [
    {
      type: FETCH_SUCCESS,
      payload: { message: `New API token${tokens.length > 1 ? "s" : ""} created` }
    },
    getApiTokens()
  ]
};

export const EpicCreateTokens: Epic<any, any> = EpicUtils.Create(request);

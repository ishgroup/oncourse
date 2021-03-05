/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { ApiToken } from "@api/model";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_API_TOKENS_REQUEST } from "../../../actions";
import ApiTokensService from "../services/ApiTokensService";

const request: EpicUtils.Request<any, any, any> = {
  type: GET_API_TOKENS_REQUEST,
  getData: () => ApiTokensService.getTokens(),
  processData: (tokens: ApiToken[]) => [initialize("ApiTokensForm", { tokens })]
};

export const EpicGetApiTokens: Epic<any, any> = EpicUtils.Create(request);

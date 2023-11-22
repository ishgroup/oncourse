/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { LoginResponse } from "@api/model";
import { Epic } from "redux-observable";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import { POST_SSO_AUTHENTICATION_REQUEST } from "../actions";
import LoginService from "../services/LoginService";
import LoginServiceErrorsHandler from "../services/LoginServiceErrorsHandler";
import { processLoginActions } from "./processLoginActions";

const request: EpicUtils.Request<LoginResponse, { ssoType: string, code: string }> = {
  type: POST_SSO_AUTHENTICATION_REQUEST,
  getData: ({ ssoType, code }) => LoginService.loginSso(ssoType, code),
  processData: processLoginActions,
  processError: response => LoginServiceErrorsHandler(response)
};

export const EpicPostSSOAuthentication: Epic<any, any> = EpicUtils.Create(request);
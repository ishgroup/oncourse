/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { LoginRequest, LoginResponse } from "@api/model";
import { Epic } from "redux-observable";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import {POST_AUTHENTICATION_REQUEST } from "../actions";
import LoginService from "../services/LoginService";
import LoginServiceErrorsHandler from "../services/LoginServiceErrorsHandler";
import { processLoginActions } from "./processLoginActions";

const request: EpicUtils.Request<LoginResponse, { body: LoginRequest, host, port }> = {
  type: POST_AUTHENTICATION_REQUEST,
  getData: payload => LoginService.postLoginRequest(payload.body, payload.host, payload.port),
  processData: processLoginActions,
  processError: response => LoginServiceErrorsHandler(response)
};

export const EpicPostAuthentication: Epic<any, any> = EpicUtils.Create(request);
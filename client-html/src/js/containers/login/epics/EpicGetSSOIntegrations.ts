/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { IntegrationType } from '@api/model';
import { Epic } from "redux-observable";
import * as EpicUtils from "../../../common/epics/EpicUtils";
import IntegrationService from "../../automation/containers/integrations/services";
import { GET_SSO_INTEGRATIONS, GET_SSO_INTEGRATIONS_FULFILLED } from "../actions";
import LoginServiceErrorsHandler from "../services/LoginServiceErrorsHandler";

const request: EpicUtils.Request<IntegrationType[]> = {
  type: GET_SSO_INTEGRATIONS,
  getData: () => IntegrationService.getSsoPluginTypes(),
  processData: ssoTypes => [{
    type: GET_SSO_INTEGRATIONS_FULFILLED,
    payload: { ssoTypes }
  }],
  processError: response => LoginServiceErrorsHandler(response, "Failed to get sso types")
};

export const EpicGetSSOIntegrations: Epic<any, any> = EpicUtils.Create(request);
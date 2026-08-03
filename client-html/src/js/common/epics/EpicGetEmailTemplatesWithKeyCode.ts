/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { EmailTemplate } from "@api/model";
import { Epic } from "redux-observable";
import EmailTemplateService from "../../containers/automation/containers/email-templates/services/EmailTemplateService";
import { GET_EMAIL_TEMPLATES_WITH_KEYCODE, GET_EMAIL_TEMPLATES_WITH_KEYCODE_FULFILLED } from "../actions";
import FetchErrorHandler from "../api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "./EpicUtils";

const request: EpicUtils.Request = {
  type: GET_EMAIL_TEMPLATES_WITH_KEYCODE,
  getData: ({entities}) => Promise.all(entities.map(entity => EmailTemplateService.getEmailTemplatesWithKeyCode(entity))),
  processData: (records: EmailTemplate[]) => {
    return [
      {
        type: GET_EMAIL_TEMPLATES_WITH_KEYCODE_FULFILLED,
        payload: records
      }
    ];
  },
  processError: response => {
    if (response && response.status === 403) {
      return [];
    }

    return FetchErrorHandler(response);
  }
};

export const EpicGetEmailTemplatesWithKeyCode: Epic<any, any> = EpicUtils.Create(request);

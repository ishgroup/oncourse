/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { EmailTemplate } from "@api/model";
import * as EpicUtils from "./EpicUtils";
import { GET_EMAIL_TEMPLATES_WITH_KEYCODE, GET_EMAIL_TEMPLATES_WITH_KEYCODE_FULFILLED } from "../actions";
import EmailTemplateService from "../services/EmailTemplateService";
import FetchErrorHandler from "../api/fetch-errors-handlers/FetchErrorHandler";

const sortEmailBodyDown = (a, b) => {
  const aName = a.name;
  const bName = b.name;

  if (typeof aName === "string" && aName.toLowerCase().includes("body")) {
    return 1;
  }

  if (typeof bName === "string" && bName.toLowerCase().includes("body")) {
    return -1;
  }

  return 0;
};

const request: EpicUtils.Request = {
  type: GET_EMAIL_TEMPLATES_WITH_KEYCODE,
  getData: ({ entities }) => Promise.all(entities.map(entity => EmailTemplateService.getEmailTemplatesWithKeyCode(entity))),
  processData: (records: EmailTemplate[]) => {
    const sortedRecords = records.flat().map(r => {
      if (Array.isArray(r.variables) && r.variables.length) {
        r.variables.sort(sortEmailBodyDown);
      }
      return r;
    });

    return [
      {
        type: GET_EMAIL_TEMPLATES_WITH_KEYCODE_FULFILLED,
        payload: sortedRecords
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

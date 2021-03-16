import { EmailTemplate } from "@api/model";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import {
  GET_EMAIL_TEMPLATE,
  GET_EMAIL_TEMPLATES_LIST,
  UPDATE_EMAIL_TEMPLATE,
  UPDATE_EMAIL_TEMPLATE_FULFILLED
} from "../actions";
import EmailTemplateService from "../services/EmailTemplateService";

const request: EpicUtils.Request<{ emailTemplate: EmailTemplate }, { emailTemplate: EmailTemplate }> = {
  type: UPDATE_EMAIL_TEMPLATE,
  getData: ({ emailTemplate }) => EmailTemplateService.update(emailTemplate.id, emailTemplate),
  processData: (v, s, { emailTemplate: { id } }) => [
      {
        type: UPDATE_EMAIL_TEMPLATE_FULFILLED
      },
      {
        type: GET_EMAIL_TEMPLATE,
        payload: id
      },
      {
        type: GET_EMAIL_TEMPLATES_LIST
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Message template updated" }
      }
    ],
  processError: response => FetchErrorHandler(response, "Failed to update  message template")
};

export const EpicUpdateEmailTemplate: Epic<any, any> = EpicUtils.Create(request);

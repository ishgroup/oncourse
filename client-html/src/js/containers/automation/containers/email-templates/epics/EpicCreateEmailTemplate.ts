import { EmailTemplate } from "@api/model";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { State } from "../../../../../reducers/state";
import { CREATE_EMAIL_TEMPLATE, CREATE_EMAIL_TEMPLATE_FULFILLED, GET_EMAIL_TEMPLATES_LIST } from "../actions";
import EmailTemplateService from "../services/EmailTemplateService";

const request: EpicUtils.Request<State, { emailTemplate: EmailTemplate }> = {
  type: CREATE_EMAIL_TEMPLATE,
  getData: ({ emailTemplate }) => EmailTemplateService.create(emailTemplate),
  processData: (v, s, { emailTemplate }) => [
      {
        type: CREATE_EMAIL_TEMPLATE_FULFILLED
      },
      {
        type: GET_EMAIL_TEMPLATES_LIST,
        payload: { keyCodeToSelect: emailTemplate.keyCode }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Message template created" }
      }
    ],
  processError: response => FetchErrorHandler(response, "Failed to create message template")
};

export const EpicCreateEmailTemplate: Epic<any, any> = EpicUtils.Create(request);

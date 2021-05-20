import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import { EmailTemplate } from "@api/model";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_EMAIL_TEMPLATE, GET_EMAIL_TEMPLATE_FULFILLED } from "../actions";
import { EMAIL_TEMPLATES_FORM_NAME } from "../EmailTemplates";
import EmailTemplateService from "../services/EmailTemplateService";

const request: EpicUtils.Request<EmailTemplate, number> = {
  type: GET_EMAIL_TEMPLATE,
  getData: id => EmailTemplateService.get(id),
  processData: editRecord => [
      {
        type: GET_EMAIL_TEMPLATE_FULFILLED
      },
      initialize(EMAIL_TEMPLATES_FORM_NAME, editRecord)
    ],
  processError: response => [
      ...FetchErrorHandler(response, "Failed to get email template"),
      initialize(EMAIL_TEMPLATES_FORM_NAME, null)
    ]
};

export const EpicGetEmailTemplate: Epic<any, any> = EpicUtils.Create(request);

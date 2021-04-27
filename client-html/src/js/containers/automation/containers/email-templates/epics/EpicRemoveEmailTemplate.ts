import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import history from "../../../../../constants/History";
import { GET_EMAIL_TEMPLATES_LIST, REMOVE_EMAIL_TEMPLATE, REMOVE_EMAIL_TEMPLATE_FULFILLED } from "../actions";
import EmailTemplateService from "../services/EmailTemplateService";

const request: EpicUtils.Request<any, number> = {
  type: REMOVE_EMAIL_TEMPLATE,
  getData: id => EmailTemplateService.remove(id),
  processData: () => {
    history.push("/automation/email-templates");

    return [
      {
        type: REMOVE_EMAIL_TEMPLATE_FULFILLED
      },
      {
        type: GET_EMAIL_TEMPLATES_LIST
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Message template deleted" }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to delete message template")
};

export const EpicRemoveEmailTemplate: Epic<any, any> = EpicUtils.Create(request);

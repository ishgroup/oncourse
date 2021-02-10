import { Epic } from "redux-observable";
import * as EpicUtils from "./EpicUtils";
import { CHECK_SITENAME, SET_SITENAME_VALID_VALUE } from "../actions/index";
import InstantFetchErrorHandler from "../../api/fetch-errors-handlers/InstantFetchErrorHandler";
import BillingService from "../../api/services/BillingApi";

const request: EpicUtils.Request<any, any, any> = {
  type: CHECK_SITENAME,
  getData: ({ name, token }) => BillingService.verifyCollegeName(name, token),
  processData: (response: boolean) => {
    return [
      { type: SET_SITENAME_VALID_VALUE, payload: response },
    ]},
  processError: response => {
    return [
      ...InstantFetchErrorHandler(response, "Failed to check name"),
      { type: SET_SITENAME_VALID_VALUE, payload: false }
    ]
  }
};

export const EpicCheckSiteName: Epic<any, any> = EpicUtils.Create(request);
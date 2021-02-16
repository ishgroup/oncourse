import { Epic } from "redux-observable";
import * as EpicUtils from "./EpicUtils";
import { COLLEGE_WAS_CREATED, CREATE_COLLEGE, SET_LOADING_VALUE } from "../actions/index";
import InstantFetchErrorHandler from "../../api/fetch-errors-handlers/InstantFetchErrorHandler";
import BillingService from "../../api/services/BillingApi";

const request: EpicUtils.Request<any, any, any> = {
  type: CREATE_COLLEGE,
  getData: (data) => BillingService.createCollege(data),
  processData: () => {
    return [
      { type: COLLEGE_WAS_CREATED, payload: true },
      { type: SET_LOADING_VALUE, payload: false }
    ]},
  processError: response => {
    return [...InstantFetchErrorHandler(response),
      { type: SET_LOADING_VALUE, payload: false }
    ]
  }
};

export const EpicCreateCollege: Epic<any, any> = EpicUtils.Create(request);
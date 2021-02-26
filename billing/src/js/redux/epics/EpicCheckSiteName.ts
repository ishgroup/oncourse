import { Epic } from "redux-observable";
import * as EpicUtils from "./EpicUtils";
import {
  CHECK_SITENAME,
  SET_SITENAME_VALID_VALUE,
  SET_SEND_TOKEN_AGAIN_VALUE,
  SET_LOADING_VALUE,
  SHOW_MESSAGE
} from "../actions/index";
import InstantFetchErrorHandler from "../../api/fetch-errors-handlers/InstantFetchErrorHandler";
import BillingService from "../../api/services/BillingApi";

const request: EpicUtils.Request<any, any, any> = {
  type: CHECK_SITENAME,
  getData: ({ name, token }) => BillingService.verifyCollegeName(name, token),
  processData: (response: boolean) => {
    const returnedValue:any[] = [
      { type: SET_SITENAME_VALID_VALUE, payload: response },
      { type: SET_SEND_TOKEN_AGAIN_VALUE, payload: false },
      { type: SET_LOADING_VALUE, payload: false },
    ];

    if (!response) {
      returnedValue.push({
        type: SHOW_MESSAGE,
        payload: {
          message: "Site name is already taken",
          error: true
        }
      })
    }
    return returnedValue;
  },
  processError: response => {
    return [
      ...InstantFetchErrorHandler(response),
      { type: SET_SITENAME_VALID_VALUE, payload: false },
      { type: SET_LOADING_VALUE, payload: false }
    ]
  }
};

export const EpicCheckSiteName: Epic<any, any> = EpicUtils.Create(request);
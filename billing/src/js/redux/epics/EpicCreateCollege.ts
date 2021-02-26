import { Epic } from "redux-observable";
import * as EpicUtils from "./EpicUtils";
import { COLLEGE_WAS_CREATED, CREATE_COLLEGE, SET_LOADING_VALUE, SET_SERVER_ERROR_VALUE } from "../actions/index";
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
    return [
      { type: SET_LOADING_VALUE, payload: false },
      { type: SET_SERVER_ERROR_VALUE, payload: true },
    ]
  }
};

export const EpicCreateCollege: Epic<any, any> = EpicUtils.Create(request);
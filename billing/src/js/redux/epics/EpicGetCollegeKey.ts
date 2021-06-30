import { Epic } from "redux-observable";
import * as EpicUtils from "./EpicUtils";
import { GET_COLLEGE_KEY, SET_COLLEGE_KEY } from "../actions";
import BillingService from "../../api/services/BillingApi";

const request: EpicUtils.Request = {
  type: GET_COLLEGE_KEY,
  getData: () => BillingService.getCollegeKey(),
  processData: payload => [{ type: SET_COLLEGE_KEY, payload }]
};

export const EpicGetCollegeKey: Epic<any, any> = EpicUtils.Create(request);

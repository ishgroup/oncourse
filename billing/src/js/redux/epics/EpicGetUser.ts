import { Epic } from "redux-observable";
import * as EpicUtils from "./EpicUtils";
import { GET_USER, GET_USER_FULFILLED } from "../actions";
import BillingService from "../../api/services/BillingApi";

const request: EpicUtils.Request = {
  type: GET_USER,
  getData: userId => BillingService.getUser(userId),
  processData: user => [{ type: GET_USER_FULFILLED, payload: user }]
};

export const EpicGetUser: Epic<any, any> = EpicUtils.Create(request);

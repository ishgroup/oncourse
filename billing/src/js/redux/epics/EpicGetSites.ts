import { Epic } from "redux-observable";
import * as EpicUtils from "./EpicUtils";
import { GET_SITES, GET_SITES_FULFILLED } from "../actions";
import BillingService from "../../api/services/BillingApi";

const request: EpicUtils.Request = {
  type: GET_SITES,
  getData: userId => BillingService.getSites(userId),
  processData: sites => [{ type: GET_SITES_FULFILLED, payload: sites }]
};

export const EpicGetSites: Epic<any, any> = EpicUtils.Create(request);

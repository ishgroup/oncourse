import { Epic } from "redux-observable";
import * as EpicUtils from "./EpicUtils";
import {getSites, setLoadingValue, showMessage, UPDATE_COLLEGE_SITES} from "../actions";
import BillingService from "../../api/services/BillingApi";

const request: EpicUtils.Request = {
  type: UPDATE_COLLEGE_SITES,
  getData: sites => BillingService.updateSites(sites),
  processData: (v, s) => [
    showMessage({
      message: "Websites updated",
      error: false
    }),
    getSites(s.user.userKey),
    setLoadingValue(false)
  ]
};

export const EpicUpdateCollegeSites: Epic<any, any> = EpicUtils.Create(request);

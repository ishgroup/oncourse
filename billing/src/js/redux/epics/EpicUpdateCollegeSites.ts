import { Epic } from "redux-observable";
import * as EpicUtils from "./EpicUtils";
import {getSites, setLoadingValue, showMessage, UPDATE_COLLEGE_SITES} from "../actions";
import BillingService from "../../api/services/BillingApi";

const request: EpicUtils.Request = {
  type: UPDATE_COLLEGE_SITES,
  getData: sites => {
    const updated = sites.map(({prefix, key, ...rest}) =>  ({
      ...rest,
      key: `${prefix}-${key}`
    }))
    return BillingService.updateSites(updated)
  },
  processData: () => [
    showMessage({
      message: "Websites updated", success: true
    }),
    getSites(),
    setLoadingValue(false)
  ]
};

export const EpicUpdateCollegeSites: Epic<any, any> = EpicUtils.Create(request);

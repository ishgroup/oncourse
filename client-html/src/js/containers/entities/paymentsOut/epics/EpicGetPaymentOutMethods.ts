import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import PreferencesService from "../../../preferences/services/PreferencesService";
import { GET_ACTIVE_PAYMENT_OUT_METHODS, GET_ACTIVE_PAYMENT_OUT_METHODS_FULFILLED } from "../actions";

const request: EpicUtils.Request = {
  type: GET_ACTIVE_PAYMENT_OUT_METHODS,
  getData: () => PreferencesService.getPaymentTypes(),
  processData: (paymentMethods: any, state: any, payload) => {
    const paymentOutMethods = paymentMethods.filter(({ systemType }) => systemType !== true);
    return [
      {
        type: GET_ACTIVE_PAYMENT_OUT_METHODS_FULFILLED,
        payload: paymentOutMethods
      }
    ];
  }
};

export const EpicGetPaymentOutMethods: Epic<any, any> = EpicUtils.Create(request);

import {IshState} from "../../services/IshState";
import CheckoutService from "../services/CheckoutService";
import {ItemSelect, PROCEED_TO_PAYMENT} from "../containers/summary/actions/Actions";
import {convertFromEnrolment} from "../containers/summary/reducers/State";
import {updateAmountRequest} from "../actions/Actions";
import * as EpicUtils from "./EpicUtils";
import {CheckoutModel} from "../../model/checkout/CheckoutModel";

const request: EpicUtils.Request<CheckoutModel, IshState> = {
  type: PROCEED_TO_PAYMENT,
  getData: (payload: any, state: IshState) :Promise<CheckoutModel> => CheckoutService.getCheckoutModel(state),
  processData: (value: CheckoutModel, state: IshState) => {
    return [{type: ItemSelect, payload: convertFromEnrolment(value)}, updateAmountRequest()];
  }
};
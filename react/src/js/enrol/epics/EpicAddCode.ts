import {Epic} from "redux-observable";
import "rxjs";
import {IshState} from "../../services/IshState";
import * as EpicUtils from "./EpicUtils";
import {ADD_CODE_REQUEST,  updateSummaryRequest} from "../actions/Actions";
import {Actions} from "../../web/actions/Actions";
import {Promotion} from "../../model/web/Promotion";
import CheckoutService from "../services/CheckoutService";


const request: EpicUtils.Request<Promotion, IshState> = {
  type: ADD_CODE_REQUEST,
  getData: CheckoutService.getPromotion,
  processData: (value: Promotion, state: IshState) => {
    return [{type: Actions.ADD_PROMOTION_TO_CART, payload: value.code}, updateSummaryRequest()];
  },
};

export const EpicAddCode: Epic<any, any> = EpicUtils.Create(request);

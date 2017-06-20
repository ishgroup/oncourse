import {Epic} from "redux-observable";
import "rxjs";
import {IshState} from "../../services/IshState";
import * as EpicUtils from "./EpicUtils";
import {ADD_CODE_REQUEST, getCheckoutModelFromBackend} from "../actions/Actions";
import {Actions} from "../../web/actions/Actions";
import {Promotion} from "../../model/web/Promotion";
import CheckoutService from "../services/CheckoutService";
import {FULFILLED} from "../../common/actions/ActionUtils";
import {normalize} from "normalizr";
import {PromotionsSchema} from "../../NormalizeSchema";


const request: EpicUtils.Request<Promotion, IshState> = {
  type: ADD_CODE_REQUEST,
  getData: CheckoutService.getPromotion,
  processData: (value: Promotion, state: IshState) => {
    return [{
      type: FULFILLED(Actions.ADD_PROMOTION_TO_CART),
      payload: normalize(value, PromotionsSchema),
    }, getCheckoutModelFromBackend()];
  },
};

export const EpicAddCode: Epic<any, any> = EpicUtils.Create(request);

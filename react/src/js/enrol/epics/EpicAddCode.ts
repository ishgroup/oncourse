import {Epic} from "redux-observable";
import "rxjs";
import {IshState} from "../../services/IshState";
import * as EpicUtils from "./EpicUtils";
import {ADD_CODE_REQUEST, getCheckoutModelFromBackend, ADD_REDEEM_VOUCHER_TO_STATE} from "../actions/Actions";
import {Actions} from "../../web/actions/Actions";
import {SubmitCodeResponse} from "../../model/web/SubmitCodeResponse";
import CheckoutService from "../services/CheckoutService";
import {FULFILLED} from "../../common/actions/ActionUtils";
import {normalize} from "normalizr";
import {PromotionsSchema} from "../../NormalizeSchema";


const request: EpicUtils.Request<SubmitCodeResponse, IshState> = {
  type: ADD_CODE_REQUEST,
  getData: CheckoutService.submitCode,
  processData: (value: SubmitCodeResponse, state: IshState) => {
    const result = [];

    if (value.promotion) {
      result.push(
        {
          type: FULFILLED(Actions.ADD_PROMOTION_TO_CART),
          payload: normalize(value.promotion, PromotionsSchema),
        },
      );
    }

    if (value.voucher) {
      result.push(
        {
          type: FULFILLED(ADD_REDEEM_VOUCHER_TO_STATE),
          payload: value.voucher,
        },
      );
    }

    result.push(getCheckoutModelFromBackend());

    return result;
  },
};

export const EpicAddCode: Epic<any, any> = EpicUtils.Create(request);

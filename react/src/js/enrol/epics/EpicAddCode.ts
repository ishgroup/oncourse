import {Epic} from "redux-observable";
import "rxjs";
import {IshState} from "../../services/IshState";
import * as EpicUtils from "./EpicUtils";
import {
  ADD_CODE_REQUEST, getCheckoutModelFromBackend, addRedeemVoucherToState,
  SHOW_MESSAGES_REQUEST,
} from "../actions/Actions";
import {Actions} from "../../web/actions/Actions";
import {CodeResponse} from "../../model/checkout/CodeResponse";
import CheckoutService from "../services/CheckoutService";
import {FULFILLED} from "../../common/actions/ActionUtils";
import {normalize} from "normalizr";
import {PromotionsSchema} from "../../NormalizeSchema";
import {addPayerFromVoucher} from "../containers/contact-add/actions/Actions";
import {addContactToSummary} from "../containers/summary/actions/Actions";


const request: EpicUtils.Request<CodeResponse, IshState> = {
  type: ADD_CODE_REQUEST,
  getData: CheckoutService.submitCode,
  processData: (value: CodeResponse, state: IshState) => {
    const result = [];

    const {promotion, voucher} = value;
    const hasActiveVoucherPayer = CheckoutService.hasActiveVoucherPayer(state.checkout);
    const preventAddVoucher = voucher && voucher.payer && hasActiveVoucherPayer;

    if (promotion) {
      result.push({
        type: FULFILLED(Actions.ADD_PROMOTION_TO_CART),
        payload: normalize(promotion, PromotionsSchema),
      });
    }


    if (voucher && !preventAddVoucher) {
      result.push(addRedeemVoucherToState(voucher));

      if (voucher.payer) {
        result.push(addPayerFromVoucher(voucher.payer));
        result.push(addContactToSummary(voucher.payer));
      }
    }

    if (preventAddVoucher) {
      return [
        {
          type: SHOW_MESSAGES_REQUEST,
          payload: {
            data: 'You already have selected voucher with payer'
          },
          meta: {
            form: null,
          },
        },
      ];
    }

    result.push(getCheckoutModelFromBackend());

    return result;
  },
};

export const EpicAddCode: Epic<any, any> = EpicUtils.Create(request);

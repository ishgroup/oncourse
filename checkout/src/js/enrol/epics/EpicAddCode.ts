import {Epic} from "redux-observable";
import "rxjs";
import {IshState} from "../../services/IshState";
import * as EpicUtils from "../../common/epics/EpicUtils";
import {
  ADD_CODE_REQUEST, getCheckoutModelFromBackend, addRedeemVoucherToState,
} from "../actions/Actions";
import {Actions} from "../../web/actions/Actions";
import {CodeResponse} from "../../model";
import CheckoutService from "../services/CheckoutService";
import {FULFILLED} from "../../common/actions/ActionUtils";
import {normalize} from "normalizr";
import {PromotionsSchema} from "../../NormalizeSchema";
import {addPayerFromVoucher} from "../containers/contact-add/actions/Actions";
import {addContactToSummary} from "../containers/summary/actions/Actions";


const request: EpicUtils.Request<CodeResponse, IshState> = {
  type: ADD_CODE_REQUEST,
  getData: (payload, state) => (
    CheckoutService.ifCodeExist(payload, state)
      ? Promise.reject({data: 'This code was already added.'})
      : CheckoutService.submitCode(payload)
  ),
  processData: (value: CodeResponse, state: IshState) => {
    const result = [];

    const {promotion, voucher} = value;
    const hasActiveVoucherPayer = CheckoutService.hasActiveVoucherPayer(state.checkout);
    const preventAddVoucher = voucher && voucher.payer && hasActiveVoucherPayer;

    if (preventAddVoucher) return [EpicUtils.showCommonError({message: 'You already have selected voucher with payer'})];

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
        result.push(addContactToSummary(voucher.payer, true));
      }
    }

    result.push(getCheckoutModelFromBackend());

    return result;
  },
};

export const EpicAddCode: Epic<any, any> = EpicUtils.Create(request);

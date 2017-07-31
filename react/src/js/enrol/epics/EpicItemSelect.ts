import {Epic} from "redux-observable";
import "rxjs";
import {SELECT_ITEM_REQUEST, UPDATE_ITEM, updateItem} from "../containers/summary/actions/Actions";
import CheckoutService from "../services/CheckoutService";
import {IshState} from "../../services/IshState";
import * as EpicUtils from "./EpicUtils";
import {Enrolment, Application, PurchaseItem} from "../../model";
import {getAmount} from "../actions/Actions";
import {FULFILLED} from "../../common/actions/ActionUtils";

const request: EpicUtils.Request<Enrolment | Application, IshState> = {
  type: SELECT_ITEM_REQUEST,
  getData: CheckoutService.updateItem,
  processData: (value: PurchaseItem, state: IshState) => {
    return [
      {type: FULFILLED(SELECT_ITEM_REQUEST)},
      updateItem(value),
      getAmount(),
    ];
  },
};

export const EpicItemSelect: Epic<any, any> = EpicUtils.Create(request);

import {Epic} from "redux-observable";
import "rxjs";
import {SELECT_ITEM, SELECT_ITEM_REQUEST} from "../containers/summary/actions/Actions";
import CheckoutService from "../services/CheckoutService";
import {IshState} from "../../services/IshState";
import * as EpicUtils from "./EpicUtils";
import {Enrolment} from "../../model/checkout/Enrolment";
import {ItemToState} from "../containers/summary/reducers/State";
import {getAmount} from "../actions/Actions";
import {Application} from "../../model/checkout/Application";
import {PurchaseItem} from "../../model/checkout/Index";

const request: EpicUtils.Request<Enrolment | Application, IshState> = {
  type: SELECT_ITEM_REQUEST,
  getData: CheckoutService.updateItem,
  processData: (value: PurchaseItem, state: IshState) => {
    return [
      {type: SELECT_ITEM, payload: ItemToState(value)},
      getAmount(),
    ];
  },
};

export const EpicItemSelect: Epic<any, any> = EpicUtils.Create(request);

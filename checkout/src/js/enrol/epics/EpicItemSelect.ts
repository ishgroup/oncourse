import {Epic} from "redux-observable";
import "rxjs";
import {SELECT_ITEM_REQUEST, updateItem} from "../containers/summary/actions/Actions";
import {IshState} from "../../services/IshState";
import * as EpicUtils from "../../common/epics/EpicUtils";
import {Enrolment, Application, PurchaseItem} from "../../model";
import {getCheckoutModelFromBackend} from "../actions/Actions";
import {FULFILLED} from "../../common/actions/ActionUtils";
import CheckoutServiceV2 from "../services/CheckoutServiceV2";

const request: EpicUtils.Request<Enrolment | Application, IshState> = {
  type: SELECT_ITEM_REQUEST,
  getData: CheckoutServiceV2.updateItem,
  processData: (value: PurchaseItem) => {
    return [
      {type: FULFILLED(SELECT_ITEM_REQUEST)},
      updateItem(value),
      getCheckoutModelFromBackend(),
    ];
  },
};

export const EpicItemSelect: Epic<any, any> = EpicUtils.Create(request);

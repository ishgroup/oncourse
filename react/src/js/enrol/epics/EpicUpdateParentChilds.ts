import {Epic} from "redux-observable";
import "rxjs";
import CheckoutService from "../services/CheckoutService";
import {IshState} from "../../services/IshState";
import * as EpicUtils from "./EpicUtils";
import {UPDATE_PARENT_CHILDS} from "../actions/Actions";
import {Amount} from "../../model/checkout/Amount";

const request: EpicUtils.Request<any, IshState> = {
  type: UPDATE_PARENT_CHILDS,
  getData: (payload, state: IshState) => CheckoutService.createParentChildrenRelation(payload.parentId, payload.childIds),
  processData: (response, state: IshState) => {
    console.log(response);
    return [

    ];
  },
};

export const EpicUpdateParentChilds: Epic<any, any> = EpicUtils.Create(request);

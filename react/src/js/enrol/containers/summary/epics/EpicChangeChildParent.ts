import {Epic} from "redux-observable";
import "rxjs";
import CheckoutService from "../../../services/CheckoutService";
import {IshState} from "../../../../services/IshState";
import * as EpicUtils from "../../../epics/EpicUtils";
import {CHANGE_CHILD_PARENT_REQUEST, CHANGE_CHILD_PARENT_FULFILLED} from "../actions/Actions";

const request: EpicUtils.Request<any, IshState> = {
  type: CHANGE_CHILD_PARENT_REQUEST,
  getData: (payload, state: IshState) => CheckoutService.changeParent(payload.parentId, payload.childId),
  processData: (response, state: IshState, payload) => {
    return [
      {
        type: CHANGE_CHILD_PARENT_FULFILLED,
        payload: {childId: payload.childId, parentId: payload.parentId},
      },
    ];
  },
};

export const EpicChangeChildParent: Epic<any, any> = EpicUtils.Create(request);

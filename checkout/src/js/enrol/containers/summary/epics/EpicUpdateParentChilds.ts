import {Epic} from "redux-observable";
import "rxjs";
import CheckoutService from "../../../services/CheckoutService";
import {IshState} from "../../../../services/IshState";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import {UPDATE_PARENT_CHILDS_FULFILLED, UPDATE_PARENT_CHILDS_REQUEST} from "../../../actions/Actions";

const request: EpicUtils.Request<any, IshState> = {
  type: UPDATE_PARENT_CHILDS_REQUEST,
  getData: (payload, state: IshState) => CheckoutService.createParentChildrenRelation(payload.parentId, payload.childIds),
  processData: (response, state: IshState, payload) => {
    return [
      {
        type: UPDATE_PARENT_CHILDS_FULFILLED,
        payload: {parentId: payload.parentId, childIds: payload.childIds},
      },
    ];
  },
};

export const EpicUpdateParentChilds: Epic<any, any> = EpicUtils.Create(request);

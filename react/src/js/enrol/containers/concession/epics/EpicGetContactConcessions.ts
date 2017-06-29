import "rxjs";
import {Epic} from "redux-observable";
import {IshState} from "../../../../services/IshState";
import * as EpicUtils from "../../../epics/EpicUtils";

import CheckoutService from "../../../services/CheckoutService";
import {GET_CONTACT_CONCESSIONS_REQUEST} from "../actions/Actions";
import {FULFILLED} from "../../../../common/actions/ActionUtils";

const request: EpicUtils.Request<any, IshState> = {
  type: GET_CONTACT_CONCESSIONS_REQUEST,
  getData: (payload, state: IshState) => CheckoutService.getContactConcessions(payload, state),
  processData: (value, state: IshState) => {
    return [{
      type: FULFILLED(GET_CONTACT_CONCESSIONS_REQUEST),
      payload: value,
    }];
  },
};

export const GetContactConcessions: Epic<any, IshState> = EpicUtils.Create(request);
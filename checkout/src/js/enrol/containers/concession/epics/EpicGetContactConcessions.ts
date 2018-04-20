import "rxjs";
import {Epic} from "redux-observable";
import {IshState} from "../../../../services/IshState";
import * as EpicUtils from "../../../../common/epics/EpicUtils";

import CheckoutService from "../../../services/CheckoutService";
import {GET_CONTACT_CONCESSIONS_AND_MEMBERSHIPS_REQUEST} from "../actions/Actions";
import {FULFILLED} from "../../../../common/actions/ActionUtils";

const request: EpicUtils.Request<any, IshState> = {
  type: GET_CONTACT_CONCESSIONS_AND_MEMBERSHIPS_REQUEST,
  getData: (payload, state: IshState) => CheckoutService.getContactConcessionsAndMemberships(payload, state),
  processData: (value, state: IshState) => {
    return [{
      type: FULFILLED(GET_CONTACT_CONCESSIONS_AND_MEMBERSHIPS_REQUEST),
      payload: value,
    }];
  },
};

export const GetContactConcessions: Epic<any, IshState> = EpicUtils.Create(request);
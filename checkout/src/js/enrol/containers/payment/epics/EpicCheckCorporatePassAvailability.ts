import {IshState} from "../../../../services/IshState";
import CheckoutService from "../../../services/CheckoutService";
import {IAction} from "../../../../actions/IshAction";
import {
  CHECK_CORPORATE_PASS_AVAILABILITY_FULFILLED,
  CHECK_CORPORATE_PASS_AVAILABILITY_REQUEST,
} from "../actions/Actions";
import {Create, Request} from "../../../epics/EpicUtils";
import {Observable} from "rxjs/Observable";
import {Epic} from "redux-observable";

const request: Request<any, IshState> = {
  type: CHECK_CORPORATE_PASS_AVAILABILITY_REQUEST,
  getData: (payload: any, state: IshState): Promise<any> => {
    return CheckoutService.checkIfCorporatePassAvailable(state);
  },
  processData: (response: boolean, state: IshState): IAction<any>[] | Observable<any> => {
    return [
      {
        type: CHECK_CORPORATE_PASS_AVAILABILITY_FULFILLED,
        payload: response,
      },
    ];
  },
};

export const CheckCorporatePassAvailability: Epic<any, any> = Create(request);

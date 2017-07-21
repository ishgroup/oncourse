import "rxjs";
import CheckoutService from "../services/CheckoutService";
import {IshState} from "../../services/IshState";
import {Create, Request} from "./EpicUtils";
import {Preferences} from "../../model";
import {Epic} from "redux-observable";
import {addPreferencesToState, GET_PREFERENCES} from "../actions/Actions";

const request: Request<any, IshState> = {
  type: GET_PREFERENCES,
  getData: () => CheckoutService.getPreferences(),
  processData: (response: Preferences, state: IshState) => {
    return [
      addPreferencesToState(response),
    ];
  },
};

export const EpicGetPreferences: Epic<any, IshState> = Create(request);

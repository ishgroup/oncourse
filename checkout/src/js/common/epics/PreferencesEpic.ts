import "rxjs";
import {Epic} from "redux-observable";
import {IshState} from "../../services/IshState";
import * as EpicUtils from "../../epics/epicsUtils";
import {Actions} from "../actions/Actions";
import {Preferences} from "../../model";
import CommonService from "../services/CommonService";

const request: EpicUtils.Request<any, IshState> = {
  type: Actions.GET_PREFERENCES_REQUEST,
  getData: () => CommonService.getPreferences(),
  processData: (value: Preferences, state: IshState) => {
    return [
      {
        type: Actions.GET_PREFERENCES_REQUEST_FULFILLED,
        payload: value,
      },
    ];
  },
};

export const EpicGetPreferences: Epic<any, IshState> = EpicUtils.Create(request);

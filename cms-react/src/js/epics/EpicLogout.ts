import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "./EpicUtils";
import {LOG_OUT_FULFILLED, LOG_OUT_REQUEST} from "../actions/actions";
import AuthService from "../services/AuthService";
import {User} from "../model";


const request: EpicUtils.Request<any, any> = {
  type: LOG_OUT_REQUEST,
  getData: (payload, state) => AuthService.logout(),
  processData: (user: User, state: any) => {
    return [
      {
        type: LOG_OUT_FULFILLED,
        payload: user,
      },
    ];
  },
};

export const EpicLogout: Epic<any, any> = EpicUtils.Create(request);

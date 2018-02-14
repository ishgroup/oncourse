
import {Epic} from "redux-observable";
import "rxjs";
import * as EpicUtils from "../../../epics/EpicUtils";
import {GET_USER_FULFILLED, GET_USER_REQUEST} from "../actions";
import AuthService from "../../../services/AuthService";
import {User} from "../../../model";


const request: EpicUtils.Request<any, any> = {
  type: GET_USER_REQUEST,
  getData: (payload, state) => AuthService.getUser(),
  processData: (user: User, state: any) => {
    
    return [
      {
        type: GET_USER_FULFILLED,
        payload: user,
      },
    ];
  },
};

export const EpicGetUser: Epic<any, any> = EpicUtils.Create(request);

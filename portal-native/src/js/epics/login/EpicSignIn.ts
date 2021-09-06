/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { LoginRequest, LoginResponse } from '@api/model';
import { createRequest, Request } from '../../utils/EpicUtils';
import {
  setIsLogged, setLoginStage, SIGN_IN
} from '../../actions/LoginActions';
import LoginService from '../../services/LoginService';
import { LoginStages } from '../../model/Login';
import { setToken } from '../../utils/SessionStorageUtils';
import { LoginErrorHandler } from '../../utils/ApiUtils';

const request: Request<LoginRequest, LoginResponse> = {
  type: SIGN_IN,
  getData: (req) => LoginService.signIn(req),
  processData: (response) => {
    const actions = [];
    if (response.vefiryEmail) {
      actions.push(setLoginStage(LoginStages.EmaiConfirm));
    }
    // if (response.user) {
    //   actions.push(setUser(response.user));
    // }
    if (response.token) {
      setToken(response.token);
      actions.push(setIsLogged(true));
    }

    return actions;
  },
  processError: (data) => LoginErrorHandler(data)
};

export default createRequest(request);

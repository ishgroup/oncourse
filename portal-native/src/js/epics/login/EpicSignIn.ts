/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { LoginRequest, LoginResponse } from '@api/model';
import { createRequest, Request } from '../../utils/EpicUtils';
import { setLoginStage, SIGN_IN, signInFulfilled } from '../../actions/LoginActions';
import LoginService from '../../services/LoginService';
import { LoginStages } from '../../model/Login';
import { setToken } from '../../utils/SessionStorage';

const request: Request<LoginRequest, LoginResponse> = {
  type: SIGN_IN,
  getData: (req) => LoginService.signIn(req),
  processData: (response) => {
    if (response.vefiryEmail) {
      return [setLoginStage(LoginStages.EmaiConfirm)];
    }
    if (response.token) {
      setToken(response.token);
      return [signInFulfilled()];
    }

    return [];
  }
};

export default createRequest(request);

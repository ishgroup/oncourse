/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { LoginRequest, LoginResponse } from '@api/model';
import { createRequest, Request } from '../../utils/EpicUtils';
import { SIGN_IN, signInFulfilled } from '../../actions/LoginActions';
import LoginService from '../../services/LoginService';

const request: Request<LoginRequest, LoginResponse> = {
  type: SIGN_IN,
  getData: (req) => LoginService.signIn(req),
  processData: (response) => [signInFulfilled(response)]
};

export default createRequest(request);

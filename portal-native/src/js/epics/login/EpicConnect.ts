/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { TokenResponse, LoginResponse } from '@api/model';
import { createRequest, Request } from '../../utils/EpicUtils';
import { CONNECT, signInFulfilled } from '../../actions/LoginActions';
import LoginService from '../../services/LoginService';

const request: Request<TokenResponse, LoginResponse> = {
  type: CONNECT,
  getData: (tokenResponse) => LoginService.connect(tokenResponse),
  processData: (response) => [signInFulfilled(response)]
};

export default createRequest(request);

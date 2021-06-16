/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { LoginRequest } from '@api/model';
import { createRequest, Request } from '../../utils/EpicUtils';
import { SIGN_IN_FULFILLED, SIGN_UP } from '../../actions/LoginActions';
import LoginService from '../../services/LoginService';

const request: Request<any, LoginRequest> = {
  type: SIGN_UP,
  getData: (req) => LoginService.signUp(req),
  processData: () => [{
    type: SIGN_IN_FULFILLED,
  }],
};

export default createRequest(request);

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { createRequest, Request } from '../../utils/EpicUtils';
import { EMAIL_LOGIN, setLoginStage } from '../../actions/LoginActions';
import LoginService from '../../services/LoginService';
import { LoginStages } from '../../model/Login';
import { LoginErrorHandler } from '../../utils/ApiUtils';

const request: Request<string, boolean> = {
  type: EMAIL_LOGIN,
  getData: (email) => LoginService.emailLogin(email),
  processData: () => [setLoginStage(LoginStages.EmaiConfirm)],
  processError: (data) => LoginErrorHandler(data, 'Failed to email link')
};

export default createRequest(request);

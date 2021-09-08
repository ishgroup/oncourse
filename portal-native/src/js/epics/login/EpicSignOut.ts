/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { createRequest, Request } from '../../utils/EpicUtils';
import { SIGN_OUT } from '../../actions/LoginActions';
import LoginService from '../../services/LoginService';

const request: Request = {
  type: SIGN_OUT,
  getData: () => LoginService.signOut(),
  processData: () => [],
};

export default createRequest(request);

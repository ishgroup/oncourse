/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Session } from '@api/model';
import { createRequest, Request } from '../../utils/EpicUtils';
import { GET_USER_SESSIONS, getUserSessionsFulfilled } from '../../actions/SessionActions';
import SessionsService from '../../services/SessionsService';

const request: Request<undefined, Session[]> = {
  type: GET_USER_SESSIONS,
  getData: () => SessionsService.getUserSessions(),
  processData: (sessions) => [getUserSessionsFulfilled(sessions || [])]
};

export default createRequest(request);

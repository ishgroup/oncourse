/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { createRequest, Request } from '../../utils/EpicUtils';
import LoginService from '../../services/LoginService';
import { GET_CLIENT_IDS, setClientIds } from '../../actions/ThirdPartyActions';

const request: Request<null, { [key: string]: string; }> = {
  type: GET_CLIENT_IDS,
  getData: () => LoginService.ssoClientIds(),
  processData: (ids) => [setClientIds(Object.keys(ids).reduce((p, c) => ({
    ...p,
    [c]: {
      clientId: ids[c]
    }
  }), {}))]
};

export default createRequest(request);

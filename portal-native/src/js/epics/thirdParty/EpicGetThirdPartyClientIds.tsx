/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { createRequest, Request } from '../../utils/EpicUtils';
import LoginService from '../../services/LoginService';
import { GET_CLIENT_IDS, setClientIds } from '../../actions/ThirdPartyActions';
import { ThirdPartyKeysResponse, ThirdPartyState } from '../../model/ThirdParty';

const request: Request<null, ThirdPartyKeysResponse> = {
  type: GET_CLIENT_IDS,
  getData: () => LoginService.ssoClientIds(),
  processData: (res) => [
    setClientIds(Object.keys(res)
      .reduce<ThirdPartyState>((p, c) => ({
      ...p,
      [c]: {
        webClientId: res[c]?.web,
        androidClientId: res[c]?.android,
        iosClientId: res[c]?.ios
      }
    }), {}))]
};

export default createRequest(request);

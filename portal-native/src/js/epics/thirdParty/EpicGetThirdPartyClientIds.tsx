/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ClientId } from '@api/model';
import { createRequest, Request } from '../../utils/EpicUtils';
import LoginService from '../../services/LoginService';
import { GET_CLIENT_IDS, setClientIds } from '../../actions/ThirdPartyActions';
import { ThirdPartyState } from '../../model/ThirdParty';

const request: Request<null, ClientId[]> = {
  type: GET_CLIENT_IDS,
  getData: () => LoginService.ssoClientIds(),
  processData: (res) => {
    const result: ThirdPartyState = {};

    res.forEach((key) => {
      if (!result[key.ssOProvider]) {
        result[key.ssOProvider] = {
          webClientId: '',
          iosClientId: '',
          androidClientId: ''
        };
      }

      switch (key.platform) {
        case 'Android': {
          result[key.ssOProvider].androidClientId = key.clientId;
          break;
        }
        case 'iOS': {
          result[key.ssOProvider].iosClientId = key.clientId;
          break;
        }
        case 'Web': {
          result[key.ssOProvider].webClientId = key.clientId;
        }
      }
    });

    return [
      setClientIds(result)
    ];
  }
};

export default createRequest(request);

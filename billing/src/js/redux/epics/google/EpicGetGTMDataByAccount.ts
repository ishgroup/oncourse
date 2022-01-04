/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { Request, Create } from '../EpicUtils';
import { GET_GTM_DATA_BY_ACCOUNT, setGtmAndGaData } from '../../actions/Google';
import GoogleService from '../../../api/services/GoogleService';
import { getTokenString } from '../../../utils/Google';
import { GoogleState } from '../../../models/Google';

const request: Request<GoogleState, string> = {
  type: GET_GTM_DATA_BY_ACCOUNT,
  getData: async (accountId, state) => {
    const token = getTokenString(state.google);
    const gtmContainers = { ...state.google.gtmContainers };

    const container = await GoogleService.getGTMContainers(token, accountId);
    if (container) {
      gtmContainers[accountId] = container.container;
    }

    return {
      gtmContainers
    };
  },
  processData: (res) => [
    setGtmAndGaData(res)
  ]
};

export const EpicGetGTMDataByAccount: Epic<any, any> = Create(request);

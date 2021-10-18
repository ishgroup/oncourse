/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { Request, Create } from '../EpicUtils';
import { GET_GTM_AND_GA_DATA, getGtmAndGaDataFulfilled } from '../../actions/Google';
import GoogleService from '../../../api/services/GoogleService';
import { getTokenString } from '../../../utils/Google';

const request: Request = {
  type: GET_GTM_AND_GA_DATA,
  getData: async (p, state) => {
    const token = getTokenString(state);
    const gtmAccounts = await GoogleService.getGTMAccounts(token);
    const gtmContainers = {};

    for (const acc of gtmAccounts.account) {
      const container = await GoogleService.getGTMContainers(token, acc.accountId);
      if (container) {
        gtmContainers[acc.accountId] = container.container;
      }
    }

    const gaAccounts = await GoogleService.getGAAccounts(token);

    return [
      gtmAccounts.account,
      gaAccounts.items,
      Object.keys(gtmContainers).length ? gtmContainers : null
    ];
  },
  processData: ([gtmAccounts, gaAccounts, gtmContainers]) => [
    getGtmAndGaDataFulfilled(gtmAccounts, gaAccounts, gtmContainers)
  ]
};

export const EpicGetGTMandGAData: Epic<any, any> = Create(request);

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { Request, Create } from '../EpicUtils';
import { GET_GTM_AND_GA_DATA, getGtmAndGaDataFulfilled } from '../../actions/Google';
import GoogleService from '../../../api/services/GoogleService';
import { getTokenString } from '../../../utils/Google';
import { GoogleState } from '../../../models/Google';

const request: Request<GoogleState, null> = {
  type: GET_GTM_AND_GA_DATA,
  getData: async (p, state) => {
    const token = getTokenString(state.google);
    const gtmAccounts = await GoogleService.getGTMAccounts(token);
    const gtmContainers = {};
    const gaWebProperties = {};

    for (const acc of gtmAccounts.account) {
      const container = await GoogleService.getGTMContainers(token, acc.accountId);
      if (container) {
        gtmContainers[acc.accountId] = container.container;
      }
    }

    const gaAccounts = await GoogleService.getGAAccounts(token);

    for (const acc of gaAccounts.items) {
      const property = await GoogleService.getGAWebProperties(token, acc.id);
      if (property) {
        gaWebProperties[acc.id] = property.items;
      }
    }

    const result: GoogleState = {
      gaAccounts: gaAccounts.items,
      gtmAccounts: gtmAccounts.account,
      gtmContainers,
      gaWebProperties
    };

    return result;
  },
  processData: (res) => [
    getGtmAndGaDataFulfilled(res)
  ]
};

export const EpicGetGTMandGAData: Epic<any, any> = Create(request);

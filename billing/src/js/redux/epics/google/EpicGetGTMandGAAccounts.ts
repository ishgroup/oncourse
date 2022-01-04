/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { Request, Create } from '../EpicUtils';
import { GET_GTM_AND_GA_ACCOUNTS, setGtmAndGaData } from '../../actions/Google';
import GoogleService from '../../../api/services/GoogleService';
import { getTokenString } from '../../../utils/Google';
import { GoogleState } from '../../../models/Google';

const request: Request<GoogleState, null> = {
  type: GET_GTM_AND_GA_ACCOUNTS,
  getData: async (p, state) => {
    const token = getTokenString(state.google);

    const [
      gtmAccounts,
      gaAccounts
    ] = await Promise.all([
      await GoogleService.getGTMAccounts(token),
      await GoogleService.getGAAccounts(token)
    ]);

    const result: GoogleState = {
      gaAccounts: gaAccounts.items,
      gtmAccounts: gtmAccounts.account
    };

    return result;
  },
  processData: (res) => [
    setGtmAndGaData(res)
  ]
};

export const EpicGetGTMandGAAccounts: Epic<any, any> = Create(request);

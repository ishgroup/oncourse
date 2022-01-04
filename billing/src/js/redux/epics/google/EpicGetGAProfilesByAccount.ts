/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { Request, Create } from '../EpicUtils';
import { GET_GA_PROFILES, setGtmAndGaData } from '../../actions/Google';
import GoogleService from '../../../api/services/GoogleService';
import { getTokenString } from '../../../utils/Google';
import { GAProfilesRequestAction, GoogleState } from '../../../models/Google';

const request: Request<GoogleState, GAProfilesRequestAction> = {
  type: GET_GA_PROFILES,
  getData: async ({ gaAccountId, gaPropertyId }, state) => {
    const token = getTokenString(state.google);
    const gaWebProfiles = { ...state.google.gaWebProfiles };

    const profiles = await GoogleService.getGAWebProfiles(
      token,
      gaAccountId,
      gaPropertyId
    );
    if (profiles) {
      gaWebProfiles[gaPropertyId] = profiles.items;
    }

    return {
      gaWebProfiles
    };
  },
  processData: (res) => [
    setGtmAndGaData(res)
  ]
};

export const EpicGetGAProfilesByAccount: Epic<any, any> = Create(request);

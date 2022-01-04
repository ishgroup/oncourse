/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import { Request, Create } from '../EpicUtils';
import { GET_GA_WEB_PROPERTIES_BY_ACCOUNT, setGtmAndGaData } from '../../actions/Google';
import GoogleService from '../../../api/services/GoogleService';
import { getTokenString } from '../../../utils/Google';
import { GoogleState } from '../../../models/Google';

const request: Request<GoogleState, string> = {
  type: GET_GA_WEB_PROPERTIES_BY_ACCOUNT,
  getData: async (gaAccountId, state) => {
    const token = getTokenString(state.google);
    const gaWebProperties = { ...state.google.gaWebProperties };

    const property = await GoogleService.getGAWebProperties(token, gaAccountId);

    if (property) {
      gaWebProperties[gaAccountId] = property.items;
    }

    return {
      gaWebProperties
    };
  },
  processData: (res) => [
    setGtmAndGaData(res)
  ]
};

export const EpicGetGAWebPropertiesByAccount: Epic<any, any> = Create(request);

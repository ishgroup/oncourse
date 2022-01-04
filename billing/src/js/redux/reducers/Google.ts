/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from '../../models/IshAction';
import { GoogleState } from '../../models/Google';
import {
  GET_CLIENT_ID_FULFILLED,
  SET_GTM_AND_GA_DATA,
  SET_GOOGLE_CREDENTIALS
} from '../actions/Google';

const Initial: GoogleState = {
  profile: null,
  token: null,
  gtmAccounts: [],
  gtmContainers: {},
  gaAccounts: [],
  gaWebProperties: {},
  gaWebProfiles: {}
};

export const googleReducer = (state: GoogleState = Initial, action: IAction): GoogleState => {
  switch (action.type) {
    case SET_GTM_AND_GA_DATA:
    case SET_GOOGLE_CREDENTIALS:
    case GET_CLIENT_ID_FULFILLED:
      return {
        ...state,
        ...action.payload
      };

    default:
      return {
        ...state
      };
  }
};

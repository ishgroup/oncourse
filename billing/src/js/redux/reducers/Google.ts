/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from '../../models/IshAction';
import { GoogleState } from '../../models/Google';
import {
  GET_CLIENT_ID_FULFILLED,
  GET_GTM_AND_GA_DATA,
  GET_GTM_AND_GA_DATA_FULFILLED,
  SET_GOOGLE_CREDENTIALS
} from '../actions/Google';
import { FETCH_FAIL } from '../actions';

const Initial: GoogleState = {
  loading: false,
  profile: null,
  token: null,
  gtmAccounts: null,
  gtmContainers: null,
  gaAccounts: null,
  gaWebProperties: null,
  gaWebProfiles: null
};

export const googleReducer = (state: GoogleState = Initial, action: IAction): GoogleState => {
  switch (action.type) {
    case GET_GTM_AND_GA_DATA:
      return {
        ...state,
        loading: true
      };
    case FETCH_FAIL:
      return {
        ...state,
        loading: false
      };
    case GET_GTM_AND_GA_DATA_FULFILLED:
    case SET_GOOGLE_CREDENTIALS:
    case GET_CLIENT_ID_FULFILLED:
      return {
        ...state,
        loading: false,
        ...action.payload
      };

    default:
      return {
        ...state
      };
  }
};

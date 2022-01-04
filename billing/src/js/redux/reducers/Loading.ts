/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from '../../models/IshAction';
import {
  FETCH_FAIL,
  SET_LOADING_VALUE,
} from '../actions';
import { GET_SITES, GET_SITES_FULFILLED, UPDATE_COLLEGE_SITES } from '../actions/Sites';
import { CREATE_COLLEGE, GET_COLLEGE_KEY, SET_COLLEGE_KEY } from '../actions/College';
import {
  CONFIGURE_GOOGLE_FOR_SITE,
  GET_GA_WEB_PROPERTIES_BY_ACCOUNT,
  GET_GA_PROFILES,
  GET_GTM_DATA_BY_ACCOUNT,
  SET_GTM_AND_GA_DATA
} from '../actions/Google';
import { GET_SETTINGS, GET_SETTINGS_FULFILLED, UPDATE_SETTINGS } from '../actions/Settings';

export const loadingReducer = (state = false, action: IAction): boolean => {
  switch (action.type) {
    case GET_SITES_FULFILLED:
    case SET_COLLEGE_KEY:
    case FETCH_FAIL:
    case GET_SETTINGS_FULFILLED:
    case SET_GTM_AND_GA_DATA:
      return false;
    case GET_COLLEGE_KEY:
    case GET_SITES:
    case CREATE_COLLEGE:
    case UPDATE_COLLEGE_SITES:
    case CONFIGURE_GOOGLE_FOR_SITE:
    case GET_SETTINGS:
    case UPDATE_SETTINGS:
    case GET_GTM_DATA_BY_ACCOUNT:
    case GET_GA_WEB_PROPERTIES_BY_ACCOUNT:
    case GET_GA_PROFILES:
      return true;
    case SET_LOADING_VALUE:
      return action.payload;
    default:
      return state;
  }
};

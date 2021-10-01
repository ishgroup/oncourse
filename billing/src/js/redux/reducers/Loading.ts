/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { IAction } from '../../models/IshAction';
import {
  CREATE_COLLEGE,
  GET_COLLEGE_KEY,
  GET_SITES,
  GET_SITES_FULFILLED,
  SET_COLLEGE_KEY,
  SET_LOADING_VALUE,
  UPDATE_COLLEGE_SITES
} from '../actions';

export const loadingReducer = (state = false, action: IAction): boolean => {
  switch (action.type) {
    case GET_SITES_FULFILLED:
    case SET_COLLEGE_KEY:
      return false;
    case GET_COLLEGE_KEY:
    case GET_SITES:
    case CREATE_COLLEGE:
    case UPDATE_COLLEGE_SITES:
      return true;
    case SET_LOADING_VALUE:
      return action.payload;
    default:
      return false;
  }
};

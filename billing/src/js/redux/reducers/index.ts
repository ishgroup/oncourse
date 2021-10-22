/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { combineReducers } from 'redux';
import { collegeReducer } from './College';
import { State } from '../../models/State';
import { loadingReducer } from './Loading';
import { sitesReducer } from './Sites';
import { googleReducer } from './Google';
import { formReducer } from './Form';
import { messageReducer } from './Message';
import { confirmReducer } from './Confirm';
import { settingsReducer } from './Settings';

export const billingReducers = combineReducers<State>(
  {
    college: collegeReducer,
    loading: loadingReducer,
    sites: sitesReducer,
    google: googleReducer,
    message: messageReducer,
    form: formReducer,
    confirm: confirmReducer,
    settings: settingsReducer
  }
);

/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from 'redux-observable';
import * as EpicUtils from '../../../common/epics/EpicUtils';
import { GET_LOCATION, GET_LOCATION_FULFILLED } from '../actions';
import PreferencesService from '../services/PreferencesService';

const request: EpicUtils.Request = {
  type: GET_LOCATION,
  getData: () => PreferencesService.getLocation(),
  processData: location => {
    return [
      {
        type: GET_LOCATION_FULFILLED,
        payload: location
      }
    ];
  }
};

export const EpicGetLocation: Epic<any, any> = EpicUtils.Create(request);
/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { SettingsDTO } from '@api/model';
import { IAction } from '../../models/IshAction';
import { GET_SETTINGS_FULFILLED } from '../actions/Settings';

const Initial: SettingsDTO = {};

export const settingsReducer = (state: SettingsDTO = Initial, action: IAction): SettingsDTO => {
  switch (action.type) {
    case GET_SETTINGS_FULFILLED:
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
